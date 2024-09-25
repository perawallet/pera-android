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

import com.algorand.android.caching.CacheResult
import com.algorand.android.caching.SharedPrefLocalSource
import com.algorand.android.currency.domain.usecase.RemovePrimaryCurrencyChangeListener
import com.algorand.android.currency.domain.usecase.SetPrimaryCurrencyChangeListener
import com.algorand.android.parity.domain.usecase.ClearSelectedCurrencyDetailCache
import com.algorand.android.parity.domain.usecase.GetSelectedCurrencyDetailFlow
import com.algorand.android.parity.domain.usecase.InitializeParityCache
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
internal class ParityCacheManager @Inject constructor(
    private val getSelectedCurrencyDetailFlow: GetSelectedCurrencyDetailFlow,
    private val initializeParityCache: InitializeParityCache,
    private val clearSelectedCurrencyDetailCache: ClearSelectedCurrencyDetailCache,
    private val removePrimaryCurrencyChangeListener: RemovePrimaryCurrencyChangeListener,
    setPrimaryCurrencyChangeListener: SetPrimaryCurrencyChangeListener
) : BaseCacheManager() {

    private val currencyChangeListener = SharedPrefLocalSource.OnChangeListener<String> {
        handleCurrencyChange()
    }
    private val mutex = Mutex()

    init {
        setPrimaryCurrencyChangeListener(currencyChangeListener)
        startJob()
    }

    override fun clearResources() {
        super.clearResources()
        removePrimaryCurrencyChangeListener(currencyChangeListener)
    }

    override suspend fun doJob(coroutineScope: CoroutineScope) {
        getSelectedCurrencyDetailFlow().distinctUntilChanged().collectLatest {
            when (it) {
                null -> fetchSelectedCurrencyDetail()
                is CacheResult.Success -> waitAndFetchSelectedCurrencyDetail(NEXT_FETCH_DELAY_AFTER_SUCCESS)
                is CacheResult.Error -> waitAndFetchSelectedCurrencyDetail(NEXT_FETCH_DELAY_AFTER_ERROR)
            }
        }
    }

    fun refreshSelectedCurrencyDetailCache() {
        if (mutex.isLocked) return
        stopCurrentJob()
        startJob()
    }

    private suspend fun waitAndFetchSelectedCurrencyDetail(delayAsMillis: Long) {
        delay(delayAsMillis)
        fetchSelectedCurrencyDetail()
    }

    private suspend fun fetchSelectedCurrencyDetail() {
        mutex.withLock {
            initializeParityCache()
        }
    }

    private fun handleCurrencyChange() {
        coroutineScope.launch {
            stopCurrentJob()
            clearSelectedCurrencyDetailCache()
            startJob()
        }
    }

    companion object {
        private const val NEXT_FETCH_DELAY_AFTER_ERROR = 2500L
        private const val NEXT_FETCH_DELAY_AFTER_SUCCESS = 60_000L
    }
}
