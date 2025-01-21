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

package com.algorand.android.banner.data.cache

import com.algorand.android.cache.LocalCache
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.model.AssetInboxAllAccounts
import com.algorand.android.utils.CacheResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetInboxLocalCache @Inject constructor() : LocalCache<String, AssetInboxAllAccounts>() {

    override suspend fun put(value: CacheResult.Success<AssetInboxAllAccounts>) {
        val key = value.data.address
        cacheValue(key, value)
    }

    override suspend fun put(key: String, value: CacheResult.Error<AssetInboxAllAccounts>) {
        cacheValue(key, value)
    }

    override suspend fun put(valueList: List<CacheResult.Success<AssetInboxAllAccounts>>) {
        val cacheResultPairList = valueList.map {
            val key = it.data.address
            Pair(key, it)
        }
        cacheAll(cacheResultPairList)
    }
}
