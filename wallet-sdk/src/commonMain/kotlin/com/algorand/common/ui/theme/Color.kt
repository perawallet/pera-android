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

import androidx.compose.ui.graphics.Color

object ThemedColors {
    val defaultColor: PeraColor = PeraLightColor

    fun getColorsByMode(isDarkMode: Boolean): PeraColor {
        return if (isDarkMode) PeraDarkColor else PeraLightColor
    }
}

object ColorPalette {

    val Transparent = Color(0x00000000)

    val White = Color(0xFFFFFFFF)
    val WhiteAlpha12 = Color(0x1FFFFFFF)
    val WhiteAlpha16 = Color(0x29FFFFFF)
    val WhiteAlpha60 = Color(0x99FFFFFF)
    val WhiteAlpha84 = Color(0xD6FFFFFF)

    val Black = Color(0xFF000000)
    val BlackAlpha64 = Color(0xA3000000)

    val Turquoise900 = Color(0xFF0B4D68)
    val Turquoise800 = Color(0xFF136880)
    val Turquoise700 = Color(0xFF1F8E9D)
    val Turquoise700Alpha10 = Color(0x1A2CB7BC)
    val Turquoise700Alpha12 = Color(0x1F2CB7BC)
    val Turquoise700Alpha20 = Color(0x331F8E9D)
    val Turquoise700Alpha28 = Color(0x472CB7BC)
    val Turquoise600 = Color(0xFF2CB7BC)
    val Turquoise500 = Color(0xFF3EDBD2)
    val Turquoise500Alpha12 = Color(0x1F3EDBD2)
    val Turquoise500Alpha24 = Color(0x3D3EDBD2)
    val Turquoise400 = Color(0xFF6BE9D6)
    val Turquoise300 = Color(0xFF8BF4DB)
    val Turquoise200 = Color(0xFFB2FBE3)
    val Turquoise100 = Color(0xFFD8FDEE)
    val Turquoise50 = Color(0xFFEBFEF7)

    val Purple900 = Color(0xFF231566)
    val Purple800 = Color(0xFF34207B)
    val Purple700 = Color(0xFF4C2F99)
    val Purple600 = Color(0xFF6841B7)
    val Purple500 = Color(0xFF8755D5)
    val Purple400 = Color(0xFFAB7CE5)
    val Purple300 = Color(0xFFC499F1)
    val Purple200 = Color(0xFFDDBCF9)
    val Purple100 = Color(0xFFF0DDFC)
    val Purple50 = Color(0xFFF7EEFD)

    val Salmon900 = Color(0xFF7A1128)
    val Salmon850 = Color(0xFFA91413)
    val Salmon850Alpha20 = Color(0x33A91413)
    val Salmon800 = Color(0xFF931D2D)
    val Salmon700 = Color(0xFFB72D37)
    val Salmon600 = Color(0xFFDB4645)
    val Salmon600Alpha10 = Color(0x1ADB4645)
    val Salmon500 = Color(0xFFFF6D5F)
    val Salmon500Alpha12 = Color(0x1FFF6D5F)
    val Salmon500Alpha24 = Color(0x3DFF6D5F)
    val Salmon400 = Color(0xFFFF9B86)
    val Salmon300 = Color(0xFFFFB69F)
    val Salmon200 = Color(0xFFFFD3BE)
    val Salmon100 = Color(0xFFFFECDF)
    val Salmon50 = Color(0xFFFFF5EF)

    val Blush900 = Color(0xFF772552)
    val Blush800 = Color(0xFF8E3B63)
    val Blush700 = Color(0xFFB15D7D)
    val Blush600 = Color(0xFFD5859D)
    val Blush500 = Color(0xFFF8B7C4)
    val Blush400 = Color(0xFFFAC9CE)
    val Blush300 = Color(0xFFFCD5D5)
    val Blush200 = Color(0xFFFEE5E3)
    val Blush100 = Color(0xFFFEF3F1)
    val Blush50 = Color(0xFFFFF9F8)

    val Gray900 = Color(0xFF18181B)
    val Gray900Alpha60 = Color(0x9918181B)
    val Gray900Alpha90 = Color(0xE518181B)
    val Gray900Alpha12 = Color(0x1F18181B)
    val Gray800 = Color(0xFF27272A)
    val Gray700 = Color(0xFF3F3F46)
    val Gray600 = Color(0xFF52525B)
    val Gray600Alpha92 = Color(0xEB52525B)
    val Gray500 = Color(0xFF71717A)
    val Gray500Alpha50 = Color(0x8071717A)
    val Gray400 = Color(0xFFA1A1AA)
    val Gray400Alpha50 = Color(0x80A1A1AA)
    val Gray300 = Color(0xFFD4D4D8)
    val Gray200 = Color(0xFFE4E4E7)
    val Gray100 = Color(0xFFF1F1F2)
    val Gray50 = Color(0xFFFAFAFA)

