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

package com.algorand.android.ui.asset.detail.view.markets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

@Composable
fun AssetMarketsSocialMedia(socialMedia: AssetMarketsDetail.SocialMedia, onUrlClick: (String) -> Unit) {
    if (!socialMedia.hasAnyValidUrl()) return
    with(socialMedia) {
        Column {
            discordUrl?.let { discordUrl ->
                SocialMediaRow(R.drawable.ic_discord, R.string.discord) { onUrlClick(discordUrl) }
            }
            telegramUrl?.let { telegramUrl ->
                if (discordUrl != null) Spacer(modifier = Modifier.height(20.dp))
                SocialMediaRow(R.drawable.ic_telegram, R.string.telegram) { onUrlClick(telegramUrl) }
            }
            twitterUrl?.let { twitterUrl ->
                if (discordUrl != null || telegramUrl != null) Spacer(modifier = Modifier.height(20.dp))
                SocialMediaRow(R.drawable.ic_twitter, R.string.twitter) { onUrlClick(twitterUrl) }
            }
        }
    }
}

private fun AssetMarketsDetail.SocialMedia.hasAnyValidUrl(): Boolean {
    return !discordUrl.isNullOrBlank() || !telegramUrl.isNullOrBlank() || !twitterUrl.isNullOrBlank()
}

@Composable
private fun SocialMediaRow(iconResId: Int, titleResId: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clickableNoRipple(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(iconResId),
            contentDescription = null,
            tint = PeraTheme.colors.text.grayLighter
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(titleResId),
            style = PeraTheme.typography.body.regular.sans,
            color = PeraTheme.colors.text.main
        )
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_external_link),
            contentDescription = null,
            tint = PeraTheme.colors.text.grayLighter
        )
    }
}
