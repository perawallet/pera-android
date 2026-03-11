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

import android.content.res.Resources
import android.text.format.DateUtils
import com.algorand.android.R
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignatureStatus
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionState
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountTransactionViewState
import com.algorand.android.utils.ALGO_DECIMALS
import com.algorand.android.utils.decodeBase64
import com.algorand.android.utils.formatAsAlgoAmount
import com.algorand.android.utils.formatAsAlgoString
import com.algorand.android.utils.getAlgorandMobileDateFormatter
import com.algorand.android.utils.parseFormattedDate
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.algosdk.transaction.model.RawTransactionType
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.TransactionListWithFullSignature
import java.math.BigDecimal
import java.math.BigInteger

internal class GetJointAccountTransactionViewStateUseCase(
    private val dependencies: GetJointAccountTransactionViewStateDependencies,
    private val resources: Resources
) : GetJointAccountTransactionViewState {

    override suspend operator fun invoke(signRequestId: String): PeraResult<JointAccountTransactionViewState> {
        val deviceId = dependencies.getDeviceConfig().deviceId.toLongOrNull()
            ?: return PeraResult.Error(Exception("Device ID not available"))

        return when (val result = dependencies.getSignRequestWithSignatures(deviceId, signRequestId)) {
            is PeraResult.Success -> createViewState(result.data)
            is PeraResult.Error -> result
        }
    }

    private suspend fun createViewState(
        signRequest: SignRequestWithFullSignature
    ): PeraResult<JointAccountTransactionViewState> {
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
            buildViewState(
                signRequest = signRequest,
                jointAccountAddress = jointAccountAddress,
                threshold = jointAccount.threshold
                    ?: return PeraResult.Error(Exception("Joint account threshold is null")),
                transactionData = transactionData,
                participantData = participantData,
                expirationData = expirationData,
                rawTransactions = transactionLists.firstOrNull()?.rawTransactions.orEmpty()
            )
        )
    }

    private suspend fun buildParticipantData(
        participantAddresses: List<String>,
        transactionLists: List<TransactionListWithFullSignature>,
        signRequest: SignRequestWithFullSignature
    ): ParticipantData {
        val responses = transactionLists.firstOrNull()?.responses.orEmpty()
        val localAccountAddresses = dependencies.getLocalAccountsAddresses().toSet()

        val localParticipants = participantAddresses.filter { it in localAccountAddresses }
        val signerAccounts = dependencies.getJointAccountSignerItems(participantAddresses, responses)

        val unsignedLocal = signerAccounts
            .filter {
                it.signatureStatus == JointAccountSignatureStatus.Pending &&
                    it.isLocalAccount &&
                    !it.isLedgerAccount
            }
            .map { it.accountAddress }

        val unsignedLedger = signerAccounts
            .filter { it.canSignWithLedger }
            .map { it.accountAddress }

        val signedCount = signerAccounts.count { it.signatureStatus == JointAccountSignatureStatus.Signed }
        val hasProposer = checkHasProposer(signRequest.proposerAddress, signerAccounts)

        return ParticipantData(
            signerAccounts = signerAccounts,
            signedCount = signedCount,
            localParticipants = localParticipants,
            unsignedLocal = unsignedLocal,
            unsignedLedger = unsignedLedger,
            hasProposer = hasProposer,
            currentUserAddress = localParticipants.firstOrNull()
        )
    }

    private suspend fun checkHasProposer(
        proposerAddress: String?,
        signerAccounts: List<JointAccountSignerItem>
    ): Boolean {
        if (proposerAddress != null) {
            return dependencies.getJointAccountSignerItems.hasSigningCapableLocalAccount(proposerAddress)
        }
        return signerAccounts.any {
            it.isLocalAccount && it.signatureStatus == JointAccountSignatureStatus.Signed
        }
    }

    private fun buildExpirationData(signRequest: SignRequestWithFullSignature): ExpirationData {
        val status = signRequest.status
        val isExpiredByStatus = status == SignRequestStatus.EXPIRED || status?.isFinalized() == true
        val isExpiredByTime = isSignRequestExpiredByTime(signRequest)
        val isExpired = isExpiredByStatus || isExpiredByTime
        val canBeSigned = status?.isWaiting() == true && !isExpiredByTime
        val timeRemaining = calculateTimeRemaining(signRequest, isExpired)

        return ExpirationData(isExpired, canBeSigned, timeRemaining)
    }

    private fun isSignRequestExpiredByTime(signRequest: SignRequestWithFullSignature): Boolean {
        val expireDatetime = signRequest.lastValidExpectedDatetime
            ?: signRequest.transactionLists?.firstOrNull()?.lastValidExpectedDatetime
            ?: return false

        val expireDateTime = expireDatetime.parseFormattedDate(getAlgorandMobileDateFormatter()) ?: return false
        return dependencies.timeProvider.getZonedDateTimeNow().isAfter(expireDateTime)
    }

    private suspend fun buildViewState(
        signRequest: SignRequestWithFullSignature,
        jointAccountAddress: String,
        threshold: Int,
        transactionData: TransactionData,
        participantData: ParticipantData,
        expirationData: ExpirationData,
        rawTransactions: List<String>
    ): JointAccountTransactionViewState {
        val hasAlreadySigned = participantData.unsignedLocal.isEmpty() &&
                participantData.localParticipants.isNotEmpty()
        val hasUnsigned = participantData.unsignedLocal.isNotEmpty() ||
                participantData.unsignedLedger.isNotEmpty()
        val showPendingDirectly = !expirationData.canBeSigned ||
                participantData.localParticipants.isEmpty() ||
                !hasUnsigned

        val transactionState = when (signRequest.status) {
            SignRequestStatus.FAILED -> JointAccountTransactionState.Failed(signRequest.failReasonDisplay)
            SignRequestStatus.EXPIRED -> JointAccountTransactionState.Expired
            SignRequestStatus.DECLINED -> JointAccountTransactionState.Declined
            SignRequestStatus.CONFIRMED -> JointAccountTransactionState.Completed
            SignRequestStatus.READY -> JointAccountTransactionState.ReadyToSubmit
            else -> JointAccountTransactionState.AwaitingConfirmation
        }

        return JointAccountTransactionViewState(
            jointAccountDisplayName = dependencies.getAccountDisplayName(jointAccountAddress),
            jointAccountIconPreview = dependencies.getAccountIconDrawablePreview(jointAccountAddress),
            recipientAddress = transactionData.recipientAddress,
            recipientShortAddress = transactionData.recipientAddress.toShortenedAddress(),
            amount = transactionData.amountFormatted.formatAsAlgoAmount(),
            convertedAmount = transactionData.convertedAmount,
            transactionFee = transactionData.feeFormatted.formatAsAlgoAmount(transactionSign = "-"),
            transactionState = transactionState,
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
            hasProposerAddress = participantData.hasProposer,
            failReasonDisplay = signRequest.failReasonDisplay
        )
    }

    private fun calculateTimeRemaining(
        signRequest: SignRequestWithFullSignature,
        isExpired: Boolean
    ): String? {
        if (isExpired) return resources.getString(R.string.zero_minutes_short)

        val expireDatetime = signRequest.lastValidExpectedDatetime
            ?: signRequest.transactionLists?.firstOrNull()?.lastValidExpectedDatetime
            ?: return null

        val expireDateTime = expireDatetime.parseFormattedDate(getAlgorandMobileDateFormatter())
            ?: return null

        val millis = expireDateTime.toInstant().toEpochMilli() -
                dependencies.timeProvider.getZonedDateTimeNow().toInstant().toEpochMilli()
        return formatTimeLeft(millis)
    }

    private fun formatTimeLeft(millis: Long): String = when {
        millis <= 0 -> resources.getString(R.string.zero_minutes_short)
        millis < DateUtils.MINUTE_IN_MILLIS -> resources.getString(R.string.one_minute_short)
        millis < DateUtils.HOUR_IN_MILLIS -> resources.getString(
            R.string.minutes_short,
            (millis / DateUtils.MINUTE_IN_MILLIS).toInt()
        )

        millis < DateUtils.DAY_IN_MILLIS -> resources.getString(
            R.string.hours_short,
            (millis / DateUtils.HOUR_IN_MILLIS).toInt()
        )

        else -> resources.getString(R.string.days_short, (millis / DateUtils.DAY_IN_MILLIS).toInt())
    }

    private fun extractTransactionData(
        transactionLists: List<TransactionListWithFullSignature>
    ): TransactionData {
        var totalAmount = BigInteger.ZERO
        var recipientAddress = ""
        var totalFee = 0L

        transactionLists.forEach { list ->
            list.rawTransactions?.forEach { raw ->
                val bytes = raw.decodeBase64() ?: return@forEach
                val transaction = dependencies.parseTransactionMessagePack(bytes) ?: return@forEach

                when (transaction.transactionType) {
                    RawTransactionType.PAY_TRANSACTION -> {
                        transaction.amount?.toBigIntegerOrNull()?.let { totalAmount = totalAmount.add(it) }
                        if (recipientAddress.isEmpty()) {
                            recipientAddress = transaction.receiverAddress?.decodedAddress.orEmpty()
                        }
                    }

                    RawTransactionType.ASSET_TRANSACTION -> {
                        if (recipientAddress.isEmpty()) {
                            recipientAddress = transaction.assetReceiverAddress?.decodedAddress.orEmpty()
                        }
                    }

                    else -> Unit
                }
                if (recipientAddress.isEmpty()) {
                    recipientAddress = transaction.receiverAddress?.decodedAddress.orEmpty()
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
        return dependencies.formatAlgoAsDisplayCurrency(algoAmount)
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
