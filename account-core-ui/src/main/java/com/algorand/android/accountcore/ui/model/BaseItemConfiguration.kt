package com.algorand.android.accountcore.ui.model

import com.algorand.android.core.component.detail.domain.model.AccountType
import com.algorand.android.drawableui.asset.BaseAssetDrawableProvider
import java.math.BigDecimal

sealed class BaseItemConfiguration {

    abstract val primaryValueText: String?
    abstract val secondaryValueText: String?

    abstract val primaryValue: BigDecimal?
    abstract val secondaryValue: BigDecimal?

    abstract val actionButtonConfiguration: ButtonConfiguration?
    abstract val checkButtonConfiguration: ButtonConfiguration?
    abstract val dragButtonConfiguration: ButtonConfiguration?

    data class AccountItemConfiguration(
        override val primaryValueText: String? = null,
        override val secondaryValueText: String? = null,
        override val actionButtonConfiguration: ButtonConfiguration? = null,
        override val checkButtonConfiguration: ButtonConfiguration? = null,
        override val dragButtonConfiguration: ButtonConfiguration? = null,
        override val primaryValue: BigDecimal? = null,
        override val secondaryValue: BigDecimal? = null,
        val showWarning: Boolean? = null,
        val accountAddress: String,
        val accountIconDrawablePreview: AccountIconDrawablePreview? = null,
        val governorIconResource: GovernorIconResource? = null,
        val accountDisplayName: AccountDisplayName? = null,
        val accountType: AccountType? = null,
        val accountAssetCount: Int? = null,
        val startSmallIconResource: Int? = null
    ) : BaseItemConfiguration()

    sealed class BaseAssetItemConfiguration : BaseItemConfiguration() {

        abstract val verificationTierConfiguration: VerificationTierConfiguration?
        abstract val assetId: Long
        abstract val assetIconDrawableProvider: BaseAssetDrawableProvider?
        abstract val primaryAssetName: AssetName?
        abstract val secondaryAssetName: AssetName?
        abstract val showWithAssetId: Boolean?

        data class AssetItemConfiguration(
            override val assetId: Long,
            override val primaryValue: BigDecimal? = null,
            override val primaryValueText: String? = null,
            override val secondaryValue: BigDecimal? = null,
            override val secondaryValueText: String? = null,
            override val primaryAssetName: AssetName? = null,
            override val secondaryAssetName: AssetName? = null,
            override val verificationTierConfiguration: VerificationTierConfiguration? = null,
            override val assetIconDrawableProvider: BaseAssetDrawableProvider? = null,
            override val showWithAssetId: Boolean? = null,
            override val checkButtonConfiguration: ButtonConfiguration? = null,
            override val dragButtonConfiguration: ButtonConfiguration? = null,
            override val actionButtonConfiguration: ButtonConfiguration? = null,
            val isPending: Boolean? = null
        ) : BaseAssetItemConfiguration()

        data class CollectibleItemConfiguration(
            override val assetId: Long,
            override val primaryValue: BigDecimal? = null,
            override val primaryValueText: String? = null,
            override val secondaryValue: BigDecimal? = null,
            override val secondaryValueText: String? = null,
            override val primaryAssetName: AssetName? = null,
            override val secondaryAssetName: AssetName? = null,
            override val verificationTierConfiguration: VerificationTierConfiguration? = null,
            override val assetIconDrawableProvider: BaseAssetDrawableProvider? = null,
            override val showWithAssetId: Boolean? = null,
            override val checkButtonConfiguration: ButtonConfiguration? = null,
            override val dragButtonConfiguration: ButtonConfiguration? = null,
            override val actionButtonConfiguration: ButtonConfiguration? = null
        ) : BaseAssetItemConfiguration()
    }
}
