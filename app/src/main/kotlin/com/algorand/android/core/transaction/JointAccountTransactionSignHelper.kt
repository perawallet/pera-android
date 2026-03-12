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

import com.algorand.android.models.TransactionSignData
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.SignAndSubmitJointAccountSignature
import com.algorand.android.utils.extensions.encodeBase64
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetSignableAccountsByAddresses
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountProposerAddress
import com.algorand.wallet.jointaccount.transaction.domain.model.CreateSignRequestInput
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequest
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestResponseInput
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestResult
import com.algorand.wallet.jointaccount.transaction.domain.usecase.ProposeJointSignRequest
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import javax.inject.Inject

private const val JOINT_SIGN_REQUEST_TYPE_ASYNC = "async"

class JointAccountTransactionSignHelper @Inject constructor(
    private val getLocalAccount: GetLocalAccount,
    private val getSignableAccountsByAddresses: GetSignableAccountsByAddresses,
    private val localAccountSigningHelper: LocalAccountSigningHelper,
    private val proposeJointSignRequest: ProposeJointSignRequest,
    private val signAndSubmitJointAccountSignature: SignAndSubmitJointAccountSignature,
    private val getJointAccountProposerAddress: GetJointAccountProposerAddress,
    private val refreshInboxCache: RefreshInboxCache
) {

    suspend fun handleJointAccountTransaction(
        jointAccountAddress: String,
        transactionDataList: List<TransactionSignData>
    ): JointSignResult {
        val jointAccount = getLocalAccount(jointAccountAddress) as? LocalAccount.Joint
            ?: return JointSignResult.Error
        val proposerAddress = getJointAccountProposerAddress(jointAccount)
            ?: return JointSignResult.Error
        val rawTransactionLists = prepareRawTransactionLists(transactionDataList)
            ?: return JointSignResult.Error

        val proposerAccount = getLocalAccount(proposerAddress) ?: return JointSignResult.Error

        return if (proposerAccount is LocalAccount.LedgerBle) {
            JointSignResult.NeedsLedgerSign(
                PendingJointAccountProposal(
                    jointAccount = jointAccount,
                    proposerAddress = proposerAddress,
                    rawTransactionLists = rawTransactionLists,
                    ledgerAccount = proposerAccount
                )
            )
        } else {
            proposeWithLocalSignature(jointAccount, proposerAddress, rawTransactionLists, transactionDataList)
        }
    }

    suspend fun completeJointAccountProposal(
        pendingProposal: PendingJointAccountProposal,
        signatureBase64List: List<String>
    ): JointSignResult {
        val transactionSignatureLists = listOf(signatureBase64List)
        val preparedData = PreparedJointAccountData(
            jointAccount = pendingProposal.jointAccount,
            proposerAddress = pendingProposal.proposerAddress,
            rawTransactionLists = pendingProposal.rawTransactionLists,
            transactionSignatureLists = transactionSignatureLists
        )

        val inputData = mapToCreateSignRequestInput(preparedData)
        val result = proposeJointSignRequest(inputData)

        return processProposalResult(result, preparedData)
    }

    private suspend fun proposeWithLocalSignature(
        jointAccount: LocalAccount.Joint,
        proposerAddress: String,
        rawTransactionLists: List<List<String>>,
        transactionDataList: List<TransactionSignData>
    ): JointSignResult {
        val transactionSignatureLists = prepareTransactionSignatureLists(transactionDataList, proposerAddress)
            ?: return JointSignResult.Error

        val preparedData = PreparedJointAccountData(
            jointAccount = jointAccount,
            proposerAddress = proposerAddress,
            rawTransactionLists = rawTransactionLists,
            transactionSignatureLists = transactionSignatureLists
        )

        val inputData = mapToCreateSignRequestInput(preparedData)
        val result = proposeJointSignRequest(inputData)

        return processProposalResult(result, preparedData)
    }

    private suspend fun processProposalResult(
        result: PeraResult<Any>,
        preparedData: PreparedJointAccountData
    ): JointSignResult {
        if (result !is PeraResult.Success) {
            return JointSignResult.Error
        }

        val signRequestId = (result.data as? JointSignRequest)?.id
            ?.takeIf { it.isNotBlank() } ?: return JointSignResult.Error

        autoSignWithLocalAccounts(
            signRequestId = signRequestId,
            jointAccount = preparedData.jointAccount,
            rawTransactions = preparedData.rawTransactionLists.flatten()
        )
        refreshInboxCache()
        return JointSignResult.Success(signRequestId)
    }

    private suspend fun autoSignWithLocalAccounts(
        signRequestId: String,
        jointAccount: LocalAccount.Joint,
        rawTransactions: List<String>
    ) {
        val eligibleSigners = getSignableAccountsByAddresses(jointAccount.participantAddresses)
        if (eligibleSigners.isEmpty()) return

        signAndSubmitJointAccountSignature(
            signRequestId = signRequestId,
            participantAddresses = eligibleSigners.map { it.algoAddress },
            rawTransactions = rawTransactions
        )
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
            is LocalAccount.Algo25 -> {
                localAccountSigningHelper.signWithAlgo25AccountReturnSignature(transactionBytes, signerAddress)
            }

            is LocalAccount.HdKey -> {
                localAccountSigningHelper.signWithHdKeyAccountReturnSignature(transactionBytes, signerAccount)
            }

            else -> null
        }
    }

    private fun mapToCreateSignRequestInput(data: PreparedJointAccountData): CreateSignRequestInput {
        return with(data) {
            val defaultResponse = ProposeJointSignRequestResponseInput(
                address = data.proposerAddress,
                responseType = ProposeJointSignRequestResult.SIGNED,
                signatures = data.transactionSignatureLists
            )
            CreateSignRequestInput(
                jointAccountAddress = jointAccount.algoAddress,
                proposerAddress = proposerAddress,
                type = JOINT_SIGN_REQUEST_TYPE_ASYNC,
                rawTransactionLists = rawTransactionLists,
                responses = listOf(defaultResponse)
            )
        }
    }

    private data class PreparedJointAccountData(
        val jointAccount: LocalAccount.Joint,
        val proposerAddress: String,
        val rawTransactionLists: List<List<String>>,
        val transactionSignatureLists: List<List<String?>>
    )

    data class PendingJointAccountProposal(
        val jointAccount: LocalAccount.Joint,
        val proposerAddress: String,
        val rawTransactionLists: List<List<String>>,
        val ledgerAccount: LocalAccount.LedgerBle
    )

    sealed interface JointSignResult {
        data class Success(val signRequestId: String) : JointSignResult
        data class NeedsLedgerSign(val pendingProposal: PendingJointAccountProposal) : JointSignResult
        data object Error : JointSignResult
    }
}
