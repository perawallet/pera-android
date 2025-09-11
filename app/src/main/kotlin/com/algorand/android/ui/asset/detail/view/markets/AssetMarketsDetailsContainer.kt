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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail.About
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail.AssetDescription
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail.BadgeDescription
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail.Report
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail.SocialMedia
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail.Statistics
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
fun AssetMarketsDetailsContainer(details: List<AssetMarketsDetail>, listener: AssetMarketsDetailsListener) {
    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 40.dp)) {

        details.forEach { item ->
            when (item) {
                is Statistics -> AssetMarketsStatistics(item) { }
                is About -> {
                    Divider()
                    AssetMarketsAbout(item, listener::onUrlClick, listener::onCreatorAddressClick)
                }
                is AssetDescription.Text -> {
                    Divider()
                    AssetMarketDescription(item)
                }
                is AssetDescription.TextResource -> {
                    Divider()
                    AssetMarketDescription(item)
                }
                is BadgeDescription -> {
                    Divider()
                    AssetMarketBadgeDescription(item, listener::onUrlClick)
                }
                is SocialMedia -> {
                    Divider()
                    AssetMarketsSocialMedia(item, listener::onUrlClick)
                }
                is Report -> {
                    Divider()
                    AssetMarketsReport(item) { listener.onReportClick(item.assetId, item.assetName.orEmpty()) }
                }
            }
        }
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .padding(vertical = 36.dp)
            .background(color = PeraTheme.colors.layer.grayLighter)
            .fillMaxWidth()
            .height(1.dp)
    )
}

interface AssetMarketsDetailsListener {
    fun onUrlClick(url: String)
    fun onReportClick(assetId: Long, assetShortName: String)
    fun onCreatorAddressClick(address: String)
}
