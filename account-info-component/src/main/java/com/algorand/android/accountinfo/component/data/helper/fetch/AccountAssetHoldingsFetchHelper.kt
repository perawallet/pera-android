package com.algorand.android.accountinfo.component.data.helper.fetch

import com.algorand.android.accountinfo.component.data.model.AssetHoldingResponse
import com.algorand.android.foundation.PeraResult

internal interface AccountAssetHoldingsFetchHelper {
    suspend fun fetchAccountAssetHoldings(accountAddress: String): PeraResult<List<AssetHoldingResponse>>
}
