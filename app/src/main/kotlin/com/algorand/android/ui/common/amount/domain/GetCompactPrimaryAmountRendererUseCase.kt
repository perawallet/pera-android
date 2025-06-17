/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.common.amount.domain

import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencyName
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.CompactFormattedAmount
import com.algorand.android.ui.common.amount.CompactFormattedAmount.FractionalType.Asset
import com.algorand.android.ui.common.amount.CompactFormattedAmount.FractionalType.Fiat
import com.algorand.android.ui.common.amount.PeraAmount
import javax.inject.Inject

internal class GetCompactPrimaryAmountRendererUseCase @Inject constructor(
    private val isPrimaryCurrencyAlgo: IsPrimaryCurrencyAlgo,
    private val getPrimaryCurrencySymbol: GetPrimaryCurrencySymbol,
    private val getPrimaryCurrencyName: GetPrimaryCurrencyName,
) : GetCompactPrimaryAmountRenderer {

    override fun invoke(amount: PeraAmount, amountRendererType: AmountRenderer.RenderType): AmountRenderer {
        val primaryCurrencySymbol = getPrimaryCurrencySymbol() ?: getPrimaryCurrencyName()
        val fractionalType = if (isPrimaryCurrencyAlgo()) Asset else Fiat
        val formattedAmount = CompactFormattedAmount(amount, fractionalType)
        return AmountRenderer(formattedAmount, amountRendererType, primaryCurrencySymbol)
    }
}
