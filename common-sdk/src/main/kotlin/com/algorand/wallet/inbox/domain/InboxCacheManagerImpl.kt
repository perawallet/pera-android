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
import com.algorand.wallet.cache.LifecycleAwareCacheManager
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.model.InboxSearchInput
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import com.algorand.wallet.inbox.domain.usecase.CacheInboxMessages
import com.algorand.wallet.inbox.domain.usecase.ClearInboxCache
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddresses
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestTransactionList
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestType
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

internal class InboxCacheManagerImpl @Inject constructor(
    private val cacheManager: LifecycleAwareCacheManager,
    private val getAccountDetailCacheStatusFlow: GetAccountDetailCacheStatusFlow,
    private val cacheInboxMessages: CacheInboxMessages,
    private val clearInboxCache: ClearInboxCache,
    private val getInboxValidAddresses: GetInboxValidAddresses,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    private val inboxApiRepository: InboxApiRepository,
    private val syncSignPollingTrigger: SyncSignPollingTrigger
) : InboxCacheManager, LifecycleAwareCacheManager.CacheManagerListener {

    private val triggeredSignRequestIds: MutableSet<String> =
        Collections.newSetFromMap(ConcurrentHashMap())

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
        while (true) {
            try {
                updateInboxCache()
            } catch (_: Exception) {
                // Continue polling on transient failures
            }
            delay(INBOX_POLL_INTERVAL_MS)
        }
    }

    private suspend fun updateInboxCache() {
        val validAddresses = getInboxValidAddresses()
        if (validAddresses.isEmpty()) {
            triggeredSignRequestIds.clear()
            clearInboxCache()
            return
        }

        val deviceId = getSelectedNodeDeviceId() ?: return
        val deviceIdLong = deviceId.toLongOrNull() ?: return

        val inboxSearchInput = InboxSearchInput(addresses = validAddresses)
        inboxApiRepository.getInboxMessages(deviceIdLong, inboxSearchInput).use(
            onSuccess = { inboxMessages ->
                cacheInboxMessages(inboxMessages)
                triggerSyncSignPollingIfNeeded(inboxMessages, deviceId, validAddresses.toSet())
            },
            onFailed = { _, _ ->
            }
        )
    }

    private fun triggerSyncSignPollingIfNeeded(
        inboxMessages: InboxMessages,
        deviceId: String,
        localAddresses: Set<String>
    ) {
        inboxMessages.jointAccountSignRequests.orEmpty()
            .filter { request ->
                request.type == SignRequestType.SYNC &&
                    request.status == SignRequestStatus.PENDING &&
                    request.id != null &&
                    request.id !in triggeredSignRequestIds &&
                    hasLocalAccountAlreadySigned(request.transactionLists, localAddresses)
            }
            .forEach { request ->
                val requestId = request.id ?: return@forEach
                triggeredSignRequestIds.add(requestId)
                syncSignPollingTrigger.onPendingSyncSignRequestDetected(
                    deviceId = deviceId,
                    signRequestId = requestId,
                    jointAccountAddress = request.jointAccount?.address.orEmpty()
                )
            }
    }

    private fun hasLocalAccountAlreadySigned(
        transactionLists: List<JointSignRequestTransactionList>?,
        localAddresses: Set<String>
    ): Boolean {
        return transactionLists?.any { txList ->
            txList.responses?.any { response ->
                response.address in localAddresses &&
                    response.response == SignRequestResponseType.SIGNED
            } == true
        } == true
    }

    override suspend fun refreshCache() {
        updateInboxCache()
    }

    private companion object {
        const val INBOX_POLL_INTERVAL_MS = 6_000L
    }
}
