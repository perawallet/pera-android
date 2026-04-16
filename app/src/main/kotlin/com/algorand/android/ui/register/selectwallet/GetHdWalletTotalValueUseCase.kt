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

package com.algorand.android.ui.register.selectwallet

import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.domain.GetCompactPrimaryAmountRenderer
import com.algorand.android.ui.common.amount.domain.GetCompactSecondaryAmountRenderer
import com.algorand.wallet.account.local.domain.model.HdWalletSummary
import java.math.BigDecimal
import javax.inject.Inject

internal class GetHdWalletTotalValueUseCase @Inject constructor(
    private val getAccountLite: GetAccountLite,
    private val getCompactPrimaryAmountRenderer: GetCompactPrimaryAmountRenderer,
    private val getCompactSecondaryAmountRenderer: GetCompactSecondaryAmountRenderer
) : GetHdWalletTotalValue {

    override suspend fun invoke(summary: HdWalletSummary): Pair<String, String> {
        var totalPrimary = BigDecimal.ZERO
        var totalSecondary = BigDecimal.ZERO
        summary.addresses.forEach { address ->
            val cachedInfo = getAccountLite(address)?.cachedInfo
            totalPrimary += cachedInfo?.primaryAccountValue ?: BigDecimal.ZERO
            totalSecondary += cachedInfo?.secondaryAccountValue ?: BigDecimal.ZERO
        }
        val renderType = AmountRenderer.RenderType.Plain
        val primaryRenderer = getCompactPrimaryAmountRenderer(PeraAmount(totalPrimary), renderType)
        val secondaryRenderer = getCompactSecondaryAmountRenderer(PeraAmount(totalSecondary), renderType)
        return Pair(primaryRenderer.getDisplayValue(), secondaryRenderer.getDisplayValue())
    }
}
