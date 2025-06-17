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

package com.algorand.wallet.privacy.data.repository

import com.algorand.wallet.foundation.cache.FlowPersistentCache
import com.algorand.wallet.privacy.data.mapper.PrivacyModeCacheValueMapper
import com.algorand.wallet.privacy.data.mapper.PrivacyModeMapper
import com.algorand.wallet.privacy.data.model.PrivacyModeCacheValue
import com.algorand.wallet.privacy.domain.model.PrivacyMode
import com.algorand.wallet.privacy.domain.repository.PrivacyModeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DefaultPrivacyModeRepository(
    private val flowPersistentCache: FlowPersistentCache<PrivacyModeCacheValue>,
    private val privacyModeMapper: PrivacyModeMapper,
    private val privacyModeCacheValueMapper: PrivacyModeCacheValueMapper
) : PrivacyModeRepository {

    override fun getPrivacyModeFlow(): Flow<PrivacyMode> {
        return flowPersistentCache.observe().map {
            privacyModeMapper(it)
        }
    }

    override suspend fun getPrivacyMode(): PrivacyMode {
        return privacyModeMapper(flowPersistentCache.get())
    }

    override suspend fun setPrivacyMode(privacyMode: PrivacyMode) {
        flowPersistentCache.put(privacyModeCacheValueMapper(privacyMode))
    }
}
