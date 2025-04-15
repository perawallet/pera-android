package com.algorand.android.ui.compose.widget.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.icon.PeraIcon

@Preview
@Composable
fun PeraPrimaryButtonPreview() {
    PeraTheme {
        PeraPrimaryButton(
            onClick = { },
            text = "Primary Button"
        )
    }
}

@Preview
@Composable
fun PeraPrimaryButtonDarkPreview() {
    PeraTheme(isDarkTheme = true) {
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
            onClick = { },
            text = "Primary Button Disabled",
            state = PeraButtonState.DISABLED
        )
    }
}

@Preview
@Composable
fun PeraPrimaryButtonDarkDisabledPreview() {
    PeraTheme(isDarkTheme = true) {
        PeraPrimaryButton(
            modifier = Modifier,
            onClick = { },
            text = "Primary Button Disabled",
            state = PeraButtonState.DISABLED
        )
    }
}

@Preview
@Composable
fun PeraSecondaryButtonPreview() {
    PeraTheme {
        PeraSecondaryButton(
            onClick = { },
            text = "Secondary Button"
        )
    }
}

@Preview
@Composable
fun PeraSecondaryButtonDarkPreview() {
    PeraTheme(isDarkTheme = true) {
        PeraSecondaryButton(
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
            state = PeraButtonState.DISABLED
        )
    }
}

@Preview
@Composable
fun PeraSecondaryButtonDarkDisabledPreview() {
    PeraTheme(isDarkTheme = true) {
        PeraSecondaryButton(
            onClick = { },
            text = "Secondary Button Disabled",
            state = PeraButtonState.DISABLED
        )
    }
}

@Preview
@Composable
fun PeraSecondaryButtonWithIconPreview() {
    PeraTheme {
        PeraSecondaryButton(
            onClick = { },
            text = "Secondary Button",
            leftIcon = {
                PeraIcon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = stringResource(id = R.string.check),
                    modifier = Modifier
                )
            }
        )
    }
}

@Preview
@Composable
fun PeraPrimaryButtonProgressPreview() {
    PeraTheme {
        PeraPrimaryButton(
            onClick = { },
            text = "Primary Button",
            state = PeraButtonState.PROGRESS
        )
    }
}

@Preview
@Composable
fun PeraSecondaryButtonProgressPreview() {
    PeraTheme {
        PeraSecondaryButton(
            onClick = { },
            text = "Secondary Button",
            state = PeraButtonState.PROGRESS
        )
    }
}
