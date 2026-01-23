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

package com.algorand.wallet.inbox.domain

import androidx.lifecycle.Lifecycle
import com.algorand.wallet.account.info.domain.model.AccountCacheStatus.INITIALIZED
import com.algorand.wallet.account.info.domain.usecase.GetAccountDetailCacheStatusFlow
import com.algorand.wallet.account.info.domain.usecase.GetAllAccountInformationFlow
import com.algorand.wallet.cache.LifecycleAwareCacheManager
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.inbox.domain.model.InboxSearchInput
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import com.algorand.wallet.inbox.domain.usecase.CacheInboxMessages
import com.algorand.wallet.inbox.domain.usecase.ClearInboxCache
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddresses
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import javax.inject.Named

internal class InboxCacheManagerImpl @Inject constructor(
    private val cacheManager: LifecycleAwareCacheManager,
    private val getAccountDetailCacheStatusFlow: GetAccountDetailCacheStatusFlow,
    private val cacheInboxMessages: CacheInboxMessages,
    private val clearInboxCache: ClearInboxCache,
    private val getInboxValidAddresses: GetInboxValidAddresses,
    private val getAllAccountInformationFlow: GetAllAccountInformationFlow,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    @param:Named(InboxApiRepository.INJECTION_NAME)
    private val inboxApiRepository: InboxApiRepository
) : InboxCacheManager, LifecycleAwareCacheManager.CacheManagerListener {

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
            updateInboxCache()
        }
    }

    private suspend fun updateInboxCache() {
        val validAddresses = getInboxValidAddresses()
        if (validAddresses.isEmpty()) {
            clearInboxCache()
            return
        }

        val deviceId = getSelectedNodeDeviceId()?.toLongOrNull() ?: run {
            return
        }

        val inboxSearchInput = InboxSearchInput(addresses = validAddresses)
        inboxApiRepository.getInboxMessages(deviceId, inboxSearchInput).use(
            onSuccess = { inboxMessages ->
                cacheInboxMessages(inboxMessages)
            },
            onFailed = { _, _ ->
            }
        )
    }

    override suspend fun refreshCache() {
        updateInboxCache()
    }
}
