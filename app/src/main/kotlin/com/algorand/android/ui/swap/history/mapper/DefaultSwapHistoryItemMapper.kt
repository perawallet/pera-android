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

package com.algorand.android.ui.swap.history.mapper

import com.algorand.android.ui.common.amount.DecimalConfig
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.PlainFormattedAmount.SimplePlainFormattedAmount
import com.algorand.android.ui.compose.widget.asset.icon.mapper.AssetIconDrawableMapper
import com.algorand.android.ui.swap.history.model.SwapHistoryItem
import com.algorand.android.utils.TXN_DATE_PATTERN
import com.algorand.android.utils.format
import com.algorand.wallet.swap.domain.model.SwapHistory
import javax.inject.Inject

internal class DefaultSwapHistoryItemMapper @Inject constructor(
    private val assetIconDrawableMapper: AssetIconDrawableMapper
) : SwapHistoryItemMapper {

    override fun invoke(swapHistory: SwapHistory): SwapHistoryItem {
        return with(swapHistory) {
            SwapHistoryItem(
                id = id,
                assetInShortName = swapHistory.assetInDetail.shortName,
                assetInDrawable = assetIconDrawableMapper.map(id, assetInDetail.logoUrl, assetInDetail.shortName),
                assetOutShortName = assetOutDetail.shortName,
                assetOutDrawable = assetIconDrawableMapper.map(id, assetOutDetail.logoUrl, assetOutDetail.shortName),
                amountIn = getFormattedAmount(assetInDetail),
                amountOut = getFormattedAmount(assetOutDetail),
                datetime = swapHistory.datetime?.format(TXN_DATE_PATTERN)
            )
        }
    }

    private fun getFormattedAmount(detail: SwapHistory.AssetDetail): String {
        return with(detail) {
            val normalizedAmount = amount.movePointLeft(decimal)
            SimplePlainFormattedAmount(PeraAmount(normalizedAmount), DecimalConfig(decimal)).getFormattedValue()
        }
    }
}
