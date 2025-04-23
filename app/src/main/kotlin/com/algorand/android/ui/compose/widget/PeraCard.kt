package com.algorand.android.ui.compose.widget
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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraHighlightedText
import com.algorand.android.ui.compose.widget.text.PeraLinkText
import com.algorand.android.ui.compose.widget.text.PeraTitleText

@SuppressWarnings("LongMethod")
@Composable
fun PeraCard(
    title: String,
    description: String,
    footer: String,
    highlighted: String? = null,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = 24.dp,
                bottom = 24.dp
            ),
        onClick = onClick,
        shape = CardDefaults.outlinedShape,
        border = CardDefaults.outlinedCardBorder(enabled = true),
        colors = CardDefaults.outlinedCardColors(containerColor = PeraTheme.colors.background.primary),
    ) {
        Column(Modifier.padding(all = 20.dp)) {
            Row {
                PeraTitleText(text = title)
                highlighted?.let {
                    PeraHighlightedText(
                        text = it
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PeraBodyText(modifier = Modifier.weight(1f), text = description)
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(40.dp)
                        .clip(shape = CircleShape)
                        .background(color = PeraTheme.colors.layer.grayLighter)
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        tint = PeraTheme.colors.text.grayLighter,
                        contentDescription = "Right Arrow"
                    )
                }
            }
            PeraLinkText(text = footer)
        }
    }
}

@PreviewLightDark
@Composable
fun PeraCardPreview() {
    PeraCard(
        title = stringResource(R.string.mnemonic_type_algo25_title),
        description = stringResource(R.string.mnemonic_type_algo25_description),
        footer = stringResource(R.string.mnemonic_type_algo25_footer),
        highlighted = stringResource(R.string.recommended),
        onClick = { }
    )
}
