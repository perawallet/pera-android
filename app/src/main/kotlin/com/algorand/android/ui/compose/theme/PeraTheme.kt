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

package com.algorand.android.ui.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.algorand.android.ui.compose.typography.PeraTypography
import com.algorand.android.ui.compose.typography.peraTypography

val localPeraColors = staticCompositionLocalOf {
    ThemedColors.defaultColor
}

@Composable
fun PeraTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val peraColors = ThemedColors.getColorsByMode(isDarkTheme)
    CompositionLocalProvider(
        localPeraColors provides peraColors,
        content = content
    )
}

object PeraTheme {
    val colors: PeraColor
        @Composable
        get() = localPeraColors.current

    val typography: PeraTypography
        @Composable
        get() = peraTypography()
}
