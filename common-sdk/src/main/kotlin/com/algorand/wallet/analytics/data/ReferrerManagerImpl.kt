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

package com.algorand.wallet.analytics.data

import android.util.Log
import com.algorand.wallet.analytics.domain.ReferrerManager
import com.algorand.wallet.analytics.domain.repository.ReferrerRepository
import com.algorand.wallet.analytics.domain.utils.UrlReferrerParser
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class ReferrerManagerImpl @Inject constructor(
    private val installReferralApiClient: InstallReferrerApiClient,
    private val referrerRespository: ReferrerRepository,
    private val urlQueryParser: UrlReferrerParser,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReferrerManager {

    override suspend fun initialize() {
        val installReferrerClient = installReferralApiClient.initialize()
        installReferrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse.OK -> {
                        val response: ReferrerDetails = installReferrerClient.installReferrer
                        val referrerUrl = response.installReferrer

                        Log.i("InstallReferrer", "Referrer URL: $referrerUrl")
                        val referrerData = urlQueryParser.getReferrerData(referrerUrl)
                        CoroutineScope(coroutineDispatcher).launch {
                            referrerRespository.saveReferrerData(referrerData)
                        }
                    }
                    com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        Log.i("InstallReferrer", "Feature not supported on this device")
                    }
                    com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        Log.i("InstallReferrer", "Referrer service unavailable")
                    }
                }
                installReferrerClient.endConnection()
            }

            override fun onInstallReferrerServiceDisconnected() {
                Log.i("InstallReferrer", "Referrer service disconnected")
            }
        })
    }
}
