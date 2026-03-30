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

package com.algorand.android.modules.addaccount.joint.transaction.model

import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview

data class JointAccountTransactionViewState(
    val jointAccountDisplayName: AccountDisplayName,
    val jointAccountIconPreview: AccountIconDrawablePreview,
    val centerPreview: JointAccountSignRequestCenterPreview,
    val addressForClipboard: String,
    val recipientAddress: String,
    val recipientShortAddress: String,
    val amount: String,
    val convertedAmount: String,
    val transactionFee: String,
    val transactionState: JointAccountTransactionState,
    val signerAccounts: List<JointAccountSignerItem>,
    val signedCount: Int,
    val requiredSignatureCount: Int,
    val timeRemaining: String? = null,
    val transactionId: String? = null,
    val jointAccountAddress: String? = null,
    val currentUserParticipantAddress: String? = null,
    val isExpired: Boolean = false,
    val hasCurrentUserAlreadySigned: Boolean = false,
    val shouldShowPendingSignaturesDirectly: Boolean = false,
    val rawTransactions: List<String> = emptyList(),
    val allLocalParticipantAddresses: List<String> = emptyList(),
    val unsignedLocalParticipantAddresses: List<String> = emptyList(),
    val unsignedLedgerParticipantAddresses: List<String> = emptyList(),
    val hasProposerAddress: Boolean = false,
    val isRekeyTransaction: Boolean = false,
    val failReasonDisplay: String? = null
)
