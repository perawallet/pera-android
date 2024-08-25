/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.webview

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

private const val TINYMAN_FAQ_PRICE_IMPACT_URL = "https://docs.tinyman.org/faq#what-is-a-price-impact"

fun Context.openUrlWithCustomTabs(url: String) {
    try {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(this, Uri.parse(url))
    } catch (runtimeException: RuntimeException) {
//        recordException(runtimeException)
    }
}

fun Context.openTinymanFaqPriceImpactUrl() {
    openUrlWithCustomTabs(TINYMAN_FAQ_PRICE_IMPACT_URL)
}
