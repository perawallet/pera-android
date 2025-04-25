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

package com.algorand.wallet.asset.assetinbox.domain

import androidx.lifecycle.Lifecycle
import com.algorand.wallet.account.info.domain.model.AccountCacheStatus.INITIALIZED
import com.algorand.wallet.account.info.domain.usecase.GetAccountDetailCacheStatusFlow
import com.algorand.wallet.account.info.domain.usecase.GetAllAccountInformationFlow
import com.algorand.wallet.asset.assetinbox.domain.usecase.CacheAssetInboxRequests
import com.algorand.wallet.asset.assetinbox.domain.usecase.ClearAssetInboxCache
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxRequests
import com.algorand.wallet.asset.assetinbox.domain.usecase.GetAssetInboxValidAddresses
import com.algorand.wallet.cache.LifecycleAwareCacheManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest

internal class AssetInboxCacheManagerImpl @Inject constructor(
    private val cacheManager: LifecycleAwareCacheManager,
    private val getAccountDetailCacheStatusFlow: GetAccountDetailCacheStatusFlow,
    private val getAssetInboxRequests: GetAssetInboxRequests,
    private val cacheAssetInboxRequests: CacheAssetInboxRequests,
    private val clearAssetInboxCache: ClearAssetInboxCache,
    private val getAssetInboxValidAddresses: GetAssetInboxValidAddresses,
    private val getAllAccountInformationFlow: GetAllAccountInformationFlow
) : AssetInboxCacheManager, LifecycleAwareCacheManager.CacheManagerListener {

    override suspend fun onInitializeManager(coroutineScope: CoroutineScope) {
        initialize()
    }

    override suspend fun onStartJob(coroutineScope: CoroutineScope) {
        runManagerJob()
    }

    override fun initialize(lifecycle: Lifecycle) {
        cacheManager.setListener(this)
        lifecycle.addObserver(cacheManager)
    }

    private suspend fun initialize() {
        getAccountDetailCacheStatusFlow().collectLatest { cacheStatus ->
            if (cacheStatus == INITIALIZED) {
                cacheManager.stopCurrentJob()
                cacheManager.startJob()
            }
        }
    }

    private suspend fun runManagerJob() {
        getAllAccountInformationFlow().collectLatest {
            clearAssetInboxCache()
            updateAssetInboxCache()
        }
    }

    private suspend fun updateAssetInboxCache() {
        val validAddresses = getAssetInboxValidAddresses()
        getAssetInboxRequests(validAddresses).use(
            onSuccess = { requests ->
                cacheAssetInboxRequests(requests)
            },
            onFailed = { _, _ ->
                clearAssetInboxCache()
            }
        )
    }
}
