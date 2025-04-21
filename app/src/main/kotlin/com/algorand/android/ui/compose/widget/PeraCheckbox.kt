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
fun PeraCheckbox(
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
fun PeraCheckboxPreview() {
    val interactionSource = remember { MutableInteractionSource() }
    val checkedState = remember { mutableStateOf(ToggleableState.Indeterminate) }
    PeraTheme {
        PeraCheckbox(
            Modifier.background(color = PeraTheme.colors.background.primary),
            checkedState = { checkedState.value },
            interactionSource = interactionSource,
            onClick = { }
        )
    }
}
