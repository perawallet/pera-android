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

package com.algorand.android.accountinfo.component.domain.usecase.implementation

import com.algorand.android.accountinfo.component.domain.usecase.GetAllAccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.IsAssetOwnedByAnyAccount
import javax.inject.Inject

internal class IsAssetOwnedByAnyAccountUseCase @Inject constructor(
    private val getAllAccountInformation: GetAllAccountInformation
) : IsAssetOwnedByAnyAccount {

    override suspend fun invoke(assetId: Long): Boolean {
        return getAllAccountInformation().values.any { accountInfo ->
            accountInfo?.assetHoldings?.any { assetHolding ->
                assetHolding.assetId == assetId
            } == true
        }
    }
}
