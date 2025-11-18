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

package com.algorand.android.ui.compose.widget.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.algorand.android.ui.compose.theme.PeraTheme

@PreviewLightDark
@Composable
fun PreviewPeraHeadlineText() {
    PeraTheme {
        PeraHeadlineText(text = "Headline Text")
    }
}

@PreviewLightDark
@Composable
fun PreviewPeraTitleText() {
    PeraTheme {
        PeraTitleText(text = "Title Text")
    }
}

@PreviewLightDark
@Composable
fun PreviewPeraBodyText() {
    PeraTheme {
        PeraBodyText(text = "Body Text")
    }
}

@PreviewLightDark
@Composable
fun PreviewPeraLinkText() {
    PeraTheme {
        PeraLinkText(text = "Link Text")
    }
}

@PreviewLightDark
@Composable
fun PreviewPeraScrimText() {
    PeraTheme {
        PeraScrimText(text = "Scrim Text")
    }
}

@PreviewLightDark
@Composable
fun PreviewPeraWarningText() {
    PeraTheme {
        PeraWarningText(text = "Warning Text")
    }
}

@PreviewLightDark
@Composable
fun PreviewPeraHighlightedGreenText() {
    PeraTheme {
        PeraHighlightedGreenText(text = "Highlighted Text")
    }
}

@PreviewLightDark
@Composable
fun PreviewPeraHighlightedGrayText() {
    PeraTheme {
        PeraHighlightedGrayText(text = "Highlighted Text")
    }
}
