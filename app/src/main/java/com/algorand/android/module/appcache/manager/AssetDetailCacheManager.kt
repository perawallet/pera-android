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

package com.algorand.android.module.appcache.manager

import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccountCountFlow
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.accountinfo.component.domain.usecase.GetAllAssetHoldingIds
import com.algorand.android.module.appcache.model.AccountCacheStatus.INITIALIZED
import com.algorand.android.module.appcache.model.AssetCacheStatus
import com.algorand.android.module.appcache.model.AssetCacheStatus.EMPTY
import com.algorand.android.module.appcache.usecase.GetAccountDetailCacheStatusFlow
import com.algorand.android.assetdetail.component.asset.domain.usecase.FetchAndCacheAssets
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn

@Singleton
internal class AssetDetailCacheManager @Inject constructor(
    private val getAllAssetHoldingIds: GetAllAssetHoldingIds,
    private val getAccountDetailCacheStatusFlow: GetAccountDetailCacheStatusFlow,
    private val getLocalAccountCountFlow: GetLocalAccountCountFlow,
    private val fetchAndCacheAssets: FetchAndCacheAssets,
    private val getLocalAccounts: GetLocalAccounts
) : BaseCacheManager() {

    private val _cacheStatusFlow = MutableStateFlow<AssetCacheStatus>(AssetCacheStatus.IDLE)
    val cacheStatusFlow = _cacheStatusFlow.asStateFlow()

    override suspend fun initialize(coroutineScope: CoroutineScope) {
        combine(
            getAccountDetailCacheStatusFlow().distinctUntilChanged(),
            getLocalAccountCountFlow().distinctUntilChanged()
        ) { accountDetailCacheStatus, localAccountCount ->
            when {
                accountDetailCacheStatus == INITIALIZED && localAccountCount == 0 -> updateCacheStatus(EMPTY)
                accountDetailCacheStatus == INITIALIZED && localAccountCount > 0 -> startJob()
                else -> if (isCurrentJobActive) stopCurrentJob()
            }
        }.launchIn(coroutineScope)
    }

    override suspend fun doJob(coroutineScope: CoroutineScope) {
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
