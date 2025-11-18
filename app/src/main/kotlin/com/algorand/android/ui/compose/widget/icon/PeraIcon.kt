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

package com.algorand.android.ui.compose.widget.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Fit
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
private fun PeraCoreIcon(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String,
    tintColor: Color?,
    contentScale: ContentScale
) {
    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = contentDescription,
        colorFilter = tintColor?.let { ColorFilter.tint(it) },
        contentScale = contentScale
    )
}

@Composable
fun PeraIcon(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String,
    tintColor: Color? = null,
    contentScale: ContentScale = Fit
) {
    PeraCoreIcon(
        modifier = modifier,
        painter = painter,
        contentDescription = contentDescription,
        tintColor = tintColor,
        contentScale = contentScale
    )
}

@Composable
fun PeraIconRoundShapeBig(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String
) {
    Box(
        modifier = modifier
            .padding(start = 10.dp)
            .size(64.dp)
            .clip(shape = CircleShape)
            .background(color = PeraTheme.colors.layer.grayLighter)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .height(40.dp)
                .width(40.dp),
            imageVector = imageVector,
            tint = PeraTheme.colors.text.main,
            contentDescription = contentDescription
        )
    }
}
