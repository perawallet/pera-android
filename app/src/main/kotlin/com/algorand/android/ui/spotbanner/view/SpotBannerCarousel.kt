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

@file:Suppress("MagicNumber")

package com.algorand.android.ui.spotbanner.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraPagerIndicator
import com.algorand.wallet.spotbanner.domain.model.SpotBanner

@Composable
fun SpotBannerCarousel(spotBanners: List<SpotBanner>, listener: SpotBannerCarouselListener?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var banners by remember { mutableStateOf(spotBanners) }
        val pagerState = rememberPagerState { banners.size }
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 12.dp
        ) {
            when (val banner = banners[it]) {
                is SpotBanner.Generic -> {
                    val modifier = Modifier.spotBannerItem { listener?.onSpotBannerBannerClick(banner) }
                    GenericSpotBanner(modifier, banner) {
                        banners = banners.toMutableList().apply { remove(banner) }
                        listener?.onDismissSpotBannerClick(banner)
                    }
                }

                SpotBanner.BackupPassphrase -> {
                    val modifier = Modifier.spotBannerItem { listener?.onBackupPassphraseBannerClick() }
                    BackupPassphraseSpotBanner(modifier)
                }
            }
        }
        if (banners.size > 1) {
            Spacer(modifier = Modifier.height(12.dp))
            PeraPagerIndicator(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                pagerState = pagerState
            )
        }
    }
}

@Composable
internal fun Modifier.spotBannerItem(onClick: () -> Unit): Modifier {
    val shape = RoundedCornerShape(16.dp)
    return this
        .defaultMinSize(minHeight = 72.dp)
        .background(color = PeraTheme.colors.background.primary, shape = shape)
        .border(width = 1.dp, color = PeraTheme.colors.layer.gray, shape = shape)
        .clickable(remember { MutableInteractionSource() }, indication = null) { onClick() }
}

@Composable
internal fun RowScope.SpotBannerText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = PeraTheme.colors.text.main
) {
    Text(
        modifier = modifier
            .weight(1f)
            .padding(horizontal = 12.dp),
        text = text,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = color,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

interface SpotBannerCarouselListener {
    fun onBackupPassphraseBannerClick()
    fun onDismissSpotBannerClick(spotBanner: SpotBanner.Generic)
    fun onSpotBannerBannerClick(spotBanner: SpotBanner.Generic)
}
