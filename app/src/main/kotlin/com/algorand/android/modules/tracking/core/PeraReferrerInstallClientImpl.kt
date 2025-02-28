package com.algorand.android.modules.tracking.core

import android.content.Context
import android.util.Log
import com.algorand.wallet.analytics.domain.service.PeraReferrerInstallClient
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class PeraReferrerInstallClientImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PeraReferrerInstallClient {

    override suspend fun getReferrerUrl(): String? = suspendCancellableCoroutine { continuation ->
        val referrerClient = InstallReferrerClient.newBuilder(context).build()

        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            val response = referrerClient.installReferrer
                            val referrerUrl = response.installReferrer
                            Log.i("InstallReferrer", "Referrer URL: $referrerUrl")
                            continuation.resume(referrerUrl)
                        } catch (e: Exception) {
                            Log.e("InstallReferrer", "Error getting referrer", e)
                            continuation.resume(null)
                        } finally {
                            referrerClient.endConnection()
                        }
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        Log.i("InstallReferrer", "Feature not supported on this device")
                        continuation.resume(null)
                        referrerClient.endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        Log.i("InstallReferrer", "Referrer service unavailable")
                        continuation.resume(null)
                        referrerClient.endConnection()
                    }
                    else -> {
                        Log.i("InstallReferrer", "Unknown response code: $responseCode")
                        continuation.resume(null)
                        referrerClient.endConnection()
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                Log.i("InstallReferrer", "Referrer service disconnected")
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        })

        continuation.invokeOnCancellation {
            referrerClient.endConnection()
        }
    }
}
