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

object PeraDarkColor : PeraColor {

    override val background = ColorPalette.Gray900
    override val backgroundSecondary = ColorPalette.Gray800
    override val systemElements = ColorPalette.White

    override val textMain = ColorPalette.Gray100
    override val textGray = ColorPalette.Gray400
    override val textGrayLighter = ColorPalette.Gray500

    override val layerGray = ColorPalette.Gray700
    override val layerGrayLighter = ColorPalette.Gray800
    override val layerGrayLightest = ColorPalette.Gray800

    override val linkIcon = ColorPalette.Gray100
    override val linkPrimary = ColorPalette.Yellow400

    override val buttonPrimaryBg = ColorPalette.Yellow400
    override val buttonPrimaryFocusBg = ColorPalette.Yellow500
    override val buttonPrimaryDisabledBg = ColorPalette.Gray800
    override val buttonPrimaryText = ColorPalette.Gray900
    override val buttonPrimaryDisabledText = ColorPalette.Gray500

    override val buttonSecondaryBg = ColorPalette.Gray800
    override val buttonSecondaryFocusBg = ColorPalette.Gray900
    override val buttonSecondaryDisabledBg = ColorPalette.Gray800
    override val buttonSecondaryText = ColorPalette.Gray100
    override val buttonSecondaryDisabledText = ColorPalette.Gray500

    override val buttonGhostBg = ColorPalette.Gray900
    override val buttonGhostFocusBg = ColorPalette.Gray800
    override val buttonGhostDisabledBg = ColorPalette.Gray900
    override val buttonGhostText = ColorPalette.Gray100
    override val buttonGhostDisabledText = ColorPalette.Gray500

    override val buttonFloatBg = ColorPalette.White
    override val buttonFloatFocusBg = ColorPalette.Gray100
    override val buttonFloatIconMain = ColorPalette.Gray900
    override val buttonFloatIconLighter = ColorPalette.Gray900

    override val buttonHelperBg = ColorPalette.Yellow400
    override val buttonHelperFocusBg = ColorPalette.Yellow400Alpha20
    override val buttonHelperDisabledBg = ColorPalette.Yellow400Alpha5
    override val buttonHelperIcon = ColorPalette.Yellow400
    override val buttonHelperDisabledIcon = ColorPalette.Yellow400Alpha50
    override val buttonHelperPeraIcon = ColorPalette.Yellow400

    override val buttonSquareBg = ColorPalette.Turquoise700Alpha12
    override val buttonSquareFocusBg = ColorPalette.Turquoise700Alpha28
    override val buttonSquareSecondaryBg = ColorPalette.Gray800
    override val buttonSquareIcon = ColorPalette.Turquoise600
    override val buttonSquareSecondaryIcon = ColorPalette.Gray500

    override val negative = ColorPalette.Salmon500
    override val negativeLighter = ColorPalette.Salmon500Alpha12
    override val positive = ColorPalette.Turquoise600
    override val positiveLighter = ColorPalette.Turquoise500Alpha12
    override val success = ColorPalette.Yellow400
    override val successCheckmark = ColorPalette.Gray900
    override val heroBg = Color(0xFF1D1D21)

    override val bannerBg = ColorPalette.Gray800
    override val bannerButton = ColorPalette.WhiteAlpha12
    override val bannerIconBg = ColorPalette.Turquoise700Alpha20
    override val bannerText = ColorPalette.White

    override val wallet1 = ColorPalette.Blush600
    override val wallet1Icon = Color(0xFF9B0C48)
    override val wallet2 = ColorPalette.Salmon500
    override val wallet2Icon = Color(0xFFFFEAC2)
    override val wallet3 = ColorPalette.Purple500
    override val wallet3Icon = Color(0xFFFFAEE3)
    override val wallet4 = ColorPalette.Turquoise300
    override val wallet4Icon = ColorPalette.Turquoise800
    override val wallet5 = ColorPalette.Salmon400
    override val wallet5Icon = Color(0xFF424F76)
    override val walletPlaceholder = ColorPalette.Gray800
    override val walletPlaceholderIcon = ColorPalette.Gray400

    override val wallet1IconGovernor = Color(0xFF9B1F69)
    override val wallet3IconGovernor = ColorPalette.Purple500
    override val wallet4IconGovernor = ColorPalette.Turquoise800

    override val tabBarButton = ColorPalette.Gray800
    override val tabBarBackground = ColorPalette.Gray900
    override val tabBarIconActive = ColorPalette.Gray50
    override val tabBarIconNonActive = ColorPalette.Gray500
    override val tabBarIconDisabled = ColorPalette.Gray500Alpha50

    override val bottomSheetLine = Color(0xFFE6E7E9)

    override val modalityBg = ColorPalette.Black

    override val switchBg = ColorPalette.Yellow500
    override val switchOffBg = ColorPalette.Gray800
    override val switchDisabledBg = ColorPalette.Gray400Alpha50

    override val nftIconBg = Color(0xFF292929)
    override val nftIcon = ColorPalette.White

    override val trustedIconBg = ColorPalette.Turquoise600
    override val trustedIconInline = ColorPalette.Gray900
    override val trustedIconBgOpacity16 = Color(0xFF291A304A)

    override val verifiedIconBg = Color(0xFF1A304A)
    override val verifiedIconInline = Color(0xFF48A7FE)
    override val verifiedIconBgOpacity80 = Color(0xFFCC1A304A)

    override val suspiciousIconBg = ColorPalette.Salmon500
    override val suspiciousIconInline = ColorPalette.Gray900
    override val suspiciousIconBgOpacity16 = Color(0xFF29FF6D5F)

    override val toastBackground = ColorPalette.Gray600Alpha92
    override val toastTitle = ColorPalette.White
    override val toastDescription = ColorPalette.WhiteAlpha60

    override val testnetBg = ColorPalette.Yellow500
    override val testnetText = ColorPalette.Gray900

    override val algoIconBg = ColorPalette.Black
    override val algoIcon = ColorPalette.White

    override val buttonStrokeColor = ColorPalette.Gray800
}
