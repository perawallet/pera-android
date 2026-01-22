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

import android.content.res.Resources
import android.text.format.DateUtils
import com.algorand.android.R
import com.algorand.android.models.Result
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.inbox.allaccounts.domain.model.SignatureRequestInboxItem
import com.algorand.android.modules.transaction.domain.GetTransactionParams
import com.algorand.android.utils.getAlgorandMobileDateFormatter
import com.algorand.android.utils.getRelativeTimeDifference
import com.algorand.android.utils.parseFormattedDate
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequest
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import java.time.ZonedDateTime
import javax.inject.Inject

class SignatureRequestInboxItemMapper @Inject constructor(
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getTransactionParams: GetTransactionParams
) {

    suspend fun getCurrentBlockNumber(): Long? {
        return (getTransactionParams() as? Result.Success)?.data?.lastRound
    }

    suspend fun mapToSignatureRequestInboxItem(
        jointSignRequestDTO: JointSignRequest,
        resources: Resources,
        lastOpenedTime: ZonedDateTime?,
        currentBlockNumber: Long?,
        localAccountAddresses: List<String>
    ): SignatureRequestInboxItem? {
        val requiredData = extractRequiredData(jointSignRequestDTO) ?: return null

        val isExpired = isSignRequestExpired(jointSignRequestDTO)
        val creationDateTime = getCreationDateTime(jointSignRequestDTO)

        return SignatureRequestInboxItem(
            signRequestId = requiredData.signRequestId,
            jointAccountAddress = requiredData.jointAccountAddress,
            jointAccountAddressShortened = requiredData.jointAccountAddress.toShortenedAddress(),
            accountIconDrawablePreview = getAccountIconDrawablePreview(requiredData.jointAccountAddress),
            description = resources.getString(
                R.string.signature_request_description,
                requiredData.jointAccountAddress.toShortenedAddress()
            ),
            timeAgo = getTimeAgo(jointSignRequestDTO, resources, currentBlockNumber),
            signedCount = getSignedCount(jointSignRequestDTO),
            totalCount = requiredData.threshold,
            timeLeft = if (isExpired) "0m" else getTimeLeft(jointSignRequestDTO),
            isRead = isRead(creationDateTime, lastOpenedTime),
            isExpired = isExpired,
            canUserSign = canUserSign(
                jointSignRequestDTO,
                requiredData.participantAddresses,
                localAccountAddresses,
                isExpired
            )
        )
    }

    private fun extractRequiredData(dto: JointSignRequest): RequiredData? {
        val jointAccount = dto.jointAccount
        val signRequestId = dto.id
        val address = jointAccount?.address
        val threshold = jointAccount?.threshold
        val participants = jointAccount?.participantAddresses

        return if (signRequestId != null && address != null && threshold != null && participants != null) {
            RequiredData(signRequestId, address, threshold, participants)
        } else {
            null
        }
    }

    private fun canUserSign(
        jointSignRequestDTO: JointSignRequest,
        participantAddresses: List<String>,
        localAccountAddresses: List<String>,
        isExpired: Boolean
    ): Boolean {
        if (isExpired) return false

        val localParticipants = participantAddresses.filter { it in localAccountAddresses }
        if (localParticipants.isEmpty()) return false

        val respondedAddresses = jointSignRequestDTO.transactionLists
            ?.firstOrNull()
            ?.responses
            ?.filter {
                it.response == SignRequestResponseType.SIGNED ||
                    it.response == SignRequestResponseType.REJECTED
            }
            ?.mapNotNull { it.address }
            .orEmpty()

        return localParticipants.any { it !in respondedAddresses }
    }

    private fun isSignRequestExpired(dto: JointSignRequest): Boolean {
        val expireDateTime = getExpireDateTime(dto) ?: return false
        return ZonedDateTime.now().isAfter(expireDateTime)
    }

    private fun getSignedCount(dto: JointSignRequest): Int {
        return dto.transactionLists
            ?.flatMap { it.responses.orEmpty() }
            ?.filter { it.response == SignRequestResponseType.SIGNED && !it.address.isNullOrBlank() }
            ?.mapNotNull { it.address }
            ?.toSet()
            ?.size ?: 0
    }

    private fun getTimeAgo(
        dto: JointSignRequest,
        resources: Resources,
        currentBlockNumber: Long?
    ): String {
        val transactionList = dto.transactionLists?.firstOrNull()
        val firstValidBlock = transactionList?.firstValidBlock?.toLongOrNull()

        if (firstValidBlock != null && currentBlockNumber != null) {
            val blocksSinceCreation = currentBlockNumber - firstValidBlock
            val timeDifferenceMillis = blocksSinceCreation * BLOCK_TIME_MILLIS

            if (timeDifferenceMillis < 0) return resources.getString(R.string.just_now)

            val estimatedCreationDateTime = ZonedDateTime.now().minusSeconds(timeDifferenceMillis / MILLIS_PER_SECOND)
            return getRelativeTimeDifference(resources, estimatedCreationDateTime, timeDifferenceMillis)
        }

        val expireDateTime = getExpireDateTime(dto) ?: return ""
        val estimatedCreationDateTime = expireDateTime.minusMinutes(VALIDITY_WINDOW_MINUTES)
        val timeDifference = ZonedDateTime.now().toInstant().toEpochMilli() -
            estimatedCreationDateTime.toInstant().toEpochMilli()

        if (timeDifference < 0) return resources.getString(R.string.just_now)

        return getRelativeTimeDifference(resources, estimatedCreationDateTime, timeDifference)
    }

    private fun getTimeLeft(dto: JointSignRequest): String? {
        val expireDateTime = getExpireDateTime(dto) ?: return null
        val timeDifferenceMillis = expireDateTime.toInstant().toEpochMilli() -
            ZonedDateTime.now().toInstant().toEpochMilli()
        return formatTimeLeft(timeDifferenceMillis)
    }

    private fun formatTimeLeft(millis: Long): String {
        if (millis <= THIRTY_SECONDS_MILLIS) return "0m"

        return when {
            millis < DateUtils.MINUTE_IN_MILLIS -> "1m"
            millis < DateUtils.HOUR_IN_MILLIS -> "${millis / DateUtils.MINUTE_IN_MILLIS}m"
            millis < DateUtils.DAY_IN_MILLIS -> "${millis / DateUtils.HOUR_IN_MILLIS}h"
            else -> "${millis / DateUtils.DAY_IN_MILLIS}d"
        }
    }

    private fun getExpireDateTime(dto: JointSignRequest): ZonedDateTime? {
        val expireDatetimeString = dto.expectedExpireDatetime
            ?: dto.transactionLists?.firstOrNull()?.expectedExpireDatetime
            ?: return null
        return expireDatetimeString.parseFormattedDate(getAlgorandMobileDateFormatter())
    }

    // TODO: This returns the joint account creation time, not the sign request creation time.
    // The sign request DTO doesn't have a creation timestamp. Consider adding one to the API.
    private fun getCreationDateTime(dto: JointSignRequest): ZonedDateTime {
        val creationDatetimeString = dto.jointAccount?.creationDatetime ?: return ZonedDateTime.now()
        return creationDatetimeString.parseFormattedDate(getAlgorandMobileDateFormatter()) ?: ZonedDateTime.now()
    }

    private fun isRead(creationDateTime: ZonedDateTime, lastOpenedTime: ZonedDateTime?): Boolean {
        return lastOpenedTime == null || !creationDateTime.isAfter(lastOpenedTime)
    }

    private data class RequiredData(
        val signRequestId: String,
        val jointAccountAddress: String,
        val threshold: Int,
        val participantAddresses: List<String>
    )

    private companion object {
        const val BLOCK_TIME_MILLIS = 3500L
        const val MILLIS_PER_SECOND = 1000L
        const val VALIDITY_WINDOW_MINUTES = 50L
        const val THIRTY_SECONDS_MILLIS = 30_000L
    }
}
