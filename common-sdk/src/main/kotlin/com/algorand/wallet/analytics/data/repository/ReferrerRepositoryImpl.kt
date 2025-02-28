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

package com.algorand.wallet.analytics.data.repository

import android.content.SharedPreferences
import com.algorand.wallet.analytics.domain.repository.ReferrerRepository
import com.algorand.wallet.analytics.domain.model.ReferrerData
import com.algorand.wallet.analytics.domain.util.GA4.UTM_CAMPAIGN
import com.algorand.wallet.analytics.domain.util.GA4.UTM_CONTENT
import com.algorand.wallet.analytics.domain.util.GA4.UTM_MEDIUM
import com.algorand.wallet.analytics.domain.util.GA4.UTM_SOURCE
import com.algorand.wallet.analytics.domain.util.GA4.UTM_TERM
import javax.inject.Inject

internal class ReferrerRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
): ReferrerRepository {
    override suspend fun saveReferrerData(referrerData: ReferrerData) {
        sharedPreferences.edit().apply {
            referrerData.utmSource?.let { putString(UTM_SOURCE, it) }
            referrerData.utmMedium?.let { putString(UTM_MEDIUM, it) }
            referrerData.utmCampaign?.let { putString(UTM_CAMPAIGN, it) }
            referrerData.utmTerm?.let { putString(UTM_TERM, it) }
            referrerData.utmContent?.let { putString(UTM_CONTENT, it) }
            apply()
        }
    }

    override suspend fun getReferrerData(): ReferrerData {
        return ReferrerData(
            utmSource = sharedPreferences.getString(UTM_SOURCE, null),
            utmMedium = sharedPreferences.getString(UTM_MEDIUM, null) ,
            utmCampaign = sharedPreferences.getString(UTM_CAMPAIGN, null) ,
            utmTerm = sharedPreferences.getString(UTM_TERM, null) ,
            utmContent = sharedPreferences.getString(UTM_CONTENT, null)
        )
    }
}
