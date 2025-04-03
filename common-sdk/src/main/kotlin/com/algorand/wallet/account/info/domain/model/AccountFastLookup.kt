package com.algorand.wallet.account.info.domain.model

import java.math.BigDecimal

data class AccountFastLookup(
    val algoValue: BigDecimal,
    val usdValue: BigDecimal,
    val accountExists: Boolean
)
