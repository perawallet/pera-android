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

package com.algorand.common.ui.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

data class PeraTypography(
    val title: Title,
    val body: Body,
    val footnote: Footnote,
    val caption: Caption
) {

    data class Title(
        val regular: TitleRegular,
        val large: TitleLarge,
        val small: TitleSmall
    ) {
        data class TitleRegular(
            val sans: TextStyle,
            val sansMedium: TextStyle,
            val sansBold: TextStyle,
        )

        data class TitleLarge(
            val sans: TextStyle,
            val sansMedium: TextStyle,
            val mono: TextStyle,
            val monoMedium: TextStyle
        )

        data class TitleSmall(
            val sans: TextStyle,
            val sansMedium: TextStyle
        )
    }

    data class Body(
        val regular: BodyRegular,
        val large: BodyLarge
    ) {
        data class BodyRegular(
            val sans: TextStyle,
            val sansMedium: TextStyle,
            val sansBold: TextStyle,
            val mono: TextStyle,
            val monoMedium: TextStyle
        )

        data class BodyLarge(
            val sans: TextStyle,
            val sansMedium: TextStyle,
            val mono: TextStyle
        )
    }

    data class Footnote(
        val sans: TextStyle,
        val sansBold: TextStyle,
        val sansMedium: TextStyle,
        val mono: TextStyle,
        val monoMedium: TextStyle
    )

    data class Caption(
        val sans: TextStyle,
        val sansBold: TextStyle,
        val sansMedium: TextStyle,
        val mono: TextStyle,
        val monoMedium: TextStyle
    )
}

@Composable
fun PeraTypography() = PeraTypography(
    title = getPeraTypographyTitle(),
    body = getPeraTypographyBody(),
    footnote = getPeraTypographyFootnote(),
    caption = getPeraTypographyCaption()
)
