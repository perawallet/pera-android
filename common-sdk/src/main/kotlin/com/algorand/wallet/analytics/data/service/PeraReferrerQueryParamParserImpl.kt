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

package com.algorand.wallet.analytics

import com.algorand.wallet.analytics.domain.service.PeraReferrerQueryParamParser
import com.algorand.wallet.analytics.domain.model.ReferrerData
import com.algorand.wallet.analytics.domain.util.GA4.UTM_CAMPAIGN
import com.algorand.wallet.analytics.domain.util.GA4.UTM_CONTENT
import com.algorand.wallet.analytics.domain.util.GA4.UTM_MEDIUM
import com.algorand.wallet.analytics.domain.util.GA4.UTM_SOURCE
import com.algorand.wallet.analytics.domain.util.GA4.UTM_TERM
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

internal class PeraReferrerQueryParamParserImpl @Inject constructor(): PeraReferrerQueryParamParser {

    override fun getReferrerData(queryString: String?): ReferrerData {
        val params = mutableMapOf<String, String>()

        queryString?.split("&")?.forEach { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) {
                val key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8.name())
                val value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8.name())
                params[key] = value
            }
        }

        return ReferrerData(
            utmSource = params.getOrDefault(UTM_SOURCE, null),
            utmMedium = params.getOrDefault(UTM_MEDIUM, null),
            utmCampaign = params.getOrDefault(UTM_CAMPAIGN, null),
            utmTerm = params.getOrDefault(UTM_TERM, null),
            utmContent = params.getOrDefault(UTM_CONTENT, null)
        )
    }
}