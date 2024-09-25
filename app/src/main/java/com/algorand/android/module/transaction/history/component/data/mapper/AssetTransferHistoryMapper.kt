package com.algorand.android.module.transaction.history.component.data.mapper

import com.algorand.android.module.transaction.history.component.data.model.AssetTransferHistoryResponse
import com.algorand.android.module.transaction.history.component.domain.model.AssetTransferHistory

internal interface AssetTransferHistoryMapper {

    operator fun invoke(response: AssetTransferHistoryResponse?): AssetTransferHistory?
}
