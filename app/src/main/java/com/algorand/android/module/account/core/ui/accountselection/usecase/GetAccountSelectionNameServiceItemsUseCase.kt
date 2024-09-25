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

package com.algorand.android.module.account.core.ui.accountselection.usecase

import com.algorand.android.module.account.core.ui.accountselection.mapper.AccountSelectionListItemMapper
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem.BaseAccountItem
import com.algorand.android.nameservice.domain.usecase.GetNameServiceSearchResults
import javax.inject.Inject

internal class GetAccountSelectionNameServiceItemsUseCase @Inject constructor(
    private val getNameServiceSearchResults: GetNameServiceSearchResults,
    private val accountSelectionListItemMapper: AccountSelectionListItemMapper
) : GetAccountSelectionNameServiceItems {

    override suspend fun invoke(query: String): List<BaseAccountItem.NftDomainAccountItem> {
        return if (query.isNotBlank()) {
            getNameServiceSearchResults(query).map {
                accountSelectionListItemMapper.mapToNftDomainAccountItem(it)
            }
        } else emptyList()
    }
}
