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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.peraMono

private val DialPadButtonSize = 72.dp

private val DialPadDigitTextStyle = TextStyle(
    fontFamily = peraMono,
    fontWeight = FontWeight.Normal,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp
)

@Composable
fun DialPadContainer(
    onDigitClick: (Int) -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier,
    separator: String? = null,
    onSeparatorClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DialPadRow(
            digits = listOf(1, 2, 3),
            onDigitClick = onDigitClick
        )
        DialPadRow(
            digits = listOf(4, 5, 6),
            onDigitClick = onDigitClick
        )
        DialPadRow(
            digits = listOf(7, 8, 9),
            onDigitClick = onDigitClick
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (separator != null) {
                DialPadButton(label = separator, onClick = onSeparatorClick)
            } else {
                Spacer(modifier = Modifier.size(DialPadButtonSize))
            }
            DialPadButton(label = "0", onClick = { onDigitClick(0) })
            DialPadIconButton(
                iconRes = R.drawable.ic_delete,
                onClick = onBackspaceClick
            )
        }
    }
}

@Composable
private fun DialPadRow(
    digits: List<Int>,
    onDigitClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        digits.forEach { digit ->
            DialPadButton(
                label = digit.toString(),
                onClick = { onDigitClick(digit) }
            )
        }
    }
}

@Composable
private fun DialPadButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(DialPadButtonSize)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = DialPadDigitTextStyle,
            color = PeraTheme.colors.text.main
        )
    }
}

@Composable
private fun DialPadIconButton(
    iconRes: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(DialPadButtonSize)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(iconRes),
            tint = PeraTheme.colors.text.main,
            contentDescription = null
        )
    }
}
