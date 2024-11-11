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

package com.algorand.common.remoteconfig.data.service

import android.util.Log
import com.google.firebase.ktx.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal actual fun getFirebaseRemoteConfigService(): FirebaseRemoteConfigService {
    return FirebaseRemoteConfigServiceImpl()
}

internal class FirebaseRemoteConfigServiceImpl : FirebaseRemoteConfigService {

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(getFirebaseRemoteConfigSettings())
        }
    }

    override suspend fun fetchRemoteConfig() = suspendCoroutine { continuation ->
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Fetch succeeded")
                continuation.resume(Unit)
            } else {
                Log.d(TAG, "Fetch failed")
                continuation.resume(Unit)
            }
        }
    }

    override fun getBoolean(key: String): Boolean {
        return remoteConfig.getBoolean(key)
    }

    private fun getFirebaseRemoteConfigSettings(): FirebaseRemoteConfigSettings {
        return FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0L else FETCH_INTERVAL_IN_SECS)
            .build()
    }

    private companion object {
        const val FETCH_INTERVAL_IN_SECS: Long = 3600L // 1 hour
        const val TAG = "FirebaseRemoteConfigServiceImpl"
    }
}
