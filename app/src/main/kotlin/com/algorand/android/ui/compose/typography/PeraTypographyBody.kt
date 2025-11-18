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

package com.algorand.android.ui.compose.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.algorand.android.ui.compose.widget.peraMono
import com.algorand.android.ui.compose.widget.peraSans

@Composable
fun getPeraTypographyBody(): PeraTypography.Body = PeraTypography.Body(
    regular = getBodyRegular(),
    large = getBodyLarge()
)

@Composable
private fun getBodyRegular(): PeraTypography.Body.BodyRegular =
    PeraTypography.Body.BodyRegular(
        sans = TextStyle(
            fontFamily = peraSans,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        sansMedium = TextStyle(
            fontFamily = peraSans,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        sansBold = TextStyle(
            fontFamily = peraSans,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        mono = TextStyle(
            fontFamily = peraMono,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            letterSpacing = (-0.72).sp
        ),
        monoMedium = TextStyle(
            fontFamily = peraMono,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            letterSpacing = (-0.72).sp
        )
    )

@Composable
private fun getBodyLarge(): PeraTypography.Body.BodyLarge =
    PeraTypography.Body.BodyLarge(
        sans = TextStyle(
            fontFamily = peraSans,
            fontWeight = FontWeight.Normal,
            fontSize = 19.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        sansMedium = TextStyle(
            fontFamily = peraSans,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        mono = TextStyle(
            fontFamily = peraMono,
            fontWeight = FontWeight.Normal,
            fontSize = 19.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.72).sp
        )
    )
