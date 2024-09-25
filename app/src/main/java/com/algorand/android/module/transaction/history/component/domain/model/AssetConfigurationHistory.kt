package com.algorand.android.module.transaction.history.component.domain.model

import java.math.BigInteger

data class AssetConfigurationHistory(
    val assetId: Long?,
    val creator: String?,
    val decimals: BigInteger?,
    val defaultFrozen: Boolean?,
    val metadataHash: String?,
    val name: String?,
    val nameB64: String?,
    val maxSupply: BigInteger?,
    val unitName: String?,
    val unitNameB64: String?,
    val url: String?,
    val urlB64: String?
)