    val Yellow600 = Color(0xFFC77700)
    val Yellow500 = Color(0xFFEDB21C)
    val Yellow400 = Color(0xFFFFEE55)
    val Yellow400Alpha50 = Color(0x80FFEE55)
    val Yellow400Alpha20 = Color(0x33FFEE55)
    val Yellow400Alpha10 = Color(0x1AFFEE55)
    val Yellow400Alpha5 = Color(0x0DFFEE55)
    val Yellow300 = Color(0xFFFFF387)
    val Yellow200 = Color(0xFFFFF8BA)
    val Yellow100 = Color(0xFFFFFBD4)
}

interface PeraColor {

    val background: Color
    val backgroundSecondary: Color
    val systemElements: Color

    val textMain: Color
    val textGray: Color
    val textGrayLighter: Color

    val layerGray: Color
    val layerGrayLighter: Color
    val layerGrayLightest: Color

    val linkPrimary: Color
    val linkIcon: Color

    val buttonPrimaryBg: Color
    val buttonPrimaryFocusBg: Color
    val buttonPrimaryDisabledBg: Color
    val buttonPrimaryText: Color
    val buttonPrimaryDisabledText: Color

    val buttonSecondaryBg: Color
    val buttonSecondaryFocusBg: Color
    val buttonSecondaryDisabledBg: Color
    val buttonSecondaryText: Color
    val buttonSecondaryDisabledText: Color

    val buttonGhostBg: Color
    val buttonGhostFocusBg: Color
    val buttonGhostDisabledBg: Color
    val buttonGhostText: Color
    val buttonGhostDisabledText: Color

    val buttonFloatBg: Color
    val buttonFloatFocusBg: Color
    val buttonFloatIconMain: Color
    val buttonFloatIconLighter: Color

    val buttonHelperBg: Color
    val buttonHelperFocusBg: Color
    val buttonHelperDisabledBg: Color
    val buttonHelperIcon: Color
    val buttonHelperDisabledIcon: Color
    val buttonHelperPeraIcon: Color

    val buttonSquareBg: Color
    val buttonSquareFocusBg: Color
    val buttonSquareSecondaryBg: Color
    val buttonSquareIcon: Color
    val buttonSquareSecondaryIcon: Color

    val negative: Color
    val negativeLighter: Color
    val positive: Color
    val positiveLighter: Color
    val success: Color
    val successCheckmark: Color
    val heroBg: Color

    val bannerBg: Color
    val bannerButton: Color
    val bannerIconBg: Color
    val bannerText: Color

    val wallet1: Color
    val wallet1Icon: Color
    val wallet2: Color
    val wallet2Icon: Color
    val wallet3: Color
    val wallet3Icon: Color
    val wallet4: Color
    val wallet4Icon: Color
    val wallet5: Color
    val wallet5Icon: Color
    val walletPlaceholder: Color
    val walletPlaceholderIcon: Color

    val wallet1IconGovernor: Color
    val wallet3IconGovernor: Color
    val wallet4IconGovernor: Color

    val tabBarButton: Color
    val tabBarBackground: Color
    val tabBarIconActive: Color
    val tabBarIconNonActive: Color
    val tabBarIconDisabled: Color

    val bottomSheetLine: Color

    val modalityBg: Color

    val switchBg: Color
    val switchOffBg: Color
    val switchDisabledBg: Color

    val nftIconBg: Color
    val nftIcon: Color

    val trustedIconBg: Color
    val trustedIconInline: Color
    val trustedIconBgOpacity16: Color

    val verifiedIconBg: Color
    val verifiedIconInline: Color
    val verifiedIconBgOpacity80: Color

    val suspiciousIconBg: Color
    val suspiciousIconInline: Color
    val suspiciousIconBgOpacity16: Color

    val toastBackground: Color
    val toastTitle: Color
    val toastDescription: Color

    val testnetBg: Color
    val testnetText: Color

    val algoIconBg: Color
    val algoIcon: Color

    val buttonStrokeColor: Color
}
