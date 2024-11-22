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

object PeraLightColor : PeraColor {

    override val background = ColorPalette.White
    override val backgroundSecondary = ColorPalette.Gray50
    override val systemElements = ColorPalette.Black

    override val textMain = ColorPalette.Gray900
    override val textGray = ColorPalette.Gray500
    override val textGrayLighter = ColorPalette.Gray400

    override val layerGray = ColorPalette.Gray200
    override val layerGrayLighter = ColorPalette.Gray100
    override val layerGrayLightest = ColorPalette.Gray50

    override val linkIcon = ColorPalette.Turquoise600
    override val linkPrimary = ColorPalette.Turquoise700

    override val buttonPrimaryBg = ColorPalette.Gray800
    override val buttonPrimaryFocusBg = ColorPalette.Gray700
    override val buttonPrimaryDisabledBg = ColorPalette.Gray100
    override val buttonPrimaryText = ColorPalette.White
    override val buttonPrimaryDisabledText = ColorPalette.Gray500

    override val buttonSecondaryBg = ColorPalette.Gray100
    override val buttonSecondaryFocusBg = ColorPalette.Gray200
    override val buttonSecondaryDisabledBg = ColorPalette.Gray100
    override val buttonSecondaryText = ColorPalette.Gray900
    override val buttonSecondaryDisabledText = ColorPalette.Gray500

    override val buttonGhostBg = ColorPalette.White
    override val buttonGhostFocusBg = ColorPalette.Gray100
    override val buttonGhostDisabledBg = ColorPalette.White
    override val buttonGhostText = ColorPalette.Gray900
    override val buttonGhostDisabledText = ColorPalette.Gray500

    override val buttonFloatBg = ColorPalette.White
    override val buttonFloatFocusBg = ColorPalette.Gray100
    override val buttonFloatIconMain = ColorPalette.Gray900
    override val buttonFloatIconLighter = ColorPalette.White

    override val buttonHelperBg = ColorPalette.Gray800
    override val buttonHelperFocusBg = ColorPalette.Gray700
    override val buttonHelperDisabledBg = ColorPalette.Gray100
    override val buttonHelperIcon = ColorPalette.White
    override val buttonHelperDisabledIcon = ColorPalette.Gray500
    override val buttonHelperPeraIcon = ColorPalette.Yellow400

    override val buttonSquareBg = ColorPalette.Turquoise700Alpha12
    override val buttonSquareFocusBg = ColorPalette.Turquoise700Alpha28
    override val buttonSquareSecondaryBg = ColorPalette.Gray100
    override val buttonSquareIcon = ColorPalette.Turquoise700
    override val buttonSquareSecondaryIcon = ColorPalette.Gray500

    override val negative = ColorPalette.Salmon600
    override val negativeLighter = ColorPalette.Salmon100
    override val positive = ColorPalette.Turquoise700
    override val positiveLighter = ColorPalette.Turquoise100
    override val success = ColorPalette.Turquoise600
    override val successCheckmark = ColorPalette.White
    override val heroBg = ColorPalette.Gray50

    override val bannerBg = ColorPalette.Turquoise600
    override val bannerButton = ColorPalette.WhiteAlpha12
    override val bannerIconBg = ColorPalette.Turquoise700Alpha20
    override val bannerText = ColorPalette.White

    override val wallet1 = ColorPalette.Blush600
    override val wallet1Icon = ColorPalette.Blush900
    override val wallet2 = ColorPalette.Salmon500
    override val wallet2Icon = ColorPalette.White
    override val wallet3 = ColorPalette.Purple500
    override val wallet3Icon = ColorPalette.White
    override val wallet4 = ColorPalette.Turquoise300
    override val wallet4Icon = ColorPalette.Turquoise800
    override val wallet5 = ColorPalette.Salmon400
    override val wallet5Icon = Color(0xFF424F76)
    override val walletPlaceholder = ColorPalette.Gray100
    override val walletPlaceholderIcon = ColorPalette.Gray500

    override val wallet1IconGovernor = Color(0xFF9B1F69)
    override val wallet3IconGovernor = ColorPalette.Purple500
    override val wallet4IconGovernor = ColorPalette.Turquoise700

    override val tabBarButton = ColorPalette.Gray800
    override val tabBarBackground = ColorPalette.White
    override val tabBarIconActive = ColorPalette.Gray900
    override val tabBarIconNonActive = ColorPalette.Gray400
    override val tabBarIconDisabled = ColorPalette.Gray400Alpha50

    override val bottomSheetLine = ColorPalette.Gray200

    override val modalityBg = ColorPalette.Gray900

    override val switchBg = ColorPalette.Turquoise600
    override val switchOffBg = ColorPalette.Gray400
    override val switchDisabledBg = ColorPalette.Gray400Alpha50

    override val nftIconBg = ColorPalette.Gray900Alpha60
    override val nftIcon = ColorPalette.White

    override val trustedIconBg = ColorPalette.Turquoise600
    override val trustedIconInline = ColorPalette.White
    override val trustedIconBgOpacity16 = Color(0x292CB7BC)

    override val verifiedIconBg = ColorPalette.Turquoise100
    override val verifiedIconInline = ColorPalette.Turquoise700
    override val verifiedIconBgOpacity80 = Color(0xCCCEEEFE)

    override val suspiciousIconBg = ColorPalette.Salmon500
    override val suspiciousIconInline = ColorPalette.White
    override val suspiciousIconBgOpacity16 = Color(0x29FF6D5F)

    override val toastBackground = ColorPalette.Gray900
    override val toastTitle = ColorPalette.White
    override val toastDescription = ColorPalette.WhiteAlpha60

    override val testnetBg = ColorPalette.Yellow500
    override val testnetText = ColorPalette.Gray900

    override val algoIconBg = ColorPalette.Black
    override val algoIcon = ColorPalette.White

    override val buttonStrokeColor = ColorPalette.Gray100
}
