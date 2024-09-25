package com.algorand.android.module.transaction.history.component.data.mapper

import com.algorand.android.module.transaction.history.component.data.model.AssetFreezeHistoryResponse
import com.algorand.android.module.transaction.history.component.domain.model.AssetFreezeHistory

internal interface AssetFreezeHistoryMapper {
    operator fun invoke(response: AssetFreezeHistoryResponse?): AssetFreezeHistory?
}
