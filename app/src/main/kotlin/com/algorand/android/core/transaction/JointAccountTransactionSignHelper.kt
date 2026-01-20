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

package com.algorand.android.core.transaction

import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.util.Encoder
import com.algorand.android.models.TransactionSignData
import com.algorand.android.utils.extensions.encodeBase64
import com.algorand.android.utils.signTx
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountProposerAddress
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.usecase.ProposeJointSignRequest
import javax.inject.Inject

private const val JOINT_SIGN_REQUEST_TYPE_ASYNC = "async"

class JointAccountTransactionSignHelper @Inject constructor(
    private val getLocalAccount: GetLocalAccount,
    private val getLocalAccounts: GetLocalAccounts,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdSeed: GetHdSeed,
    private val signHdKeyTransaction: SignHdKeyTransaction,
    private val proposeJointSignRequest: ProposeJointSignRequest,
    private val getJointAccountProposerAddress: GetJointAccountProposerAddress,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress
) {

    data class JointSignResult(
        val isSuccess: Boolean,
        val signRequestId: String? = null
    )

    suspend fun handleJointAccountTransaction(
        jointAccountAddress: String,
        transactionDataList: List<TransactionSignData>
    ): JointSignResult {
        val preparedData = prepareJointAccountData(jointAccountAddress, transactionDataList)
            ?: return JointSignResult(isSuccess = false)

        val result = proposeJointSignRequest(
            jointAccountAddress = preparedData.jointAccount.algoAddress,
            proposerAddress = preparedData.proposerAddress,
            type = JOINT_SIGN_REQUEST_TYPE_ASYNC,
            rawTransactionLists = preparedData.rawTransactionLists,
            transactionSignatureLists = preparedData.transactionSignatureLists
        )

        return processProposalResult(result, preparedData)
    }

    private suspend fun prepareJointAccountData(
        jointAccountAddress: String,
        transactionDataList: List<TransactionSignData>
    ): PreparedJointAccountData? {
        val jointAccount = getLocalAccount(jointAccountAddress) as? LocalAccount.Joint ?: return null
        val proposerAddress = getJointAccountProposerAddress(jointAccount) ?: return null
        val signerAddress = getAccountRekeyAdminAddress(proposerAddress) ?: proposerAddress
        val rawTransactionLists = prepareRawTransactionLists(transactionDataList) ?: return null
        val transactionSignatureLists = prepareTransactionSignatureLists(transactionDataList, signerAddress)
            ?: return null

        return PreparedJointAccountData(
            jointAccount = jointAccount,
            proposerAddress = proposerAddress,
            rawTransactionLists = rawTransactionLists,
            transactionSignatureLists = transactionSignatureLists
        )
    }

    private suspend fun processProposalResult(
        result: PeraResult<Any>,
        preparedData: PreparedJointAccountData
    ): JointSignResult {
        if (result !is PeraResult.Success) {
            return JointSignResult(isSuccess = false)
        }

        val signRequestId = (result.data as? JointSignRequestDTO)?.id
            ?.takeIf { it.isNotBlank() } ?: return JointSignResult(isSuccess = false)

        autoSignWithLocalAccounts(
            signRequestId = signRequestId,
            jointAccount = preparedData.jointAccount,
            rawTransactions = preparedData.rawTransactionLists.flatten()
        )
        return JointSignResult(isSuccess = true, signRequestId = signRequestId)
    }

    private data class PreparedJointAccountData(
        val jointAccount: LocalAccount.Joint,
        val proposerAddress: String,
        val rawTransactionLists: List<List<String>>,
        val transactionSignatureLists: List<List<String?>>
    )

    private suspend fun autoSignWithLocalAccounts(
        signRequestId: String,
        jointAccount: LocalAccount.Joint,
        rawTransactions: List<String>
    ) {
        val participantAddresses = jointAccount.participantAddresses
        val allLocalAccounts = getLocalAccounts()

        val eligibleSigners = participantAddresses.mapNotNull { address ->
            allLocalAccounts.find { it.algoAddress == address }
        }.filter { localAccount ->
            localAccount is LocalAccount.Algo25 || localAccount is LocalAccount.HdKey
        }

        TODO("Implement auto sign with local accounts")
    }

    companion object {
        private const val TAG = "JointAcctTxnSignHelper"
    }

    private fun prepareRawTransactionLists(transactionDataList: List<TransactionSignData>): List<List<String>>? {
        val rawTransactions = transactionDataList.mapNotNull { transactionData ->
            transactionData.transactionByteArray?.encodeBase64()
        }
        return rawTransactions.takeIf { it.size == transactionDataList.size }?.let { listOf(it) }
    }

    private suspend fun prepareTransactionSignatureLists(
        transactionDataList: List<TransactionSignData>,
        signerAddress: String
    ): List<List<String?>>? {
        val signatures = mutableListOf<String?>()
        for (transactionData in transactionDataList) {
            val transactionBytes = transactionData.transactionByteArray ?: return null
            val signatureBytes = getTransactionSignatureBytes(transactionBytes, signerAddress) ?: return null
            signatures.add(signatureBytes.encodeBase64())
        }
        return signatures.takeIf { it.isNotEmpty() }?.let { listOf(it) }
    }

    private suspend fun getTransactionSignatureBytes(
        transactionBytes: ByteArray,
        signerAddress: String
    ): ByteArray? {
        val signerAccount = getLocalAccount(signerAddress) ?: return null
        return when (signerAccount) {
            is LocalAccount.Algo25 -> signWithAlgo25Account(transactionBytes, signerAddress)
            is LocalAccount.HdKey -> signWithHdKeyAccount(transactionBytes, signerAccount)
            else -> null
        }
    }

    private suspend fun signWithAlgo25Account(transactionBytes: ByteArray, signerAddress: String): ByteArray? {
        val secretKey = getAlgo25SecretKey(signerAddress) ?: return null
        return try {
            val signedTransaction = runCatching { transactionBytes.signTx(secretKey) }.getOrNull()
                ?.takeIf { it.isNotEmpty() } ?: return null
            extractSignatureFromSignedTransaction(signedTransaction)
        } finally {
            secretKey.clearFromMemory()
        }
    }

    private suspend fun signWithHdKeyAccount(transactionBytes: ByteArray, hdKey: LocalAccount.HdKey): ByteArray? {
        val seed = getHdSeed(seedId = hdKey.seedId) ?: return null
        return try {
            signHdKeyTransaction.signTransactionSignatureOnly(
                transactionBytes,
                seed,
                hdKey.account,
                hdKey.change,
                hdKey.keyIndex
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
