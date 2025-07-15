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

package com.algorand.android.ui.compose.widget.chart.view

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput

@Composable
internal fun Modifier.detectChartTapGestures(
    points: List<Offset>,
    onTap: () -> Unit,
    onOffsetSelected: (Offset?) -> Unit
): Modifier {
    var selectedOffset by remember { mutableStateOf<Offset?>(null) }
    LaunchedEffect(selectedOffset) {
        onOffsetSelected(selectedOffset)
    }
    return this
        .pointerInput(points) {
            detectDragGestures(
                onDragStart = { offset -> selectedOffset = offset },
                onDrag = { change, _ -> selectedOffset = change.position },
                onDragCancel = { selectedOffset = null },
                onDragEnd = { selectedOffset = null }
            )
        }
        .pointerInput(points) {
            detectTapGestures(
                onPress = { offset ->
                    selectedOffset = offset
                    onTap()
                }
            )
        }
}
