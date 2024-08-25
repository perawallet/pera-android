package com.algorand.android.core.component.collectible.domain.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.detail.ImageCollectibleDetail
import com.algorand.android.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleImageData
import com.algorand.android.parity.domain.model.ParityValue
import java.math.BigInteger
import javax.inject.Inject

internal class OwnedCollectibleImageDataMapperImpl @Inject constructor() : OwnedCollectibleImageDataMapper {

    override fun invoke(
        collectibleDetail: ImageCollectibleDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): OwnedCollectibleImageData {
        return OwnedCollectibleImageData(
            id = collectibleDetail.id,
            name = collectibleDetail.fullName,
            shortName = collectibleDetail.shortName,
            amount = amount,
            formattedAmount = formattedAmount,
            formattedCompactAmount = formattedCompactAmount,
            parityValueInSelectedCurrency = parityValueInSelectedCurrency,
            parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
            isAlgo = false,
            decimals = collectibleDetail.getDecimalsOrZero(),
            creatorPublicKey = collectibleDetail.creatorAddress,
            usdValue = collectibleDetail.usdValue,
            isAmountInSelectedCurrencyVisible = collectibleDetail.hasUsdValue(),
            prismUrl = collectibleDetail.prismUrl,
            collectibleName = collectibleDetail.title,
            collectionName = collectibleDetail.collectionName,
            optedInAtRound = optedInAtRound
        )
    }
}
