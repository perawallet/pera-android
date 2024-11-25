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
internal fun getPeraTypographyBody(): PeraTypography.Body {
    return PeraTypography.Body(
        regular = getPeraTypographyBodyRegular(),
        large = getPeraTypographyBodyLarge()
    )
}

@Composable
private fun getPeraTypographyBodyRegular(): PeraTypography.Body.BodyRegular {
    val body = TextStyle(
        fontSize = 15.sp,
        lineHeight = 24.sp
    )
    return PeraTypography.Body.BodyRegular(
        sans = body.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_regular, FontWeight.Normal))
        ),
        sansMedium = body.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_medium, FontWeight.Medium))
        ),
        sansBold = body.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_bold, FontWeight.Bold))
        ),
        mono = body.copy(
            fontFamily = FontFamily(Font(Res.font.dmmono_regular, FontWeight.Normal))
        ),
        monoMedium = body.copy(
            fontFamily = FontFamily(Font(Res.font.dmmono_medium, FontWeight.Medium))
        )
    )
}

@Composable
private fun getPeraTypographyBodyLarge(): PeraTypography.Body.BodyLarge {
    val body = TextStyle(
        fontSize = 19.sp,
        lineHeight = 28.sp
    )
    return PeraTypography.Body.BodyLarge(
        sans = body.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_regular, FontWeight.Normal))
        ),
        sansMedium = body.copy(
            fontFamily = FontFamily(Font(Res.font.dmsans_medium, FontWeight.Medium))
        ),
        mono = body.copy(
            fontFamily = FontFamily(Font(Res.font.dmmono_regular, FontWeight.Normal))
        )
    )
}
