package com.algorand.android.modules.parity.domain.usecase

import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.parity.domain.model.ParityDisplayValue
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.isGreaterThan
import com.algorand.android.utils.orZero
import com.algorand.wallet.asset.domain.model.AssetLite
import java.math.BigInteger
import javax.inject.Inject

internal class GetParityDisplayValueUseCase @Inject constructor(
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue
) : GetParityDisplayValue {

    override fun invoke(assetLite: AssetLite): ParityDisplayValue {
        val selectedParityValue = assetLite.getPrimaryParityValue()
        val secondaryParityValue = assetLite.getSecondaryParityValue()

        return ParityDisplayValue(
            primaryParityValue = assetLite.getPrimaryParityValue(selectedParityValue, secondaryParityValue),
            secondaryParityValue = secondaryParityValue,
            formattedAmount = assetLite.amount.formatAmount(assetLite.decimal),
            formattedCompactAmount = assetLite.amount.formatAmount(assetLite.decimal, isCompact = true),
            isAmountInSelectedCurrencyVisible = assetLite.usdValue != null && assetLite.amount isGreaterThan BigInteger.ZERO
        )
    }

    private fun AssetLite.getPrimaryParityValue(
        selectedParityValue: ParityValue,
        secondaryParityValue: ParityValue
    ): ParityValue {
        return if (isAlgo && selectedParityValue.selectedCurrencySymbol == Currency.ALGO.symbol) {
            secondaryParityValue
        } else {
            selectedParityValue
        }
    }

    private fun AssetLite.getPrimaryParityValue(): ParityValue {
        return getPrimaryCurrencyAssetParityValue(amount, usdValue.orZero(), decimal)
    }

    private fun AssetLite.getSecondaryParityValue(): ParityValue {
        return getSecondaryCurrencyAssetParityValue(amount, usdValue.orZero(), decimal)
    }
}
