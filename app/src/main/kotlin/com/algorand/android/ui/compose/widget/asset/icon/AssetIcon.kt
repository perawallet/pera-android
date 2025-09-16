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

@file:OptIn(ExperimentalGlideComposeApi::class)

package com.algorand.android.ui.compose.widget.asset.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.utils.PrismUrlBuilder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

private const val MAX_ASSET_DISPLAY_CHAR = 3
private const val SMALL_ICON_MAX_ASSET_DISPLAY_CHAR = 1
private const val SIZE_PADDING_RATIO = 5f

sealed interface AssetIconDrawable {
    data object AlgoDrawable : AssetIconDrawable
    data class AssetDrawable(private val url: String, val unitName: String?) : AssetIconDrawable {

        fun getImageUrl(containerWidthPx: Int): String {
            return PrismUrlBuilder.create(url)
                .addWidth(containerWidthPx)
                .addQuality(PrismUrlBuilder.DEFAULT_IMAGE_QUALITY)
                .build()
        }
    }
}

@Composable
fun AssetIcon(modifier: Modifier, drawable: AssetIconDrawable) {
    BoxWithConstraints(modifier = modifier) {
        when (drawable) {
            AssetIconDrawable.AlgoDrawable -> AlgoIcon()
            is AssetIconDrawable.AssetDrawable -> AssetDrawableIcon(drawable)
        }
    }
}

@Composable
fun AssetIcons(modifier: Modifier = Modifier, firstDrawable: AssetIconDrawable, secondDrawable: AssetIconDrawable) {
    Box(modifier = modifier.padding(2.dp)) {
        val iconModifier = Modifier
            .size(25.dp)
            .border(width = 2.dp, color = PeraTheme.colors.background.primary, shape = CircleShape)
            .padding(1.dp)
        AssetIcon(iconModifier, firstDrawable)
        AssetIcon(
            modifier = Modifier
                .padding(start = 14.dp, top = 14.dp)
                .then(iconModifier),
            drawable = secondDrawable
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.AlgoIcon() {
    val padding = getPaddingBySize(constraints.maxWidth, LocalDensity.current)
    Icon(
        modifier = Modifier
            .background(color = Color.Black, shape = CircleShape)
            .padding(padding),
        contentDescription = null,
        painter = painterResource(id = R.drawable.ic_algo),
        tint = Color.White
    )
}

@Composable
private fun BoxWithConstraintsScope.AssetDrawableIcon(drawable: AssetIconDrawable.AssetDrawable) {
    val placeholder = placeholder { AssetNameIcon(maxWidth, drawable.unitName) }
    val widthAsPx = with(LocalDensity.current) { maxWidth.toPx().toInt() }
    GlideImage(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
        model = drawable.getImageUrl(widthAsPx),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        failure = placeholder,
        loading = placeholder
    )
}

@Composable
private fun AssetNameIcon(maxWidth: Dp, unitName: String?) {
    val safeAssetName = if (unitName.isNullOrBlank()) stringResource(R.string.unnamed) else unitName
    val charCount = if (maxWidth < 32.dp) SMALL_ICON_MAX_ASSET_DISPLAY_CHAR else MAX_ASSET_DISPLAY_CHAR
    val displayName = safeAssetName.take(charCount).uppercase()
    var padding by remember { mutableStateOf(8.dp) }
    val density = LocalDensity.current
    Box(
        Modifier
            .fillMaxSize()
            .background(color = PeraTheme.colors.background.primary, shape = CircleShape)
            .border(1.dp, PeraTheme.colors.layer.grayLighter, shape = CircleShape)
            .onSizeChanged {
                padding = getPaddingBySize(it.width, density)
            }
            .padding(horizontal = padding),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = displayName,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = 20.sp),
            style = PeraTheme.typography.body.regular.sans.copy(
                color = PeraTheme.colors.text.gray
            )
        )
    }
}

private fun getPaddingBySize(widthPx: Int, density: Density): Dp {
    return with(density) { (widthPx / SIZE_PADDING_RATIO).toDp() }
}
