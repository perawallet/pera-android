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

package com.algorand.android.ui.compose.widget.quickaction

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme

@Composable
private fun QuickActionButton(
    @DrawableRes iconResId: Int,
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    iconTint: Color,
    showIndicator: Boolean
) {
    Column(
        modifier = Modifier.clickable(remember { MutableInteractionSource() }, indication = null) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color = backgroundColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(iconResId),
                contentDescription = text,
                tint = iconTint
            )
            if (showIndicator) {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp, end = 12.dp)
                        .size(4.dp)
                        .background(color = PeraTheme.colors.helper.negative, shape = CircleShape)
                        .align(Alignment.TopEnd)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = PeraTheme.typography.footnote.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
}

@Composable
fun PrimaryQuickActionButton(
    @DrawableRes iconResId: Int,
    text: String,
    showIndicator: Boolean = false,
    onClick: () -> Unit
) {
    QuickActionButton(
        iconResId = iconResId,
        text = text,
        onClick = onClick,
        backgroundColor = PeraTheme.colors.button.helper.background,
        iconTint = PeraTheme.colors.button.helper.icon,
        showIndicator = showIndicator
    )
}

@Composable
fun SecondaryQuickActionButton(
    @DrawableRes iconResId: Int,
    text: String,
    showIndicator: Boolean = false,
    onClick: () -> Unit
) {
    QuickActionButton(
        iconResId = iconResId,
        text = text,
        onClick = onClick,
        backgroundColor = PeraTheme.colors.layer.grayLighter,
        iconTint = PeraTheme.colors.text.main,
        showIndicator = showIndicator
    )
}
