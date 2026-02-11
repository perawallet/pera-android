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

import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionViewState

interface JointAccountTransactionProcessor {

    fun validateConfirmTransaction(
        preview: JointAccountTransactionViewState,
        signRequestId: String?
    ): ConfirmTransactionData?

    fun createUpdatedPreviewAfterSigning(
        preview: JointAccountTransactionViewState,
        signedAddresses: List<String>
    ): JointAccountTransactionViewState

    fun createLedgerSignData(
        signRequestId: String,
        rawTransactions: List<String>,
        preview: JointAccountTransactionViewState
    ): LedgerSignData?

    fun processLoadedPreview(preview: JointAccountTransactionViewState): JointAccountTransactionViewState

    fun findDeclineParticipantAddress(preview: JointAccountTransactionViewState): String?

    fun determinePostSigningAction(
        data: ConfirmTransactionData,
        updatedPreview: JointAccountTransactionViewState,
        signRequestId: String?
    ): PostSigningAction

    fun determineLedgerSuccessAction(
        preview: JointAccountTransactionViewState,
        signRequestId: String?
    ): PostSigningAction

    data class ConfirmTransactionData(
        val requestId: String,
        val preview: JointAccountTransactionViewState,
        val hasUnsignedLocalAccounts: Boolean,
        val hasUnsignedLedgerAccounts: Boolean
    )

    data class LedgerSignData(
        val signRequestId: String,
        val accountAddress: String,
        val rawTransactions: List<String>,
        val ledgerBluetoothAddress: String,
        val ledgerAccountIndex: Int
    )

    sealed interface PostSigningAction {
        data class TriggerLedgerSigning(val data: LedgerSignData) : PostSigningAction
        data object ShowPendingSignatures : PostSigningAction
    }
}
