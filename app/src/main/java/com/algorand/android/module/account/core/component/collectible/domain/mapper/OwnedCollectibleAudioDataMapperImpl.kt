package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AudioCollectibleDetail
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleAudioData
import com.algorand.android.module.parity.domain.model.ParityValue
import java.math.BigInteger
import javax.inject.Inject

internal class OwnedCollectibleAudioDataMapperImpl @Inject constructor() : OwnedCollectibleAudioDataMapper {

    override fun invoke(
        collectibleDetail: AudioCollectibleDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): OwnedCollectibleAudioData {
        return OwnedCollectibleAudioData(
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
