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

package com.algorand.android.core.transaction.sync

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.algorand.android.core.transaction.external.SwapServiceMetadata
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.models.WalletConnectSignResult
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.walletconnect.domain.WalletConnectErrorProvider
import com.algorand.android.modules.walletconnect.domain.WalletConnectManager
import com.algorand.android.modules.walletconnect.domain.model.WalletConnectVersionIdentifier
import com.algorand.android.modules.walletconnect.ui.model.WalletConnectSessionIdentifier
import com.algorand.android.usecase.SendSignedTransactionUseCase
import com.algorand.android.utils.DataResource
import com.algorand.android.utils.flatten
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import com.algorand.wallet.jointaccount.transaction.domain.MultisigTransactionAssembler
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSyncSignRequestWithSignatures
import com.algorand.wallet.jointaccount.transaction.domain.usecase.MarkSignRequestsConfirmed
import com.algorand.wallet.swap.domain.model.SignedSwapTransaction
import com.algorand.wallet.swap.domain.model.SwapStatusFailureReason
import com.algorand.wallet.swap.domain.usecase.SendSwapTransactions
import com.algorand.wallet.swap.domain.usecase.SetSwapStatusFailed
import com.algorand.wallet.transaction.domain.model.SignedTransaction
import com.algorand.wallet.utils.date.TimeProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@AndroidEntryPoint
class JointAccountSyncSignForegroundService : Service() {

    @Inject
    lateinit var getSyncSignRequestWithSignatures: GetSyncSignRequestWithSignatures

    @Inject
    lateinit var syncSignResultHolder: SyncSignResultHolder

    @Inject
    lateinit var timeProvider: TimeProvider

    @Inject
    lateinit var getAccountDisplayName: GetAccountDisplayName

    @Inject
    lateinit var multisigTransactionAssembler: MultisigTransactionAssembler

    @Inject
    lateinit var walletConnectManager: WalletConnectManager

    @Inject
    lateinit var walletConnectErrorProvider: WalletConnectErrorProvider

    @Inject
    lateinit var markSignRequestsConfirmed: MarkSignRequestsConfirmed

    @Inject
    lateinit var sendSignedTransactionUseCase: SendSignedTransactionUseCase

    @Inject
    lateinit var sendSwapTransactions: SendSwapTransactions

    @Inject
    lateinit var setSwapStatusFailed: SetSwapStatusFailed

    @Inject
    lateinit var errorLogger: PeraErrorLogger

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val activeSessions = ConcurrentHashMap<String, SyncSignSession>()
    private var isForegroundStarted = false
    private var foregroundNotificationSessionId: String? = null

