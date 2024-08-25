package com.algorand.android.transaction_history_component.data.mapper.implementation

import com.algorand.android.transaction_history_component.data.mapper.AssetTransferHistoryMapper
import com.algorand.android.transaction_history_component.data.model.AssetTransferHistoryResponse
import com.algorand.android.transaction_history_component.domain.model.AssetTransferHistory
import javax.inject.Inject

internal class AssetTransferHistoryMapperImpl @Inject constructor() : AssetTransferHistoryMapper {

    override fun invoke(response: AssetTransferHistoryResponse?): AssetTransferHistory? {
        if (response == null) return null
        return AssetTransferHistory(
            assetId = response.assetId,
            amount = response.amount,
            receiverAddress = response.receiverAddress,
            closeTo = response.closeTo
        )
    }
}
