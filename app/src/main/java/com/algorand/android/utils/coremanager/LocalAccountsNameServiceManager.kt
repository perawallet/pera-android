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

import com.algorand.android.account.localaccount.domain.usecase.*
import com.algorand.android.modules.firebase.token.FirebaseTokenManager
import com.algorand.android.modules.firebase.token.model.FirebaseTokenResult
import com.algorand.android.nameservice.domain.usecase.InitializeAccountNameService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.*

/**
 * Helper class to manage local accounts name services continuously.
 */
@Singleton
class LocalAccountsNameServiceManager @Inject constructor(
    private val firebaseTokenManager: FirebaseTokenManager,
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
            firebaseTokenManager.firebaseTokenResultFlow
        ) { localAccounts, firebaseTokenResult ->
            if (localAccounts.isNotEmpty() && firebaseTokenResult is FirebaseTokenResult.TokenLoaded) {
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
