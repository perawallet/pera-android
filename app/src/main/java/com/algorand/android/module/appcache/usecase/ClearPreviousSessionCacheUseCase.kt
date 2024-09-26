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
import javax.inject.Inject

internal class ClearPreviousSessionCacheUseCase @Inject constructor(
    private val clearAccountInformationCache: ClearAccountInformationCache,
    private val clearAssetCache: ClearAssetCache,
) : ClearPreviousSessionCache {

    override suspend fun invoke() {
        clearAccountInformationCache()
        clearAssetCache()
    }
}
