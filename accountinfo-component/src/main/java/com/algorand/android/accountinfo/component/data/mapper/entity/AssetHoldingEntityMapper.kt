package com.algorand.android.accountinfo.component.data.mapper.entity

import com.algorand.android.accountinfo.component.data.model.AssetHoldingResponse
import com.algorand.android.shared_db.accountinformation.model.AssetHoldingEntity

internal interface AssetHoldingEntityMapper {
    operator fun invoke(address: String, response: AssetHoldingResponse): AssetHoldingEntity?
    operator fun invoke(responses: List<Pair<String, AssetHoldingResponse>>): List<AssetHoldingEntity>
}
