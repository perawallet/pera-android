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

package com.algorand.android.ui.swap.topfive.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.asset.icon.AssetIcons
import com.algorand.android.ui.compose.widget.modifier.shimmer
import com.algorand.android.ui.swap.topfive.model.TopSwapPairItem
import com.algorand.android.ui.swap.topfive.viewmodel.TopSwapPairsViewModel
import com.algorand.android.ui.swap.topfive.viewmodel.TopSwapPairsViewModel.ViewState

@Composable
fun TopSwapPairsContainer(viewModel: TopSwapPairsViewModel) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value
    if (viewState is ViewState.Idle) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Header()
        Spacer(modifier = Modifier.height(12.dp))
        when (viewState) {
            ViewState.Idle -> Unit
            ViewState.Error -> ErrorState()
            ViewState.Loading -> LoadingState()
            ViewState.Empty -> EmptyState()
            is ViewState.Content -> ContentState(viewState.topSwapPairItems)
        }
    }
}

@Composable
private fun ErrorState() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            tint = PeraTheme.colors.helper.negative,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.unable_to_load_top_five),
            color = PeraTheme.colors.helper.negative,
            style = PeraTheme.typography.footnote.sansMedium
        )
    }
}

@Composable
private fun EmptyState() {
    Text(
        text = stringResource(R.string.no_swap_activity_in_the),
        color = PeraTheme.colors.text.gray,
        style = PeraTheme.typography.footnote.sansMedium
    )
}

@Composable
private fun LoadingState() {
    val shimmerModifier = Modifier
        .fillMaxWidth()
        .height(68.dp)
        .shimmer()
    Column {
        Box(modifier = shimmerModifier)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = shimmerModifier)
    }
}

@Composable
private fun ContentState(topSwapPairItems: List<TopSwapPairItem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        topSwapPairItems.forEachIndexed { index, detail ->
            SwapPairItem(index, detail)
            if (index < topSwapPairItems.size - 1) {
                Box(
                    modifier = Modifier
                        .background(color = PeraTheme.colors.layer.grayLighter)
                        .fillMaxWidth()
                        .height(1.dp)
                )
            }
        }
    }
}

@Composable
private fun SwapPairItem(index: Int, detail: TopSwapPairItem) {
    Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.defaultMinSize(minWidth = 36.dp),
            text = "${index + 1}.",
            style = PeraTheme.typography.body.large.sans,
            color = PeraTheme.colors.text.gray
        )
        AssetIcons(firstDrawable = detail.assetInIconDrawable, secondDrawable = detail.assetOutIconDrawable)
        Spacer(modifier = Modifier.width(8.dp))
        Row(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(
                    R.string.asset_to_asset_formatted,
                    detail.assetInShortName.orEmpty(),
                    detail.assetOutShortName.orEmpty()
                ),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = detail.volume.getDisplayValue(),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray
        )
    }
}

@Composable
private fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.top_five_swaps),
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
        Text(
            modifier = Modifier.align(Alignment.CenterEnd),
            text = stringResource(R.string.volume_24h),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.gray
        )
    }
}
