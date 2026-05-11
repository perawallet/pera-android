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

package com.algorand.android.ui.compose.widget.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme

@PreviewLightDark
@Composable
fun SixDigitPasswordContainerEmptyPreview() {
    PeraTheme {
        SixDigitPasswordContainer(
            modifier = Modifier
                .background(color = PeraTheme.colors.background.primary)
                .padding(16.dp),
            enteredDigitCount = 0,
            isError = false
        )
    }
}

@PreviewLightDark
@Composable
fun SixDigitPasswordContainerPartialPreview() {
    PeraTheme {
        SixDigitPasswordContainer(
            modifier = Modifier
                .background(color = PeraTheme.colors.background.primary)
                .padding(16.dp),
            enteredDigitCount = 3,
            isError = false
        )
    }
}

@PreviewLightDark
@Composable
fun SixDigitPasswordContainerFullPreview() {
    PeraTheme {
        SixDigitPasswordContainer(
            modifier = Modifier
                .background(color = PeraTheme.colors.background.primary)
                .padding(16.dp),
            enteredDigitCount = 6,
            isError = false
        )
    }
}

@PreviewLightDark
@Composable
fun SixDigitPasswordContainerErrorPreview() {
    PeraTheme {
        SixDigitPasswordContainer(
            modifier = Modifier
                .background(color = PeraTheme.colors.background.primary)
                .padding(16.dp),
            enteredDigitCount = 6,
            isError = true
        )
    }
}
