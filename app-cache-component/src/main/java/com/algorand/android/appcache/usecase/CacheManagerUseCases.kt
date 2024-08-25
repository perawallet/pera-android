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

package com.algorand.android.appcache.usecase

import com.algorand.android.appcache.model.AccountCacheStatus
import com.algorand.android.appcache.model.AppCacheStatus
import com.algorand.android.appcache.model.AssetCacheStatus
import kotlinx.coroutines.flow.Flow

fun interface RefreshSelectedCurrencyDetailCache {
    suspend operator fun invoke()
}

interface IsAssetCacheStatusAtLeastEmpty {
    operator fun invoke(): Boolean
}

interface RefreshAccountCacheManager {
    suspend operator fun invoke()
}

interface UpdateAccountAndAssetCache {
    suspend operator fun invoke()
}

interface GetAccountDetailCacheStatusFlow {
    operator fun invoke(): Flow<AccountCacheStatus>
}

interface GetAssetDetailCacheStatusFlow {
    operator fun invoke(): Flow<AssetCacheStatus>
}

interface GetAppCacheStatusFlow {
    operator fun invoke(): Flow<AppCacheStatus>
}

interface ClearAppSessionCache {
    suspend operator fun invoke()
}

internal interface ClearPreviousSessionCache {
    suspend operator fun invoke()
}
