package com.algorand.wallet.asset.pricehistory.domain.model

import java.math.BigDecimal
import java.time.OffsetDateTime

data class AssetPriceHistory(
    val datetime: OffsetDateTime,
    val price: BigDecimal
)
