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

package com.algorand.android.ui.asset.collectible.listing.viewmodel

import androidx.paging.PagingData
import com.algorand.android.modules.collectibles.listingviewtype.domain.model.NFTListingViewType
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItem
import com.algorand.android.ui.asset.collectible.listing.model.CollectibleListItem
import com.algorand.wallet.viewmodel.StateViewModel

interface CollectibleListingViewModel : StateViewModel<CollectibleListingViewModel.ViewState> {

    fun logCollectibleReceiveEvent()

    fun updateSearchKeyword(query: String)

    fun saveNFTListingViewTypePreference(nftListingViewType: NFTListingViewType)

    fun clearFilters()

    sealed interface ViewState {

        data object Idle : ViewState
        data object Loading : ViewState

        data class ContentState(
            val isThereAnyAuthAddress: Boolean,
            val type: ContentStateType
        ) : ViewState {

            sealed interface ContentStateType {

                data object Error : ContentStateType

                sealed interface Empty : ContentStateType {
                    data object NoCollectible : Empty
                    data class AllFilteredOut(val filteredOutCount: Int) : Empty
                }

                data class Content(
                    val collectibleList: PagingData<CollectibleListItem>,
                    val headersList: List<BaseCollectibleListHeaderItem>
                ) : ContentStateType
            }
        }
    }
}
