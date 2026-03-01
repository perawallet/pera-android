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

package com.algorand.android.modules.addaccount.joint.transaction.viewmodel

import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignatureStatus
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionViewState
import javax.inject.Inject

internal class DefaultJointAccountTransactionProcessor @Inject constructor() :
    JointAccountTransactionProcessor {

    override fun validateConfirmTransaction(
        preview: JointAccountTransactionViewState,
        signRequestId: String?
    ): JointAccountTransactionProcessor.ConfirmTransactionData? {
        val requestId = signRequestId ?: return null
        if (preview.rawTransactions.isEmpty()) return null

        val hasUnsignedLocalAccounts = preview.unsignedLocalParticipantAddresses.isNotEmpty()
        val hasUnsignedLedgerAccounts = preview.unsignedLedgerParticipantAddresses.isNotEmpty()
        if (!hasUnsignedLocalAccounts && !hasUnsignedLedgerAccounts) return null

        return JointAccountTransactionProcessor.ConfirmTransactionData(
            requestId = requestId,
            preview = preview,
            hasUnsignedLocalAccounts = hasUnsignedLocalAccounts,
            hasUnsignedLedgerAccounts = hasUnsignedLedgerAccounts
        )
    }

    override fun createUpdatedPreviewAfterSigning(
        preview: JointAccountTransactionViewState,
        signedAddresses: List<String>
    ): JointAccountTransactionViewState {
        val confirmedSignedAddresses = signedAddresses.toSet()
        val updatedSigners = markSignersAsSigned(preview.signerAccounts, confirmedSignedAddresses)
        val newSignedCount = updatedSigners.count { it.signatureStatus == JointAccountSignatureStatus.Signed }
        val remainingUnsignedLocalAddresses = preview.unsignedLocalParticipantAddresses
            .filterNot { it in confirmedSignedAddresses }
        val isCompleted = isTransactionCompleted(newSignedCount, preview.requiredSignatureCount)
        val transactionState = if (isCompleted) {
            JointAccountTransactionState.Completed
        } else {
            JointAccountTransactionState.PendingSignatures
        }
        val finalSigners = if (isCompleted) hideProgressOnPendingSigners(updatedSigners) else updatedSigners

        return preview.copy(
            transactionState = transactionState,
            signedCount = newSignedCount,
            signerAccounts = finalSigners,
            hasCurrentUserAlreadySigned = confirmedSignedAddresses.isNotEmpty() || preview.hasCurrentUserAlreadySigned,
            unsignedLocalParticipantAddresses = remainingUnsignedLocalAddresses
        )
    }

    override fun createLedgerSignData(
        signRequestId: String,
        rawTransactions: List<String>,
        preview: JointAccountTransactionViewState
    ): JointAccountTransactionProcessor.LedgerSignData? {
        val ledgerSigner = findFirstAvailableLedgerSigner(preview.signerAccounts) ?: return null
        val bluetoothAddress = ledgerSigner.ledgerBluetoothAddress ?: return null
        val accountIndex = ledgerSigner.ledgerAccountIndex ?: return null

        return JointAccountTransactionProcessor.LedgerSignData(
            signRequestId = signRequestId,
            accountAddress = ledgerSigner.accountAddress,
            rawTransactions = rawTransactions,
            ledgerBluetoothAddress = bluetoothAddress,
            ledgerAccountIndex = accountIndex,
            accountAuthAddress = ledgerSigner.accountAuthAddress,
            isRekeyedToAnotherAccount = ledgerSigner.accountAuthAddress != null
        )
    }

    override fun processLoadedPreview(preview: JointAccountTransactionViewState): JointAccountTransactionViewState {
        val isCompletedBySignatureCount = isTransactionCompleted(preview.signedCount, preview.requiredSignatureCount)
        val shouldComplete = isCompletedBySignatureCount &&
                preview.transactionState == JointAccountTransactionState.PendingSignatures

        val effectivePreview = if (shouldComplete) {
            preview.copy(transactionState = JointAccountTransactionState.Completed)
        } else {
            preview
        }

        val isFinalized = effectivePreview.transactionState is JointAccountTransactionState.Failed ||
                effectivePreview.transactionState == JointAccountTransactionState.Completed ||
                effectivePreview.transactionState == JointAccountTransactionState.Canceled ||
                effectivePreview.transactionState == JointAccountTransactionState.Expired ||
                effectivePreview.transactionState == JointAccountTransactionState.Declined ||
                effectivePreview.isExpired

        if (!isFinalized) return effectivePreview

        return effectivePreview.copy(
            signerAccounts = hideProgressOnPendingSigners(effectivePreview.signerAccounts)
        )
    }

    override fun findDeclineParticipantAddresses(preview: JointAccountTransactionViewState): List<String> {
        return preview.unsignedLocalParticipantAddresses + preview.unsignedLedgerParticipantAddresses
    }

    override fun determinePostSigningAction(
        data: JointAccountTransactionProcessor.ConfirmTransactionData,
        updatedPreview: JointAccountTransactionViewState,
        signRequestId: String?
    ): JointAccountTransactionProcessor.PostSigningAction {
        return JointAccountTransactionProcessor.PostSigningAction.ShowPendingSignatures
    }

    override fun determineLedgerSuccessAction(
        preview: JointAccountTransactionViewState,
        signRequestId: String?
    ): JointAccountTransactionProcessor.PostSigningAction {
        return JointAccountTransactionProcessor.PostSigningAction.ShowPendingSignatures
    }

    private fun findFirstAvailableLedgerSigner(
        signerAccounts: List<JointAccountSignerItem>
    ): JointAccountSignerItem? {
        return signerAccounts.firstOrNull { it.canSignWithLedger }
    }

    private fun isTransactionCompleted(signedCount: Int, requiredSignatureCount: Int): Boolean {
        return signedCount >= requiredSignatureCount
    }

    private fun markSignersAsSigned(
        signerAccounts: List<JointAccountSignerItem>,
        signedAddresses: Set<String>
    ): List<JointAccountSignerItem> {
        return signerAccounts.map { signer ->
            if (signer.accountAddress in signedAddresses) {
                signer.copy(signatureStatus = JointAccountSignatureStatus.Signed)
            } else {
                signer
            }
        }
    }

    private fun hideProgressOnPendingSigners(
        signerAccounts: List<JointAccountSignerItem>
    ): List<JointAccountSignerItem> {
        return signerAccounts.map { signer ->
            if (signer.signatureStatus == JointAccountSignatureStatus.Pending) {
                signer.copy(showProgress = false)
            } else {
                signer
            }
        }
    }
}
