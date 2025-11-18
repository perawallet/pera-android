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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.asset.icon.AssetPairIcons
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.modifier.shimmer
import com.algorand.android.ui.swap.history.model.SwapPairHistoryItem
import com.algorand.android.ui.swap.history.viewmodel.SwapPairHistoryViewModel
import com.algorand.android.ui.swap.history.viewmodel.SwapPairHistoryViewModel.ViewState.Content
import com.algorand.android.ui.swap.history.viewmodel.SwapPairHistoryViewModel.ViewState.Empty
import com.algorand.android.ui.swap.history.viewmodel.SwapPairHistoryViewModel.ViewState.Error
import com.algorand.android.ui.swap.history.viewmodel.SwapPairHistoryViewModel.ViewState.Idle
import com.algorand.android.ui.swap.history.viewmodel.SwapPairHistoryViewModel.ViewState.Loading
import kotlinx.coroutines.launch

@Composable
fun SwapPairHistoryWidget(
    viewModel: SwapPairHistoryViewModel,
    onSeeAllClick: () -> Unit,
    onSwapPairClick: (Long, Long) -> Unit
) {
    Column {
        val viewState = viewModel.state.collectAsStateWithLifecycle()
        if (viewState.value is Idle) return
        var isSeeAllVisible by remember(viewState.value) { mutableStateOf(viewState.value is Content) }
        val scope = rememberCoroutineScope()
        Row(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.swap_history),
                style = PeraTheme.typography.body.regular.sansMedium,
                color = PeraTheme.colors.text.main
            )
            if (isSeeAllVisible) {
                Text(
                    modifier = Modifier.clickableNoRipple {
                        scope.launch { viewModel.logSeeAllClick() }
                        onSeeAllClick()
                    },
                    text = stringResource(R.string.see_all),
                    style = PeraTheme.typography.body.regular.sansMedium,
                    color = PeraTheme.colors.helper.positive
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (val state = viewState.value) {
            Idle -> Unit
            Empty -> EmptyState()
            Error -> ErrorState()
            Loading -> LoadingState()
            is Content -> ContentState(state.pairs) { pair ->
                scope.launch { viewModel.logPairSelected(pair.assetInShortName, pair.assetOutShortName) }
                onSwapPairClick(pair.assetInId, pair.assetOutId)
            }
        }
    }
}

@Composable
private fun ContentState(pairs: List<SwapPairHistoryItem>, onSwapPairClick: (SwapPairHistoryItem) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(pairs) { pair ->
            val assetPairText = stringResource(
                R.string.asset_to_asset_formatted,
                pair.assetInShortName.orEmpty(),
                pair.assetOutShortName.orEmpty()
            )
            val cornerShape = RoundedCornerShape(16.dp)
            Row(
                modifier = Modifier
                    .clickableNoRipple { onSwapPairClick(pair) }
                    .shadow(1.dp, shape = cornerShape)
                    .border(1.dp, color = PeraTheme.colors.button.strokeColor, shape = cornerShape)
                    .background(color = PeraTheme.colors.background.primary, shape = cornerShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssetPairIcons(firstDrawable = pair.assetInIconDrawable, secondDrawable = pair.assetOutIconDrawable)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = assetPairText,
                    style = PeraTheme.typography.body.regular.sansMedium,
                    color = PeraTheme.colors.text.main
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    val shimmerModifier = Modifier
        .shimmer()
        .size(184.dp, 52.dp)
    Row(modifier = Modifier.padding(start = 24.dp)) {
        Box(modifier = shimmerModifier)
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = shimmerModifier)
    }
}

@Composable
private fun ErrorState() {
    Row(modifier = Modifier.padding(horizontal = 24.dp)) {
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(R.drawable.ic_error),
            tint = PeraTheme.colors.helper.negative,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.unable_to_load_swap_history),
            color = PeraTheme.colors.helper.negative,
            style = PeraTheme.typography.footnote.sansMedium
        )
    }
}

@Composable
private fun EmptyState() {
    Text(
        modifier = Modifier.padding(horizontal = 24.dp),
        text = stringResource(R.string.your_swap_history_will_appear),
        color = PeraTheme.colors.text.gray,
        style = PeraTheme.typography.footnote.sansMedium
    )
}
