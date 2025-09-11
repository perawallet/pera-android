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

package com.algorand.android.ui.asset.detail.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewState.Content
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailHeaderViewModel.ViewState.Idle
import com.algorand.android.ui.compose.theme.ColorPalette
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.VerificationTierIcon
import com.algorand.android.ui.compose.widget.asset.icon.AssetIcon
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID

@Composable
fun AssetDetailHeader(viewModel: AssetDetailHeaderViewModel) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value
    when (viewState) {
        Idle -> Unit
        is Content -> {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssetIcon(modifier = Modifier.size(32.dp), drawable = viewState.assetIconDrawable)
                Spacer(Modifier.width(12.dp))
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    AssetNameText(viewState)
                    Spacer(Modifier.width(4.dp))
                    VerificationTierIcon(Modifier.size(16.dp), viewState.verificationTierConfiguration)
                }
                Spacer(Modifier.width(8.dp))
                NotificationButton()
                Spacer(Modifier.width(12.dp))
                FavoriteButton()
            }
        }
    }
}

@Composable
private fun NotificationButton() {
    Icon(
        modifier = Modifier
            .background(color = PeraTheme.colors.layer.grayLighter, shape = CircleShape)
            .size(28.dp)
            .padding(4.dp),
        painter = painterResource(R.drawable.ic_notification_unmute),
        contentDescription = null
    )
}

@Composable
private fun FavoriteButton() {
    Icon(
        modifier = Modifier
            .background(color = PeraTheme.colors.layer.grayLighter, shape = CircleShape)
            .size(28.dp)
            .padding(4.dp),
        painter = painterResource(R.drawable.ic_favorite_enabled),
        tint = ColorPalette.Yellow.V500, // TODO
        contentDescription = null
    )
}

@Composable
private fun AssetNameText(contentState: Content) {
    val assetName = buildString {
        append(contentState.assetName)
        if (contentState.assetId != ALGO_ID) {
            val assetId = stringResource(R.string.interpunct_asset_id, contentState.assetId)
            append(assetId)
        }
    }
    Text(
        text = assetName,
        style = PeraTheme.typography.body.large.sans,
        color = PeraTheme.colors.text.main,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
