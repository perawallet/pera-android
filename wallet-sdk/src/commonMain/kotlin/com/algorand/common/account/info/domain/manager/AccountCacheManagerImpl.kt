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

package com.algorand.common.account.info.domain.manager

import androidx.lifecycle.Lifecycle
import com.algorand.common.account.local.domain.usecase.GetLocalAccountCountFlow
import com.algorand.common.block.domain.usecase.ClearLastKnownBlockNumber
import com.algorand.common.block.domain.usecase.ShouldUpdateAccountCache
import com.algorand.common.block.domain.usecase.UpdateLastKnownBlockNumber
import com.algorand.common.cache.LifecycleAwareCacheManager
import com.algorand.common.cache.domain.usecase.UpdateAccountCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

internal class AccountCacheManagerImpl(
    private val cacheManager: LifecycleAwareCacheManager,
    private val clearLastKnownBlockNumber: ClearLastKnownBlockNumber,
    private val updateAccountCache: UpdateAccountCache,
    private val updateLastKnownBlockNumber: UpdateLastKnownBlockNumber,
    private val shouldUpdateAccountCache: ShouldUpdateAccountCache,
    private val getLocalAccountCountFlow: GetLocalAccountCountFlow
) : AccountCacheManager {

    private val cacheManagerListener = object : LifecycleAwareCacheManager.CacheManagerListener {
        override suspend fun onInitializeManager(coroutineScope: CoroutineScope) {
            initialize()
        }

        override suspend fun onStartJob(coroutineScope: CoroutineScope) {
            runManagerJob()
        }
    }

    private val localAccountCollector: suspend (Int) -> Unit = { accountCount ->
        if (accountCount > 0) cacheManager.startJob() else cacheManager.stopCurrentJob()
    }

    override fun initialize(lifecycle: Lifecycle) {
        cacheManager.setListener(cacheManagerListener)
        lifecycle.addObserver(cacheManager)
    }

    private suspend fun runManagerJob() {
        initializeJob()
        updateLastKnownBlockNumber()
        while (true) {
            updateCacheIfRequired()
            delay(NEXT_BLOCK_DELAY_AFTER)
        }
    }

    private suspend fun initialize() {
        getLocalAccountCountFlow().collectLatest(localAccountCollector)
    }

    private suspend fun initializeJob() {
        clearLastKnownBlockNumber()
    }

    private suspend fun updateCacheIfRequired() {
        shouldUpdateAccountCache().use(
            onSuccess = { shouldUpdate ->
                if (shouldUpdate) updateCacheAndLastKnownBlock()
            },
            onFailed = { _, _ ->
                updateCacheAndLastKnownBlock()
            }
        )
    }

    private suspend fun updateCacheAndLastKnownBlock() {
        updateAccountCache()
        updateLastKnownBlockNumber()
    }

    private companion object {
        const val NEXT_BLOCK_DELAY_AFTER = 3500L
    }
}
