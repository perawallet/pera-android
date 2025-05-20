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

package com.algorand.android.ui.asset.collectible.listing.account.viewmodel

import com.algorand.android.R
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItem
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItem.InfoViewItem
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItem.SearchViewItem
import com.algorand.android.ui.asset.collectible.listing.model.BaseCollectibleListHeaderItemProviderData
import com.algorand.android.ui.asset.collectible.listing.viewmodel.BaseCollectibleListHeaderItemProvider

object AccountCollectibleListingHeaderItemProvider : BaseCollectibleListHeaderItemProvider {

    override fun getHeaders(data: BaseCollectibleListHeaderItemProviderData): List<BaseCollectibleListHeaderItem> {
        return with(data) {
            listOf(
                InfoViewItem(displayedCollectibleCount = collectibleCount, isAddButtonVisible = isThereAnyAuth),
                SearchViewItem(searchViewHintResId = R.string.search_nfts, query = searchKeyword)
            )
        }
    }
}
