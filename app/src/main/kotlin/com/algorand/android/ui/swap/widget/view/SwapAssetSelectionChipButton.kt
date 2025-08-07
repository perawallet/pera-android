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

package com.algorand.android.ui.swap.widget.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.asset.AssetIcon
import com.algorand.android.ui.compose.widget.asset.AssetIconDrawable.AlgoDrawable
import com.algorand.android.ui.compose.widget.asset.AssetIconDrawable.AssetDrawable
import com.algorand.android.ui.swap.widget.viewmodel.SwapAssetSelectionViewModel.ViewState
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail

@Composable
fun SwapAssetSelectionChipButton(viewState: ViewState, backgroundColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp)
            .defaultMinSize(minHeight = 48.dp)
            .clickable { if (viewState is ViewState.Content) onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (viewState) {
            ViewState.Idle -> Unit
            ViewState.Error -> ErrorContent()
            ViewState.Loading -> LoadingContent()
            is ViewState.Content -> AssetContent(viewState.assetDetail)
        }
    }
}

@Composable
private fun AssetContent(assetDetail: SwapSelectedAssetDetail) {
    val assetDrawable = remember {
        with(assetDetail) {
            if (assetId == ALGO_ID) AlgoDrawable else AssetDrawable(imageUrl.orEmpty(), unitName)
        }
    }
    AssetIcon(
        modifier = Modifier.size(24.dp),
        drawable = assetDrawable
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
        text = assetDetail.unitName.orEmpty(),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.main
    )
    Spacer(modifier = Modifier.width(6.dp))
    Icon(
        modifier = Modifier.size(24.dp),
        painter = painterResource(R.drawable.ic_right_arrow),
        contentDescription = null,
        tint = PeraTheme.colors.text.gray
    )
}

@Composable
private fun ErrorContent() {
    Text(
        text = stringResource(R.string.choose_an_asset),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.main
    )
    Spacer(modifier = Modifier.width(6.dp))
    Icon(
        modifier = Modifier.size(24.dp),
        painter = painterResource(R.drawable.ic_right_arrow),
        contentDescription = null
    )
}

@Composable
private fun LoadingContent() {
    // TODO Shimmer
}
