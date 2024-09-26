package com.algorand.android.module.account.info.data.helper.fetch

import com.algorand.android.module.account.info.data.model.AssetHoldingResponse
import com.algorand.android.module.account.info.data.service.IndexerApi
import com.algorand.android.foundation.PeraResult
import com.algorand.android.network_utils.request
import javax.inject.Inject

internal class AccountAssetHoldingsFetchHelperImpl @Inject constructor(
    private val indexerApi: IndexerApi
) : AccountAssetHoldingsFetchHelper {

    override suspend fun fetchAccountAssetHoldings(accountAddress: String): PeraResult<List<AssetHoldingResponse>> {
        return fetch(accountAddress, null, mutableListOf())
    }

    private suspend fun fetch(
        address: String,
        nextToken: String?,
        holdings: MutableList<AssetHoldingResponse>
    ): PeraResult<List<AssetHoldingResponse>> {
        return request { indexerApi.getAccountAssets(address, ASSET_FETCH_LIMIT_PER_PAGE, nextToken) }.use(
            onSuccess = { response ->
                holdings.addAll(response.assets.orEmpty())
                if (response.nextToken != null && !response.assets.isNullOrEmpty()) {
                    fetch(address, response.nextToken, holdings)
                } else {
                    PeraResult.Success(holdings)
                }
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    companion object {
        private const val ASSET_FETCH_LIMIT_PER_PAGE = 5000
    }
}
