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

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
fun PeraTriStateCheckbox(
    modifier: Modifier = Modifier,
    checkedState: () -> ToggleableState = { ToggleableState.Indeterminate },
    interactionSource: MutableInteractionSource? = MutableInteractionSource(),
    onClick: () -> Unit,
    enabled: () -> Boolean = { true }
) {
    TriStateCheckbox(
        interactionSource = interactionSource,
        modifier = modifier,
        state = checkedState(),
        colors = CheckboxDefaults.colors(
            checkmarkColor = PeraTheme.colors.status.successCheckmark,
            checkedColor = PeraTheme.colors.status.success,
            uncheckedColor = PeraTheme.colors.layer.gray
        ),
        onClick = onClick,
        enabled = enabled()
    )
}

@PreviewLightDark
@Composable
fun PeraTriStateCheckboxPreview() {
    val interactionSource = remember { MutableInteractionSource() }
    val checkedState = remember { mutableStateOf(ToggleableState.Indeterminate) }
    PeraTheme {
        PeraTriStateCheckbox(
            Modifier.background(color = PeraTheme.colors.background.primary),
            checkedState = { checkedState.value },
            interactionSource = interactionSource,
            onClick = { }
        )
    }
}
