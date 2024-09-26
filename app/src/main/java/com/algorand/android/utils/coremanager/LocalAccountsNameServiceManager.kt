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

package com.algorand.android.utils.coremanager

import com.algorand.android.module.account.local.domain.usecase.GetAllLocalAccountAddressesAsFlow
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import com.algorand.android.module.appcache.manager.PushTokenManager
import com.algorand.android.module.appcache.model.PushTokenStatus
import com.algorand.android.module.nameservice.domain.usecase.InitializeAccountNameService
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

/**
 * Helper class to manage local accounts name services continuously.
 */
@Singleton
class LocalAccountsNameServiceManager @Inject constructor(
    private val pushTokenManager: PushTokenManager,
    private val initializeAccountNameService: InitializeAccountNameService,
    private val getLocalAccounts: GetLocalAccounts,
    private val getLocalAccountAddressesFlow: GetAllLocalAccountAddressesAsFlow
) : BaseCacheManager() {

    override suspend fun initialize(coroutineScope: CoroutineScope) {
        initObservers()
    }

    override fun doBeforeJobStarts() {
        stopCurrentJob()
    }

    override suspend fun doJob(coroutineScope: CoroutineScope) {
        updateLocalAccountNameServices()
    }

    private suspend fun initObservers() {
        combine(
            getLocalAccountAddressesFlow(),
            pushTokenManager.pushTokenStatusFlow
        ) { localAccounts, pushTokenStatusFlow ->
            if (localAccounts.isNotEmpty() && pushTokenStatusFlow == PushTokenStatus.INITIALIZED) {
                startJob()
            } else {
                stopCurrentJob()
            }
        }.collect()
    }

    private suspend fun updateLocalAccountNameServices() {
        val localAccountAddresses = getLocalAccounts().map { it.address }
        initializeAccountNameService(localAccountAddresses)
    }
}
