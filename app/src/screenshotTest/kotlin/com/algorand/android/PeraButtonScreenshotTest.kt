package com.algorand.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton

class PeraButtonScreenshotTest {

    @Preview
    @Composable
    fun PeraPrimaryButtonDisabledTest() {
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
    fun PeraPrimaryButtonTest() {
        PeraTheme {
            PeraPrimaryButton(
                onClick = { },
                text = "Primary Button222"
            )
        }
    }

    @Preview
    @Composable
    fun PeraPrimaryButtonTest2() {
        PeraTheme {
            PeraPrimaryButton(
                onClick = { },
                text = "Primary Button111"
            )
        }
    }
}
