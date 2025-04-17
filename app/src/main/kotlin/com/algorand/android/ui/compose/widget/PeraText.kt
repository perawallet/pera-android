/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
fun PeraHeadlineText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        fontFamily = peraSans,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
fun PeraTitleText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color? = MaterialTheme.colorScheme.primary
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontFamily = peraSans,
        color = color ?: MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun PeraBodyText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Left
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontFamily = peraSans,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Medium,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign
    )
}

@Composable
fun PeraBodyText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    onTextLayout: (TextLayoutResult) -> Unit
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontFamily = peraSans,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Medium,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = onTextLayout
    )
}

@Composable
fun PeraLinkText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontFamily = peraSans,
        color = MaterialTheme.colorScheme.surfaceDim,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun PeraScrimText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontFamily = peraSans,
        color = MaterialTheme.colorScheme.surfaceBright,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun PeraWarningText(modifier: Modifier = Modifier, text: String) {
    Row(modifier = modifier) {
        Image(
            modifier = Modifier.align(alignment = Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.ic_error),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.error),
            contentDescription = stringResource(R.string.error)
        )
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = peraSans,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
fun PeraHighlightedText(modifier: Modifier = Modifier, text: String) {
    Box(
        modifier = modifier
            .padding(start = 10.dp)
            .clip(shape = CircleShape)
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        PeraBodyText(
            modifier = Modifier.padding(
                start = 7.dp,
                end = 7.dp,
                top = 3.dp,
                bottom = 3.dp
            ),
            text = text
        )
    }
}

@Preview
@Composable
fun PreviewPeraHeadlineText() {
    PeraTheme {
        PeraHeadlineText(text = "Headline Text")
    }
}

@Preview
@Composable
fun PreviewPeraTitleText() {
    PeraTheme {
        PeraTitleText(text = "Title Text")
    }
}

@Preview
@Composable
fun PreviewPeraBodyText() {
    PeraTheme {
        PeraBodyText(text = "Body Text")
    }
}

@Preview
@Composable
fun PreviewPeraLinkText() {
    PeraTheme {
        PeraLinkText(text = "Link Text")
    }
}

@Preview
@Composable
fun PreviewPeraScrimText() {
    PeraTheme {
        PeraScrimText(text = "Scrim Text")
    }
}

@Preview
@Composable
fun PreviewPeraWarningText() {
    PeraTheme {
        PeraWarningText(text = "Warning Text")
    }
}

@Preview
@Composable
fun PreviewPeraHighlightedText() {
    PeraTheme {
        PeraHighlightedText(text = "Highlighted Text")
    }
}

@Preview
@Composable
fun PreviewPeraHighlightedTextDark() {
    PeraTheme(darkTheme = true) {
        PeraHighlightedText(text = "Highlighted Text")
    }
}
