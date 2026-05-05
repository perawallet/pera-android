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
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountProposerAddress
import com.algorand.wallet.jointaccount.transaction.domain.model.CreateSignRequestInput
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestResponseInput
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestResult
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestType
import com.algorand.wallet.jointaccount.transaction.domain.usecase.ProposeJointSignRequest
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.algosdk.transaction.usecase.ParseTransactionMessagePack
import javax.inject.Inject

class JointAccountTransactionSignHelper @Inject constructor(
    private val getLocalAccount: GetLocalAccount,
    private val getSignableAccountsByAddresses: GetSignableAccountsByAddresses,
    private val localAccountSigningHelper: LocalAccountSigningHelper,
    private val proposeJointSignRequest: ProposeJointSignRequest,
    private val signAndSubmitJointAccountSignature: SignAndSubmitJointAccountSignature,
    private val getJointAccountProposerAddress: GetJointAccountProposerAddress,
    private val refreshInboxCache: RefreshInboxCache,
    private val parseTransactionMessagePack: ParseTransactionMessagePack,
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress
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

        val signerAddress = resolveSignerAddress(proposerAddress)
        val signerAccount = getLocalAccount(signerAddress) ?: return JointSignResult.Error

        return if (signerAccount is LocalAccount.LedgerBle) {
            JointSignResult.NeedsLedgerSign(
                PendingJointAccountProposal(
                    jointAccount = jointAccount,
                    proposerAddress = proposerAddress,
                    rawTransactionLists = rawTransactionLists,
                    ledgerAccount = signerAccount
                )
            )
        } else {
            proposeWithLocalSignature(jointAccount, proposerAddress, rawTransactionLists, transactionDataList)
        }
    }

    suspend fun handleSyncJointAccountTransactionWithRawByteGroups(
        jointAccountAddress: String,
        rawTransactionBytesGroups: List<List<ByteArray>>
    ): JointSignResult {
        return runSyncWithRawBytes(jointAccountAddress, rawTransactionBytesGroups) ?: JointSignResult.Error
    }

    @Suppress("ReturnCount")
    private suspend fun runSyncWithRawBytes(
        jointAccountAddress: String,
        rawTransactionBytesGroups: List<List<ByteArray>>
    ): JointSignResult? {
        val jointAccount = getLocalAccount(jointAccountAddress) as? LocalAccount.Joint ?: return null
        val proposerAddress = getJointAccountProposerAddress(jointAccount) ?: return null
        if (rawTransactionBytesGroups.isEmpty()) return null

        val signerAddress = resolveSignerAddress(proposerAddress)
        val signerAccount = getLocalAccount(signerAddress) ?: return null
        if (signerAccount is LocalAccount.LedgerBle) {
            val rawTransactionLists = encodeRawTransactionGroups(rawTransactionBytesGroups) ?: return null
            return JointSignResult.SyncNeedsLedgerSign(
                PendingJointAccountProposal(jointAccount, proposerAddress, rawTransactionLists, signerAccount)
            )
        }

        val prepared = prepareSyncRequest(jointAccountAddress, rawTransactionBytesGroups) ?: return null
        val input = mapToCreateSignRequestInput(prepared, SignRequestType.SYNC)
        val result = proposeJointSignRequest(input)
        if (result !is PeraResult.Success) return null
        val signRequestId = result.data.id?.takeIf { it.isNotBlank() } ?: return null
        autoSignWithLocalAccounts(
            signRequestId = signRequestId,
            jointAccount = prepared.jointAccount,
            rawTransactionGroups = prepared.rawTransactionLists
        )
        return JointSignResult.SyncPending(
            signRequestId = signRequestId,
            proposerAddress = prepared.proposerAddress
        )
    }

    private fun encodeRawTransactionGroups(
        rawTransactionBytesGroups: List<List<ByteArray>>
    ): List<List<String>>? {
        return rawTransactionBytesGroups.map { group ->
            if (group.isEmpty()) return null
            val encoded = group.mapNotNull { it.encodeBase64() }
            if (encoded.size != group.size) return null
            encoded
        }
    }

    private suspend fun prepareSyncRequest(
        jointAccountAddress: String,
        rawTransactionBytesGroups: List<List<ByteArray>>
    ): PreparedJointAccountData? {
        val jointAccount = getLocalAccount(jointAccountAddress) as? LocalAccount.Joint ?: return null
        val proposerAddress = getJointAccountProposerAddress(jointAccount) ?: return null
        if (rawTransactionBytesGroups.isEmpty()) return null

        val encodedGroups = encodeAndSignGroups(
            rawTransactionBytesGroups, proposerAddress, jointAccountAddress
        ) ?: return null
        return PreparedJointAccountData(
            jointAccount = jointAccount,
            proposerAddress = proposerAddress,
            rawTransactionLists = encodedGroups.first,
            transactionSignatureLists = encodedGroups.second
        )
    }

    private suspend fun encodeAndSignGroups(
        rawTransactionBytesGroups: List<List<ByteArray>>,
        proposerAddress: String,
        jointAccountAddress: String
    ): Pair<List<List<String>>, List<List<String?>>>? {
        val rawTransactionLists = mutableListOf<List<String>>()
        val transactionSignatureLists = mutableListOf<List<String?>>()
        for (group in rawTransactionBytesGroups) {
            if (group.isEmpty()) return null
            val encodedGroup = group.mapNotNull { it.encodeBase64() }
            if (encodedGroup.size != group.size) return null
            rawTransactionLists.add(encodedGroup)
            transactionSignatureLists.add(
                buildSignatureListForGroup(group, proposerAddress, jointAccountAddress)
            )
        }
        return Pair(rawTransactionLists, transactionSignatureLists)
    }

    private suspend fun buildSignatureListForGroup(
        rawTransactionBytesList: List<ByteArray>,
        signerAddress: String,
        jointAccountAddress: String
    ): List<String?> {
        return rawTransactionBytesList.map { txBytes ->
            val senderAddress = parseTransactionMessagePack(txBytes)?.senderAddress?.decodedAddress
            if (!shouldSignForJointAccount(senderAddress, jointAccountAddress)) {
                null
            } else {
                getTransactionSignatureBytes(txBytes, signerAddress)?.encodeBase64()
            }
        }
    }

    suspend fun completeJointAccountProposal(
        pendingProposal: PendingJointAccountProposal,
        signatureBase64List: List<String>
    ): JointSignResult {
        val preparedData = PreparedJointAccountData(
            jointAccount = pendingProposal.jointAccount,
            proposerAddress = pendingProposal.proposerAddress,
            rawTransactionLists = pendingProposal.rawTransactionLists,
            transactionSignatureLists = listOf(signatureBase64List)
        )

        val inputData = mapToCreateSignRequestInput(preparedData, SignRequestType.ASYNC)
        val result = proposeJointSignRequest(inputData)
        if (result !is PeraResult.Success) return JointSignResult.Error

        val signRequestId = result.data.id
            ?.takeIf { it.isNotBlank() } ?: return JointSignResult.Error

        autoSignWithLocalAccounts(
            signRequestId = signRequestId,
            jointAccount = pendingProposal.jointAccount,
            rawTransactionGroups = pendingProposal.rawTransactionLists
        )
        refreshInboxCache()
        return JointSignResult.Success(signRequestId)
    }

    suspend fun completeSyncJointAccountProposal(
        pendingProposal: PendingJointAccountProposal,
        signatureBase64List: List<String>
    ): JointSignResult {
        val preparedData = PreparedJointAccountData(
            jointAccount = pendingProposal.jointAccount,
            proposerAddress = pendingProposal.proposerAddress,
            rawTransactionLists = pendingProposal.rawTransactionLists,
            transactionSignatureLists = listOf(signatureBase64List)
        )

        val inputData = mapToCreateSignRequestInput(preparedData, SignRequestType.SYNC)
        val result = proposeJointSignRequest(inputData)
        if (result !is PeraResult.Success) return JointSignResult.Error

        val signRequestId = result.data.id
            ?.takeIf { it.isNotBlank() } ?: return JointSignResult.Error

        autoSignWithLocalAccounts(
            signRequestId = signRequestId,
            jointAccount = pendingProposal.jointAccount,
            rawTransactionGroups = pendingProposal.rawTransactionLists
        )
        return JointSignResult.SyncPending(
            signRequestId = signRequestId,
            proposerAddress = pendingProposal.proposerAddress
        )
    }

    private suspend fun proposeWithLocalSignature(
        jointAccount: LocalAccount.Joint,
        proposerAddress: String,
        rawTransactionLists: List<List<String>>,
        transactionDataList: List<TransactionSignData>
    ): JointSignResult {
        val transactionSignatureLists = prepareTransactionSignatureLists(
            transactionDataList, proposerAddress, jointAccount.algoAddress
        ) ?: return JointSignResult.Error

        val preparedData = PreparedJointAccountData(
            jointAccount = jointAccount,
            proposerAddress = proposerAddress,
            rawTransactionLists = rawTransactionLists,
            transactionSignatureLists = transactionSignatureLists
        )

        val inputData = mapToCreateSignRequestInput(preparedData, SignRequestType.ASYNC)
        val result = proposeJointSignRequest(inputData)
        if (result !is PeraResult.Success) return JointSignResult.Error

        val signRequestId = result.data.id
            ?.takeIf { it.isNotBlank() } ?: return JointSignResult.Error

        autoSignWithLocalAccounts(
            signRequestId = signRequestId,
            jointAccount = jointAccount,
            rawTransactionGroups = rawTransactionLists
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
        if (eligibleSigners.isEmpty()) return

        signAndSubmitJointAccountSignature(
            signRequestId = signRequestId,
            participantAddresses = eligibleSigners.map { it.algoAddress },
            rawTransactionGroups = rawTransactionGroups,
            jointAccountAddress = jointAccount.algoAddress
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
        signerAddress: String,
        jointAccountAddress: String
    ): List<List<String?>>? {
        val signatures = transactionDataList.map { transactionData ->
            val transactionBytes = transactionData.transactionByteArray ?: return null
            val senderAddress = parseTransactionMessagePack(transactionBytes)?.senderAddress?.decodedAddress
            if (!shouldSignForJointAccount(senderAddress, jointAccountAddress)) {
                null
            } else {
                getTransactionSignatureBytes(transactionBytes, signerAddress)?.encodeBase64()
            }
        }
        return signatures.takeIf { it.isNotEmpty() }?.let { listOf(it) }
    }

    private suspend fun getTransactionSignatureBytes(
        transactionBytes: ByteArray,
        signerAddress: String
    ): ByteArray? {
        val actualSigner = resolveSignerAddress(signerAddress)
        val signerAccount = getLocalAccount(actualSigner) ?: return null
        return when (signerAccount) {
            is LocalAccount.Algo25 -> {
                localAccountSigningHelper.signWithAlgo25AccountReturnSignature(transactionBytes, actualSigner)
            }

            is LocalAccount.HdKey -> {
                localAccountSigningHelper.signWithHdKeyAccountReturnSignature(transactionBytes, signerAccount)
            }

            else -> null
        }
    }

    private suspend fun resolveSignerAddress(address: String): String {
        return getAccountRekeyAdminAddress(address) ?: address
    }

    /**
     * Returns true if the transaction sender should be signed by the joint account's proposer.
     * This is the case when the sender IS the joint account, or when the sender is rekeyed
     * TO the joint account (meaning the joint account has signing authority).
     */
    private suspend fun shouldSignForJointAccount(
        senderAddress: String?,
        jointAccountAddress: String
    ): Boolean {
        if (senderAddress == null) return false
        if (senderAddress == jointAccountAddress) return true
        return getAccountRekeyAdminAddress(senderAddress) == jointAccountAddress
    }

    private fun mapToCreateSignRequestInput(
        data: PreparedJointAccountData,
        type: SignRequestType
    ): CreateSignRequestInput {
        return with(data) {
            val defaultResponse = ProposeJointSignRequestResponseInput(
                address = proposerAddress,
                responseType = ProposeJointSignRequestResult.SIGNED,
                signatures = transactionSignatureLists
            )
            CreateSignRequestInput(
                jointAccountAddress = jointAccount.algoAddress,
                proposerAddress = proposerAddress,
                type = type,
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
        data class SyncNeedsLedgerSign(val pendingProposal: PendingJointAccountProposal) : JointSignResult
        data object Error : JointSignResult
    }
}
