package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
fun PeraPrimaryButton(
    modifier: Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: () -> Boolean = { true }
) {
    Button(
        onClick = { onClick() },
        modifier = modifier.height(48.dp),
        shape = ShapeDefaults.ExtraSmall,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        enabled = enabled()
    ) {
        Text(text = text)
    }
}

@Composable
fun PeraSecondaryButton(
    modifier: Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: () -> Boolean = { true }
) {
    Button(
        onClick = { onClick() },
        modifier = modifier.height(48.dp),
        shape = ShapeDefaults.ExtraSmall,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        enabled = enabled()
    ) {
        Text(text = text)
    }
}

@Preview
@Composable
fun PeraPrimaryButtonPreview() {
    PeraTheme {
        PeraPrimaryButton(
            modifier = Modifier,
            onClick = { },
            text = "Primary Button"
        )
    }
}

@Preview
@Composable
fun PeraPrimaryButtonDarkPreview() {
    PeraTheme(darkTheme = true) {
        PeraPrimaryButton(
            modifier = Modifier,
            onClick = { },
            text = "Primary Button"
        )
    }
}

@Preview
@Composable
fun PeraPrimaryButtonDisabledPreview() {
    PeraTheme {
        PeraPrimaryButton(
            modifier = Modifier,
            onClick = { },
            text = "Primary Button Disabled",
            { false }
        )
    }
}

@Preview
@Composable
fun PeraPrimaryButtonDarkDisabledPreview() {
    PeraTheme(darkTheme = true) {
        PeraPrimaryButton(
            modifier = Modifier,
            onClick = { },
            text = "Primary Button Disabled",
            { false }
        )
    }
}

@Preview
@Composable
fun PeraSecondaryButtonPreview() {
    PeraTheme {
        PeraSecondaryButton(
            modifier = Modifier,
            onClick = { },
            text = "Secondary Button"
        )
    }
}

@Preview
@Composable
fun PeraSecondaryButtonDisabledPreview() {
    PeraTheme {
        PeraSecondaryButton(
            modifier = Modifier,
            onClick = { },
            text = "Secondary Button Disabled",
            { false }
        )
    }
}
