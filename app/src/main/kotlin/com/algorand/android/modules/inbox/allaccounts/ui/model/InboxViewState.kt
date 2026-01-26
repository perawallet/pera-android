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

package com.algorand.android.modules.inbox.allaccounts.ui.model

import com.algorand.android.modules.inbox.allaccounts.domain.model.InboxWithAccount
import com.algorand.android.modules.inbox.allaccounts.domain.model.SignatureRequestInboxItem
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem

sealed interface InboxViewState {
    data object Loading : InboxViewState

    data object Empty : InboxViewState

    data class Content(
        val inboxWithAccountList: List<InboxWithAccount>,
        val signatureRequestList: List<SignatureRequestInboxItem>,
        val jointAccountInvitationList: List<JointAccountInvitationInboxItem>,
        val filterAccountAddress: String? = null
    ) : InboxViewState

    data class Error(val message: String? = null) : InboxViewState
}
