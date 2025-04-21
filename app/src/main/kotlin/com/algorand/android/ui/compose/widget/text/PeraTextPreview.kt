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
fun PreviewPeraHighlightedText() {
    PeraTheme {
        PeraHighlightedText(text = "Highlighted Text")
    }
}
