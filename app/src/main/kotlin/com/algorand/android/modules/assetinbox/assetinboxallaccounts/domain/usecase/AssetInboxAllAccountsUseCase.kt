/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.usecase

import com.algorand.android.core.AccountManager
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.model.AssetInboxAllAccounts
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.repository.AssetInboxAllAccountsRepository
import com.algorand.android.utils.CacheResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class AssetInboxAllAccountsUseCase @Inject constructor(
    private val assetInboxAllAccountsRepository: AssetInboxAllAccountsRepository,
    private val accountManager: AccountManager,
) {

    suspend fun updateAssetInboxAllAccountsCache() {
        assetInboxAllAccountsRepository.getAssetInboxAllAccounts(
            accountManager.getAllAccountsAddressesExceptWatch()
        ).use(
            onSuccess = { assetInboxAllAccounts ->
                assetInboxAllAccountsRepository.cacheAssetInboxAllAccounts(
                    assetInboxAllAccounts.map { CacheResult.Success.create(it) }
                )
            },
            onFailed = { _, _ ->
                assetInboxAllAccountsRepository.clearAssetInboxAllAccountsCache()
            })
    }

    suspend fun getAssetInboxAllAccountsCacheFlow():
            StateFlow<HashMap<String, CacheResult<AssetInboxAllAccounts>>> {
        return assetInboxAllAccountsRepository.getAssetInboxAllAccountsCacheFlow()
    }

    suspend fun getAssetInboxCountCacheFlow(): Flow<Int> {
        return assetInboxAllAccountsRepository.getAssetInboxCountCacheFlow()
    }
}
