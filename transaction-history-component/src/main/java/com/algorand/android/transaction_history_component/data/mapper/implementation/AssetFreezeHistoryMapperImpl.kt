package com.algorand.android.transaction_history_component.data.mapper.implementation

import com.algorand.android.transaction_history_component.data.mapper.AssetFreezeHistoryMapper
import com.algorand.android.transaction_history_component.data.model.AssetFreezeHistoryResponse
import com.algorand.android.transaction_history_component.domain.model.AssetFreezeHistory
import javax.inject.Inject

internal class AssetFreezeHistoryMapperImpl @Inject constructor() : AssetFreezeHistoryMapper {

    override fun invoke(response: AssetFreezeHistoryResponse?): AssetFreezeHistory? {
        if (response == null) return null
        return AssetFreezeHistory(
            receiverAddress = response.receiverAddress,
            newFreezeStatus = response.newFreezeStatus,
            assetId = response.assetId
        )
    }
}
