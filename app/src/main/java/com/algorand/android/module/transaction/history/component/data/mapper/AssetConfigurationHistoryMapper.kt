package com.algorand.android.module.transaction.history.component.data.mapper

import com.algorand.android.module.transaction.history.component.data.model.AssetConfigurationHistoryResponse
import com.algorand.android.module.transaction.history.component.domain.model.AssetConfigurationHistory

internal interface AssetConfigurationHistoryMapper {
    operator fun invoke(response: AssetConfigurationHistoryResponse?): AssetConfigurationHistory?
}
