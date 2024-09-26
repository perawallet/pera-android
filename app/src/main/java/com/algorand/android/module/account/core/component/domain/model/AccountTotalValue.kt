package com.algorand.android.module.account.core.component.domain.model

import java.math.BigDecimal

data class AccountTotalValue(
    val primaryAccountValue: BigDecimal,
    val secondaryAccountValue: BigDecimal,
    val assetCount: Int
)
