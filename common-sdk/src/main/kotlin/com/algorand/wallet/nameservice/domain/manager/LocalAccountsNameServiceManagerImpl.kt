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

package com.algorand.wallet.nameservice.domain.manager

import androidx.lifecycle.Lifecycle
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountCountFlow
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.analytics.domain.model.FirebaseTokenStatus
import com.algorand.wallet.analytics.domain.usecase.GetFirebaseTokenStatusFlow
import com.algorand.wallet.cache.LifecycleAwareCacheManager
import com.algorand.wallet.nameservice.domain.usecase.InitializeAccountNameService
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

internal class LocalAccountsNameServiceManagerImpl @Inject constructor(
    private val cacheManager: LifecycleAwareCacheManager,
    private val getFirebaseTokenStatusFlow: GetFirebaseTokenStatusFlow,
    private val getLocalAccountCountFlow: GetLocalAccountCountFlow,
    private val initializeAccountNameService: InitializeAccountNameService,
    private val getLocalAccountAddresses: GetLocalAccountsAddresses
) : LocalAccountsNameServiceManager, LifecycleAwareCacheManager.CacheManagerListener {

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
        combine(getLocalAccountCountFlow(), getFirebaseTokenStatusFlow()) { localAccountCount, firebaseTokenStatus ->
            if (localAccountCount > 0 && firebaseTokenStatus is FirebaseTokenStatus.Success) {
                cacheManager.stopCurrentJob()
                cacheManager.startJob()
            } else {
                cacheManager.stopCurrentJob()
            }
        }.collect()
    }

    private suspend fun runManagerJob() {
        val localAccountAddresses = getLocalAccountAddresses()
        initializeAccountNameService(localAccountAddresses)
    }
}
