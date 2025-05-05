package com.algorand.wallet.account.info.data.mapper.model

import com.algorand.wallet.account.info.data.database.model.AssetStatusEntity
import com.algorand.wallet.account.info.domain.model.AssetStatus

internal fun interface AssetStatusMapper {
    operator fun invoke(entity: AssetStatusEntity): AssetStatus
}
