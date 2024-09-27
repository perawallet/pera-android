package com.algorand.android.module.account.info.data.mapper.model

import com.algorand.android.module.account.info.data.model.AssetHoldingResponse
import com.algorand.android.module.account.info.domain.model.AssetHolding
import com.algorand.android.module.shareddb.accountinformation.model.AssetHoldingEntity

internal interface AssetHoldingMapper {
    operator fun invoke(response: AssetHoldingResponse): AssetHolding?
    operator fun invoke(entity: AssetHoldingEntity): AssetHolding
    operator fun invoke(entities: List<AssetHoldingEntity>): List<AssetHolding>
}
