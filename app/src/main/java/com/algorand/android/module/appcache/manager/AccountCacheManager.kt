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

import com.algorand.android.module.account.local.domain.usecase.GetLocalAccountCountFlow
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.info.domain.usecase.FetchAndCacheAccountInformation
import com.algorand.android.module.block.domain.usecase.ClearLastKnownBlockNumber
import com.algorand.android.module.block.domain.usecase.ShouldUpdateAccountCache
import com.algorand.android.module.block.domain.usecase.UpdateLastKnownBlockNumber
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Singleton
internal class AccountCacheManager @Inject constructor(
    private val clearLastKnownBlockNumber: ClearLastKnownBlockNumber,
    private val fetchAndCacheAccountInformation: FetchAndCacheAccountInformation,
    private val updateLastKnownBlockNumber: UpdateLastKnownBlockNumber,
    private val shouldUpdateAccountCache: ShouldUpdateAccountCache,
    private val getLocalAccountCountFlow: GetLocalAccountCountFlow,
    private val getLocalAccounts: GetLocalAccounts
) : BaseCacheManager() {

    private val localAccountCollector: suspend (Int) -> Unit = { accountCount ->
        if (accountCount > 0) startJob() else stopCurrentJob()
    }

    override suspend fun initialize(coroutineScope: CoroutineScope) {
        getLocalAccountCountFlow().collectLatest(localAccountCollector)
    }

    override fun doBeforeJobStarts() {
        coroutineScope.launch { clearLastKnownBlockNumber() }
        if (isCurrentJobActive) stopCurrentJob()
    }

    override suspend fun doJob(coroutineScope: CoroutineScope) {
        updateLastKnownBlockNumber()
        while (true) {
            updateCacheIfRequired()
            delay(NEXT_BLOCK_DELAY_AFTER)
        }
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
        val localAccountAddresses = getLocalAccounts().map { it.address }
        fetchAndCacheAccountInformation(localAccountAddresses)
        updateLastKnownBlockNumber()
    }

    companion object {
        private const val NEXT_BLOCK_DELAY_AFTER = 3500L
    }
}
