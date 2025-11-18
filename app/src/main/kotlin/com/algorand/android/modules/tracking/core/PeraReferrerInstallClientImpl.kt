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

package com.algorand.android.modules.tracking.core

import android.content.Context
import android.util.Log
import com.algorand.wallet.analytics.domain.service.PeraReferrerInstallClient
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class PeraReferrerInstallClientImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
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
