/*
 * Copyright 2025 Pera Wallet, LDA
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

import com.algorand.wallet.analytics.domain.model.ReferrerData
import com.algorand.wallet.analytics.domain.repository.ReferrerRepository
import com.algorand.wallet.foundation.cache.PersistentCache

internal class ReferrerRepositoryImpl(
    private val utmSourceStorage: PersistentCache<String>,
    private val utmMediumStorage: PersistentCache<String>,
    private val utmCampaignStorage: PersistentCache<String>,
    private val utmTermStorage: PersistentCache<String>,
    private val utmContentStorage: PersistentCache<String>
) : ReferrerRepository {

    override suspend fun saveReferrerData(referrerData: ReferrerData) {
        with(referrerData) {
            if (utmSource != null) utmSourceStorage.put(utmSource)
            if (utmMedium != null) utmMediumStorage.put(utmMedium)
            if (utmCampaign != null) utmCampaignStorage.put(utmCampaign)
            if (utmTerm != null) utmTermStorage.put(utmTerm)
            if (utmContent != null) utmContentStorage.put(utmContent)
        }
    }

    override suspend fun getReferrerData(): ReferrerData {
        return ReferrerData(
            utmSource = utmSourceStorage.get().takeUnless { it.isNullOrBlank() },
            utmMedium = utmMediumStorage.get().takeUnless { it.isNullOrBlank() },
            utmCampaign = utmCampaignStorage.get().takeUnless { it.isNullOrBlank() },
            utmTerm = utmTermStorage.get().takeUnless { it.isNullOrBlank() },
            utmContent = utmContentStorage.get().takeUnless { it.isNullOrBlank() }
        )
    }
}
