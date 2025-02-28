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

package com.algorand.wallet.analytics.data.service

import android.os.Bundle
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import com.algorand.wallet.analytics.domain.usecase.GetReferrerData
import com.algorand.wallet.analytics.domain.util.GA4.UTM_CAMPAIGN
import com.algorand.wallet.analytics.domain.util.GA4.UTM_CONTENT
import com.algorand.wallet.analytics.domain.util.GA4.UTM_MEDIUM
import com.algorand.wallet.analytics.domain.util.GA4.UTM_SOURCE
import com.algorand.wallet.analytics.domain.util.GA4.UTM_TERM
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class PeraEventTrackerImpl @Inject constructor (
    private val firebaseAnalytics: FirebaseAnalytics,
    private val getReferrerData: GetReferrerData
) : PeraEventTracker {

    override suspend fun logEvent(eventName: String) {
        val bundle = Bundle()
        val referralBundle = addReferralDataToBundle(bundle) // Add referral data
        firebaseAnalytics.logEvent(eventName, referralBundle.takeIf { referralBundle.size() > 0 })
    }

    override suspend fun logEvent(eventName: String, payloadMap: Map<String, Any>) {
        val payloadBundle = getPayloadBundle(payloadMap)
        val combinedBundle = addReferralDataToBundle(payloadBundle) // Merge referral data
        firebaseAnalytics.logEvent(eventName, combinedBundle.takeIf { combinedBundle.size() > 0 })
    }

    private fun getPayloadBundle(payloadMap: Map<String, Any>): Bundle {
        return Bundle().apply {
            payloadMap.forEach { payload ->
                with(payload) {
                    when (value) {
                        is Bundle -> putBundle(key, value as Bundle)
                        is CharSequence -> putCharSequence(key, value as CharSequence)
                        is String -> putString(key, value as String)
                        is Char -> putChar(key, value as Char)
                        is CharArray -> putCharArray(key, value as CharArray)
                        is Boolean -> putBoolean(key, value as Boolean)
                        is BooleanArray -> putBooleanArray(key, value as BooleanArray)
                        is Short -> putShort(key, value as Short)
                        is ShortArray -> putShortArray(key, value as ShortArray)
                        is Double -> putDouble(key, value as Double)
                        is DoubleArray -> putDoubleArray(key, value as DoubleArray)
                        is Int -> putInt(key, value as Int)
                        is IntArray -> putIntArray(key, value as IntArray)
                        is Byte -> putByte(key, value as Byte)
                        is ByteArray -> putByteArray(key, value as ByteArray)
                        is Float -> putFloat(key, value as Float)
                        is FloatArray -> putFloatArray(key, value as FloatArray)
                        is Long -> putLong(key, value as Long)
                        is LongArray -> putLongArray(key, value as LongArray)
                        else -> recordIllegalArgumentException(value)
                    }
                }
            }
        }
    }

    private suspend fun addReferralDataToBundle(bundle: Bundle): Bundle {
        val referralData = getReferrerData.invoke()
        referralData.let {
            it.utmSource?.let { source -> bundle.putString(UTM_SOURCE, source) }
            it.utmMedium?.let { medium -> bundle.putString(UTM_MEDIUM, medium) }
            it.utmCampaign?.let { campaign -> bundle.putString(UTM_CAMPAIGN, campaign) }
            it.utmTerm?.let { term -> bundle.putString(UTM_TERM, term) }
            it.utmContent?.let { content -> bundle.putString(UTM_CONTENT, content) }
        }
        return bundle
    }

    private fun recordIllegalArgumentException(value: Any) {
        val errorMessage = "$logTag: Not handled bundle payload type: ${value::class.java}"
        FirebaseCrashlytics.getInstance().recordException(IllegalArgumentException(errorMessage))
    }

    companion object {
        private val logTag = PeraEventTrackerImpl::class.java.simpleName
    }
}
