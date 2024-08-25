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

package com.algorand.android.utils.analytics

import androidx.core.os.bundleOf
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.google.firebase.analytics.FirebaseAnalytics

private const val CURRENCY_CHANGE_EVENT_KEY = "currency_change"
private const val CURRENCY_ID_KEY = "currency_id"

private const val ALGO_ASSET_ID_STRING = "algos"

fun FirebaseAnalytics.logCurrencyChange(newCurrencyId: String) {
    val bundle = bundleOf(CURRENCY_ID_KEY to newCurrencyId)
    logEvent(CURRENCY_CHANGE_EVENT_KEY, bundle)
}

fun FirebaseAnalytics.logScreen(page: String) {
    logEvent(page, null)
}

fun getAssetIdAsEventParam(assetId: Long): String {
    return if (assetId == ALGO_ASSET_ID) ALGO_ASSET_ID_STRING else assetId.toString()
}
