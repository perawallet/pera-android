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

package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAccountAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountAssetsData
import com.algorand.android.module.account.core.component.assetdata.model.AccountAssetData
import javax.inject.Inject

internal class GetAccountAssetsDataUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val createAccountAssetsData: CreateAccountAssetData
) : GetAccountAssetsData {

    override suspend fun invoke(address: String, includeAlgo: Boolean): AccountAssetData {
        val accountInformation = getAccountInformation(address) ?: return AccountAssetData()
        return createAccountAssetsData(accountInformation, includeAlgo)
    }
}
