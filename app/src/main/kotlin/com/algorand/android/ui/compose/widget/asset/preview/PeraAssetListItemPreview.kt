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

package com.algorand.android.ui.compose.widget.asset.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.common.amount.PeraAmount
import com.algorand.android.ui.common.amount.SimpleFormattedAmount
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.asset.AssetListItem
import com.algorand.android.ui.compose.widget.asset.PeraAssetListItem
import com.algorand.android.ui.compose.widget.asset.icon.AssetIconDrawable
import java.math.BigDecimal

@Preview(showBackground = true)
@Composable
fun PeraAssetListItemPreview(
    @PreviewParameter(PeraAssetListItemPreviewParameterProvider::class) assetListItem: AssetListItem
) {
    PeraTheme {
        PeraAssetListItem(item = assetListItem)
    }
}

private class PeraAssetListItemPreviewParameterProvider : PreviewParameterProvider<AssetListItem> {

    private val longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do"

    val assetListItem = createAssetListItem()

    override val values: Sequence<AssetListItem>
        get() = listOf(
            assetListItem,
            assetListItem.copy(name = longText),
            assetListItem.copy(unitName = longText)
        ).asSequence()

    private fun createAssetListItem(): AssetListItem {
        val primaryAmount = SimpleFormattedAmount("${Currency.ALGO.symbol}0.00")
        val secondaryAmount = SimpleFormattedAmount("0.00")
        return AssetListItem(
            assetId = 12345L,
            name = "ALGO",
            unitName = "ALGO",
            verificationTier = VerificationTierConfiguration.VERIFIED,
            assetIcon = AssetIconDrawable.AlgoDrawable,
            balance = AssetListItem.Balance(
                amount = PeraAmount(BigDecimal.ZERO),
                usdValue = PeraAmount(BigDecimal.ZERO),
                primaryAmountRenderer = AmountRenderer(primaryAmount, AmountRenderer.RenderType.Plain),
                secondaryAmountRenderer = AmountRenderer(secondaryAmount, AmountRenderer.RenderType.Plain)
            )
        )
    }
}
