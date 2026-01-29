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

import com.algorand.android.modules.addaccount.joint.creation.domain.exception.JointAccountValidationException
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.utils.getAlgorandMobileDateFormatter
import com.algorand.android.utils.parseFormattedDate
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.utils.date.TimeProvider
import java.time.ZonedDateTime
import javax.inject.Inject

class JointAccountInvitationInboxItemMapper @Inject constructor(
    private val timeProvider: TimeProvider
) {

    fun mapToJointAccountInvitationInboxItem(
        jointAccount: JointAccount,
        lastOpenedTime: ZonedDateTime?
    ): JointAccountInvitationInboxItem? {
        val accountAddress = jointAccount.address ?: return null
        val creationDatetimeString = jointAccount.creationDatetime ?: return null

        val dateFormatter = getAlgorandMobileDateFormatter()
        val creationDateTime = creationDatetimeString.parseFormattedDate(dateFormatter)
            ?: timeProvider.getZonedDateTimeNow()

        val now = timeProvider.getZonedDateTimeNow()
        val nowInTimeMillis = now.toInstant().toEpochMilli()
        val creationInTimeMillis = creationDateTime.toInstant().toEpochMilli()
        val timeDifference = nowInTimeMillis - creationInTimeMillis

        val threshold = jointAccount.threshold ?: JointAccountValidationException.MIN_PARTICIPANTS
        val participantAddresses = jointAccount.participantAddresses ?: emptyList()

        val isRead = isRead(creationDateTime, lastOpenedTime)

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

    private fun isRead(creationDateTime: ZonedDateTime, lastOpenedTime: ZonedDateTime?): Boolean {
        // If lastOpenedTime is null, mark as read (first time opening)
        // Otherwise, mark as read if creation date is before or equal to last opened time
        return lastOpenedTime == null || !creationDateTime.isAfter(lastOpenedTime)
    }
}
