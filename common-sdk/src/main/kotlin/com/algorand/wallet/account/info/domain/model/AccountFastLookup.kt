package com.algorand.wallet.account.info.domain.model

data class AccountFastLookup(
    val algoValue: String,
    val usdValue: String,
    val calculationType: String,
    val accountExists: Boolean
)
