package com.algorand.android.transaction_history_component.data.mapper

import com.algorand.android.transaction_history_component.data.model.AssetConfigurationHistoryResponse
import com.algorand.android.transaction_history_component.domain.model.AssetConfigurationHistory

internal interface AssetConfigurationHistoryMapper {
    operator fun invoke(response: AssetConfigurationHistoryResponse?): AssetConfigurationHistory?
}
