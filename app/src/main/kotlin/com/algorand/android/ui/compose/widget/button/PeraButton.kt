/*
 * Copyright (c) 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.compose.widget.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator

@Composable
private fun PeraCoreButton(modifier: PeraButtonModifier) {
    Button(
        onClick = { modifier.onClick() },
        modifier = modifier.modifier.height(48.dp),
        shape = ShapeDefaults.ExtraSmall,
        colors = modifier.colors,
        enabled = modifier.state == PeraButtonState.ENABLED
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (modifier.state) {
                PeraButtonState.PROGRESS -> {
                    PeraCircularProgressIndicator(
                        color = if (modifier.state == PeraButtonState.DISABLED) {
                            modifier.colors.disabledContentColor
                        } else {
                            modifier.colors.contentColor
                        }
                    )
                }

                else -> {
                    if (modifier.leftIcon != null) {
                        modifier.leftIcon.invoke()
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(
                        text = modifier.text,
                        color = if (modifier.state == PeraButtonState.DISABLED) {
                            modifier.colors.disabledContentColor
                        } else {
                            modifier.colors.contentColor
                        }
                    )
                    if (modifier.rightIcon != null) {
                        Spacer(modifier = Modifier.width(16.dp))
                        modifier.rightIcon.invoke()
                    }
                }
            }
        }
    }
}

@Composable
fun PeraPrimaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    state: PeraButtonState = PeraButtonState.ENABLED,
    leftIcon: @Composable (() -> Unit)? = null,
    rightIcon: @Composable (() -> Unit)? = null
) {
    PeraCoreButton(
        modifier = PeraButtonModifier(
            modifier = modifier,
            onClick = onClick,
            text = text,
            colors = ButtonDefaults.buttonColors(
                containerColor = PeraTheme.colors.button.primary.background,
                disabledContainerColor = PeraTheme.colors.button.primary.disabledBackground,
                contentColor = PeraTheme.colors.button.primary.text,
                disabledContentColor = PeraTheme.colors.button.primary.disabledText
            ),
            state = state,
            leftIcon = leftIcon,
            rightIcon = rightIcon
        )
    )
}

@Composable
fun PeraSecondaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    state: PeraButtonState = PeraButtonState.ENABLED,
    leftIcon: @Composable (() -> Unit)? = null,
    rightIcon: @Composable (() -> Unit)? = null
) {
    PeraCoreButton(
        modifier = PeraButtonModifier(
            modifier = modifier,
            onClick = onClick,
            text = text,
            colors = ButtonDefaults.buttonColors(
                containerColor = PeraTheme.colors.button.secondary.background,
                disabledContainerColor = PeraTheme.colors.button.secondary.disabledBackground,
                contentColor = PeraTheme.colors.button.secondary.text,
                disabledContentColor = PeraTheme.colors.button.secondary.disabledText
            ),
            state = state,
            leftIcon = leftIcon,
            rightIcon = rightIcon
        )
    )
}
