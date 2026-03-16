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

import android.util.Log
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
private const val JOINT_SIGN_REQUEST_TYPE_SYNC = "sync"

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

    suspend fun handleSyncJointAccountTransaction(
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
            JointSignResult.Error
        } else {
            proposeSyncWithLocalSignature(jointAccount, proposerAddress, rawTransactionLists, transactionDataList)
        }
    }

    suspend fun handleSyncJointAccountTransactionWithRawBytes(
        jointAccountAddress: String,
        rawTransactionBytesList: List<ByteArray>
    ): JointSignResult {
        val outcome = runSyncWithRawBytes(jointAccountAddress, listOf(rawTransactionBytesList))
        return outcome ?: JointSignResult.Error
    }

    suspend fun handleSyncJointAccountTransactionWithRawByteGroups(
        jointAccountAddress: String,
        rawTransactionBytesGroups: List<List<ByteArray>>
    ): JointSignResult {
        Log.d(
            TAG,
            "handleSyncWithRawByteGroups: addr=${jointAccountAddress.take(ADDR_LOG_LEN)}, " +
                "groups=${rawTransactionBytesGroups.size}, " +
                "sizes=${rawTransactionBytesGroups.map { it.size }}"
        )
        val outcome = runSyncWithRawBytes(jointAccountAddress, rawTransactionBytesGroups)
        Log.d(TAG, "handleSyncWithRawByteGroups: outcome=$outcome")
        return outcome ?: JointSignResult.Error
    }

    private suspend fun runSyncWithRawBytes(
        jointAccountAddress: String,
        rawTransactionBytesGroups: List<List<ByteArray>>
    ): JointSignResult? {
        val prepared = prepareSyncRequestWithRawByteGroups(
            jointAccountAddress,
            rawTransactionBytesGroups
        )
        if (prepared == null) {
            Log.e(TAG, "runSyncWithRawBytes: prepareSyncRequest returned null")
            return null
        }
        Log.d(
            TAG,
            "runSyncWithRawBytes: rawTxGroups=${prepared.data.rawTransactionLists.map { it.size }}, " +
                "sigGroups=${prepared.data.transactionSignatureLists.map { g -> g.map { it != null } }}"
        )
        val input = mapToCreateSignRequestInputSync(prepared.data)
        Log.d(
            TAG,
            "runSyncWithRawBytes: proposing type=${input.type}, " +
                "rawLists=${input.rawTransactionLists.map { it.size }}"
        )
        val result = proposeJointSignRequest(input)
        if (result !is PeraResult.Success) {
            Log.e(TAG, "runSyncWithRawBytes: propose FAILED result=$result")
            return null
        }
        val signRequestId = (result.data as? JointSignRequest)?.id
            ?.takeIf { it.isNotBlank() }
        if (signRequestId == null) {
            Log.e(TAG, "runSyncWithRawBytes: signRequestId is null/blank, data=${result.data}")
            return null
        }
        Log.d(TAG, "runSyncWithRawBytes: proposed signRequestId=$signRequestId")
        autoSignWithLocalAccounts(
            signRequestId = signRequestId,
            jointAccount = prepared.data.jointAccount,
            rawTransactionGroups = prepared.data.rawTransactionLists
        )
        return JointSignResult.SyncPending(
            signRequestId = signRequestId,
            proposerAddress = prepared.data.proposerAddress
        )
    }

    private data class PreparedSyncRequest(val data: PreparedJointAccountData)

    private suspend fun prepareSyncRequestWithRawByteGroups(
        jointAccountAddress: String,
        rawTransactionBytesGroups: List<List<ByteArray>>
    ): PreparedSyncRequest? {
        val jointAccount = getLocalAccount(jointAccountAddress) as? LocalAccount.Joint
        val proposerAddress = jointAccount?.let { getJointAccountProposerAddress(it) }
        Log.d(
            TAG,
            "prepareSyncReq: jointAccount=${jointAccount != null}, " +
                "proposer=${proposerAddress?.take(ADDR_LOG_LEN)}, " +
                "groupCount=${rawTransactionBytesGroups.size}"
        )
        if (rawTransactionBytesGroups.isEmpty() || jointAccount == null || proposerAddress == null) {
            Log.e(TAG, "prepareSyncReq: FAIL early check")
            return null
        }
        val rawTransactionLists = mutableListOf<List<String>>()
        val transactionSignatureLists = mutableListOf<List<String?>>()
        for ((idx, group) in rawTransactionBytesGroups.withIndex()) {
            if (group.isEmpty()) {
                Log.e(TAG, "prepareSyncReq: FAIL group[$idx] is empty")
                return null
            }
            val encodedGroup = group.mapNotNull { it.encodeBase64() }
            if (encodedGroup.size != group.size) {
                Log.e(TAG, "prepareSyncReq: FAIL group[$idx] encode mismatch")
                return null
            }
            rawTransactionLists.add(encodedGroup)
            val groupSignatures = buildSignatureListForGroup(group, proposerAddress)
            Log.d(
                TAG,
                "prepareSyncReq: group[$idx] txns=${group.size}, " +
                    "sigs=${groupSignatures.map { it != null }}"
            )
            transactionSignatureLists.add(groupSignatures)
        }
        val preparedData = PreparedJointAccountData(
            jointAccount = jointAccount,
            proposerAddress = proposerAddress,
            rawTransactionLists = rawTransactionLists,
            transactionSignatureLists = transactionSignatureLists
        )
        return PreparedSyncRequest(preparedData)
    }

    private suspend fun buildSignatureListForGroup(
        rawTransactionBytesList: List<ByteArray>,
        signerAddress: String
    ): List<String?> {
        return rawTransactionBytesList.map { txBytes ->
            getTransactionSignatureBytes(txBytes, signerAddress)?.encodeBase64()
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

    private suspend fun proposeSyncWithLocalSignature(
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

        val inputData = mapToCreateSignRequestInputSync(preparedData)
        val result = proposeJointSignRequest(inputData)

        if (result !is PeraResult.Success) {
            return JointSignResult.Error
        }

        val signRequestId = (result.data as? JointSignRequest)?.id
            ?.takeIf { it.isNotBlank() } ?: return JointSignResult.Error

        autoSignWithLocalAccounts(
            signRequestId = signRequestId,
            jointAccount = jointAccount,
            rawTransactionGroups = rawTransactionLists
        )
        return JointSignResult.SyncPending(signRequestId = signRequestId, proposerAddress = proposerAddress)
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
            rawTransactionGroups = preparedData.rawTransactionLists
        )
        refreshInboxCache()
        return JointSignResult.Success(signRequestId)
    }

    private suspend fun autoSignWithLocalAccounts(
        signRequestId: String,
        jointAccount: LocalAccount.Joint,
        rawTransactionGroups: List<List<String>>
    ) {
        val eligibleSigners = getSignableAccountsByAddresses(jointAccount.participantAddresses)
        Log.d(
            TAG,
            "autoSign: signers=${eligibleSigners.map { it.algoAddress.take(ADDR_LOG_LEN) }}, " +
                "groups=${rawTransactionGroups.map { it.size }}"
        )
        if (eligibleSigners.isEmpty()) return

        val result = signAndSubmitJointAccountSignature(
            signRequestId = signRequestId,
            participantAddresses = eligibleSigners.map { it.algoAddress },
            rawTransactionGroups = rawTransactionGroups
        )
        Log.d(
            TAG,
            "autoSign: signedAddresses=${result.signedAddresses.map { it.take(8) }}, " +
                "apiResult=${result.apiResult}"
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
        val signatures = transactionDataList.map { transactionData ->
            val transactionBytes = transactionData.transactionByteArray ?: return null
            getTransactionSignatureBytes(transactionBytes, signerAddress)?.encodeBase64()
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

    private fun mapToCreateSignRequestInputSync(data: PreparedJointAccountData): CreateSignRequestInput {
        return with(data) {
            val defaultResponse = ProposeJointSignRequestResponseInput(
                address = data.proposerAddress,
                responseType = ProposeJointSignRequestResult.SIGNED,
                signatures = data.transactionSignatureLists
            )
            CreateSignRequestInput(
                jointAccountAddress = jointAccount.algoAddress,
                proposerAddress = proposerAddress,
                type = JOINT_SIGN_REQUEST_TYPE_SYNC,
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
        data class SyncPending(val signRequestId: String, val proposerAddress: String) : JointSignResult
        data class NeedsLedgerSign(val pendingProposal: PendingJointAccountProposal) : JointSignResult
        data object Error : JointSignResult
    }

    companion object {
        private const val TAG = "JOINT_SIGN_DEBUG"
        private const val ADDR_LOG_LEN = 8
    }
}
