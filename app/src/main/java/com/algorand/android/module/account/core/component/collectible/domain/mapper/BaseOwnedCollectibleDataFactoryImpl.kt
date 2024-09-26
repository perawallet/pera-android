package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.module.account.info.domain.model.AssetHolding
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AudioCollectibleDetail
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.ImageCollectibleDetail
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.MixedCollectibleDetail
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.UnsupportedCollectibleDetail
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.VideoCollectibleDetail
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.formatting.FormatAmountByCollectibleFractionalDigit
import com.algorand.android.module.parity.domain.model.ParityValue
import com.algorand.android.module.parity.domain.usecase.primary.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.module.parity.domain.usecase.secondary.GetSecondaryCurrencyAssetParityValue
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import javax.inject.Inject

internal class BaseOwnedCollectibleDataFactoryImpl @Inject constructor(
    private val ownedCollectibleImageDataMapper: OwnedCollectibleImageDataMapper,
    private val ownedCollectibleVideoDataMapper: OwnedCollectibleVideoDataMapper,
    private val ownedCollectibleMixedDataMapper: OwnedCollectibleMixedDataMapper,
    private val ownedCollectibleAudioDataMapper: OwnedCollectibleAudioDataMapper,
    private val ownedCollectibleNotSupportedDataMapper: OwnedCollectibleNotSupportedDataMapper,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue,
    private val formatAmountByCollectibleFractionalDigit: FormatAmountByCollectibleFractionalDigit
) : BaseOwnedCollectibleDataFactory {

    override fun invoke(
        assetHolding: AssetHolding,
        collectibleDetail: CollectibleDetail
    ): BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData {
        val safeDecimal = collectibleDetail.getDecimalsOrZero()
        val parityValueInSelectedCurrency =
            getPrimaryValueInSelectedCurrency(assetHolding, collectibleDetail.usdValue, safeDecimal)
        val parityValueInSecondaryCurrency =
            getSecondaryValueInSelectedCurrency(assetHolding, collectibleDetail.usdValue, safeDecimal)
        return when (collectibleDetail) {
            is ImageCollectibleDetail -> {
                ownedCollectibleImageDataMapper(
                    collectibleDetail = collectibleDetail,
                    amount = assetHolding.amount,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(assetHolding.amount, safeDecimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(
                        assetHolding.amount,
                        safeDecimal,
                        true
                    ),
                    parityValueInSelectedCurrency = parityValueInSelectedCurrency,
                    parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
                    optedInAtRound = assetHolding.optedInAtRound
                )
            }
            is VideoCollectibleDetail -> {
                ownedCollectibleVideoDataMapper(
                    collectibleDetail = collectibleDetail,
                    amount = assetHolding.amount,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(assetHolding.amount, safeDecimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(
                        assetHolding.amount,
                        safeDecimal,
                        true
                    ),
                    parityValueInSelectedCurrency = parityValueInSelectedCurrency,
                    parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
                    optedInAtRound = assetHolding.optedInAtRound
                )
            }
            is MixedCollectibleDetail -> {
                ownedCollectibleMixedDataMapper(
                    collectibleDetail = collectibleDetail,
                    amount = assetHolding.amount,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(assetHolding.amount, safeDecimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(
                        assetHolding.amount,
                        safeDecimal,
                        true
                    ),
                    parityValueInSelectedCurrency = parityValueInSelectedCurrency,
                    parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
                    optedInAtRound = assetHolding.optedInAtRound
                )
            }
            is AudioCollectibleDetail -> {
                ownedCollectibleAudioDataMapper(
                    collectibleDetail = collectibleDetail,
                    amount = assetHolding.amount,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(assetHolding.amount, safeDecimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(
                        assetHolding.amount,
                        safeDecimal,
                        true
                    ),
                    parityValueInSelectedCurrency = parityValueInSelectedCurrency,
                    parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
                    optedInAtRound = assetHolding.optedInAtRound
                )
            }
            is UnsupportedCollectibleDetail -> {
                ownedCollectibleNotSupportedDataMapper(
                    collectibleDetail = collectibleDetail,
                    amount = assetHolding.amount,
                    formattedAmount = formatAmountByCollectibleFractionalDigit(assetHolding.amount, safeDecimal),
                    formattedCompactAmount = formatAmountByCollectibleFractionalDigit(
                        assetHolding.amount,
                        safeDecimal,
                        true
                    ),
                    parityValueInSelectedCurrency = parityValueInSelectedCurrency,
                    parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
                    optedInAtRound = assetHolding.optedInAtRound
                )
            }
        }
    }

    private fun getPrimaryValueInSelectedCurrency(
        assetHolding: AssetHolding,
        usdValue: BigDecimal?,
        decimals: Int
    ): ParityValue {
        return getPrimaryCurrencyAssetParityValue(usdValue ?: ZERO, decimals, assetHolding.amount)
    }

    private fun getSecondaryValueInSelectedCurrency(
        assetHolding: AssetHolding,
        usdValue: BigDecimal?,
        decimals: Int
    ): ParityValue {
        return getSecondaryCurrencyAssetParityValue(usdValue ?: ZERO, decimals, assetHolding.amount)
    }
}
