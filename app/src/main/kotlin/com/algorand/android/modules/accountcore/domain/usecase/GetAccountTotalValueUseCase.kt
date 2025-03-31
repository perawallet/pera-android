/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountcore.domain.usecase

import com.algorand.android.modules.accountcore.domain.model.AccountTotalValue
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.modules.parity.domain.usecase.GetAlgoAmountValue
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.utils.orZero
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import com.algorand.wallet.asset.domain.usecase.GetAsset
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
        return getAccountTotalValue(accountInformation, includeAlgo)
    }

    override suspend fun invoke(accountInformation: AccountInformation, includeAlgo: Boolean): AccountTotalValue {
        return getAccountTotalValue(accountInformation, includeAlgo)
    }

    private suspend fun getAccountTotalValue(
        accountInformation: AccountInformation,
        includeAlgo: Boolean
    ): AccountTotalValue {
        var primaryAccountValue = BigDecimal.ZERO
        var secondaryAccountValue = BigDecimal.ZERO
        var assetCount = 0
        accountInformation.assetHoldings.forEach { assetHolding ->
            val assetInformation = getAsset(assetHolding.assetId)
            if (assetInformation != null) {
                val (primaryParityValue, secondaryParityValue) = getAssetParityValue(
                    fractionDecimals = assetInformation.getDecimalsOrZero(),
                    assetAmount = assetHolding.amount,
                    assetUsdValue = assetInformation.usdValue.orZero()
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
            assetAmount,
            assetUsdValue,
            safeDecimal
        )
        val assetParityValueInSecondaryCurrency = getSecondaryCurrencyAssetParityValue(
            assetAmount,
            assetUsdValue,
            safeDecimal
        )
        return Pair(assetParityValueInSelectedCurrency, assetParityValueInSecondaryCurrency)
    }
}
