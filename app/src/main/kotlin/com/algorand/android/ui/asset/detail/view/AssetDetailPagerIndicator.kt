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

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import kotlin.math.absoluteValue

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AssetDetailPagerIndicator(pagerState: PagerState, onPageClick: (Int) -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        val offsetFraction = pagerState.currentPageOffsetFraction
        val offset by remember(offsetFraction) {
            derivedStateOf {
                when {
                    offsetFraction == 0f -> pagerState.currentPage.toFloat()
                    offsetFraction < 0f -> 1f - offsetFraction.absoluteValue
                    else -> offsetFraction
                }.coerceIn(0f, 1f)
            }
        }
        val itemWidth = (maxWidth.value / 2f).dp
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            PageTitle(
                modifier = Modifier
                    .clickableNoRipple { onPageClick(0) }
                    .width(itemWidth),
                titleResId = R.string.holdings,
                isSelected = pagerState.currentPage == 0
            )
            PageTitle(
                modifier = Modifier
                    .clickableNoRipple { onPageClick(1) }
                    .width(itemWidth),
                titleResId = R.string.markets,
                isSelected = pagerState.currentPage == 1
            )
        }

        Indicator(Modifier.align(Alignment.BottomStart), itemWidth, offset)
    }
}

@Composable
private fun PageTitle(modifier: Modifier, titleResId: Int, isSelected: Boolean) {
    Text(
        modifier = modifier,
        text = stringResource(titleResId),
        color = PeraTheme.colors.text.main,
        textAlign = TextAlign.Center,
        style = if (isSelected) PeraTheme.typography.body.regular.sansMedium else PeraTheme.typography.body.regular.sans
    )
}

@Composable
private fun Indicator(modifier: Modifier, itemWidth: Dp, offset: Float) {
    val xOffset = itemWidth.value * offset
    Box(
        modifier = modifier
            .offset(xOffset.dp)
            .background(color = PeraTheme.colors.text.main)
            .width(itemWidth)
            .height(2.dp)
    )
}
