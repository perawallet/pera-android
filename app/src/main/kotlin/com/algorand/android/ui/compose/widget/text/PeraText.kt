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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
        style = PeraTheme.typography.title.regular.sansMedium,
        color = PeraTheme.colors.text.main,
    )
}

@Composable
fun PeraTitleText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = PeraTheme.colors.text.main
) {
    Text(
        modifier = modifier,
        text = text,
        style = PeraTheme.typography.body.large.sansMedium,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun PeraBodyText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Left,
    color: Color = PeraTheme.colors.text.gray
) {
    Text(
        modifier = modifier,
        text = text,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = color,
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
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.text.gray,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = onTextLayout
    )
}

@Composable
fun PeraLinkText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.link.primary,
    )
}

@Composable
fun PeraScrimText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.link.primary,
    )
}

@Composable
fun PeraFootnoteText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.link.primary,
    )
}

@Composable
fun PeraWarningText(modifier: Modifier = Modifier, text: String) {
    Row(modifier = modifier) {
        Image(
            modifier = Modifier.align(alignment = Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.ic_error),
            colorFilter = ColorFilter.tint(color = PeraTheme.colors.status.negative),
            contentDescription = stringResource(R.string.error)
        )
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = text,
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.status.negative,
        )
    }
}

@Composable
fun PeraHighlightedText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    backgroundColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            text = text,
            style = PeraTheme.typography.caption.sansMedium,
            color = textColor
        )
    }
}

@Composable
fun PeraHighlightedGreenText(modifier: Modifier = Modifier, text: String) {
    PeraHighlightedText(
        modifier = modifier,
        text = text,
        textColor = PeraTheme.colors.wallet.wallet4.icon,
        backgroundColor = PeraTheme.colors.wallet.wallet4.background
    )
}

@Composable
fun PeraHighlightedGrayText(modifier: Modifier = Modifier, text: String) {
    PeraHighlightedText(
        modifier = modifier,
        text = text,
        textColor = PeraTheme.colors.text.gray,
        backgroundColor = PeraTheme.colors.layer.grayLighter
    )
}
