package com.algorand.android.module.account.core.component.domain.mapper

import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier
import com.algorand.android.assetutils.AssetConstants.ALGO_DECIMALS
import com.algorand.android.assetutils.AssetConstants.ALGO_FULL_NAME
import com.algorand.android.assetutils.AssetConstants.ALGO_ID
import com.algorand.android.assetutils.AssetConstants.ALGO_SHORT_NAME
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.formatting.formatAmount
import com.algorand.android.module.parity.domain.model.ParityValue
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class AlgoAssetDataMapperImpl @Inject constructor() : AlgoAssetDataMapper {

    override fun invoke(
        amount: BigInteger,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        usdValue: BigDecimal
    ): BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData {
        return BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData(
            id = ALGO_ID,
            name = ALGO_FULL_NAME,
            shortName = ALGO_SHORT_NAME,
            amount = amount,
            formattedAmount = amount.formatAmount(ALGO_DECIMALS),
            formattedCompactAmount = amount.formatAmount(ALGO_DECIMALS, isCompact = true),
            isAlgo = true,
            decimals = ALGO_DECIMALS,
            creatorPublicKey = "",
            usdValue = usdValue,
            isAmountInSelectedCurrencyVisible = true, // Algo always has a currency value
            parityValueInSelectedCurrency = parityValueInSelectedCurrency,
            parityValueInSecondaryCurrency = parityValueInSecondaryCurrency,
            prismUrl = null, // Algo does not have prism url
            verificationTier = VerificationTier.TRUSTED,
            optedInAtRound = null
        )
    }
}
