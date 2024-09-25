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

package com.algorand.android.module.asset.action.ui.usecase

import com.algorand.android.module.account.core.ui.usecase.GetAccountDisplayName
import com.algorand.android.module.account.core.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.module.asset.action.ui.model.AssetActionAccountDetail
import javax.inject.Inject

internal class GetAssetActionAccountDetailUseCase @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview
) : GetAssetActionAccountDetail {

    override suspend fun invoke(accountAddress: String): AssetActionAccountDetail {
        return AssetActionAccountDetail(
            address = accountAddress,
            displayName = getAccountDisplayName(accountAddress).primaryDisplayName,
            iconDrawablePreview = getAccountIconDrawablePreview(accountAddress)
        )
    }
}
