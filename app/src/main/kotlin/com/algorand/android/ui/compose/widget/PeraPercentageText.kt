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

package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import java.text.DecimalFormat
import kotlin.math.absoluteValue

@Composable
fun PeraPercentageText(modifier: Modifier = Modifier, percentage: Float) {
    val formattedText = DecimalFormat().apply { maximumFractionDigits = 2 }.format(percentage.absoluteValue)
    val textColor = when {
        percentage > 0f -> PeraTheme.colors.helper.positive
        percentage < 0f -> PeraTheme.colors.helper.negative
        else -> PeraTheme.colors.text.gray
    }

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        when {
            percentage > 0f -> {
                ArrowIcon(R.drawable.ic_arrow_up_line, PeraTheme.colors.helper.positive)
                Spacer(modifier = Modifier.width(6.dp))
            }
            percentage < 0f -> {
                ArrowIcon(R.drawable.ic_arrow_down_line, PeraTheme.colors.helper.negative)
                Spacer(modifier = Modifier.width(6.dp))
            }
        }
        Text(
            text = formattedText,
            style = PeraTheme.typography.body.regular.sansMedium,
            color = textColor
        )
    }
}

@Composable
private fun ArrowIcon(iconRes: Int, color: Color) {
    Icon(
        modifier = Modifier
            .size(20.dp)
            .background(color = color.copy(alpha = .1f), shape = CircleShape)
            .padding(2.dp),
        painter = painterResource(iconRes),
        tint = color,
        contentDescription = null
    )
}
