/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */


package com.algorand.wallet.account.core.domain.usecase

import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.usecase.FetchAccountInformation
import com.algorand.wallet.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class FetchAccountInformationAndCacheAssetsUseCase @Inject constructor(
    private val fetchAccountInformation: FetchAccountInformation,
    private val fetchAndCacheAssets: FetchAndCacheAssets
) : FetchAccountInformationAndCacheAssets {

    override suspend fun invoke(address: String): PeraResult<AccountInformation> {
        val accountInformationResult = fetchAccountInformation(address)
        if (accountInformationResult.isFailed || accountInformationResult.getDataOrNull() == null) {
            return accountInformationResult
        }
        val assetIds = accountInformationResult.getDataOrNull()!!.assetHoldings.map { it.assetId }
        val assetResult = fetchAndCacheAssets(assetIds, false)
        if (assetResult.isFailed) return PeraResult.Error(assetResult.getExceptionOrNull() ?: Exception())
        return PeraResult.Success(accountInformationResult.getDataOrNull()!!)
    }
}
