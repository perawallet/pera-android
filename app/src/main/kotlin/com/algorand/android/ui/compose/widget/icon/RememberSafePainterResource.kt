/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.algorand.android.ui.compose.widget.icon

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap

@Composable
fun rememberSafePainterResource(@DrawableRes drawableResId: Int): Painter? {
    val context = LocalContext.current
    return remember(context, drawableResId) {
        try {
            AppCompatResources.getDrawable(context, drawableResId)
                ?.toBitmap()
                ?.asImageBitmap()
                ?.let(::BitmapPainter)
        } catch (_: Resources.NotFoundException) {
            null
        }
    }
}
