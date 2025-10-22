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

package com.algorand.wallet.devoptions.data.cache

import com.algorand.wallet.foundation.cache.FlowPersistentCache
import javax.inject.Inject

internal class DefaultDeveloperOptionsFeatureFlagsCache @Inject constructor(
    private val persistentCache: FlowPersistentCache<Map<String, Boolean>>
) : DeveloperOptionsFeatureFlagsCache {

    override fun setFeatureFlag(featureName: String, isEnabled: Boolean) {
        val currentFlags = persistentCache.get()
        val updatedFlags = currentFlags.toMutableMap().apply {
            this[featureName] = isEnabled
        }
        persistentCache.put(updatedFlags)
    }

    override fun isFeatureFlagEnabled(featureName: String): Boolean? = persistentCache.get()[featureName]

    override fun clearFeatureFlag(featureName: String) {
        val currentFlags = persistentCache.get()
        val updatedFlags = currentFlags.toMutableMap().apply {
            this.remove(featureName)
        }
        persistentCache.put(updatedFlags)
    }

    override fun getAllFeatureFlags(): Map<String, Boolean> = persistentCache.get()

    override fun clear() = persistentCache.clear()
}
