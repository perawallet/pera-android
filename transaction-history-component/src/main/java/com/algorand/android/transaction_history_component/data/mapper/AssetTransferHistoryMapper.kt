package com.algorand.android.transaction_history_component.data.mapper

import com.algorand.android.transaction_history_component.data.model.AssetTransferHistoryResponse
import com.algorand.android.transaction_history_component.domain.model.AssetTransferHistory

internal interface AssetTransferHistoryMapper {

    operator fun invoke(response: AssetTransferHistoryResponse?): AssetTransferHistory?
}
