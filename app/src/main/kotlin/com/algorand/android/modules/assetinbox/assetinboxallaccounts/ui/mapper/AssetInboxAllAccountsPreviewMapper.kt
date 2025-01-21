/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.mapper

import com.algorand.android.models.Account
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.model.AssetInboxAllAccounts
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.domain.model.AssetInboxAllAccountsWithAccount
import com.algorand.android.modules.assetinbox.assetinboxallaccounts.ui.model.AssetInboxAllAccountsPreview
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event

interface AssetInboxAllAccountsPreviewMapper {
    operator fun invoke(
        assetInboxAllAccountsList: List<AssetInboxAllAccounts>,
        accounts: List<Account>,
        isLoading: Boolean,
        isEmptyStateVisible: Boolean,
        showError: Event<ErrorResource>?,
        onNavBack: Event<Unit>?,
    ): AssetInboxAllAccountsPreview

    fun getInitialPreview(): AssetInboxAllAccountsPreview

    fun mapToAssetInboxAllAccountsWithAccount(
        assetInboxAllAccountsList: List<AssetInboxAllAccounts>,
        accounts: List<Account>
    ): List<AssetInboxAllAccountsWithAccount>
}
