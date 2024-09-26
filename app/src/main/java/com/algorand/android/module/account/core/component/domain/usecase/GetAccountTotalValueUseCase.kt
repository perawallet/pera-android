package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAsset
import com.algorand.android.module.account.core.component.domain.model.AccountTotalValue
import com.algorand.android.parity.domain.model.ParityValue
import com.algorand.android.parity.domain.usecase.GetAlgoAmountValue
import com.algorand.android.parity.domain.usecase.primary.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.parity.domain.usecase.secondary.GetSecondaryCurrencyAssetParityValue
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

internal class GetAccountTotalValueUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation,
    private val getAsset: GetAsset,
    private val getPrimaryCurrencyAssetParityValue: GetPrimaryCurrencyAssetParityValue,
    private val getSecondaryCurrencyAssetParityValue: GetSecondaryCurrencyAssetParityValue,
    private val getAlgoAmountValue: GetAlgoAmountValue
) : GetAccountTotalValue {

    override suspend fun invoke(address: String, includeAlgo: Boolean): AccountTotalValue {
        val accountInformation = getAccountInformation(address) ?: return getDefaultAccountValue()

        var primaryAccountValue = BigDecimal.ZERO
        var secondaryAccountValue = BigDecimal.ZERO
        var assetCount = 0

        accountInformation.assetHoldings.forEach { assetHolding ->
            val assetInformation = getAsset(assetHolding.assetId)
            if (assetInformation != null) {
                val (primaryParityValue, secondaryParityValue) = getAssetParityValue(
                    fractionDecimals = assetInformation.getDecimalsOrZero(),
                    assetAmount = assetHolding.amount,
                    assetUsdValue = assetInformation.usdValue ?: BigDecimal.ZERO
                )
                primaryAccountValue += primaryParityValue.amountAsCurrency
                secondaryAccountValue += secondaryParityValue.amountAsCurrency
                assetCount++
            }
        }

        if (includeAlgo) {
            val algoAmountValue = getAlgoAmountValue(accountInformation.amount)
            primaryAccountValue += algoAmountValue.parityValueInSelectedCurrency.amountAsCurrency
            secondaryAccountValue += algoAmountValue.parityValueInSecondaryCurrency.amountAsCurrency
            assetCount++
        }

        return AccountTotalValue(
            primaryAccountValue = primaryAccountValue,
            secondaryAccountValue = secondaryAccountValue,
            assetCount = assetCount
        )
    }

    private fun getDefaultAccountValue(): AccountTotalValue {
        return AccountTotalValue(BigDecimal.ZERO, BigDecimal.ZERO, 0)
    }

    private fun getAssetParityValue(
        fractionDecimals: Int?,
        assetAmount: BigInteger,
        assetUsdValue: BigDecimal,
    ): Pair<ParityValue, ParityValue> {
        val safeDecimal = fractionDecimals ?: 0
        val assetParityValueInSelectedCurrency = getPrimaryCurrencyAssetParityValue(
            assetUsdValue,
            safeDecimal,
            assetAmount
        )
        val assetParityValueInSecondaryCurrency = getSecondaryCurrencyAssetParityValue(
            assetUsdValue,
            safeDecimal,
            assetAmount
        )
        return Pair(assetParityValueInSelectedCurrency, assetParityValueInSecondaryCurrency)
    }
}
