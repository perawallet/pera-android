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

package com.algorand.android.ui.compose.widget.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

private const val MAX_ASSET_DISPLAY_CHAR = 3
private const val SIZE_PADDING_RATIO = 5f

sealed interface AssetIconDrawable {
    data object AlgoDrawable : AssetIconDrawable
    data class AssetDrawable(val url: String, val unitName: String?) : AssetIconDrawable
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
private fun BoxWithConstraintsScope.AlgoIcon() {
    val padding = with(LocalDensity.current) {
        constraints.maxWidth.toDp().value / SIZE_PADDING_RATIO
    }
    Icon(
        modifier = Modifier
            .background(color = Color.Black, shape = CircleShape)
            .padding(padding.dp),
        contentDescription = null,
        painter = painterResource(id = R.drawable.ic_algo),
        tint = Color.White
    )
}

@Composable
private fun AssetDrawableIcon(drawable: AssetIconDrawable.AssetDrawable) {
    val placeholder = placeholder { AssetNameIcon(drawable.unitName) }
    GlideImage(
        model = drawable.url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        failure = placeholder,
        loading = placeholder,
    )
}

@Composable
private fun AssetNameIcon(unitName: String?) {
    val safeAssetName = if (unitName.isNullOrBlank()) stringResource(R.string.unnamed) else unitName
    val displayName = safeAssetName.take(MAX_ASSET_DISPLAY_CHAR).uppercase()
    Box(
        Modifier
            .fillMaxSize()
            .background(color = PeraTheme.colors.background.primary, shape = CircleShape)
            .border(1.dp, PeraTheme.colors.layer.grayLighter, shape = CircleShape)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = displayName,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = 20.sp),
        )
    }
}
