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

package com.algorand.android.ui.swap.history.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.asset.icon.AssetPairIcons
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.swap.history.model.SwapHistoryItem
import kotlinx.coroutines.flow.Flow

@Composable
fun PagingSwapHistoryList(
    modifier: Modifier = Modifier,
    pagingList: Flow<PagingData<SwapHistoryItem>>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onSwapItemClick: (SwapHistoryItem) -> Unit
) {
    val swapHistory = pagingList.collectAsLazyPagingItems()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = contentPadding,
    ) {
        items(
            count = swapHistory.itemCount,
            key = { index -> swapHistory[index]?.id ?: index }
        ) { index ->
            swapHistory[index]?.let { swapHistoryItem ->
                SwapHistoryListItem(swapHistoryItem, onSwapItemClick)
                if (index in 0 until swapHistory.itemCount - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = PeraTheme.colors.layer.grayLighter
                    )
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun SwapHistoryListItem(item: SwapHistoryItem, onClick: (SwapHistoryItem) -> Unit) {
    Row(modifier = Modifier.clickableNoRipple { onClick(item) }, verticalAlignment = Alignment.CenterVertically) {
        AssetPairIcons(firstDrawable = item.assetInDrawable, secondDrawable = item.assetOutDrawable)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = getItemDescription(item),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            modifier = Modifier
                .rotate(45f)
                .size(24.dp),
            painter = painterResource(R.drawable.ic_arrow_up_line),
            contentDescription = null,
            tint = PeraTheme.colors.text.grayLighter
        )
    }
}

@Composable
private fun getItemDescription(item: SwapHistoryItem): AnnotatedString {
    val boldWeight = PeraTheme.typography.body.regular.sansBold.fontWeight
    val dateColor = PeraTheme.colors.text.gray
    val mainSpan = SpanStyle(
        color = PeraTheme.colors.text.main,
        fontStyle = PeraTheme.typography.body.regular.sans.fontStyle
    )
    val descriptionTextTemplate = stringResource(R.string.swapped_asset_for_asset_formatted)
    return remember(item) {
        buildAnnotatedString {
            val assetInAmount = "${item.amountIn} ${item.assetInShortName}"
            val assetOutAmount = "${item.amountOut} ${item.assetOutShortName}"
            val descriptionText = String.format(descriptionTextTemplate, assetInAmount, assetOutAmount)
            val startIndex = descriptionText.indexOf(assetOutAmount)
            val endIndex = startIndex + assetOutAmount.length

            withStyle(style = mainSpan) {
                append(descriptionText.take(startIndex))
            }

            withStyle(style = mainSpan.copy(fontWeight = boldWeight)) {
                append(assetOutAmount)
            }
            withStyle(style = mainSpan) {
                append(descriptionText.substring(endIndex, descriptionText.length))
            }

            if (item.datetime != null) {
                withStyle(style = mainSpan.copy(color = dateColor)) {
                    append(" • ")
                    append(item.datetime)
                }
            }
        }
    }
}
