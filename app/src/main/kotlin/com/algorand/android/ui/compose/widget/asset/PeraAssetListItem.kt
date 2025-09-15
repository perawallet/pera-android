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

package com.algorand.android.ui.compose.widget.asset

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.common.amount.AmountRenderer
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.VerificationTierIcon
import com.algorand.android.ui.compose.widget.asset.icon.AssetIcon

@Composable
fun PeraAssetListItem(modifier: Modifier = Modifier, item: AssetListItem) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AssetIcon(modifier = Modifier.size(40.dp), item.assetIcon)
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    AssetName(modifier = Modifier.weight(1f, false), item.name)
                    Spacer(modifier = Modifier.width(6.dp))
                    VerificationTierIcon(Modifier.size(16.dp), item.verificationTier)
                }
                item.balance?.primaryAmountRenderer?.let {
                    Spacer(modifier = Modifier.width(12.dp))
                    PrimaryAmount(it)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AssetUnitName(modifier = Modifier.weight(1f), item.unitName)
                item.balance?.secondaryAmountRenderer?.let {
                    Spacer(modifier = Modifier.width(12.dp))
                    SecondaryAmount(it)
                }
            }
        }
    }
}

@Composable
private fun AssetName(modifier: Modifier, name: String?) {
    Text(
        modifier = modifier,
        text = name ?: stringResource(R.string.unnamed),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.main,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun AssetUnitName(modifier: Modifier, unitName: String?) {
    Text(
        modifier = modifier,
        text = unitName ?: stringResource(R.string.unnamed),
        style = PeraTheme.typography.footnote.sans,
        color = PeraTheme.colors.text.grayLighter,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PrimaryAmount(amountRenderer: AmountRenderer) {
    Text(
        text = amountRenderer.getDisplayValue(),
        style = PeraTheme.typography.body.regular.sansBold,
        color = PeraTheme.colors.text.main
    )
}

@Composable
private fun SecondaryAmount(amountRenderer: AmountRenderer) {
    Text(
        text = amountRenderer.getDisplayValue(),
        style = PeraTheme.typography.footnote.sans,
        color = PeraTheme.colors.text.gray
    )
}
