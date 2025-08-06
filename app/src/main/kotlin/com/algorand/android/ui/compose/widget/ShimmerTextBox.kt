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

package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import com.algorand.android.ui.compose.widget.modifier.shimmer

@Composable
fun ShimmerTextBox(textStyle: TextStyle, width: Dp) {
    val textHeight = calculateTextHeight(textStyle)
    Box(
        modifier = Modifier
            .height(textHeight)
            .width(width)
            .shimmer()
    )
}

@Composable
private fun calculateTextHeight(textStyle: TextStyle): Dp {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val textHeight = textMeasurer.measure(text = AnnotatedString("A"), style = textStyle).size.height
    return with(density) { textHeight.toDp() }
}
