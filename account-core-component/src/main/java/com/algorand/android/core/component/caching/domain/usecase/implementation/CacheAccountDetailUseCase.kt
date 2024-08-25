package com.algorand.android.core.component.caching.domain.usecase.implementation

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.FetchAndCacheAccountInformation
import com.algorand.android.assetdetail.component.asset.domain.usecase.FetchAndCacheAssets
import com.algorand.android.core.component.caching.domain.usecase.CacheAccountDetail
import com.algorand.android.foundation.PeraResult
import javax.inject.Inject

internal class CacheAccountDetailUseCase @Inject constructor(
    private val fetchAndCacheAccountInformation: FetchAndCacheAccountInformation,
    private val fetchAndCacheAssets: FetchAndCacheAssets
) : CacheAccountDetail {

    override suspend fun invoke(address: String): PeraResult<AccountInformation> {
        val accountInformationMap = fetchAndCacheAccountInformation(listOf(address))
        val accountInformation = accountInformationMap[address]
            ?: return PeraResult.Error(Exception("Failed to fetch account information"))
        val accountAssetHoldingIds = accountInformation.assetHoldings.map { it.assetId }
        fetchAndCacheAssets(accountAssetHoldingIds, false)
        return PeraResult.Success(accountInformation)
    }
}
