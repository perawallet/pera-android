package com.algorand.android.module.account.core.component.domain.mapper

import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.asset.detail.component.asset.domain.model.detail.AssetDetail
import com.algorand.android.module.parity.domain.model.ParityValue
import java.math.BigInteger
import javax.inject.Inject

internal class OwnedAssetDataMapperImpl @Inject constructor() : OwnedAssetDataMapper {

    override fun invoke(
        assetDetail: AssetDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData {
        return BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData(
            id = assetDetail.id,
            name = assetDetail.fullName,
            shortName = assetDetail.shortName,
            amount = amount,
            formattedAmount = formattedAmount,
            formattedCompactAmount = formattedCompactAmount,
            isAlgo = false,
            decimals = assetDetail.getDecimalsOrZero(),
            creatorPublicKey = assetDetail.creatorAddress,
            usdValue = assetDetail.usdValue,
            isAmountInSelectedCurrencyVisible = assetDetail.usdValue != null && amount.compareTo(BigInteger.ZERO) == 1,
            parityValueInSelectedCurrency = parityValueInSelectedCurrency,
            parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
            prismUrl = assetDetail.logoUri,
            verificationTier = assetDetail.verificationTier,
            optedInAtRound = optedInAtRound
        )
    }
}
