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

package com.algorand.wallet

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object InstallReferrerHelper {

    private const val REFERRER = "REF"
    private const val UTM_CAMPAIGN = "utm_campaign"
    private const val UTM_SOURCE = "utm_source"
    private const val UTM_MEDIUM = "utm_medium"
    private const val UTM_TERM = "utm_term"
    private const val UTM_CONTENT = "utm_content"

    private val UTM_PARAMS = listOf(UTM_CAMPAIGN, UTM_SOURCE, UTM_MEDIUM, UTM_TERM, UTM_CONTENT)

    fun fetchInstallReferrer(context: Context) {
        val referrerClient = InstallReferrerClient.newBuilder(context).build()

        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        val response: ReferrerDetails = referrerClient.installReferrer
                        val referrerUrl = response.installReferrer

                        Log.i("InstallReferrer", "Referrer URL: $referrerUrl")
                        saveReferrerData(context, referrerUrl)
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        Log.i("InstallReferrer", "Feature not supported on this device")
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        Log.i("InstallReferrer", "Referrer service unavailable")
                    }
                }
                referrerClient.endConnection()
            }

            override fun onInstallReferrerServiceDisconnected() {
                Log.i("InstallReferrer", "Referrer service disconnected")
            }
        })
    }

    fun saveReferrerData(context: Context, referrerString: String?) {
        if (referrerString.isNullOrEmpty()) {
            Log.i("InstallReferrer", "No referrer string found.")
            return
        }

        val params = decodeQueryParams(referrerString)
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(REFERRER, Context.MODE_PRIVATE)

        sharedPreferences.edit().apply {
            UTM_PARAMS.forEach { param ->
                params[param]?.let { putString(param, it) }
            }
            apply()
        }

        Log.i("InstallReferrer", "Referrer data saved: $params")
    }

    fun decodeQueryParams(query: String?): Map<String, String> {
        return query?.split("&")
            ?.mapNotNull { param ->
                val parts = param.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8.name())
                    val value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8.name())
                    key to value
                } else null
            }?.toMap() ?: emptyMap()
    }
}
