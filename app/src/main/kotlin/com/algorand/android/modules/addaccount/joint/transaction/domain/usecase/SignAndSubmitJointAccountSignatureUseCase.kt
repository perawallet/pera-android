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
import app.perawallet.gomobilesdk.sdk.Sdk
import com.algorand.android.utils.decodeBase64
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.algosdk.transaction.usecase.ParseTransactionMessagePack
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
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import javax.inject.Inject

data class SignAndSubmitResult(
    val signedAddresses: List<String>,
    val apiResult: PeraResult<JointSignRequest>?
)

fun interface SignAndSubmitJointAccountSignature {
    suspend operator fun invoke(
        signRequestId: String,
        participantAddresses: List<String>,
        rawTransactionGroups: List<List<String>>,
        jointAccountAddress: String?
    ): SignAndSubmitResult
}

internal class SignAndSubmitJointAccountSignatureUseCase @Inject constructor(
    private val addJointAccountSignature: AddJointAccountSignature,
    private val getLocalAccount: GetLocalAccount,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdSeed: GetHdSeed,
    private val signHdKeyTransaction: SignHdKeyTransaction,
    private val parseTransactionMessagePack: ParseTransactionMessagePack,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress
) : SignAndSubmitJointAccountSignature {

    override suspend fun invoke(
        signRequestId: String,
        participantAddresses: List<String>,
        rawTransactionGroups: List<List<String>>,
        jointAccountAddress: String?
    ): SignAndSubmitResult {
        val signatureInputs = mutableListOf<AddSignatureInput>()
        val signedAddresses = mutableListOf<String>()
        val uniqueAddresses = participantAddresses.distinct()

        for (participantAddress in uniqueAddresses) {
            val groupedSignatures = signAllTransactionGroups(
                rawTransactionGroups, participantAddress, jointAccountAddress
            )
            if (groupedSignatures == null || !hasAnySignature(groupedSignatures)) {
                continue
            }
            signatureInputs.add(
                AddSignatureInput(
                    address = participantAddress,
                    response = SignRequestResponseType.SIGNED,
                    signatures = groupedSignatures,
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

    private suspend fun signAllTransactionGroups(
        rawTransactionGroups: List<List<String>>,
        participantAddress: String,
        jointAccountAddress: String?
    ): List<List<String?>>? {
        return rawTransactionGroups.map { group ->
            signAllTransactions(group, participantAddress, jointAccountAddress) ?: return null
        }
    }

    private suspend fun signAllTransactions(
        rawTransactions: List<String>,
        participantAddress: String,
        jointAccountAddress: String?
    ): List<String?>? {
        val signerAddress = getAccountRekeyAdminAddress(participantAddress) ?: participantAddress
        val localAccount = getLocalAccount(signerAddress) ?: return null
        return rawTransactions.map { rawTransaction ->
            val transactionBytes = rawTransaction.decodeBase64() ?: return null
            if (jointAccountAddress != null &&
                !isJointAccountTransaction(transactionBytes, jointAccountAddress)
            ) {
                return@map null
            }
            signTransaction(transactionBytes, localAccount)
                ?.let { Base64.encodeToString(it, Base64.NO_WRAP) }
        }
    }

    private suspend fun isJointAccountTransaction(
        transactionBytes: ByteArray,
        jointAccountAddress: String
    ): Boolean {
        val senderAddress = parseTransactionMessagePack(transactionBytes)
            ?.senderAddress?.decodedAddress ?: return false
        if (senderAddress == jointAccountAddress) return true
        return getAccountRekeyAdminAddress(senderAddress) == jointAccountAddress
    }

    private fun hasAnySignature(groupedSignatures: List<List<String?>>): Boolean {
        return groupedSignatures.any { group -> group.any { it != null } }
    }

    private suspend fun signTransaction(transactionBytes: ByteArray, localAccount: LocalAccount): ByteArray? {
        return when (localAccount) {
            is LocalAccount.Algo25 -> {
                val secretKey = getAlgo25SecretKey(localAccount.algoAddress) ?: return null
                try {
                    Sdk.signTransactionReturnSignature(secretKey, transactionBytes)
                } finally {
                    secretKey.clearFromMemory()
                }
            }
            is LocalAccount.HdKey -> {
                val seed = getHdSeed(seedId = localAccount.seedId) ?: return null
                try {
                    signHdKeyTransaction.signTransactionReturnSignature(
                        transactionBytes, seed, localAccount.account, localAccount.change, localAccount.keyIndex
                    )
                } finally {
                    seed.clearFromMemory()
                }
            }
            else -> null
        }
    }
}
