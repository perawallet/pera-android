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
import com.algorand.android.utils.parseFormattedDate
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequest
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.utils.date.RelativeTimeDifference
import com.algorand.wallet.utils.date.TimeProvider
import java.time.ZonedDateTime
import javax.inject.Inject

class SignatureRequestInboxItemMapper @Inject constructor(
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getTransactionParams: GetTransactionParams,
    private val timeProvider: TimeProvider,
    private val relativeTimeDifference: RelativeTimeDifference
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

        val statusLine =
            mapStatusToStatusLine(jointSignRequestDTO.status, resources, jointSignRequestDTO.failReasonDisplay)
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
            signedCount = getSignedCount(jointSignRequestDTO, requiredData.participantAddresses),
            totalCount = requiredData.participantAddresses.size,
            timeLeft = if (statusLine.isError) {
                resources.getString(R.string.zero_minutes_short)
            } else {
                getTimeLeft(jointSignRequestDTO, resources)
            },
            isRead = isRead(creationDateTime, lastOpenedTime),
            statusLineText = statusLine.text,
            statusLineIsError = statusLine.isError,
            failReasonDisplay = jointSignRequestDTO.failReasonDisplay,
            canUserSign = canUserSign(
                jointSignRequestDTO,
                requiredData.participantAddresses,
                localAccountAddresses,
                statusLine.isError
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

    private fun mapStatusToStatusLine(
        status: SignRequestStatus?,
        resources: Resources,
        failReasonDisplay: String?
    ): StatusLineData {
        return when (status) {
            SignRequestStatus.FAILED -> StatusLineData(
                text = failReasonDisplay?.takeIf { it.isNotBlank() }
                    ?: resources.getString(R.string.failed_transaction),
                isError = true
            )

            SignRequestStatus.EXPIRED -> StatusLineData(
                text = resources.getString(R.string.expired_transaction),
                isError = true
            )

            SignRequestStatus.DECLINED -> StatusLineData(
                text = resources.getString(R.string.declined_transaction),
                isError = true
            )

            SignRequestStatus.CONFIRMED -> StatusLineData(
                text = resources.getString(R.string.transaction_successfully_completed),
                isError = false
            )

            SignRequestStatus.READY, SignRequestStatus.SUBMITTING -> StatusLineData(
                text = resources.getString(R.string.submitting_transaction),
                isError = false
            )

            else -> StatusLineData(
                text = resources.getString(R.string.pending_transaction),
                isError = false
            )
        }
    }

    private data class StatusLineData(val text: String, val isError: Boolean)

    private fun canUserSign(
        jointSignRequestDTO: JointSignRequest,
        participantAddresses: List<String>,
        localAccountAddresses: List<String>,
        isBlockedByStatus: Boolean
    ): Boolean {
        if (isBlockedByStatus) return false

        val localParticipants = participantAddresses.filter { it in localAccountAddresses }
        if (localParticipants.isEmpty()) return false

        val respondedAddresses = jointSignRequestDTO.transactionLists
            ?.firstOrNull()
            ?.responses
            ?.filter {
                it.response == SignRequestResponseType.SIGNED ||
                        it.response == SignRequestResponseType.DECLINED
            }
            ?.mapNotNull { it.address }
            .orEmpty()

        return localParticipants.any { it !in respondedAddresses }
    }

    private fun getSignedCount(dto: JointSignRequest, participantAddresses: List<String>): Int {
        val signedAddresses = dto.transactionLists
            ?.flatMap { it.responses.orEmpty() }
            ?.filter { it.response == SignRequestResponseType.SIGNED && !it.address.isNullOrBlank() }
            ?.mapNotNull { it.address }
            ?.toSet()
            .orEmpty()
        return participantAddresses.count { it in signedAddresses }
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

            val estimatedCreationDateTime = timeProvider.getZonedDateTimeNow()
                .minusSeconds(timeDifferenceMillis / MILLIS_PER_SECOND)
            return formatRelativeTime(
                relativeTimeDifference.getRelativeTime(estimatedCreationDateTime, timeDifferenceMillis),
                resources
            )
        }

        val expireDateTime = getExpireDateTime(dto) ?: return ""
        val estimatedCreationDateTime = expireDateTime.minusMinutes(VALIDITY_WINDOW_MINUTES)
        val timeDifference = timeProvider.getCurrentTimeMillis() -
                estimatedCreationDateTime.toInstant().toEpochMilli()

        if (timeDifference < 0) return resources.getString(R.string.just_now)

        return formatRelativeTime(
            relativeTimeDifference.getRelativeTime(estimatedCreationDateTime, timeDifference),
            resources
        )
    }

    private fun formatRelativeTime(
        relativeTime: RelativeTimeDifference.RelativeTime,
        resources: Resources
    ): String {
        return when (relativeTime) {
            is RelativeTimeDifference.RelativeTime.Now -> resources.getString(R.string.just_now)
            is RelativeTimeDifference.RelativeTime.Minutes -> resources.getQuantityString(
                R.plurals.min_ago,
                relativeTime.value,
                relativeTime.value.toString()
            )

            is RelativeTimeDifference.RelativeTime.Hours -> resources.getQuantityString(
                R.plurals.hours_ago,
                relativeTime.value,
                relativeTime.value.toString()
            )

            is RelativeTimeDifference.RelativeTime.Days -> resources.getQuantityString(
                R.plurals.days_ago,
                relativeTime.value,
                relativeTime.value.toString()
            )

            is RelativeTimeDifference.RelativeTime.Date -> relativeTime.value
        }
    }

    private fun getTimeLeft(dto: JointSignRequest, resources: Resources): String? {
        val expireDateTime = getExpireDateTime(dto) ?: return null
        val timeDifferenceMillis = expireDateTime.toInstant().toEpochMilli() -
                timeProvider.getCurrentTimeMillis()
        return formatTimeLeft(timeDifferenceMillis, resources)
    }

    private fun formatTimeLeft(millis: Long, resources: Resources): String {
        if (millis <= THIRTY_SECONDS_MILLIS) return resources.getString(R.string.zero_minutes_short)

        return when {
            millis < DateUtils.MINUTE_IN_MILLIS -> resources.getString(R.string.one_minute_short)
            millis < DateUtils.HOUR_IN_MILLIS -> resources.getString(
                R.string.minutes_short,
                millis / DateUtils.MINUTE_IN_MILLIS
            )

            millis < DateUtils.DAY_IN_MILLIS -> resources.getString(
                R.string.hours_short,
                millis / DateUtils.HOUR_IN_MILLIS
            )

            else -> resources.getString(R.string.days_short, millis / DateUtils.DAY_IN_MILLIS)
        }
    }

    private fun getExpireDateTime(dto: JointSignRequest): ZonedDateTime? {
        val expireDatetimeString = dto.expectedExpireDatetime
            ?: dto.transactionLists?.firstOrNull()?.expectedExpireDatetime
            ?: return null
        return expireDatetimeString.parseFormattedDate(getAlgorandMobileDateFormatter())
    }

    private fun getCreationDateTime(dto: JointSignRequest): ZonedDateTime {
        val creationDatetimeString = dto.jointAccount?.creationDatetime
            ?: return timeProvider.getZonedDateTimeNow()
        return creationDatetimeString.parseFormattedDate(getAlgorandMobileDateFormatter())
            ?: timeProvider.getZonedDateTimeNow()
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
