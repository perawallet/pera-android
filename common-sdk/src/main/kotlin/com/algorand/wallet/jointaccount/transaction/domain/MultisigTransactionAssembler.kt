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

import android.util.Base64
import com.algorand.algosdk.crypto.Address
import com.algorand.algosdk.crypto.Ed25519PublicKey
import com.algorand.algosdk.crypto.MultisigSignature
import com.algorand.algosdk.crypto.Signature
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.transaction.Transaction
import com.algorand.algosdk.util.Encoder
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.transaction.domain.model.ParticipantSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import javax.inject.Inject

class MultisigTransactionAssembler @Inject constructor() {

    fun assemble(
        rawTransactionsBase64: List<String>,
        participantAddresses: List<String>,
        version: Int,
        threshold: Int,
        responses: List<ParticipantSignature>
    ): PeraResult<List<ByteArray>> {
        if (rawTransactionsBase64.isEmpty()) return PeraResult.Success(emptyList())
        val signedList = mutableListOf<ByteArray>()
        for (txIndex in rawTransactionsBase64.indices) {
            val rawTxBytes = decodeBase64(rawTransactionsBase64[txIndex]) ?: return PeraResult.Error(Exception("Invalid base64 raw transaction"))
            val tx = runCatching {
                Encoder.decodeFromMsgPack(rawTxBytes, Transaction::class.java)
            }.getOrElse { e -> return PeraResult.Error(Exception(e)) }
            val subsigs = buildSubsigsWithSignatures(
                participantAddresses = participantAddresses,
                responses = responses.filter { it.type == SignRequestResponseType.SIGNED },
                txIndex = txIndex
            ) ?: return PeraResult.Error(Exception("Failed to build multisig subsigs"))
            val multisigSig = MultisigSignature(version, threshold, subsigs)
            val signedTx = SignedTransaction(tx, multisigSig)
            val encoded = runCatching {
                Encoder.encodeToMsgPack(signedTx)
            }.getOrElse { e -> return PeraResult.Error(Exception(e)) }
            signedList.add(encoded)
        }
        return PeraResult.Success(signedList)
    }

    private fun buildSubsigsWithSignatures(
        participantAddresses: List<String>,
        responses: List<ParticipantSignature>,
        txIndex: Int
    ): List<MultisigSignature.MultisigSubsig>? {
        val signatureByAddress = responses.associate { response ->
            val sigBase64 = response.signatures.getOrNull(txIndex)
            val sigBytes = sigBase64?.let { decodeBase64(it) }
            response.address to sigBytes
        }.filterValues { it != null }.mapValues { it.value!! }
        return participantAddresses.map { address ->
            val publicKeyBytes = runCatching { Address(address).getBytes() }.getOrNull() ?: return null
            val sig = signatureByAddress[address]?.let { Signature(it) }
            MultisigSignature.MultisigSubsig(Ed25519PublicKey(publicKeyBytes), sig)
        }
    }

    private fun decodeBase64(str: String): ByteArray? {
        return runCatching { Base64.decode(str, Base64.NO_WRAP) }.getOrNull()
            ?.takeIf { it.isNotEmpty() }
    }
}
