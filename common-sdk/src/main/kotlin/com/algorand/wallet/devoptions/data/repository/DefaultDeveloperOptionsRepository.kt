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

package com.algorand.wallet.devoptions.data.repository

import com.algorand.wallet.devoptions.data.cache.DeveloperOptionsFeatureFlagsCache
import com.algorand.wallet.devoptions.domain.repository.DeveloperOptionsRepository
import com.algorand.wallet.foundation.cache.FlowPersistentCache

internal class DefaultDeveloperOptionsRepository(
    private val developerOptionsPersistentCache: FlowPersistentCache<Boolean>,
    private val developerOptionsFeatureFlagsCache: DeveloperOptionsFeatureFlagsCache
) : DeveloperOptionsRepository {

    override fun enableDeveloperOptions() = developerOptionsPersistentCache.put(true)

    override fun disableDeveloperOptions() = developerOptionsPersistentCache.put(false)

    override fun isDeveloperOptionsEnabled(): Boolean = developerOptionsPersistentCache.get()

    override fun clearDeveloperOptions() {
        developerOptionsPersistentCache.clear()
        developerOptionsFeatureFlagsCache.clear()
    }

    override fun setFeatureFlagStatus(featureName: String, isEnabled: Boolean) {
        developerOptionsFeatureFlagsCache.setFeatureFlag(featureName, isEnabled)
    }

    override fun isFeatureFlagEnabled(featureName: String): Boolean? {
        return developerOptionsFeatureFlagsCache.isFeatureFlagEnabled(featureName)
    }

    override fun clearFeatureFlag(featureName: String) = developerOptionsFeatureFlagsCache.clearFeatureFlag(featureName)

    override fun getFeatureFlags(): Map<String, Boolean> = developerOptionsFeatureFlagsCache.getAllFeatureFlags()
}
