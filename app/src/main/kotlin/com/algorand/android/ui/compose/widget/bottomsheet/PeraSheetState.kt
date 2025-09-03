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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.algorand.android.ui.compose.widget.bottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PeraSheetState(
    val sheetState: SheetState,
    private val isVisible: MutableState<Boolean>,
    private val scope: CoroutineScope
) {

    fun show() {
        isVisible.value = true
    }

    fun hide() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                isVisible.value = false
            }
        }
    }

    @Composable
    fun SheetContent(content: @Composable () -> Unit) {
        if (isVisible.value) {
            content()
        }
    }
}

@Composable
fun rememberPeraSheetState(
    scope: CoroutineScope,
    skipPartiallyExpanded: Boolean = true,
    initialVisibility: Boolean = false
): PeraSheetState {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    val isVisible = remember { mutableStateOf(initialVisibility) }
    return remember { PeraSheetState(sheetState, isVisible, scope) }
}
