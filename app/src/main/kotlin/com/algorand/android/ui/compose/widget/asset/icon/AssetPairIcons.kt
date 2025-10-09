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

package com.algorand.android.ui.compose.widget.asset.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

private val ICON_SIZE = 24.dp

@Composable
fun AssetPairIcons(modifier: Modifier = Modifier, firstDrawable: AssetIconDrawable, secondDrawable: AssetIconDrawable) {
    Box(modifier = modifier.padding(2.dp)) {
        val iconModifier = Modifier
            .size(25.dp)
            .border(width = 2.dp, color = PeraTheme.colors.background.primary, shape = CircleShape)
            .padding(1.dp)
        AssetPairIcon(iconModifier, firstDrawable)
        AssetPairIcon(
            modifier = Modifier
                .padding(start = 14.dp, top = 14.dp)
                .then(iconModifier),
            drawable = secondDrawable
        )
    }
}

@Composable
private fun AssetPairIcon(modifier: Modifier, drawable: AssetIconDrawable) {
    Box(modifier, contentAlignment = Alignment.Center) {
        when (drawable) {
            AssetIconDrawable.AlgoDrawable -> AlgoIcon()
            is AssetIconDrawable.AssetDrawable -> AssetDrawableIcon(drawable)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun AssetDrawableIcon(drawable: AssetIconDrawable.AssetDrawable) {
    if (drawable.isUrlValid()) {
        val widthAsPx = with(LocalDensity.current) { ICON_SIZE.toPx().toInt() }
        GlideImage(
            modifier = Modifier.clip(CircleShape),
            model = drawable.getImageUrl(widthAsPx),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            failure = placeholder { AssetNameIcon(drawable.unitName) }
        )
    } else {
        AssetNameIcon(drawable.unitName)
    }
}

@Composable
private fun AssetNameIcon(unitName: String?) {
    val safeAssetName = if (unitName.isNullOrBlank()) stringResource(R.string.unnamed) else unitName
    val displayName = safeAssetName.take(1).uppercase()
    Box(
        Modifier
            .background(color = PeraTheme.colors.background.primary, shape = CircleShape)
            .fillMaxSize()
            .border(1.dp, PeraTheme.colors.layer.grayLighter, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayName,
            color = PeraTheme.colors.text.gray,
            style = PeraTheme.typography.body.regular.sans,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AlgoIcon() {
    Image(
        modifier = Modifier.size(ICON_SIZE),
        contentDescription = null,
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_algo_circle)
    )
}
