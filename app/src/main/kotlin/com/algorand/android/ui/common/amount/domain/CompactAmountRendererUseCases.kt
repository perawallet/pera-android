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

import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.wallet.asset.domain.model.Asset
import java.math.BigDecimal
import java.math.BigInteger

fun interface GetCompactPrimaryAmountRenderer {
    operator fun invoke(amount: PeraAmount, amountRendererType: AmountRenderer.RenderType): AmountRenderer
}

fun interface GetCompactSecondaryAmountRenderer {
    operator fun invoke(amount: PeraAmount, amountRendererType: AmountRenderer.RenderType): AmountRenderer
}

fun interface GetCompactPrimaryFiatAmountRenderer {
    operator fun invoke(amount: PeraAmount, amountRendererType: AmountRenderer.RenderType): AmountRenderer
}

fun interface GetPrimaryFiatAmountRenderer {
    operator fun invoke(usdValue: BigDecimal, amount: BigDecimal, renderType: AmountRenderer.RenderType): AmountRenderer
}

fun interface GetPrimaryCurrencyAmountRenderer {
    operator fun invoke(usdValue: BigDecimal, amount: BigDecimal, renderType: AmountRenderer.RenderType): AmountRenderer
}

fun interface GetSecondaryCurrencyAmountRenderer {
    operator fun invoke(asset: Asset, amount: BigInteger, renderType: AmountRenderer.RenderType): AmountRenderer
}
