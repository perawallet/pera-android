/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.swap.ui.confirmswap.model

import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.designsystem.R
import com.algorand.android.utils.formatAmount
import java.math.BigDecimal
import java.math.RoundingMode

data class SwapPriceRatioProvider(
    private val fromAssetShortName: AssetName,
    private val fromAmount: BigDecimal,
    private val fromAssetDecimal: Int,
    private val toAssetShortName: AssetName,
    private val toAmount: BigDecimal,
    private val toAssetDecimal: Int
) {

    private var isFromToToAssetRatioBeingDisplayed = false
    private val roundingMode = RoundingMode.FLOOR

    fun getSwitchedRatioState(): AnnotatedString {
        isFromToToAssetRatioBeingDisplayed = !isFromToToAssetRatioBeingDisplayed
        return getRatioState()
    }

    fun getRatioState(): AnnotatedString {
        return if (isFromToToAssetRatioBeingDisplayed) {
            getToAssetToFromAssetRatio()
        } else {
            getFromAssetToToAssetRatio()
        }
    }

    private fun getFromAssetToToAssetRatio(): AnnotatedString {
        val ratio = fromAmount
            .movePointLeft(fromAssetDecimal)
            .divide(toAmount.movePointLeft(toAssetDecimal), fromAssetDecimal, roundingMode)
            .formatAmount(fromAssetDecimal, isDecimalFixed = false)
        return createAnnotatedString(
            ratio = ratio,
            firstAssetShortName = fromAssetShortName.assetName,
            secondAssetShortName = toAssetShortName.assetName
        )
    }

    private fun getToAssetToFromAssetRatio(): AnnotatedString {
        val ratio = toAmount
            .movePointLeft(toAssetDecimal)
            .divide(fromAmount.movePointLeft(fromAssetDecimal), toAssetDecimal, roundingMode)
            .formatAmount(toAssetDecimal, isDecimalFixed = false)
        return createAnnotatedString(
            ratio = ratio,
            firstAssetShortName = toAssetShortName.assetName,
            secondAssetShortName = fromAssetShortName.assetName
        )
    }

    private fun createAnnotatedString(
        ratio: String,
        firstAssetShortName: String,
        secondAssetShortName: String
    ): AnnotatedString {
        return AnnotatedString(
            stringResId = R.string.from_to_to_asset_ratio,
            replacementList = listOf(
                "ratio" to ratio,
                "firstAssetShortName" to firstAssetShortName,
                "secondAssetShortName" to secondAssetShortName
            )
        )
    }
}
