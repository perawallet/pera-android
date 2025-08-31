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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID

@Composable
fun AssetMarketsAbout(
    about: AssetMarketsDetail.About,
    onUrlClick: (String) -> Unit,
    onCreatorAddressClick: (String) -> Unit
) {
    Column {
        AssetMarketsSectionTitle(stringResource(R.string.about_asset_name, about.assetName.orEmpty()))
        Spacer(modifier = Modifier.height(24.dp))
        AssetId(about.assetId)
        CreatorAddress(about.assetCreatorAddress, onCreatorAddressClick)
        AsaUrl(about.displayAsaUrl, about.asaUrl, onUrlClick)
        PeraExplorer(about.peraExplorerUrl, onUrlClick)
        ProjectWebsite(about.projectWebsiteUrl, onUrlClick)
    }
}

@Composable
private fun CreatorAddress(address: String?, onClick: (String) -> Unit) {
    address?.let {
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            AboutTitle(R.string.creator)
            AboutDescription(address.toShortenedAddress()) { onClick(address) }
        }
    }
}

@Composable
private fun AsaUrl(displayUrl: String?, url: String?, onClick: (String) -> Unit) {
    if (!displayUrl.isNullOrBlank() && !url.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            AboutTitle(R.string.asa_url)
            AboutDescription(displayUrl) { onClick(url) }
        }
    }
}

@Composable
private fun PeraExplorer(url: String?, onClick: (String) -> Unit) {
    url?.let {
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AboutTitle(R.string.show_on)
            Image(
                painter = painterResource(R.drawable.ic_pera_round),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            AboutDescription(stringResource(R.string.pera_explorer)) { onClick(url) }
        }
    }
}

@Composable
private fun ProjectWebsite(url: String?, onClick: (String) -> Unit) {
    url?.let {
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            AboutTitle(R.string.project_website)
            AboutDescription(stringResource(R.string.open_with_browser)) { onClick(url) }
        }
    }
}

@Composable
private fun AssetId(assetId: Long?) {
    if (assetId != null && assetId != ALGO_ID) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AboutTitle(R.string.asa_id)
            Text(
                text = assetId.toString(),
                color = PeraTheme.colors.text.main,
                style = PeraTheme.typography.body.regular.sansMedium,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun AboutDescription(text: String, onClick: () -> Unit) {
    Text(
        modifier = Modifier.clickableNoRipple(onClick = onClick),
        text = text,
        color = PeraTheme.colors.helper.positive,
        style = PeraTheme.typography.body.regular.sansMedium,
        textAlign = TextAlign.End,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun RowScope.AboutTitle(titleResId: Int) {
    Text(
        modifier = Modifier
            .padding(end = 24.dp)
            .weight(1f),
        text = stringResource(titleResId),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.gray
    )
}
