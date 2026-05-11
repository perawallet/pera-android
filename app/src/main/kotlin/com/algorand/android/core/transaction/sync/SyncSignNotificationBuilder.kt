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
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateUtils
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.algorand.android.MainActivity
import com.algorand.android.R
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.utils.date.TimeProvider
import com.algorand.android.utils.getAlgorandMobileDateFormatter
import com.algorand.android.utils.parseFormattedDate

internal class SyncSignNotificationBuilder(
    private val service: Service,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val timeProvider: TimeProvider
) {

    fun createNotificationChannel() {
        val notificationManager = getNotificationManager()
        notificationManager.deleteNotificationChannel(LEGACY_CHANNEL_ID)
        val channel = NotificationChannel(
            CHANNEL_ID,
            service.getString(R.string.sync_sign_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setShowBadge(false)
            setSound(null, null)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun buildPlaceholderNotification(session: SyncSignSession): Notification {
        val title = service.getString(R.string.signature_request_description, TITLE_PLACEHOLDER)
        val statusText = service.getString(R.string.pending_transaction)
        val contentPendingIntent = createContentPendingIntent(session)

        val collapsedView = RemoteViews(service.packageName, R.layout.notification_sync_sign_small)
        applySyncSignNotificationRow(collapsedView, title, statusText)
        collapsedView.setOnClickPendingIntent(R.id.sync_sign_notification_root, contentPendingIntent)

        val notification = NotificationCompat.Builder(service, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_small)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notification.contentView = collapsedView
        notification.flags = notification.flags or
            Notification.FLAG_NO_CLEAR or
            Notification.FLAG_ONGOING_EVENT

        return notification
    }

    suspend fun buildPollingNotification(
        session: SyncSignSession,
        signRequest: SignRequestWithFullSignature?
    ): Notification {
        val (signedCount, threshold) = if (signRequest != null) {
            getSignedCountAndThreshold(signRequest)
        } else {
            1 to 2
        }
        val title = getNotificationTitle(session, signRequest)
        val statusText = getPollingStatusLineText(signRequest?.status)
        val signedPortion = service.getString(R.string.of_signed, signedCount, threshold)
        val timeLeftShort = signRequest?.let { getTimeLeftShort(it) }
        val timeLeftFull = timeLeftShort?.let { service.getString(R.string.time_left, it) }

        val contentPendingIntent = createContentPendingIntent(session)

        val collapsedView = RemoteViews(service.packageName, R.layout.notification_sync_sign_small)
        applySyncSignNotificationRow(collapsedView, title, statusText)
        collapsedView.setOnClickPendingIntent(R.id.sync_sign_notification_root, contentPendingIntent)

        val expandedView = RemoteViews(service.packageName, R.layout.notification_sync_sign_large)
        applySyncSignNotificationRow(expandedView, title, statusText)
        expandedView.setOnClickPendingIntent(R.id.sync_sign_notification_root, contentPendingIntent)

        expandedView.setTextViewText(R.id.notification_signed_count, signedPortion)
        expandedView.setImageViewResource(R.id.notification_signed_icon, R.drawable.ic_user)
        expandedView.setImageViewResource(R.id.notification_time_left_icon, R.drawable.ic_clock)
        if (timeLeftFull != null) {
            expandedView.setTextViewText(R.id.notification_time_left, timeLeftFull)
            expandedView.setViewVisibility(R.id.notification_time_left_pill, View.VISIBLE)
        } else {
            expandedView.setViewVisibility(R.id.notification_time_left_pill, View.GONE)
        }

        val notification = NotificationCompat.Builder(service, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_small)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notification.contentView = collapsedView
        notification.bigContentView = expandedView
        notification.flags = notification.flags or
            Notification.FLAG_NO_CLEAR or
            Notification.FLAG_ONGOING_EVENT

        return notification
    }

    fun getNotificationManager(): NotificationManager {
        return service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun applySyncSignNotificationRow(
        remoteViews: RemoteViews,
        title: String,
        statusText: String
    ) {
        remoteViews.setTextViewText(R.id.notification_title, title)
        remoteViews.setTextViewText(R.id.notification_status_text, statusText)
        remoteViews.setImageViewResource(R.id.notification_status_icon, R.drawable.ic_pending)
    }

    private suspend fun getNotificationTitle(
        session: SyncSignSession,
        signRequest: SignRequestWithFullSignature?
    ): String {
        val address = signRequest?.jointAccount?.address?.takeIf { it.isNotBlank() }
            ?: session.jointAccountAddress.takeIf { it.isNotBlank() }
        val displayLabel = if (address != null) {
            getAccountDisplayName(address).primaryDisplayName
        } else {
            TITLE_PLACEHOLDER
        }
        return service.getString(R.string.signature_request_description, displayLabel)
    }

    private fun getPollingStatusLineText(status: SignRequestStatus?): String {
        return when (status) {
            SignRequestStatus.SUBMITTING -> service.getString(R.string.submitting_transaction)
            else -> service.getString(R.string.pending_transaction)
        }
    }

    private fun getSignedCountAndThreshold(signRequest: SignRequestWithFullSignature): Pair<Int, Int> {
        val threshold = signRequest.jointAccount?.threshold ?: 2
        val signedCount = signRequest.transactionLists?.firstOrNull()?.responses
            ?.count { it.type == SignRequestResponseType.SIGNED } ?: 1
        return signedCount to threshold
    }

    private fun getTimeLeftShort(signRequest: SignRequestWithFullSignature): String? {
        val expireDatetime = signRequest.lastValidExpectedDatetime
            ?: signRequest.transactionLists?.firstOrNull()?.lastValidExpectedDatetime
            ?: return null
        val expireDateTime = expireDatetime.parseFormattedDate(getAlgorandMobileDateFormatter())
            ?: return null
        val millis = expireDateTime.toInstant().toEpochMilli() -
            timeProvider.getZonedDateTimeNow().toInstant().toEpochMilli()
        return formatTimeLeft(millis)
    }

    private fun formatTimeLeft(millis: Long): String = when {
        millis <= THIRTY_SECONDS_MILLIS -> service.getString(R.string.zero_minutes_short)
        millis < DateUtils.MINUTE_IN_MILLIS -> service.getString(R.string.one_minute_short)
        millis < DateUtils.HOUR_IN_MILLIS -> service.getString(
            R.string.minutes_short,
            (millis / DateUtils.MINUTE_IN_MILLIS).toInt()
        )
        millis < DateUtils.DAY_IN_MILLIS -> service.getString(
            R.string.hours_short,
            (millis / DateUtils.HOUR_IN_MILLIS).toInt()
        )
        else -> service.getString(R.string.days_short, (millis / DateUtils.DAY_IN_MILLIS).toInt())
    }

    private fun createContentPendingIntent(session: SyncSignSession): PendingIntent {
        val deeplink = "$SIGN_REQUEST_DEEPLINK_PREFIX${session.signRequestId}"
        val deeplinkIntent = Intent().apply {
            putExtra(MainActivity.DEEPLINK_KEY, deeplink)
        }
        val intent = MainActivity.newIntentWithDeeplinkOrNavigation(service, deeplinkIntent).apply {
            action = System.currentTimeMillis().toString()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(service, session.notificationId, intent, flags)
    }

    private companion object {
        const val LEGACY_CHANNEL_ID = "joint_account_sync_sign"
        const val CHANNEL_ID = "joint_account_sync_sign_v2"
        const val THIRTY_SECONDS_MILLIS = 30_000L
        const val TITLE_PLACEHOLDER = "…"
        const val SIGN_REQUEST_DEEPLINK_PREFIX =
            "perawallet://app/joint-account-sign-request/?signRequestId="
    }
}
