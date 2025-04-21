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
internal fun getPeraTypographyCaption(): PeraTypography.Caption {
    val caption = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
    return PeraTypography.Caption(
        sans = caption.copy(
            fontFamily = FontFamily(Font(R.font.dmsans_regular, FontWeight.Normal)),
        ),
        sansMedium = caption.copy(
            fontFamily = FontFamily(Font(R.font.dmsans_medium, FontWeight.Medium)),
        ),
        sansBold = caption.copy(
            fontFamily = FontFamily(Font(R.font.dmsans_bold, FontWeight.Bold)),
        ),
        mono = caption.copy(
            fontFamily = FontFamily(Font(R.font.dmmono_regular, FontWeight.Normal)),
        ),
        monoMedium = caption.copy(
            fontFamily = FontFamily(Font(R.font.dmmono_medium, FontWeight.Medium)),
        )
    )
}
