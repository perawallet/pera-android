package com.algorand.android.module.account.info.data.helper.fetch

import com.algorand.android.module.account.info.data.model.AssetHoldingResponse
import com.algorand.android.foundation.PeraResult

internal interface AccountAssetHoldingsFetchHelper {
    suspend fun fetchAccountAssetHoldings(accountAddress: String): PeraResult<List<AssetHoldingResponse>>
}
