/*
 *  Copyright 2025 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.mapper

import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.model.AssetInboxAllAccountsWithAccount
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.model.AssetInboxAllAccountsPreview
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import com.algorand.wallet.asset.assetinbox.domain.model.AssetInboxRequest
import javax.inject.Inject

class AssetInboxAllAccountsPreviewMapperImpl @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
) : AssetInboxAllAccountsPreviewMapper {

    override suspend fun invoke(
        assetInboxAllAccountsList: List<AssetInboxRequest>,
        addresses: List<String>,
        isLoading: Boolean,
        isEmptyStateVisible: Boolean,
        showError: Event<ErrorResource>?,
        onNavBack: Event<Unit>?,
    ): AssetInboxAllAccountsPreview {
        return AssetInboxAllAccountsPreview(
            isLoading = isLoading,
            isEmptyStateVisible = isEmptyStateVisible,
            showError = showError,
            assetInboxAllAccountsWithAccountList = mapToAssetInboxAllAccountsWithAccount(
                assetInboxAllAccountsList,
                addresses
            )
        )
    }

    override fun getInitialPreview(): AssetInboxAllAccountsPreview {
        return AssetInboxAllAccountsPreview(
            isLoading = true,
            isEmptyStateVisible = false,
            showError = null,
            assetInboxAllAccountsWithAccountList = emptyList()
        )
    }

    private suspend fun mapToAssetInboxAllAccountsWithAccount(
        assetInboxAllAccountsList: List<AssetInboxRequest>,
        addresses: List<String>
    ): List<AssetInboxAllAccountsWithAccount> {
        return assetInboxAllAccountsList.mapNotNull { assetInboxAllAccounts ->
            if (assetInboxAllAccounts.requestCount <= 0) {
                null
            } else {
                addresses.firstOrNull { it == assetInboxAllAccounts.address }?.let { address ->
                    AssetInboxAllAccountsWithAccount(
                        address = assetInboxAllAccounts.address,
                        requestCount = assetInboxAllAccounts.requestCount,
                        accountAddress = address,
                        accountDisplayName = getAccountDisplayName(address),
                        accountIconDrawablePreview = getAccountIconDrawablePreview(address)
                    )
                }
            }
        }
    }
}
