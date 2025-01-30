package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PeraPrimaryButton(modifier: Modifier, onClick: () -> Unit, text: String) {
    Button(
        onClick = { onClick() },
        modifier = modifier.height(48.dp),
        shape = ShapeDefaults.ExtraSmall,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun PeraSecondaryButton(modifier: Modifier, onClick: () -> Unit, text: String) {
    Button(
        onClick = { onClick() },
        modifier = modifier.height(48.dp),
        shape = ShapeDefaults.ExtraSmall,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = text)
    }
}
