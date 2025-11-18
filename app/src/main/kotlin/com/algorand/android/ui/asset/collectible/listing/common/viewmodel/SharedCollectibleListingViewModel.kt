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

package com.algorand.android.ui.asset.collectible.listing.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import com.algorand.android.modules.collectibles.filter.domain.usecase.ShouldDisplayWatchAccountNFTsPreferenceUseCase
import com.algorand.android.ui.asset.collectible.listing.viewmodel.BaseCollectibleListHeaderItemProvider
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModel
import com.algorand.android.ui.asset.collectible.listing.viewmodel.CollectibleListingViewModelDelegate
import com.algorand.wallet.account.detail.domain.model.AccountType.NoAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SharedCollectibleListingViewModel @Inject constructor(
    private val viewModelDelegate: CollectibleListingViewModelDelegate,
    private val getAccountLiteCacheFlow: GetAccountLiteCacheFlow,
    private val shouldDisplayWatchAccountNFTs: ShouldDisplayWatchAccountNFTsPreferenceUseCase,
) : ViewModel(), CollectibleListingViewModel by viewModelDelegate {

    fun initPreview(registerBottomNavDelegation: Boolean) {
        viewModelDelegate.init(
            viewModelScope,
            getFilteredAccountLiteCacheFlow(),
            getHeaderItemProvider(registerBottomNavDelegation)
        )
    }

    private fun getHeaderItemProvider(registerBottomNavDelegation: Boolean): BaseCollectibleListHeaderItemProvider {
        return if (registerBottomNavDelegation) {
            SharedCollectibleListingHeaderItemProvider
        } else {
            SharedNestedCollectibleListingHeaderItemProvider
        }
    }

    private fun getFilteredAccountLiteCacheFlow(): Flow<AccountLiteCacheStatus> {
        return getAccountLiteCacheFlow().map { cacheStatus ->
            if (cacheStatus is AccountLiteCacheStatus.Data) {
                if (shouldDisplayWatchAccountNFTs()) {
                    cacheStatus
                } else {
                    cacheStatus.copy(
                        accountLites = cacheStatus.accountLites.filter { it.value.cachedInfo?.type != NoAuth }
                    )
                }
            } else {
                cacheStatus
            }
        }
    }

    override fun onCleared() {
        viewModelDelegate.clearResources()
        super.onCleared()
    }
}
