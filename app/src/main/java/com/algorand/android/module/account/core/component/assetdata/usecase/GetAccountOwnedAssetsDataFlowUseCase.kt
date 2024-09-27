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

package com.algorand.android.module.account.core.component.assetdata.usecase

import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformationFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

internal class GetAccountOwnedAssetsDataFlowUseCase @Inject constructor(
    private val getAccountInformationFlow: GetAccountInformationFlow,
    private val getAccountOwnedAssetsData: GetAccountOwnedAssetsData
) : GetAccountOwnedAssetsDataFlow {

    override fun invoke(address: String, includeAlgo: Boolean): Flow<List<BaseOwnedAssetData.OwnedAssetData>> {
        return getAccountInformationFlow(address).mapNotNull { accountInformation ->
            if (accountInformation == null) return@mapNotNull null
            getAccountOwnedAssetsData(accountInformation, includeAlgo)
        }
    }
}
