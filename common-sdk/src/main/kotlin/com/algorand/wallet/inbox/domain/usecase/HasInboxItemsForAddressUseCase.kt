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

package com.algorand.wallet.inbox.domain.usecase

import com.algorand.wallet.inbox.domain.model.InboxMessagesDTO
import com.algorand.wallet.inbox.domain.repository.InboxRepository
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import javax.inject.Inject

internal class HasInboxItemsForAddressUseCase @Inject constructor(
    private val inboxRepository: InboxRepository,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled
) : HasInboxItemsForAddress {

    override suspend fun invoke(address: String): Boolean {
        val inboxMessages = inboxRepository.getInboxMessages() ?: return false
        val isJointAccountEnabled = isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key)

        return if (isJointAccountEnabled) {
            hasAssetInboxRequests(inboxMessages, address) ||
                    hasJointAccountInvitations(inboxMessages, address) ||
                    hasSignRequests(inboxMessages, address)
        } else {
            hasAssetInboxRequests(inboxMessages, address)
        }
    }

    private fun hasAssetInboxRequests(messages: InboxMessagesDTO, address: String): Boolean {
        return messages.assetInboxes.orEmpty().any {
            it.address == address && it.requestCount > 0
        }
    }

    private fun hasJointAccountInvitations(messages: InboxMessagesDTO, address: String): Boolean {
        return messages.jointAccountImportRequests.orEmpty().any {
            it.participantAddresses.orEmpty().contains(address)
        }
    }

    private fun hasSignRequests(messages: InboxMessagesDTO, address: String): Boolean {
        return messages.jointAccountSignRequests.orEmpty().any { signRequest ->
            signRequest.jointAccount?.let { jointAccount ->
                jointAccount.address == address ||
                        jointAccount.participantAddresses.orEmpty().contains(address)
            } ?: false
        }
    }
}
