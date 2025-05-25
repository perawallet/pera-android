package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
fun PeraClickableText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: () -> Unit
) {
    val annotatedText = remember {
        buildAnnotatedString {
                append(
                    text
                )
            pushStringAnnotation(tag = "learn_more", annotation = "https://example.com")
            withStyle(style = SpanStyle(color = Color.Yellow, fontWeight = FontWeight.Bold)) {
                append(" Learn more")
            }
            pop()
        }
    }

    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                annotatedText.getStringAnnotations(
                    tag = "learn_more",
                    start = layoutResult.getOffsetForPosition(pos),
                    end = layoutResult.getOffsetForPosition(pos)
                ).firstOrNull()?.let {
                    onClick()
                }
            }
        }
    }

    Text(
        text = annotatedText,
        modifier = modifier.then(pressIndicator),
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        color = PeraTheme.colors.text.main,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        }
    )
}
