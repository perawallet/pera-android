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

package com.algorand.android.modules.addaccount.joint.transaction.domain.usecase

import android.util.Base64
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.util.Encoder
import com.algorand.android.utils.decodeBase64
import com.algorand.android.utils.signTx
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.transaction.domain.model.AddSignatureInput
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequest
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.usecase.AddJointAccountSignature
import javax.inject.Inject

data class SignAndSubmitResult(
    val signedAddresses: List<String>,
    val apiResult: PeraResult<JointSignRequest>?
)

fun interface SignAndSubmitJointAccountSignature {
    suspend operator fun invoke(
        signRequestId: String,
        participantAddresses: List<String>,
        rawTransactions: List<String>
    ): SignAndSubmitResult
}

internal class SignAndSubmitJointAccountSignatureUseCase @Inject constructor(
    private val addJointAccountSignature: AddJointAccountSignature,
    private val getLocalAccount: GetLocalAccount,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdSeed: GetHdSeed,
    private val signHdKeyTransaction: SignHdKeyTransaction
) : SignAndSubmitJointAccountSignature {

    override suspend fun invoke(
        signRequestId: String,
        participantAddresses: List<String>,
        rawTransactions: List<String>
    ): SignAndSubmitResult {
        val signatureInputs = mutableListOf<AddSignatureInput>()
        val signedAddresses = mutableListOf<String>()

        for (participantAddress in participantAddresses) {
            val signatures = signAllTransactions(rawTransactions, participantAddress) ?: continue
            signatureInputs.add(
                AddSignatureInput(
                    address = participantAddress,
                    response = SignRequestResponseType.SIGNED,
                    signatures = listOf(signatures),
                    deviceId = null
                )
            )
            signedAddresses.add(participantAddress)
        }

        if (signatureInputs.isEmpty()) {
            return SignAndSubmitResult(signedAddresses = emptyList(), apiResult = null)
        }

        val apiResult = addJointAccountSignature(signRequestId, signatureInputs)
        val confirmedSignedAddresses = if (apiResult is PeraResult.Success) signedAddresses else emptyList()
        return SignAndSubmitResult(signedAddresses = confirmedSignedAddresses, apiResult = apiResult)
    }

    private suspend fun signAllTransactions(
        rawTransactions: List<String>,
        participantAddress: String
    ): List<String?>? {
        val signatures = mutableListOf<String?>()
        for (rawTransaction in rawTransactions) {
            val transactionBytes = rawTransaction.decodeBase64() ?: return null
            val signatureBytes = signTransaction(transactionBytes, participantAddress) ?: return null
            signatures.add(Base64.encodeToString(signatureBytes, Base64.NO_WRAP))
        }
        return signatures
    }

    private suspend fun signTransaction(transactionBytes: ByteArray, signerAddress: String): ByteArray? {
        val localAccount = getLocalAccount(signerAddress) ?: return null

        return when (localAccount) {
            is LocalAccount.Algo25 -> signAlgo25Transaction(transactionBytes, signerAddress)
            is LocalAccount.HdKey -> signHdKeyTransaction(transactionBytes, localAccount)
            else -> null
        }
    }

    private suspend fun signAlgo25Transaction(transactionBytes: ByteArray, signerAddress: String): ByteArray? {
        val secretKey = getAlgo25SecretKey(signerAddress) ?: return null
        return try {
            val signedTransaction = runCatching { transactionBytes.signTx(secretKey) }.getOrNull()
                ?.takeIf { it.isNotEmpty() } ?: return null
            extractSignatureFromSignedTransaction(signedTransaction)
        } finally {
            secretKey.clearFromMemory()
        }
    }

    private suspend fun signHdKeyTransaction(
        transactionBytes: ByteArray,
        hdKeyAccount: LocalAccount.HdKey
    ): ByteArray? {
        val seed = getHdSeed(seedId = hdKeyAccount.seedId) ?: return null
        return try {
            signHdKeyTransaction.signTransactionReturnSignature(
                transactionBytes,
                seed,
                hdKeyAccount.account,
                hdKeyAccount.change,
                hdKeyAccount.keyIndex
            )
        } finally {
            seed.clearFromMemory()
        }
    }

    private fun extractSignatureFromSignedTransaction(signedTransactionBytes: ByteArray): ByteArray? {
        if (signedTransactionBytes.isEmpty()) return null
        return runCatching {
            val signedTransaction = Encoder.decodeFromMsgPack(signedTransactionBytes, SignedTransaction::class.java)
            signedTransaction.sig?.bytes?.takeIf { it.isNotEmpty() }
        }.getOrNull()
    }
}
