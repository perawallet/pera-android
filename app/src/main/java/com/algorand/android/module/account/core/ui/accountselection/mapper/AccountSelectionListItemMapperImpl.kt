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

package com.algorand.android.module.account.core.ui.accountselection.mapper

import androidx.core.net.toUri
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem.BaseAccountItem.AccountErrorItem
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem.BaseAccountItem.AccountItem
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem.BaseAccountItem.ContactItem
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem.BaseAccountItem.NftDomainAccountItem
import com.algorand.android.module.account.core.ui.accountsorting.domain.model.BaseAccountAndAssetListItem.AccountListItem
import com.algorand.android.module.account.core.component.utils.toShortenedAddress
import com.algorand.android.module.nameservice.domain.model.NameService
import com.algorand.android.module.nameservice.domain.model.NameServiceSearchResult
import javax.inject.Inject

internal class AccountSelectionListItemMapperImpl @Inject constructor() : AccountSelectionListItemMapper {

    override fun mapToErrorAccountItem(accountListItem: AccountListItem): AccountErrorItem {
        return AccountErrorItem(accountListItem = accountListItem)
    }

    override fun mapToAccountItem(accountListItem: AccountListItem): AccountItem {
        return AccountItem(accountListItem = accountListItem)
    }

    override fun mapToContactItem(name: String, address: String, imageUri: String?): ContactItem {
        return ContactItem(
            displayName = name, address = address, imageUri = imageUri?.toUri()
        )
    }

    override fun mapToNftDomainAccountItem(nameService: NameService): NftDomainAccountItem {
        return NftDomainAccountItem(
            displayName = nameService.nameServiceName ?: nameService.accountAddress.toShortenedAddress(),
            address = nameService.accountAddress,
            serviceLogoUrl = nameService.nameServiceUri
        )
    }

    override fun mapToNftDomainAccountItem(nameService: NameServiceSearchResult): NftDomainAccountItem {
        return NftDomainAccountItem(
            displayName = nameService.name,
            address = nameService.accountAddress,
            serviceLogoUrl = nameService.service?.logoUrl
        )
    }
}
