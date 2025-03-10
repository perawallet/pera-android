package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
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
        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.outline),
        onClick = onClick,
        enabled = enabled()
    )
}

@Preview
@Composable
fun PeraCheckboxPreview() {
    val interactionSource = remember { MutableInteractionSource() }
    val checkedState = remember { mutableStateOf(ToggleableState.Indeterminate) }
    PeraTheme {
        PeraCheckbox(
            checkedState = { checkedState.value },
            interactionSource = interactionSource,
            onClick = { }
        )
    }
}
