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

@PreviewLightDark
@Composable
fun PeraTertiaryButtonPreview() {
    PeraTheme {
        PeraTertiaryButton(
            onClick = { },
            text = "Tertiary Button"
        )
    }
}

@PreviewLightDark
@Composable
fun PeraTertiaryButtonDisabledPreview() {
    PeraTheme {
        PeraTertiaryButton(
            onClick = { },
            text = "Tertiary Button Disabled",
            state = PeraButtonState.DISABLED
        )
    }
}

@PreviewLightDark
@Composable
fun PeraTertiaryButtonWithIconPreview() {
    PeraTheme {
        PeraTertiaryButton(
            onClick = { },
            text = "Tertiary Button",
            rightIcon = {
                PeraIcon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = stringResource(id = R.string.check),
                    modifier = Modifier
                )
            },
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
fun PeraTertiaryButtonProgressPreview() {
    PeraTheme {
        PeraTertiaryButton(
            onClick = { },
            text = "Tertiary Button",
            state = PeraButtonState.PROGRESS
        )
    }
}
