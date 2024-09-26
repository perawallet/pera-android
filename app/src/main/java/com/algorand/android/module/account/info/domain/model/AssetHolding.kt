package com.algorand.android.module.account.info.domain.model

import java.math.BigInteger

data class AssetHolding(
    val amount: BigInteger,
    val assetId: Long,
    val isDeleted: Boolean,
    val isFrozen: Boolean,
    val optedInAtRound: Long?,
    val optedOutAtRound: Long?,
    val status: AssetStatus
)
