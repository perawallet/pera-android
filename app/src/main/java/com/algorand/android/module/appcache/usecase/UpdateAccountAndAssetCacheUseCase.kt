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

import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.info.domain.usecase.FetchAndCacheAccountInformation
import com.algorand.android.module.asset.detail.component.asset.domain.usecase.FetchAndCacheAssets
import javax.inject.Inject

internal class UpdateAccountAndAssetCacheUseCase @Inject constructor(
    private val getLocalAccounts: GetLocalAccounts,
    private val fetchAndCacheAccountInformation: FetchAndCacheAccountInformation,
    private val fetchAndCacheAssets: FetchAndCacheAssets
) : UpdateAccountAndAssetCache {

    override suspend fun invoke() {
        val localAccountAddresses = getLocalAccounts().map { it.address }
        val result = fetchAndCacheAccountInformation(localAccountAddresses)
        val assetIds = result.values.mapNotNull { it?.assetHoldings?.map { it.assetId } }.flatten()
        fetchAndCacheAssets(assetIds, includeDeleted = false)
    }
}
