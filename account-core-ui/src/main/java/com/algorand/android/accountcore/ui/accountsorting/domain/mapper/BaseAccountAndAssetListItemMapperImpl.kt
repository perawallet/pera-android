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

package com.algorand.android.accountcore.ui.accountsorting.domain.mapper

import com.algorand.android.accountcore.ui.accountsorting.domain.model.BaseAccountAndAssetListItem
import com.algorand.android.accountcore.ui.model.BaseItemConfiguration
import javax.inject.Inject

internal class BaseAccountAndAssetListItemMapperImpl @Inject constructor() : BaseAccountAndAssetListItemMapper {

    override fun invoke(itemConfiguration: BaseItemConfiguration.AccountItemConfiguration): BaseAccountAndAssetListItem.AccountListItem {
        return BaseAccountAndAssetListItem.AccountListItem(itemConfiguration)
    }
}