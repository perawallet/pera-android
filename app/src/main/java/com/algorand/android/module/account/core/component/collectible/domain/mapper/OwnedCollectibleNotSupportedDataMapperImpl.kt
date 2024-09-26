package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.UnsupportedCollectibleDetail
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleUnsupportedData
import com.algorand.android.parity.domain.model.ParityValue
import java.math.BigInteger
import javax.inject.Inject

internal class OwnedCollectibleNotSupportedDataMapperImpl @Inject constructor() :
    OwnedCollectibleNotSupportedDataMapper {

    override fun invoke(
        collectibleDetail: UnsupportedCollectibleDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): OwnedCollectibleUnsupportedData {
        return OwnedCollectibleUnsupportedData(
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
