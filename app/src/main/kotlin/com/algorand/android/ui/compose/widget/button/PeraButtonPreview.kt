package com.algorand.android.ui.compose.widget.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.icon.PeraIcon

@PreviewLightDark
@Composable
fun PeraPrimaryButtonPreview() {
    PeraTheme {
        PeraPrimaryButton(
            onClick = { },
            text = "Primary Button"
        )
    }
}

@PreviewLightDark
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

@PreviewLightDark
@Composable
fun PeraSecondaryButtonPreview() {
    PeraTheme {
        PeraSecondaryButton(
            onClick = { },
            text = "Secondary Button"
        )
    }
}

@PreviewLightDark
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

@PreviewLightDark
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

@PreviewLightDark
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

@PreviewLightDark
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
