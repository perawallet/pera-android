package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
fun PeraCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    interactionSource: MutableInteractionSource? = MutableInteractionSource(),
    enabled: () -> Boolean = { true },
    onCheckChanged: (Boolean) -> Unit
) {
    Checkbox(
        interactionSource = interactionSource,
        modifier = modifier,
        colors = CheckboxDefaults.colors(
            checkmarkColor = PeraTheme.colors.status.successCheckmark,
            checkedColor = PeraTheme.colors.status.success,
            uncheckedColor = PeraTheme.colors.layer.gray
        ),
        checked = checked,
        enabled = enabled(),
        onCheckedChange = onCheckChanged
    )
}

@PreviewLightDark
@Composable
fun PeraCheckboxPreview() {
    val interactionSource = remember { MutableInteractionSource() }
    val checkedState = remember { mutableStateOf(true) }
    PeraTheme {
        PeraCheckbox(
            Modifier.background(color = PeraTheme.colors.background.primary),
            onCheckChanged = { checkedState.value = it },
            interactionSource = interactionSource,
            checked = checkedState.value
        )
    }
}
