package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.MixedCollectibleDetail
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleMixedData
import com.algorand.android.parity.domain.model.ParityValue
import java.math.BigInteger
import javax.inject.Inject

internal class OwnedCollectibleMixedDataMapperImpl @Inject constructor() : OwnedCollectibleMixedDataMapper {

    override fun invoke(
        collectibleDetail: MixedCollectibleDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): OwnedCollectibleMixedData {
        return OwnedCollectibleMixedData(
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
