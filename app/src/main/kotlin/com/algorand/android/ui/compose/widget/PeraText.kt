/*
 * Copyright 2022 Pera Wallet, LDA
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.algorand.android.R

@Composable
fun PeraHeadlineText(modifier: Modifier = Modifier, text: String) {
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
fun PeraTitleText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontFamily = peraSans,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
fun PeraBodyText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontFamily = peraSans,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Medium,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun PeraLinkText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontFamily = peraSans,
        color = MaterialTheme.colorScheme.outline,
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
            contentDescription = "error"
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
