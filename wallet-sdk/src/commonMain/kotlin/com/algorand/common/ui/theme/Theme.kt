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

package com.algorand.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.algorand.common.ui.typography.PeraTypography

val LocalCustomColors = staticCompositionLocalOf {
    ThemedColors.defaultColor
}

val LocalThemeIsDark = compositionLocalOf { mutableStateOf(true) }

@Composable
fun PeraTheme(
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()
    val isDarkState = remember { mutableStateOf(systemIsDark) }
    val customColors = ThemedColors.getColorsByMode(isDarkState.value)
    CompositionLocalProvider(
        LocalThemeIsDark provides isDarkState,
        LocalCustomColors provides customColors
    ) {
        val isDark by isDarkState
        SystemAppearance(!isDark)
        content()
    }
}

@Composable
internal expect fun SystemAppearance(isDark: Boolean)

object PeraTheme {
    val colors: PeraColor
        @Composable
        get() = LocalCustomColors.current

    val typography
        @Composable
        get() = PeraTypography()
}
