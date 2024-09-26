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

package com.algorand.android.module.account.core.component.caching.domain.usecase.implementation

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.FetchAccountInformation
import com.algorand.android.assetdetail.component.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.android.module.account.core.component.caching.domain.usecase.FetchAccountInformationAndCacheAssets
import javax.inject.Inject

internal class FetchAccountInformationAndCacheAssetsUseCase @Inject constructor(
    private val fetchAccountInformation: FetchAccountInformation,
    private val fetchAndCacheAssets: FetchAndCacheAssets
) : FetchAccountInformationAndCacheAssets {

    override suspend fun invoke(address: String): Result<AccountInformation> {
        val accountInformationResult = fetchAccountInformation(address)
        if (accountInformationResult.isFailure || accountInformationResult.getOrNull() == null) {
            return accountInformationResult
        }
        val assetIds = accountInformationResult.getOrThrow().assetHoldings.map { it.assetId }
        val assetResult = fetchAndCacheAssets(assetIds, false)
        if (assetResult.isFailed) return Result.failure(assetResult.getExceptionOrNull() ?: Exception())
        return Result.success(accountInformationResult.getOrThrow())
    }
}
