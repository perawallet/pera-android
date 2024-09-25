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

package com.algorand.android.module.appcache.usecase

import com.algorand.android.module.appcache.model.AccountCacheStatus
import com.algorand.android.module.appcache.model.AppCacheStatus
import com.algorand.android.module.appcache.model.AssetCacheStatus
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class GetAppCacheStatusFlowUseCase @Inject constructor(
    private val getAccountDetailCacheStatusFlow: GetAccountDetailCacheStatusFlow,
    private val getAssetDetailCacheStatusFlow: GetAssetDetailCacheStatusFlow
) : GetAppCacheStatusFlow {

    override fun invoke(): Flow<AppCacheStatus> {
        return combine(
            getAccountDetailCacheStatusFlow(),
            getAssetDetailCacheStatusFlow()
        ) { accountCacheStatus, assetCacheStatus ->
            when {
                !isInitializationStarted(accountCacheStatus) -> AppCacheStatus.IDLE
                isCacheInitialized(accountCacheStatus, assetCacheStatus) -> AppCacheStatus.INITIALIZED
                else -> AppCacheStatus.LOADING
            }
        }
    }

    private fun isCacheInitialized(accountStatus: AccountCacheStatus, assetStatus: AssetCacheStatus): Boolean {
        return accountStatus == AccountCacheStatus.INITIALIZED && assetStatus isAtLeast AssetCacheStatus.EMPTY
    }

    private fun isInitializationStarted(accountStatus: AccountCacheStatus): Boolean {
        return accountStatus != AccountCacheStatus.IDLE
    }
}
