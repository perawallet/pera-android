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

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple

@Composable
fun PeraToolbar(
    modifier: Modifier = Modifier,
    text: String = "",
    secondaryText: String? = null,
    textStyle: PeraToolbarTextStyle = PeraToolbarTextStyle.Default,
    rightLabel: String? = null,
    startContainer: @Composable RowScope.() -> Unit = {},
    centerContainer: (@Composable () -> Unit)? = null,
    endContainer: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 44.dp)
    ) {
        Row(modifier = Modifier.align(Alignment.CenterStart)) {
            startContainer()
        }

        if (centerContainer != null) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                centerContainer()
            }
        } else if (text.isNotEmpty() || secondaryText != null) {
            ToolbarText(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                secondaryText = secondaryText,
                textStyle = textStyle
            )
        }

        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            if (rightLabel != null) {
                Text(
                    text = rightLabel,
                    style = PeraTheme.typography.body.regular.sansMedium,
                    color = PeraTheme.colors.link.primary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
            endContainer()
        }
    }
}

@Composable
fun PeraToolbarIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    contentDescription: String? = null
) {
    Icon(
        modifier = modifier
            .size(40.dp)
            .padding(8.dp),
        painter = painterResource(iconResId),
        tint = PeraTheme.colors.text.main,
contentDescription = contentDescription
    )
}

@Composable
fun PeraToolbarTextButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val textColor = animateColorAsState(if (enabled) PeraTheme.colors.link.primary else PeraTheme.colors.text.gray)
    Text(
        modifier = modifier.clickableNoRipple(enabled) { onClick() },
        text = text,
        color = textColor.value,
        style = PeraTheme.typography.body.regular.sansMedium
    )
}

@Composable
private fun ToolbarText(
    modifier: Modifier = Modifier,
    text: String,
    secondaryText: String? = null,
    textStyle: PeraToolbarTextStyle = PeraToolbarTextStyle.Default
) {
    val primaryTextStyle = when (textStyle) {
        PeraToolbarTextStyle.Default -> PeraTheme.typography.body.regular.sansMedium
        PeraToolbarTextStyle.Large -> PeraTheme.typography.title.large.sansMedium
    }

    if (secondaryText != null) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = primaryTextStyle,
                color = PeraTheme.colors.text.main
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = secondaryText,
                style = PeraTheme.typography.footnote.sans,
                color = PeraTheme.colors.text.gray
            )
        }
    } else {
        Text(
            modifier = modifier,
            text = text,
            style = primaryTextStyle,
            color = PeraTheme.colors.text.main
        )
    }
}
