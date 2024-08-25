/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import com.algorand.android.R
import com.algorand.android.models.NotificationMetadata
import com.algorand.android.pushtoken.domain.usecase.SetPushToken
import com.algorand.android.ui.splash.LauncherActivity
import com.algorand.android.utils.Event
import com.algorand.android.utils.preference.isNotificationActivated
import com.algorand.android.utils.recordException
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class PeraFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var peraNotificationManager: PeraNotificationManager

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var setPushToken: SetPushToken

    override fun onNewToken(token: String) {
        setPushToken(token)
    }

    @SuppressWarnings("LongMethod")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (sharedPref.isNotificationActivated().not()) {
            return
        }

        val notificationData = getNotificationData(remoteMessage)
        notificationData.alertMessage = remoteMessage.data[ALERT].toString()

        if (peraNotificationManager.newNotificationLiveData.hasActiveObservers()) {
            peraNotificationManager.newNotificationLiveData.postValue(Event(notificationData))
            return
        }

        val intent = if (notificationData.url != null) {
            LauncherActivity.newIntentWithDeeplink(context = this, deeplink = notificationData.url)
        } else {
            LauncherActivity.newIntent(context = this)
        }.apply { action = System.currentTimeMillis().toString() }

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationBuilder = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(notificationData.alertMessage.orEmpty())
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationData.alertMessage.orEmpty()))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(Notification.DEFAULT_ALL)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.app_name)
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        }

        notificationManager.notify(getUniqueId(), notificationBuilder.build())
    }

    private fun getUniqueId(): Int {
        val dateFormatForNotificationId = "HHmmssSS"
        val now = Date()
        return parseInt(SimpleDateFormat(dateFormatForNotificationId).format(now))
    }

    private fun getNotificationData(remoteMessage: RemoteMessage): NotificationMetadata {
        return try {
            Gson().fromJson(remoteMessage.data[CUSTOM], NotificationMetadata::class.java)
        } catch (exception: IllegalStateException) {
            recordException(exception)
            NotificationMetadata()
        }
    }

    companion object {
        private const val DEFAULT_CHANNEL_ID = "default"
        private const val ALERT = "alert"
        private const val CUSTOM = "custom"
    }
}
