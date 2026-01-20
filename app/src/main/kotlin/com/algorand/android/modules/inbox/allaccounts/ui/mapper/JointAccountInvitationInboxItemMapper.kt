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

package com.algorand.android.modules.inbox.allaccounts.ui.mapper

import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.utils.getAlgorandMobileDateFormatter
import com.algorand.android.utils.parseFormattedDate
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccountDTO
import java.time.ZonedDateTime
import javax.inject.Inject

class JointAccountInvitationInboxItemMapper @Inject constructor() {

    fun mapToJointAccountInvitationInboxItem(
        jointAccountDTO: JointAccountDTO,
        lastOpenedTime: ZonedDateTime?
    ): JointAccountInvitationInboxItem? {
        val accountAddress = jointAccountDTO.address ?: return null
        val creationDatetimeString = jointAccountDTO.creationDatetime ?: return null

        val dateFormatter = getAlgorandMobileDateFormatter()
        val creationDateTime = creationDatetimeString.parseFormattedDate(dateFormatter)
            ?: ZonedDateTime.now()

        val now = ZonedDateTime.now()
        val nowInTimeMillis = now.toInstant().toEpochMilli()
        val creationInTimeMillis = creationDateTime.toInstant().toEpochMilli()
        val timeDifference = nowInTimeMillis - creationInTimeMillis

        val threshold = jointAccountDTO.threshold ?: 2 // Default to 2 if not provided
        val participantAddresses = jointAccountDTO.participantAddresses ?: emptyList()

        // Determine read status: if lastOpenedTime is null, mark as read (first time opening)
        // Otherwise, mark as read if creation date is before last opened time
        val isRead = if (lastOpenedTime == null) {
            true // First time opening inbox, mark everything as read
        } else {
            creationDateTime.isBefore(lastOpenedTime) || creationDateTime.isEqual(lastOpenedTime)
        }

        return JointAccountInvitationInboxItem(
            id = "${accountAddress}_$creationInTimeMillis",
            accountAddress = accountAddress,
            accountAddressShortened = accountAddress.toShortenedAddress(),
            creationDateTime = creationDateTime,
            timeDifference = timeDifference,
            isRead = isRead,
            threshold = threshold,
            participantAddresses = participantAddresses
        )
    }
}
