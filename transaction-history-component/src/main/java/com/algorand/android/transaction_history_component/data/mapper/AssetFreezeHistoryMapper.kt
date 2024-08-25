package com.algorand.android.transaction_history_component.data.mapper

import com.algorand.android.transaction_history_component.data.model.AssetFreezeHistoryResponse
import com.algorand.android.transaction_history_component.domain.model.AssetFreezeHistory

internal interface AssetFreezeHistoryMapper {
    operator fun invoke(response: AssetFreezeHistoryResponse?): AssetFreezeHistory?
}
