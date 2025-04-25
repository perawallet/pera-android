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

package com.algorand.wallet.cache.domain.usecase

import com.algorand.wallet.account.info.domain.usecase.ClearAccountInformationCache
import com.algorand.wallet.asset.assetinbox.domain.usecase.ClearAssetInboxCache
import com.algorand.wallet.asset.domain.usecase.ClearAssetCache
import com.algorand.wallet.nameservice.domain.usecase.ClearNameServiceCache
import javax.inject.Inject

internal class ClearPreviousSessionCacheUseCase @Inject constructor(
    private val clearAccountInformationCache: ClearAccountInformationCache,
    private val clearAssetCache: ClearAssetCache,
    private val clearNameServiceCache: ClearNameServiceCache,
    private val clearAssetInboxCache: ClearAssetInboxCache
) : ClearPreviousSessionCache {

    override suspend fun invoke() {
        clearAccountInformationCache()
        clearAssetCache()
        clearNameServiceCache()
        clearAssetInboxCache()
    }
}
