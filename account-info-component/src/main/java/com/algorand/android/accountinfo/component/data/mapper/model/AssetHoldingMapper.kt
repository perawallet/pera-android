package com.algorand.android.accountinfo.component.data.mapper.model

import com.algorand.android.accountinfo.component.data.model.AssetHoldingResponse
import com.algorand.android.accountinfo.component.domain.model.AssetHolding
import com.algorand.android.shared_db.accountinformation.model.AssetHoldingEntity

internal interface AssetHoldingMapper {
    operator fun invoke(response: AssetHoldingResponse): AssetHolding?
    operator fun invoke(entity: AssetHoldingEntity): AssetHolding
    operator fun invoke(entities: List<AssetHoldingEntity>): List<AssetHolding>
}
