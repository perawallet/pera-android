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

package com.algorand.common.asset.domain.manager

import androidx.lifecycle.Lifecycle
import com.algorand.common.account.info.domain.model.AccountCacheStatus.INITIALIZED
import com.algorand.common.account.info.domain.usecase.GetAccountDetailCacheStatusFlow
import com.algorand.common.account.info.domain.usecase.GetAllAssetHoldingIds
import com.algorand.common.account.local.domain.usecase.GetLocalAccountCountFlow
import com.algorand.common.account.local.domain.usecase.GetLocalAccounts
import com.algorand.common.asset.domain.model.AssetCacheStatus
import com.algorand.common.asset.domain.model.AssetCacheStatus.EMPTY
import com.algorand.common.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.common.cache.LifecycleAwareCacheManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn

internal class AssetDetailCacheManagerImpl(
    private val cacheManager: LifecycleAwareCacheManager,
    private val getAllAssetHoldingIds: GetAllAssetHoldingIds,
    private val getAccountDetailCacheStatusFlow: GetAccountDetailCacheStatusFlow,
    private val getLocalAccountCountFlow: GetLocalAccountCountFlow,
    private val fetchAndCacheAssets: FetchAndCacheAssets,
    private val getLocalAccounts: GetLocalAccounts
) : AssetDetailCacheManager {

    private val _cacheStatusFlow = MutableStateFlow<AssetCacheStatus>(AssetCacheStatus.IDLE)
    override val cacheStatusFlow = _cacheStatusFlow.asStateFlow()

    private val cacheManagerListener = object : LifecycleAwareCacheManager.CacheManagerListener {
        override suspend fun onInitializeManager(coroutineScope: CoroutineScope) {
            initialize(coroutineScope)
        }

        override suspend fun onStartJob(coroutineScope: CoroutineScope) {
            runManagerJob()
        }
    }

    private fun initialize(coroutineScope: CoroutineScope) {
        combine(
            getAccountDetailCacheStatusFlow().distinctUntilChanged(),
            getLocalAccountCountFlow().distinctUntilChanged()
        ) { accountDetailCacheStatus, localAccountCount ->
            when {
                accountDetailCacheStatus == INITIALIZED && localAccountCount == 0 -> updateCacheStatus(EMPTY)
                accountDetailCacheStatus == INITIALIZED && localAccountCount > 0 -> cacheManager.startJob()
                else -> cacheManager.stopCurrentJob()
            }
        }.launchIn(coroutineScope)
    }

    override fun initialize(lifecycle: Lifecycle) {
        cacheManager.setListener(cacheManagerListener)
        lifecycle.addObserver(cacheManager)
    }

    private suspend fun runManagerJob() {
        updateCacheStatus(AssetCacheStatus.LOADING)
        val localAccountAddresses = getLocalAccounts().map { it.address }
        val assetIds = getAllAssetHoldingIds(localAccountAddresses)
        if (assetIds.isEmpty()) {
            updateCacheStatus(EMPTY)
            return
        }
        fetchAndCacheAssets(assetIds, includeDeleted = false)
        updateCacheStatus(AssetCacheStatus.INITIALIZED)
    }

    private fun updateCacheStatus(newStatus: AssetCacheStatus) {
        if (newStatus.ordinal > _cacheStatusFlow.value.ordinal) {
            _cacheStatusFlow.value = newStatus
        }
    }
}
