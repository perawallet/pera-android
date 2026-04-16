/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.jointaccount.transaction.domain

import java.util.Base64
import com.algorand.algosdk.crypto.Address
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.transaction.domain.model.ParticipantSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import org.msgpack.core.MessagePack
import org.msgpack.core.MessagePacker
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Assembles multisig signed transactions by writing the msgpack envelope directly,
 * embedding the raw transaction bytes as-is without decode/re-encode through the
 * Java algosdk. This preserves the exact inner transaction encoding that was signed.
 */
class MultisigTransactionAssembler @Inject constructor() {

    fun assemble(
        rawTransactionsBase64: List<String>,
        participantAddresses: List<String>,
        version: Int,
        threshold: Int,
        responses: List<ParticipantSignature>,
        jointAccountAddress: String? = null
    ): PeraResult<List<ByteArray>> {
        if (rawTransactionsBase64.isEmpty()) return PeraResult.Success(emptyList())
        val signedResponses = responses.filter { it.type == SignRequestResponseType.SIGNED }
        val signaturesByAddress = buildSignaturesByAddress(signedResponses)
        val publicKeysByAddress = resolvePublicKeys(participantAddresses) ?: return PeraResult.Error(
            Exception("Failed to resolve participant public keys")
        )
        val multisigPublicKey = jointAccountAddress?.let { resolvePublicKeyBytes(it) }
        val signedList = mutableListOf<ByteArray>()
        for (txIndex in rawTransactionsBase64.indices) {
            val validSignatureCount = countValidSignaturesForTransaction(
                signaturesByAddress, txIndex
            )
            if (validSignatureCount < threshold) {
                return PeraResult.Error(
                    Exception(
                        "Not enough valid signatures: $validSignatureCount/$threshold"
                    )
                )
            }
            val rawTxBytes = decodeBase64(rawTransactionsBase64[txIndex])
                ?: return PeraResult.Error(Exception("Invalid base64 raw transaction"))
            val senderPk = extractSenderPublicKey(rawTxBytes)
            val authAddrPk = if (multisigPublicKey != null && senderPk != null &&
                !multisigPublicKey.contentEquals(senderPk)
            ) {
                multisigPublicKey
            } else {
                null
            }
            val signedTxBytes = packSignedMultisigTransaction(
                rawTxBytes = rawTxBytes,
                participantAddresses = participantAddresses,
                publicKeysByAddress = publicKeysByAddress,
                signaturesByAddress = signaturesByAddress,
                txIndex = txIndex,
                version = version,
                threshold = threshold,
                authAddrPublicKey = authAddrPk
            ) ?: return PeraResult.Error(Exception("Failed to pack signed multisig transaction"))
            signedList.add(signedTxBytes)
        }
        return PeraResult.Success(signedList)
    }

    private fun buildSignaturesByAddress(
        responses: List<ParticipantSignature>
    ): Map<String, List<String?>> {
        return responses.associate { it.address to it.signatures }
    }

    private fun countValidSignaturesForTransaction(
        signaturesByAddress: Map<String, List<String?>>,
        txIndex: Int
    ): Int {
        return signaturesByAddress.values.count { signatures ->
            val sigBase64 = signatures.getOrNull(txIndex)
            sigBase64 != null && decodeBase64(sigBase64) != null
        }
    }

    private fun resolvePublicKeys(addresses: List<String>): Map<String, ByteArray>? {
        return addresses.associateWith { address ->
            runCatching { Address(address).getBytes() }.getOrNull() ?: return null
        }
    }

    /**
     * Builds the signed multisig transaction msgpack manually.
     * The outer map keys are in alphabetical order: "msig", optionally "sgnr", and "txn".
     * The "txn" value is the raw transaction bytes embedded directly.
     * The "sgnr" field is included when the transaction sender differs from the multisig
     * address (i.e. the sender is a rekeyed account whose auth-addr is the multisig).
     */
    private fun packSignedMultisigTransaction(
        rawTxBytes: ByteArray,
        participantAddresses: List<String>,
        publicKeysByAddress: Map<String, ByteArray>,
        signaturesByAddress: Map<String, List<String?>>,
        txIndex: Int,
        version: Int,
        threshold: Int,
        authAddrPublicKey: ByteArray? = null
    ): ByteArray? {
        return runCatching {
            val outputStream = ByteArrayOutputStream()
            MessagePack.newDefaultPacker(outputStream).use { packer ->
                val fieldCount = if (authAddrPublicKey != null) 3 else 2
                packer.packMapHeader(fieldCount)

                packer.packString("msig")
                packMultisig(
                    packer = packer,
                    participantAddresses = participantAddresses,
                    publicKeysByAddress = publicKeysByAddress,
                    signaturesByAddress = signaturesByAddress,
                    txIndex = txIndex,
                    version = version,
                    threshold = threshold
                )

                if (authAddrPublicKey != null) {
                    packer.packString("sgnr")
                    packer.packBinaryHeader(authAddrPublicKey.size)
                    packer.addPayload(authAddrPublicKey)
                }

                packer.packString("txn")
                packer.addPayload(rawTxBytes)
            }
            outputStream.toByteArray()
        }.getOrNull()
    }

    private fun packMultisig(
        packer: MessagePacker,
        participantAddresses: List<String>,
        publicKeysByAddress: Map<String, ByteArray>,
        signaturesByAddress: Map<String, List<String?>>,
        txIndex: Int,
        version: Int,
        threshold: Int
    ) {
        packer.packMapHeader(3)

        packer.packString("subsig")
        packer.packArrayHeader(participantAddresses.size)
        for (address in participantAddresses) {
            val publicKey = publicKeysByAddress[address]!!
            val sigBase64 = signaturesByAddress[address]?.getOrNull(txIndex)
            val sigBytes = sigBase64?.let { decodeBase64(it) }
            packSubsig(packer, publicKey, sigBytes)
        }

        packer.packString("thr")
        packer.packInt(threshold)

        packer.packString("v")
        packer.packInt(version)
    }

    private fun packSubsig(
        packer: MessagePacker,
        publicKey: ByteArray,
        signature: ByteArray?
    ) {
        packer.packMapHeader(if (signature != null) 2 else 1)
        packer.packString("pk")
        packer.packBinaryHeader(publicKey.size)
        packer.addPayload(publicKey)
        if (signature != null) {
            packer.packString("s")
            packer.packBinaryHeader(signature.size)
            packer.addPayload(signature)
        }
    }

    /**
     * Extracts the 32-byte sender public key ("snd") from raw transaction msgpack bytes.
     */
    private fun extractSenderPublicKey(rawTxBytes: ByteArray): ByteArray? {
        return runCatching {
            val unpacker = MessagePack.newDefaultUnpacker(rawTxBytes)
            val mapSize = unpacker.unpackMapHeader()
            for (i in 0 until mapSize) {
                val key = unpacker.unpackString()
                if (key == "snd") {
                    val len = unpacker.unpackBinaryHeader()
                    val sndBytes = ByteArray(len)
                    unpacker.readPayload(sndBytes)
                    return sndBytes
                } else {
                    unpacker.skipValue()
                }
            }
            null
        }.getOrNull()
    }

    private fun resolvePublicKeyBytes(address: String): ByteArray? {
        return runCatching { Address(address).getBytes() }.getOrNull()
    }

    private fun decodeBase64(str: String): ByteArray? {
        return runCatching { Base64.getDecoder().decode(str) }.getOrNull()
            ?.takeIf { it.isNotEmpty() && !it.all { byte -> byte == 0.toByte() } }
    }
}
