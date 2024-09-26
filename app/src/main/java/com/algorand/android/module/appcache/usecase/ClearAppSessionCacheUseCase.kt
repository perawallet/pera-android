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

package com.algorand.android.module.appcache.usecase

import com.algorand.android.module.account.info.domain.usecase.ClearAccountInformationCache
import com.algorand.android.assetdetail.component.asset.domain.usecase.ClearAssetCache
import com.algorand.android.module.banner.domain.usecase.ClearBanners
import com.algorand.android.module.block.domain.usecase.ClearLastKnownBlockNumber
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

internal class ClearAppSessionCacheUseCase @Inject constructor(
    private val clearAccountInformationCache: ClearAccountInformationCache,
    private val clearAssetCache: ClearAssetCache,
    private val clearLastKnownBlockNumber: ClearLastKnownBlockNumber,
    private val clearBanners: ClearBanners
) : ClearAppSessionCache {

    override suspend fun invoke() {
        withContext(Dispatchers.IO) {
            val deferredAccountInfoCache = async { clearAccountInformationCache() }
            val deferredAssetInfoCache = async { clearAssetCache() }
            val deferredLastKnownBlockNumber = async { clearLastKnownBlockNumber() }
            val deferredBanners = async { clearBanners() }
            awaitAll(deferredAccountInfoCache, deferredAssetInfoCache, deferredLastKnownBlockNumber, deferredBanners)
        }
    }
}