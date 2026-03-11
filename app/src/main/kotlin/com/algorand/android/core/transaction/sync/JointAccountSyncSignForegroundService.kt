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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.algorand.android.R
import com.algorand.android.ui.splash.LauncherActivity
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSyncSignRequestWithSignatures
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class JointAccountSyncSignForegroundService : Service() {

    @Inject
    lateinit var getSyncSignRequestWithSignatures: GetSyncSignRequestWithSignatures

    @Inject
    lateinit var syncSignResultHolder: SyncSignResultHolder

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val signRequestId = intent?.getStringExtra(EXTRA_SIGN_REQUEST_ID) ?: run {
            stopSelf(startId)
            return START_NOT_STICKY
        }
        val proposerAddress = intent.getStringExtra(EXTRA_PROPOSER_ADDRESS) ?: run {
            stopSelf(startId)
            return START_NOT_STICKY
        }
        val arbitraryDataSignOfId = intent.getStringExtra(EXTRA_ARBITRARY_DATA_SIGN_OF_ID) ?: run {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        val notification = buildWaitingNotification(1, 2)
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )

        serviceScope.launch {
            runPollingLoop(signRequestId, proposerAddress, arbitraryDataSignOfId, startId)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private suspend fun runPollingLoop(
        signRequestId: String,
        proposerAddress: String,
        arbitraryDataSignOfId: String,
        startId: Int
    ) {
        while (serviceScope.isActive) {
            val result = getSyncSignRequestWithSignatures(
                signRequestId = signRequestId,
                proposerAddress = proposerAddress,
                arbitraryDataSignOfId = arbitraryDataSignOfId
            )
            val shouldStop = when (result) {
                is PeraResult.Success -> handleSuccessResult(result.data, signRequestId, startId)
                is PeraResult.Error -> handleErrorResult(signRequestId, startId)
            }
            if (shouldStop) return
            delay(SYNC_POLL_INTERVAL_MS)
        }
    }

    private fun handleErrorResult(signRequestId: String, startId: Int): Boolean {
        syncSignResultHolder.setResult(signRequestId, SyncSignResultHolder.SyncSignResult.Failed)
        updateNotificationAndStop(
            title = getString(R.string.sync_sign_notification_failed),
            startId = startId
        )
        return true
    }

    private fun handleSuccessResult(
        signRequest: SignRequestWithFullSignature,
        signRequestId: String,
        startId: Int
    ): Boolean {
        val (signedCount, threshold) = getSignedCountAndThreshold(signRequest)
        val outcome = computePollOutcome(signRequest, signRequestId, signedCount, threshold)
        return when (outcome) {
            PollOutcome.STOP_READY -> {
                updateNotificationAndStop(
                    title = getString(R.string.sync_sign_notification_ready),
                    startId = startId
                )
                true
            }
            PollOutcome.STOP_FAILED -> {
                updateNotificationAndStop(
                    title = getString(R.string.sync_sign_notification_failed),
                    startId = startId
                )
                true
            }
            PollOutcome.CONTINUE -> false
        }
    }

    private fun computePollOutcome(
        signRequest: SignRequestWithFullSignature,
        signRequestId: String,
        signedCount: Int,
        threshold: Int
    ): PollOutcome {
        return when (signRequest.status) {
            SignRequestStatus.READY -> {
                syncSignResultHolder.setResult(
                    signRequestId,
                    SyncSignResultHolder.SyncSignResult.SignaturesReady(signRequest)
                )
                PollOutcome.STOP_READY
            }
            SignRequestStatus.DECLINED -> {
                syncSignResultHolder.setResult(
                    signRequestId,
                    SyncSignResultHolder.SyncSignResult.Declined
                )
                PollOutcome.STOP_FAILED
            }
            SignRequestStatus.EXPIRED -> {
                syncSignResultHolder.setResult(
                    signRequestId,
                    SyncSignResultHolder.SyncSignResult.Expired
                )
                PollOutcome.STOP_FAILED
            }
            SignRequestStatus.FAILED -> {
                syncSignResultHolder.setResult(
                    signRequestId,
                    SyncSignResultHolder.SyncSignResult.Failed
                )
                PollOutcome.STOP_FAILED
            }
            SignRequestStatus.CONFIRMED,
            SignRequestStatus.PENDING,
            SignRequestStatus.SUBMITTING -> {
                updateForegroundNotification(buildWaitingNotification(signedCount, threshold))
                PollOutcome.CONTINUE
            }
            null -> {
                syncSignResultHolder.setResult(
                    signRequestId,
                    SyncSignResultHolder.SyncSignResult.Failed
                )
                PollOutcome.STOP_FAILED
            }
        }
    }

    private enum class PollOutcome {
        STOP_READY,
        STOP_FAILED,
        CONTINUE
    }

    private fun getSignedCountAndThreshold(signRequest: SignRequestWithFullSignature): Pair<Int, Int> {
        val threshold = signRequest.jointAccount?.threshold ?: 2
        val signedCount = signRequest.transactionLists?.firstOrNull()?.responses
            ?.count { it.type == SignRequestResponseType.SIGNED } ?: 1
        return signedCount to threshold
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.sync_sign_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply { setShowBadge(false) }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    private fun buildWaitingNotification(signedCount: Int, threshold: Int): Notification {
        val contentText = getString(R.string.sync_sign_notification_waiting, signedCount, threshold)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentTitle(getString(R.string.pending_signatures))
            .setContentText(contentText)
            .setOngoing(true)
            .setContentIntent(createContentPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateForegroundNotification(notification: Notification) {
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, notification)
    }

    private fun updateNotificationAndStop(title: String, startId: Int) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentTitle(title)
            .setContentIntent(createContentPendingIntent())
            .setAutoCancel(true)
            .build()
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .notify(NOTIFICATION_ID, notification)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf(startId)
    }

    private fun createContentPendingIntent(): PendingIntent {
        val intent = Intent(this, LauncherActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0)
        return PendingIntent.getActivity(this, 0, intent, flags)
    }

    companion object {
        private const val CHANNEL_ID = "joint_account_sync_sign"
        private const val NOTIFICATION_ID = 9001
        private const val SYNC_POLL_INTERVAL_MS = 3_000L

        const val EXTRA_SIGN_REQUEST_ID = "sign_request_id"
        const val EXTRA_PROPOSER_ADDRESS = "proposer_address"
        const val EXTRA_ARBITRARY_DATA_SIGN_OF_ID = "arbitrary_data_sign_of_id"
    }
}
