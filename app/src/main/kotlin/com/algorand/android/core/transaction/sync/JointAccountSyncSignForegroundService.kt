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
import com.algorand.android.models.WalletConnectSignResult
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.walletconnect.domain.WalletConnectErrorProvider
import com.algorand.android.modules.walletconnect.domain.WalletConnectManager
import com.algorand.android.modules.walletconnect.domain.model.WalletConnectVersionIdentifier
import com.algorand.android.modules.walletconnect.ui.model.WalletConnectSessionIdentifier
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.logger.PeraErrorLogger
import com.algorand.wallet.jointaccount.transaction.domain.MultisigTransactionAssembler
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSyncSignRequestWithSignatures
import com.algorand.wallet.jointaccount.transaction.domain.usecase.MarkSignRequestsConfirmed
import com.algorand.wallet.utils.date.TimeProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
    internal lateinit var algodSubmitter: SyncSignAlgodSubmitter

    @Inject
    lateinit var errorLogger: PeraErrorLogger

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
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
        val startTimeMs = timeProvider.getCurrentTimeMillis()
        var consecutiveErrors = 0
        while (serviceScope.isActive) {
            val elapsedMs = timeProvider.getCurrentTimeMillis() - startTimeMs
            if (elapsedMs >= MAX_POLLING_DURATION_MS) {
                errorLogger.logError(
                    "SyncSign: Polling timed out after ${elapsedMs}ms for signRequestId=${session.signRequestId}"
                )
                rejectWalletConnectRequestIfNeeded(session)
                syncSignResultHolder.setResult(session.signRequestId, SyncSignResultHolder.SyncSignResult.Expired)
                dismissSession(session)
                return
            }
            val result = getSyncSignRequestWithSignatures(
                deviceId = session.deviceId,
                signRequestId = session.signRequestId
            )
            val shouldStop = when (result) {
                is PeraResult.Success -> {
                    consecutiveErrors = 0
                    handleSuccessResult(session, result.data)
                }

                is PeraResult.Error -> {
                    consecutiveErrors++
                    handleErrorResult(session, result, consecutiveErrors)
                }
            }
            if (shouldStop) return
            delay(SyncSignRequestPollingManager.SYNC_POLL_INTERVAL_MS)
        }
    }

    private suspend fun handleErrorResult(
        session: SyncSignSession,
        result: PeraResult.Error,
        consecutiveErrors: Int
    ): Boolean {
        if (consecutiveErrors < MAX_CONSECUTIVE_ERRORS) return false
        errorLogger.logError(
            IllegalStateException(
                "SyncSign: Polling failed after $consecutiveErrors consecutive errors " +
                    "for signRequestId=${session.signRequestId}",
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
        val txnId = algodSubmitter.submit(session, assembledGroups)
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
        private const val MAX_CONSECUTIVE_ERRORS = 3
        private const val MAX_POLLING_DURATION_MS = 10 * 60 * 1000L
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
