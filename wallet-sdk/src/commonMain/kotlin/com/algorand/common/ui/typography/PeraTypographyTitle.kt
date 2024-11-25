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

import algorand_android.wallet_sdk.generated.resources.Res
import algorand_android.wallet_sdk.generated.resources.dmmono_medium
import algorand_android.wallet_sdk.generated.resources.dmmono_regular
import algorand_android.wallet_sdk.generated.resources.dmsans_bold
import algorand_android.wallet_sdk.generated.resources.dmsans_medium
import algorand_android.wallet_sdk.generated.resources.dmsans_regular
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

@Composable
internal fun getPeraTypographyTitle(): PeraTypography.Title {
    return PeraTypography.Title(
        regular = getPeraTypographyTitleRegular(),
        large = getPeraTypographyTitleLarge(),
        small = getPeraTypographyTitleSmall()
    )
}

@Composable
private fun getPeraTypographyTitleRegular(): PeraTypography.Title.TitleRegular {
    val titleStyle = TextStyle(
        fontSize = 32.sp,
        lineHeight = 40.sp,
    )
    return PeraTypography.Title.TitleRegular(
        sans = titleStyle.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_regular, FontWeight.Normal))
        ),
        sansMedium = titleStyle.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_medium, FontWeight.Medium))
        ),
        sansBold = titleStyle.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_bold, FontWeight.Bold))
        )
    )
}

@Composable
private fun getPeraTypographyTitleLarge(): PeraTypography.Title.TitleLarge {
    val titleStyle = TextStyle(
        fontSize = 36.sp,
        lineHeight = 48.sp,
    )
    return PeraTypography.Title.TitleLarge(
        sans = titleStyle.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_regular, FontWeight.Normal))
        ),
        sansMedium = titleStyle.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_medium, FontWeight.Medium))
        ),
        mono = titleStyle.copy(
            fontFamily = FontFamily(Font(Res.font.dmmono_regular, FontWeight.Normal))
        ),
        monoMedium = titleStyle.copy(
            fontFamily = FontFamily(Font(Res.font.dmmono_medium, FontWeight.Medium))
        )
    )
}

@Composable
private fun getPeraTypographyTitleSmall(): PeraTypography.Title.TitleSmall {
    val titleStyle = TextStyle(
        fontSize = 28.sp,
        lineHeight = 32.sp,
    )
    return PeraTypography.Title.TitleSmall(
        sans = titleStyle.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_regular, FontWeight.Normal))
        ),
        sansMedium = titleStyle.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_medium, FontWeight.Medium))
        )
    )
}
