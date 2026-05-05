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

package com.algorand.android.modules.accountdetail.jointaccountdetail.viewmodel

import com.algorand.android.modules.accountdetail.jointaccountdetail.ui.model.JointAccountParticipantItem
import com.algorand.wallet.account.local.domain.model.LocalAccount

interface JointAccountDetailProcessor {

    suspend fun createContentState(
        jointAccount: LocalAccount.Joint,
        accountAddress: String,
        showActions: Boolean
    ): JointAccountDetailViewModel.ViewState.Content

    suspend fun createContentStateFromInvitation(
        participantAddresses: List<String>,
        threshold: Int,
        accountAddress: String
    ): JointAccountDetailViewModel.ViewState.Content

    suspend fun fetchInvitationFromInbox(accountAddress: String): InvitationResult

    suspend fun fetchJointAccountFromApi(accountAddress: String): InvitationResult

    suspend fun createParticipantItems(participantAddresses: List<String>): List<JointAccountParticipantItem>

    suspend fun deleteInboxNotification(accountAddress: String)

    suspend fun isJointAccountExists(accountAddress: String): Boolean

    suspend fun getContactEditInfo(address: String): ContactEditInfo?

    suspend fun updateContactName(address: String, newName: String)

    data class InvitationData(
        val threshold: Int,
        val participantAddresses: List<String>
    )

    sealed interface InvitationResult {
        data class Success(val data: InvitationData) : InvitationResult
        data object NotFound : InvitationResult
        data object NetworkError : InvitationResult
    }

    data class ContactEditInfo(
        val contactName: String?,
        val contactPublicKey: String?,
        val contactDatabaseId: Int,
        val contactProfileImageUri: String?
    )
}
