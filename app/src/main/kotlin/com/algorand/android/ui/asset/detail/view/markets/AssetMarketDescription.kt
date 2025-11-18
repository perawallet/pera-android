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

package com.algorand.android.ui.asset.detail.view.markets

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

private const val UNDEFINED = -1
private const val DEFAULT_MAX_LINE_COUNT = 4

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AssetMarketDescription(description: AssetMarketsDetail.AssetDescription) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val widthInPx = constraints.maxWidth
        var currentMaxLines by remember { mutableIntStateOf(DEFAULT_MAX_LINE_COUNT) }
        var totalLines by remember { mutableIntStateOf(UNDEFINED) }
        Column {
            val text = when (description) {
                is AssetMarketsDetail.AssetDescription.Text -> description.text
                is AssetMarketsDetail.AssetDescription.TextResource -> stringResource(description.textResId)
            }
            AssetMarketsSectionTitle(stringResource(R.string.description))
            Spacer(modifier = Modifier.height(24.dp))
            DescriptionText(text, currentMaxLines)
            if (totalLines == UNDEFINED) {
                totalLines = getTotalLineCount(text, widthInPx)
            }
            if (totalLines != UNDEFINED && totalLines > DEFAULT_MAX_LINE_COUNT) {
                when {
                    totalLines > currentMaxLines -> ShowMoreButton { currentMaxLines = totalLines }
                    totalLines <= currentMaxLines -> ShowLessButton { currentMaxLines = DEFAULT_MAX_LINE_COUNT }
                }
            }
        }
    }
}

@Composable
private fun DescriptionText(text: String, currentMaxLines: Int) {
    Text(
        text = text,
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.main,
        maxLines = currentMaxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun getTotalLineCount(text: String, widthInPx: Int): Int {
    return Paragraph(
        text = text,
        style = PeraTheme.typography.body.regular.sans,
        constraints = Constraints(maxWidth = widthInPx),
        density = LocalDensity.current,
        fontFamilyResolver = LocalFontFamilyResolver.current
    ).lineCount
}

@Composable
private fun ShowMoreButton(onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        modifier = Modifier.clickableNoRipple(onClick = onClick),
        text = stringResource(R.string.show_more),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.helper.positive
    )
}

@Composable
private fun ShowLessButton(onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        modifier = Modifier.clickableNoRipple(onClick = onClick),
        text = stringResource(R.string.show_less),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.helper.positive
    )
}
