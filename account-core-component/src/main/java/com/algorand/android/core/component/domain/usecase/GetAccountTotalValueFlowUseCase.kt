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

package com.algorand.android.core.component.domain.usecase

import com.algorand.android.core.component.assetdata.usecase.GetAccountAssetDataFlow
import com.algorand.android.core.component.domain.model.AccountTotalValue
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

internal class GetAccountTotalValueFlowUseCase @Inject constructor(
    private val getAccountAssetDataFlow: GetAccountAssetDataFlow,
    private val getAccountCollectibleDataFlow: GetAccountCollectibleDataFlow,
    private val getAccountTotalValue: GetAccountTotalValue
) : GetAccountTotalValueFlow {

    override suspend fun invoke(address: String, includeAlgo: Boolean): Flow<AccountTotalValue> {
        return combine(
            getAccountAssetDataFlow(address, includeAlgo).distinctUntilChanged(),
            getAccountCollectibleDataFlow(address).distinctUntilChanged(),
        ) { _, _ ->
            getAccountTotalValue(address, includeAlgo)
        }
    }
}
