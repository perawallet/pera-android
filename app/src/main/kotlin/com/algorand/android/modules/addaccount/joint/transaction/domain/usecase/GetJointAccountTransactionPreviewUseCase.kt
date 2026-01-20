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

package com.algorand.android.modules.addaccount.joint.transaction.domain.usecase

import android.text.format.DateUtils
import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionPreview
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.android.utils.ALGO_DECIMALS
import com.algorand.android.utils.decodeBase64
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.android.utils.getAlgorandMobileDateFormatter
import com.algorand.android.utils.parseFormattedDate
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.algosdk.transaction.model.RawTransactionType
import com.algorand.wallet.algosdk.transaction.usecase.ParseTransactionMessagePack
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignatureDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.TransactionListWithFullSignatureDTO
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSignRequestWithSignatures
import java.math.BigDecimal
import java.math.BigInteger
import java.time.ZonedDateTime
import javax.inject.Inject

internal class GetJointAccountTransactionPreviewUseCase @Inject constructor(
    private val getSignRequestWithSignatures: GetSignRequestWithSignatures,
    private val parseTransactionMessagePack: ParseTransactionMessagePack,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val deviceIdUseCase: DeviceIdUseCase,
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses,
    private val getLocalAccounts: GetLocalAccounts,
    private val createSignerAccounts: CreateSignerAccounts,
    private val calculateConvertedAlgoAmount: CalculateConvertedAlgoAmount
) : GetJointAccountTransactionPreview {

    override suspend operator fun invoke(signRequestId: String): PeraResult<JointAccountTransactionPreview> {
        val deviceId = deviceIdUseCase.getSelectedNodeDeviceId()?.toLongOrNull()
            ?: return PeraResult.Error(Exception("Device ID not available"))

        return when (val result = getSignRequestWithSignatures(deviceId, signRequestId)) {
            is PeraResult.Success -> createPreview(result.data)
            is PeraResult.Error -> result
        }
    }

    private suspend fun createPreview(
        signRequest: SignRequestWithFullSignatureDTO
    ): PeraResult<JointAccountTransactionPreview> {
        val jointAccount = signRequest.jointAccount
            ?: return PeraResult.Error(Exception("Joint account is null"))
        val jointAccountAddress = jointAccount.address
            ?: return PeraResult.Error(Exception("Joint account address is null"))
        val transactionLists = signRequest.transactionLists
            ?: return PeraResult.Error(Exception("Transaction lists is null"))
        val participantAddresses = jointAccount.participantAddresses.orEmpty()

        val transactionData = extractTransactionData(transactionLists)
        val participantData = buildParticipantData(participantAddresses, transactionLists, signRequest)
        val expirationData = buildExpirationData(signRequest)

        return PeraResult.Success(
            buildPreview(
                signRequest = signRequest,
                jointAccountAddress = jointAccountAddress,
                threshold = jointAccount.threshold ?: 0,
                transactionData = transactionData,
                participantData = participantData,
                expirationData = expirationData,
                rawTransactions = transactionLists.firstOrNull()?.rawTransactions.orEmpty()
            )
        )
    }

    private suspend fun buildParticipantData(
        participantAddresses: List<String>,
        transactionLists: List<TransactionListWithFullSignatureDTO>,
        signRequest: SignRequestWithFullSignatureDTO
    ): ParticipantData {
        val responses = transactionLists.firstOrNull()?.responses.orEmpty()
        val responseMap = responses.associateBy { it.address }
        val localAccountAddresses = getLocalAccountsAddresses()
        val allLocalAccounts = getLocalAccounts()

        val localParticipants = participantAddresses.filter { it in localAccountAddresses }
        val respondedAddresses = responses
            .filter { it.type == SignRequestResponseType.SIGNED || it.type == SignRequestResponseType.REJECTED }
            .mapNotNull { it.address }
            .toSet()

        val unsignedLocal = localParticipants
            .filterNot { it in respondedAddresses }
            .filter { address ->
                val account = allLocalAccounts.find { it.algoAddress == address }
                account is LocalAccount.Algo25 || account is LocalAccount.HdKey
            }

        val unsignedLedger = localParticipants
            .filterNot { it in respondedAddresses }
            .filter { address ->
                allLocalAccounts.find { it.algoAddress == address } is LocalAccount.LedgerBle
            }

        val signedCount = participantAddresses.count { responseMap[it]?.type == SignRequestResponseType.SIGNED }
        val hasProposer = signRequest.proposerAddress?.let {
            createSignerAccounts.hasSigningCapableLocalAccount(it)
        } ?: false

        return ParticipantData(
            signerAccounts = createSignerAccounts(participantAddresses, responses),
            signedCount = signedCount,
            localParticipants = localParticipants,
            unsignedLocal = unsignedLocal,
            unsignedLedger = unsignedLedger,
            hasProposer = hasProposer,
            currentUserAddress = localParticipants.firstOrNull()
        )
    }

    private fun buildExpirationData(signRequest: SignRequestWithFullSignatureDTO): ExpirationData {
        val status = signRequest.status
        val isExpiredByStatus = status == SignRequestStatus.EXPIRED || status?.isFinalized() == true
        val isExpiredByTime = isSignRequestExpiredByTime(signRequest)
        val isExpired = isExpiredByStatus || isExpiredByTime
        val canBeSigned = status?.isWaiting() == true && !isExpiredByTime
        val timeRemaining = calculateTimeRemaining(signRequest, isExpired)

        return ExpirationData(isExpired, canBeSigned, timeRemaining)
    }

    private suspend fun buildPreview(
        signRequest: SignRequestWithFullSignatureDTO,
        jointAccountAddress: String,
        threshold: Int,
        transactionData: TransactionData,
        participantData: ParticipantData,
        expirationData: ExpirationData,
        rawTransactions: List<String>
    ): JointAccountTransactionPreview {
        val hasAlreadySigned = participantData.unsignedLocal.isEmpty() &&
            participantData.localParticipants.isNotEmpty()
        val hasUnsigned = participantData.unsignedLocal.isNotEmpty() ||
            participantData.unsignedLedger.isNotEmpty()
        val showPendingDirectly = !expirationData.canBeSigned ||
            participantData.localParticipants.isEmpty() ||
            !hasUnsigned

        return JointAccountTransactionPreview(
            jointAccountDisplayName = getAccountDisplayName(jointAccountAddress),
            jointAccountIconPreview = getAccountIconDrawablePreview(jointAccountAddress),
            recipientAddress = transactionData.recipientAddress,
            recipientShortAddress = transactionData.recipientAddress.toShortenedAddress(),
            amount = transactionData.amountFormatted.formatAsAlgoAmount(),
            convertedAmount = transactionData.convertedAmount,
            transactionFee = transactionData.feeFormatted.formatAsAlgoAmount(transactionSign = "-"),
            transactionState = JointAccountTransactionState.AwaitingConfirmation,
            signerAccounts = participantData.signerAccounts,
            signedCount = participantData.signedCount,
            requiredSignatureCount = threshold,
            timeRemaining = expirationData.timeRemaining,
            transactionId = signRequest.id?.toString(),
            jointAccountAddress = jointAccountAddress,
            currentUserParticipantAddress = participantData.currentUserAddress,
            isExpired = expirationData.isExpired,
            hasCurrentUserAlreadySigned = hasAlreadySigned,
            shouldShowPendingSignaturesDirectly = showPendingDirectly,
            rawTransactions = rawTransactions,
            allLocalParticipantAddresses = participantData.localParticipants,
            unsignedLocalParticipantAddresses = participantData.unsignedLocal,
            unsignedLedgerParticipantAddresses = participantData.unsignedLedger,
            hasProposerAddress = participantData.hasProposer
        )
    }

    private fun isSignRequestExpiredByTime(signRequest: SignRequestWithFullSignatureDTO): Boolean {
        val expireDatetime = signRequest.lastValidExpectedDatetime
            ?: signRequest.transactionLists?.firstOrNull()?.lastValidExpectedDatetime
            ?: return false

        val expireDateTime = expireDatetime.parseFormattedDate(getAlgorandMobileDateFormatter())
        return expireDateTime != null && ZonedDateTime.now().isAfter(expireDateTime)
    }

    private fun calculateTimeRemaining(
        signRequest: SignRequestWithFullSignatureDTO,
        isExpired: Boolean
    ): String? {
        if (isExpired) return "0m"

        val expireDatetime = signRequest.lastValidExpectedDatetime
            ?: signRequest.transactionLists?.firstOrNull()?.lastValidExpectedDatetime
            ?: return null

        val expireDateTime = expireDatetime.parseFormattedDate(getAlgorandMobileDateFormatter())
            ?: return null

        val millis = expireDateTime.toInstant().toEpochMilli() - ZonedDateTime.now().toInstant().toEpochMilli()
        return formatTimeLeft(millis)
    }

    private fun formatTimeLeft(millis: Long): String = when {
        millis <= 0 -> "0m"
        millis < DateUtils.MINUTE_IN_MILLIS -> "1m"
        millis < DateUtils.HOUR_IN_MILLIS -> "${millis / DateUtils.MINUTE_IN_MILLIS}m"
        millis < DateUtils.DAY_IN_MILLIS -> "${millis / DateUtils.HOUR_IN_MILLIS}h"
        else -> "${millis / DateUtils.DAY_IN_MILLIS}d"
    }

    private fun extractTransactionData(
        transactionLists: List<TransactionListWithFullSignatureDTO>
    ): TransactionData {
        var totalAmount = BigInteger.ZERO
        var recipientAddress = ""
        var totalFee = 0L

        transactionLists.forEach { list ->
            list.rawTransactions?.forEach { raw ->
                val bytes = raw.decodeBase64() ?: return@forEach
                val transaction = parseTransactionMessagePack(bytes) ?: return@forEach

                if (transaction.transactionType == RawTransactionType.PAY_TRANSACTION) {
                    transaction.amount?.toBigIntegerOrNull()?.let { totalAmount = totalAmount.add(it) }
                    if (recipientAddress.isEmpty()) {
                        recipientAddress = transaction.receiverAddress?.decodedAddress.orEmpty()
                    }
                }
                transaction.fee?.let { totalFee += it }
            }
        }

        val amountDecimal = totalAmount.toBigDecimal().movePointLeft(ALGO_DECIMALS)
        val feeDecimal = BigDecimal.valueOf(totalFee, ALGO_DECIMALS)

        return TransactionData(
            recipientAddress = recipientAddress,
            amountFormatted = amountDecimal.formatAsAlgoString(),
            feeFormatted = feeDecimal.formatAsAlgoString(),
            convertedAmount = calculateConvertedAmount(amountDecimal)
        )
    }

    private fun calculateConvertedAmount(algoAmount: BigDecimal): String {
        return calculateConvertedAlgoAmount(algoAmount)
    }

    private data class TransactionData(
        val recipientAddress: String,
        val amountFormatted: String,
        val feeFormatted: String,
        val convertedAmount: String
    )

    private data class ParticipantData(
        val signerAccounts: List<JointAccountSignerItem>,
        val signedCount: Int,
        val localParticipants: List<String>,
        val unsignedLocal: List<String>,
        val unsignedLedger: List<String>,
        val hasProposer: Boolean,
        val currentUserAddress: String?
    )

    private data class ExpirationData(
        val isExpired: Boolean,
        val canBeSigned: Boolean,
        val timeRemaining: String?
    )
}
