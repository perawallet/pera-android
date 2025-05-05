package com.algorand.wallet.asset.domain.model

import com.algorand.wallet.account.info.domain.model.AssetStatus
import java.math.BigDecimal
import java.math.BigInteger

data class AssetLite(
    val address: String,
    val amount: BigInteger,
    val decimal: Int,
    val usdValue: BigDecimal?,
    val totalUsdValue: BigDecimal?,
    val assetId: Long,
    val name: String?,
    val shortName: String?,
    val type: Type,
    val verificationTier: VerificationTier,
    val assetStatus: AssetStatus
) {

    val logoUrl: String?
        get() = type.logoUrl

    sealed interface Type {

        val logoUrl: String?

        data class Asset(override val logoUrl: String?) : Type

        data class Collectible(
            override val logoUrl: String?,
            val name: String,
            val collectionName: String?
        ) : Type
    }
}
