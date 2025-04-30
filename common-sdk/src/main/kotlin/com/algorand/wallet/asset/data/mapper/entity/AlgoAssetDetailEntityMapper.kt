package com.algorand.wallet.asset.data.mapper.entity

import com.algorand.wallet.asset.data.database.model.AssetDetailEntity
import java.math.BigDecimal

internal interface AlgoAssetDetailEntityMapper {
    operator fun invoke(usdValue: BigDecimal?): AssetDetailEntity
}