    private val notificationBuilder by lazy {
        SyncSignNotificationBuilder(this, getAccountDisplayName, timeProvider)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        notificationBuilder.createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val safeIntent = intent ?: run {
            stopSelfIfEmpty()
            return START_NOT_STICKY
        }
        val deviceId = safeIntent.getStringExtra(EXTRA_DEVICE_ID) ?: run {
            stopSelfIfEmpty()
            return START_NOT_STICKY
        }
        val signRequestId = safeIntent.getStringExtra(EXTRA_SIGN_REQUEST_ID) ?: run {
            stopSelfIfEmpty()
            return START_NOT_STICKY
        }
        if (activeSessions.containsKey(signRequestId)) return START_NOT_STICKY

        val session = safeIntent.buildSyncSignSession(deviceId, signRequestId)
        activeSessions[signRequestId] = session

        val notification = notificationBuilder.buildPlaceholderNotification(session)
        if (!isForegroundStarted) {
            ServiceCompat.startForeground(
                this,
                session.notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
            isForegroundStarted = true
            foregroundNotificationSessionId = signRequestId
        } else {
            notificationBuilder.getNotificationManager().notify(session.notificationId, notification)
        }

        serviceScope.launch {
            runPollingLoop(session)
        }
        return START_NOT_STICKY
    }

    private fun Intent.buildSyncSignSession(deviceId: String, signRequestId: String): SyncSignSession {
        val wcSessionId = getStringExtra(EXTRA_WC_SESSION_ID)
        val wcVersionName = getStringExtra(EXTRA_WC_VERSION)
        val wcSessionIdentifier = if (wcSessionId != null && wcVersionName != null) {
            WalletConnectSessionIdentifier(
                sessionIdentifier = wcSessionId,
                versionIdentifier = WalletConnectVersionIdentifier.valueOf(wcVersionName)
            )
        } else {
            null
        }
        val algodSubmissionKindExtra = getStringExtra(EXTRA_ALGOD_SUBMISSION_KIND)
        val algodSubmissionKind = algodSubmissionKindExtra?.let { name ->
            runCatching { JointSyncAlgodSubmissionKind.valueOf(name) }.getOrNull()
        } ?: JointSyncAlgodSubmissionKind.NONE
        val swapId = getLongExtra(EXTRA_SWAP_ID, SwapServiceMetadata.INVALID_SWAP_ID)
        val swapTxnTypes = getStringArrayExtra(EXTRA_SWAP_TXN_TYPES)?.toList().orEmpty()
        return SyncSignSession(
            signRequestId = signRequestId,
            deviceId = deviceId,
            jointAccountAddress = getStringExtra(EXTRA_JOINT_ACCOUNT_ADDRESS).orEmpty(),
            wcSessionIdentifier = wcSessionIdentifier,
            wcRequestId = getLongExtra(EXTRA_WC_REQUEST_ID, -1L),
            notificationId = NOTIFICATION_ID_BASE + (signRequestId.hashCode() and 0x7FFFFFFF) % MAX_NOTIFICATION_RANGE,
            algodSubmissionKind = algodSubmissionKind,
            swapId = swapId,
            swapTxnTypes = swapTxnTypes
        )
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private suspend fun runPollingLoop(session: SyncSignSession) {
        while (serviceScope.isActive) {
            val result = getSyncSignRequestWithSignatures(
                deviceId = session.deviceId,
                signRequestId = session.signRequestId
            )
            val shouldStop = when (result) {
                is PeraResult.Success -> handleSuccessResult(session, result.data)
                is PeraResult.Error -> handleErrorResult(session, result)
            }
            if (shouldStop) return
            delay(SyncSignRequestPollingManager.SYNC_POLL_INTERVAL_MS)
        }
    }

    private suspend fun handleErrorResult(session: SyncSignSession, result: PeraResult.Error): Boolean {
        errorLogger.logError(
            IllegalStateException(
                "SyncSign: Polling failed for signRequestId=${session.signRequestId}",
                result.exception
            )
        )
        rejectWalletConnectRequestIfNeeded(session)
        syncSignResultHolder.setResult(session.signRequestId, SyncSignResultHolder.SyncSignResult.Failed)
        dismissSession(session)
        return true
    }

    private suspend fun handleSuccessResult(
        session: SyncSignSession,
        signRequest: SignRequestWithFullSignature
    ): Boolean {
        val outcome = computePollOutcome(session, signRequest)
        return when (outcome) {
            PollOutcome.STOP_READY, PollOutcome.STOP_FAILED -> {
                dismissSession(session)
                true
            }

            PollOutcome.CONTINUE -> false
        }
    }

    private suspend fun computePollOutcome(
        session: SyncSignSession,
        signRequest: SignRequestWithFullSignature
    ): PollOutcome {
        return when (signRequest.status) {
            SignRequestStatus.READY,
            SignRequestStatus.CONFIRMED -> {
                updateNotification(session, signRequest)
                if (assembleAndSubmit(session, signRequest)) PollOutcome.STOP_READY else PollOutcome.CONTINUE
            }
            SignRequestStatus.DECLINED -> {
                rejectWalletConnectRequestIfNeeded(session)
                syncSignResultHolder.setResult(session.signRequestId, SyncSignResultHolder.SyncSignResult.Declined)
                PollOutcome.STOP_FAILED
            }
            SignRequestStatus.EXPIRED -> {
                rejectWalletConnectRequestIfNeeded(session)
                syncSignResultHolder.setResult(session.signRequestId, SyncSignResultHolder.SyncSignResult.Expired)
                PollOutcome.STOP_FAILED
            }
            SignRequestStatus.FAILED, null -> {
                rejectWalletConnectRequestIfNeeded(session)
                syncSignResultHolder.setResult(session.signRequestId, SyncSignResultHolder.SyncSignResult.Failed)
                PollOutcome.STOP_FAILED
            }
            SignRequestStatus.PENDING,
            SignRequestStatus.SUBMITTING -> {
                updateNotification(session, signRequest)
                PollOutcome.CONTINUE
            }
        }
    }

    private suspend fun updateNotification(session: SyncSignSession, signRequest: SignRequestWithFullSignature) {
        val notification = notificationBuilder.buildPollingNotification(session, signRequest)
        notificationBuilder.getNotificationManager().notify(session.notificationId, notification)
    }

    private suspend fun assembleAndSubmit(
        session: SyncSignSession,
        signRequest: SignRequestWithFullSignature
    ): Boolean {
        val assembledGroups = assembleTransactionGroups(session, signRequest)
            ?: return true
        if (assembledGroups.isEmpty() && signRequest.transactionLists?.isNotEmpty() == true) {
            return false
        }
        return submitAssembledResult(session, signRequest, assembledGroups)
    }

    private fun assembleTransactionGroups(
        session: SyncSignSession,
        signRequest: SignRequestWithFullSignature
    ): MutableList<List<ByteArray>>? {
        val jointAccount = signRequest.jointAccount
        val transactionLists = signRequest.transactionLists
        val participantAddresses = jointAccount?.participantAddresses
        val version = jointAccount?.version
        val threshold = jointAccount?.threshold

        if (transactionLists == null || participantAddresses == null || version == null || threshold == null) {
            errorLogger.logError(
                "SyncSign: Missing transaction data for signRequestId=${session.signRequestId}"
            )
            syncSignResultHolder.setResult(session.signRequestId, SyncSignResultHolder.SyncSignResult.Failed)
            return null
        }

        val assembledGroups = mutableListOf<List<ByteArray>>()
        for (txListEntry in transactionLists) {
            val rawTransactions = txListEntry.rawTransactions
            val responses = txListEntry.responses
            if (rawTransactions == null || responses == null) {
                errorLogger.logError(
                    "SyncSign: Missing transaction data for signRequestId=${session.signRequestId}"
                )
                syncSignResultHolder.setResult(session.signRequestId, SyncSignResultHolder.SyncSignResult.Failed)
                return null
            }
            when (val assembleResult = multisigTransactionAssembler.assemble(
                rawTransactionsBase64 = rawTransactions,
                participantAddresses = participantAddresses,
                version = version,
                threshold = threshold,
                responses = responses
            )) {
                is PeraResult.Success -> assembledGroups.add(assembleResult.data)
                is PeraResult.Error -> {
                    errorLogger.logError(
                        IllegalStateException(
                            "SyncSign: Assembly failed for signRequestId=${session.signRequestId}",
                            assembleResult.exception
                        )
                    )
                    syncSignResultHolder.setResult(session.signRequestId, SyncSignResultHolder.SyncSignResult.Failed)
                    return null
                }
            }
        }
        return assembledGroups
    }

    private suspend fun submitAssembledResult(
        session: SyncSignSession,
        signRequest: SignRequestWithFullSignature,
        assembledGroups: List<List<ByteArray>>
    ): Boolean {
        val allSignedBytes = assembledGroups.flatMap { it }
        val wcSession = session.wcSessionIdentifier
        val isWalletConnectSession = wcSession != null && session.wcRequestId != -1L
        val shouldSubmitInAppToAlgod = !isWalletConnectSession &&
            session.algodSubmissionKind != JointSyncAlgodSubmissionKind.NONE

        if (shouldSubmitInAppToAlgod) {
            return handleAlgodSubmission(session, signRequest, assembledGroups)
        }

        markSignRequestsConfirmed(session.deviceId, listOf(session.signRequestId))

        if (isWalletConnectSession) {
            val wc = wcSession ?: return true
            walletConnectManager.processWalletConnectSignResult(
                WalletConnectSignResult.Success(wc, session.wcRequestId, allSignedBytes)
            )
        }

        syncSignResultHolder.setResult(
            session.signRequestId,
            SyncSignResultHolder.SyncSignResult.SignaturesReady(signRequest, allSignedBytes)
        )
        return true
    }

    private suspend fun rejectWalletConnectRequestIfNeeded(session: SyncSignSession) {
        val wcSession = session.wcSessionIdentifier ?: return
        if (session.wcRequestId == -1L) return
        walletConnectManager.silentRejectRequest(
            sessionIdentifier = wcSession,
            requestId = session.wcRequestId,
            errorResponse = walletConnectErrorProvider.getUserRejectionError()
        )
    }

    private suspend fun handleAlgodSubmission(
        session: SyncSignSession,
        signRequest: SignRequestWithFullSignature,
        assembledGroups: List<List<ByteArray>>
    ): Boolean {
        val txnId = submitAssembledTransactionsToAlgod(session, assembledGroups)
        if (txnId.isNullOrBlank()) {
            errorLogger.logError(
                "SyncSign: Algod submission returned empty txnId for signRequestId=${session.signRequestId}"
            )
            syncSignResultHolder.setResult(session.signRequestId, SyncSignResultHolder.SyncSignResult.Failed)
            return true
        }
        markSignRequestsConfirmed(session.deviceId, listOf(session.signRequestId))
        syncSignResultHolder.setResult(
            session.signRequestId,
            SyncSignResultHolder.SyncSignResult.SignaturesReady(
                signRequest = signRequest,
                assembledTransactionBytes = emptyList(),
                algodTransactionIdIfAlreadySubmitted = txnId
            )
        )
        return true
    }

    private suspend fun submitAssembledTransactionsToAlgod(
        session: SyncSignSession,
        assembledGroups: List<List<ByteArray>>
    ): String? = withContext(Dispatchers.IO) {
        when (session.algodSubmissionKind) {
            JointSyncAlgodSubmissionKind.NONE -> return@withContext null
            JointSyncAlgodSubmissionKind.SWAP -> submitSwapToAlgod(session, assembledGroups)
            JointSyncAlgodSubmissionKind.ARC59_SEND,
            JointSyncAlgodSubmissionKind.ARC59_CLAIM -> submitArc59ToAlgod(session, assembledGroups)
        }
    }

    private suspend fun submitSwapToAlgod(
        session: SyncSignSession,
        assembledGroups: List<List<ByteArray>>
    ): String? {
        if (session.swapId == SwapServiceMetadata.INVALID_SWAP_ID) return null
        val flattenedPerGroup = assembledGroups.map { it.flatten() }
        val signedSwapTxns = buildSignedSwapTransactions(session.swapTxnTypes, flattenedPerGroup)
        if (signedSwapTxns == null) {
            errorLogger.logError("SyncSign: Swap txn type/count mismatch")
            setSwapStatusFailed(session.swapId, SwapStatusFailureReason.OTHER)
            return null
        }
        return when (val result = sendSwapTransactions(session.swapId, signedSwapTxns)) {
            is PeraResult.Success -> result.data.firstOrNull()?.value
            is PeraResult.Error -> {
                errorLogger.logError(
                    IllegalStateException(
                        "SyncSign: Swap send failed for swapId=${session.swapId}",
                        result.exception
                    )
                )
                setSwapStatusFailed(session.swapId, SwapStatusFailureReason.OTHER)
                null
            }
        }
    }

    private suspend fun submitArc59ToAlgod(
        session: SyncSignSession,
        assembledGroups: List<List<ByteArray>>
    ): String? {
        val flattened = assembledGroups.flatMap { it }.flatten()
        val detail: SignedTransactionDetail = when (session.algodSubmissionKind) {
            JointSyncAlgodSubmissionKind.ARC59_SEND -> SignedTransactionDetail.Arc59Send(flattened)
            JointSyncAlgodSubmissionKind.ARC59_CLAIM -> SignedTransactionDetail.Arc59ClaimOrReject(flattened)
            else -> return null
        }
        var txnId: String? = null
        sendSignedTransactionUseCase.sendSignedTransaction(detail).collect { resource ->
            when (resource) {
                is DataResource.Success -> {
                    txnId = resource.data.takeIf { it.isNotBlank() }
                }
                is DataResource.Error -> {
                    val ex = resource.exception
                        ?: IllegalStateException("SyncSign: ARC59 send failed for ${session.signRequestId}")
                    errorLogger.logError(ex)
                    txnId = null
                }
                else -> Unit
            }
        }
        return txnId
    }

    private fun buildSignedSwapTransactions(
        types: List<String>,
        allSignedBytes: List<ByteArray>
    ): List<SignedSwapTransaction>? {
        if (types.size != allSignedBytes.size) return null
        val out = mutableListOf<SignedSwapTransaction>()
        for (i in types.indices) {
            val type = parseSwapTxnType(types[i]) ?: return null
            out.add(
                SignedSwapTransaction(
                    signedTransaction = SignedTransaction(allSignedBytes[i]),
                    type = type
                )
            )
        }
        return out
    }

    private fun parseSwapTxnType(name: String): SignedSwapTransaction.Type? {
        return when (name) {
            SwapServiceMetadata.TXN_TYPE_OPTIN -> SignedSwapTransaction.Type.OptIn
            SwapServiceMetadata.TXN_TYPE_SWAP -> SignedSwapTransaction.Type.Swap
            SwapServiceMetadata.TXN_TYPE_PERA_FEE -> SignedSwapTransaction.Type.PeraFee
            else -> null
        }
    }

    private fun dismissSession(session: SyncSignSession) {
        activeSessions.remove(session.signRequestId)
        notificationBuilder.getNotificationManager().cancel(session.notificationId)
        if (activeSessions.isEmpty()) {
            foregroundNotificationSessionId = null
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } else if (session.signRequestId == foregroundNotificationSessionId) {
            promoteNextSessionToForeground()
        }
    }

    private fun promoteNextSessionToForeground() {
        val nextSession = activeSessions.values.firstOrNull() ?: return
        foregroundNotificationSessionId = nextSession.signRequestId
        val notification = notificationBuilder.buildPlaceholderNotification(nextSession)
        ServiceCompat.startForeground(
            this,
            nextSession.notificationId,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    private fun stopSelfIfEmpty() {
        if (activeSessions.isEmpty()) stopSelf()
    }

    private enum class PollOutcome {
        STOP_READY,
        STOP_FAILED,
        CONTINUE
    }

    companion object {
        private const val NOTIFICATION_ID_BASE = 9001
        private const val MAX_NOTIFICATION_RANGE = 1000
        const val EXTRA_SIGN_REQUEST_ID = "sign_request_id"
        const val EXTRA_DEVICE_ID = "device_id"
        const val EXTRA_JOINT_ACCOUNT_ADDRESS = "joint_account_address"
        const val EXTRA_WC_SESSION_ID = "wc_session_id"
        const val EXTRA_WC_VERSION = "wc_version"
        const val EXTRA_WC_REQUEST_ID = "wc_request_id"
        const val EXTRA_ALGOD_SUBMISSION_KIND = "algod_submission_kind"
        const val EXTRA_SWAP_ID = "swap_id"
        const val EXTRA_SWAP_TXN_TYPES = "swap_txn_types"
    }
}
