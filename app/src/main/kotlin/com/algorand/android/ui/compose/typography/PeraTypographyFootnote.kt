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

package com.algorand.android.ui.compose.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.algorand.android.R

@Composable
fun getPeraTypographyFootnote(): PeraTypography.Footnote {
    val footnote = TextStyle(
        fontSize = 13.sp,
        lineHeight = 20.sp
    )
    return PeraTypography.Footnote(
        sans = footnote.copy(
            fontFamily = FontFamily(Font(R.font.dmsans_regular, FontWeight.Normal)),
        ),
        sansBold = footnote.copy(
            fontFamily = FontFamily(Font(R.font.dmsans_bold, FontWeight.Bold)),
        ),
        sansMedium = footnote.copy(
            fontFamily = FontFamily(Font(R.font.dmsans_medium, FontWeight.Medium)),
        ),
        mono = footnote.copy(
            fontFamily = FontFamily(Font(R.font.dmmono_regular, FontWeight.Normal)),
        ),
        monoMedium = footnote.copy(
            fontFamily = FontFamily(Font(R.font.dmmono_medium, FontWeight.Medium)),
        )
    )
}
