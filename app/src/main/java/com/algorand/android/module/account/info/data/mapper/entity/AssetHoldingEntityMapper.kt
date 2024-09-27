package com.algorand.android.module.account.info.data.mapper.entity

import com.algorand.android.module.account.info.data.model.AssetHoldingResponse
import com.algorand.android.module.shareddb.accountinformation.model.AssetHoldingEntity

internal interface AssetHoldingEntityMapper {
    operator fun invoke(address: String, response: AssetHoldingResponse): AssetHoldingEntity?
    operator fun invoke(responses: List<Pair<String, AssetHoldingResponse>>): List<AssetHoldingEntity>
}
