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

package com.algorand.android.ui.swap.confirmation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.asset.icon.AssetIcon
import com.algorand.android.ui.swap.confirmation.model.SwapPriceImpact.WarningStatus
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState.Content
import com.algorand.android.ui.swap.confirmation.viewmodel.SwapConfirmationViewModel.ViewState.Content.AssetDetail

private const val MAX_UNIT_NAME_LENGTH = 6

@Composable
fun ColumnScope.AssetAmountContainer(content: Content) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .defaultMinSize(minHeight = 220.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            AssetAmountDetail(content.assetInDetail)
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(color = PeraTheme.colors.layer.grayLighter)
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.to).uppercase(),
                    style = PeraTheme.typography.caption.sansMedium,
                    color = PeraTheme.colors.text.grayLighter
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(color = PeraTheme.colors.layer.grayLighter)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            val hasPriceImpactWarning = content.priceImpact.warningStatus !is WarningStatus.NoWarning
            if (hasPriceImpactWarning) {
                val errorTextColor = PeraTheme.colors.helper.negative
                AssetAmountDetail(content.assetOutDetail, errorTextColor, errorTextColor)
            } else {
                AssetAmountDetail(content.assetOutDetail)
            }
        }
    }
}

@Composable
private fun AssetAmountDetail(
    detail: AssetDetail,
    primaryTextColor: Color = PeraTheme.colors.text.main,
    secondaryTextColor: Color = PeraTheme.colors.text.grayLighter,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        AssetIcon(
            modifier = Modifier.size(40.dp),
            drawable = detail.assetIconDrawable
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = detail.amount.getDisplayValue(),
                style = PeraTheme.typography.body.large.sansMedium,
                color = primaryTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = detail.approximateValue.getDisplayValue(),
                style = PeraTheme.typography.footnote.sans,
                color = secondaryTextColor
            )
        }
        Row(
            modifier = Modifier
                .background(color = PeraTheme.colors.layer.grayLightest, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = detail.shortName.orEmpty().take(MAX_UNIT_NAME_LENGTH),
                style = PeraTheme.typography.body.regular.sans,
                color = PeraTheme.colors.text.main,
            )

            detail.verificationTier.drawableResId?.let { drawableResId ->
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    modifier = Modifier.size(16.dp),
                    imageVector = ImageVector.vectorResource(drawableResId),
                    contentDescription = null
                )
            }
        }
    }
}
