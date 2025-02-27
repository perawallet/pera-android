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

package com.algorand.wallet.analytics.domain.utils

import com.algorand.wallet.analytics.domain.usecases.model.ReferralData
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class UrlReferrerParser @Inject constructor() {

    fun getReferrerData(queryString: String?): ReferralData {
        val params = mutableMapOf<String, String>()

        queryString?.split("&")?.forEach { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) {
                val key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8.name())
                val value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8.name())
                params[key] = value
            }
        }

        return ReferralData(
            utmSource = params.getOrDefault(UTM_SOURCE, null),
            utmMedium = params.getOrDefault(UTM_MEDIUM, null),
            utmCampaign = params.getOrDefault(UTM_CAMPAIGN, null),
            utmTerm = params.getOrDefault(UTM_TERM, null),
            utmContent = params.getOrDefault(UTM_CONTENT, null)
        )
    }

    companion object {
        const val UTM_SOURCE = "utm_source"
        const val UTM_MEDIUM = "utm_medium"
        const val UTM_CAMPAIGN = "utm_campaign"
        const val UTM_TERM = "utm_term"
        const val UTM_CONTENT = "utm_content"
    }
}
