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

package com.algorand.android.ui.compose.theme

import androidx.compose.ui.graphics.Color

object ThemedColors {
    val defaultColor: PeraColor = PeraLightColor

    fun getColorsByMode(isDarkMode: Boolean): PeraColor {
        return if (isDarkMode) PeraDarkColor else PeraLightColor
    }
}

@Suppress("MagicNumber")
object ColorPalette {

    val Transparent = Color(0x00000000)

    object White {
        val Default = Color(0xFFFFFFFF)
        val Alpha12 = Color(0x1FFFFFFF)
        val Alpha16 = Color(0x29FFFFFF)
        val Alpha60 = Color(0x99FFFFFF)
        val Alpha84 = Color(0xD6FFFFFF)
    }

    object Black {
        val Default = Color(0xFF000000)
        val Alpha64 = Color(0xA3000000)
        val Alpha86 = Color(0xDB000000)
    }

    object Turquoise {
        val V900 = Color(0xFF0B4D68)
        val V800 = Color(0xFF136880)
        val V700 = Color(0xFF1F8E9D)
        val V700Alpha10 = V700.copy(alpha = 0.1f)
        val V700Alpha12 = V700.copy(alpha = 0.12f)
        val V700Alpha20 = V700.copy(alpha = 0.2f)
        val V700Alpha28 = V700.copy(alpha = 0.28f)
        val V600 = Color(0xFF2CB7BC)
        val V500 = Color(0xFF3EDBD2)
        val V500Alpha12 = V500.copy(alpha = 0.12f)
        val V500Alpha24 = V500.copy(alpha = 0.24f)
        val V400 = Color(0xFF6BE9D6)
        val V300 = Color(0xFF8BF4DB)
        val V200 = Color(0xFFB2FBE3)
        val V100 = Color(0xFFD8FDEE)
        val V50 = Color(0xFFEBFEF7)
    }

    object Purple {
        val V900 = Color(0xFF231566)
        val V800 = Color(0xFF34207B)
        val V700 = Color(0xFF4C2F99)
        val V600 = Color(0xFF6841B7)
        val V500 = Color(0xFF8755D5)
        val V400 = Color(0xFFAB7CE5)
        val V400Alpha35 = V400.copy(alpha = 0.35f)
        val V300 = Color(0xFFC499F1)
        val V200 = Color(0xFFDDBCF9)
        val V100 = Color(0xFFF0DDFC)
        val V50 = Color(0xFFF7EEFD)
    }

    object Salmon {
        val V900 = Color(0xFF7A1128)
        val V850 = Color(0xFFA91413)
        val V850Alpha20 = V850.copy(alpha = 0.2f)
        val V800 = Color(0xFF931D2D)
        val V700 = Color(0xFFB72D37)
        val V600 = Color(0xFFDB4645)
        val V600Alpha10 = V600.copy(alpha = 0.1f)
        val V500 = Color(0xFFFF6D5F)
        val V500Alpha12 = V500.copy(alpha = 0.12f)
        val V500Alpha24 = V500.copy(alpha = 0.24f)
        val V400 = Color(0xFFFF9B86)
        val V300 = Color(0xFFFFB69F)
        val V200 = Color(0xFFFFD3BE)
        val V100 = Color(0xFFFFECDF)
        val V50 = Color(0xFFFFF5EF)
    }

    object Blush {
        val V900 = Color(0xFF772552)
        val V800 = Color(0xFF8E3B63)
        val V700 = Color(0xFFB15D7D)
        val V600 = Color(0xFFD5859D)
        val V500 = Color(0xFFF8B7C4)
        val V400 = Color(0xFFFAC9CE)
        val V300 = Color(0xFFFCD5D5)
        val V200 = Color(0xFFFEE5E3)
        val V100 = Color(0xFFFEF3F1)
        val V50 = Color(0xFFFFF9F8)

        // These colors are not defined in Figma color palette
        val Wallet1Icon = Color(0xFF9B0C48)
        val Wallet1IconGovernor = Color(0xFF9B1F69)
    }

    object Gray {
        val V900 = Color(0xFF18181B)
        val V900Alpha60 = V900.copy(alpha = 0.6f)
        val V900Alpha90 = V900.copy(alpha = 0.9f)
        val V900Alpha12 = V900.copy(alpha = 0.12f)
        val V800 = Color(0xFF27272A)
        val V700 = Color(0xFF3F3F46)
        val V600 = Color(0xFF52525B)
        val V600Alpha92 = V600.copy(alpha = 0.92f)
        val V500 = Color(0xFF71717A)
        val V500Alpha50 = V500.copy(alpha = 0.5f)
        val V400 = Color(0xFFA1A1AA)
        val V400Alpha50 = V400.copy(alpha = 0.5f)
        val V300 = Color(0xFFD4D4D8)
        val V200 = Color(0xFFE4E4E7)
        val V100 = Color(0xFFF1F1F2)
        val V50 = Color(0xFFFAFAFA)

        // These colors are not defined in Figma color palette
        val HeroBackground = Color(0xFF1D1D21)
        val BottomSheetLine = Color(0xFFE6E7E9)
        val NftIconBackground = Color(0xFF292929)
        val TrustedIconBackgroundOpacity = Color(0xFF291A304A)
    }

    object Yellow {
        val V600 = Color(0xFFC77700)
        val V500 = Color(0xFFEDB21C)
        val V400 = Color(0xFFFFEE55)
        val V400Alpha50 = V400.copy(alpha = 0.5f)
        val V400Alpha20 = V400.copy(alpha = 0.2f)
        val V400Alpha10 = V400.copy(alpha = 0.1f)
        val V400Alpha5 = V400.copy(alpha = 0.05f)
        val V300 = Color(0xFFFFF387)
        val V200 = Color(0xFFFFF8BA)
        val V100 = Color(0xFFFFFBD4)
        val V50 = Color(0xFFFFFDEA)

        // These colors are not defined in Figma color palette
        val Wallet2Icon = Color(0xFFFFEAC2)
    }

    object Blue {
        // These colors are not defined in Figma color palette
        val V900 = Color(0xFF1A304A)
        val V800 = Color(0xFF48A7FE)
        val V700 = Color(0xFFCC1A304A)
    }

    object Pink {
        // These colors are not defined in Figma color palette
        val V900 = Color(0xFFFFAEE3)
    }

    object Navy {
        val V900 = Color(0xFF171835)
        val V800 = Color(0xFF1A304A)
        val V800Alpha80 = V800.copy(alpha = 0.8f)
        val V800Alpha16 = V800.copy(alpha = 0.16f)
    }

    object Red {
        // These colors are not defined in Figma color palette
        val V900 = Color(0xFF29FF6D5F)
    }

    object Discover {
        val HelperPurple = Purple.V400
        val HelperPurpleAlpha35 = Purple.V400Alpha35
        val HelperText = Gray.V900
        val Warning = Salmon.V500
    }

    object Verification {
        val BadgeBackgroundGradientStart = Gray.V800
    }

    object Notification {
        val IconPlaceholderTint = Gray.V400
        val IconPlaceholderBorder = Gray.V800
    }

    object Backup {
        val BannerTitle = Black.Alpha64
        val BannerDescription = Gray.V900
        val BannerIcon = Gray.V900
        val BannerIconBackground = Salmon.V850Alpha20
    }

    object Divider {
        val Dark = Gray.V800
    }

    object Chart {
        val TimeFrameButton = Gray.V800
        val TimeFrameDefaultText = Gray.V500
    }

    object WalletConnect {
        val AccountSelectionBorder = Gray.V800
        val DappIconBackground = Gray.V800
        val DappIconBorder = Transparent
    }

    object TextField {
        val DefaultBackground = Gray.V700
        val TypingColor = Gray.V100
        val FocusedLine = Gray.V100
        val UnfocusedLine = Gray.V700
        val SearchBarIcon = Gray.V500
    }

    object Transaction {
        val ConfirmedText = Turquoise.V600
        val AmountPositive = Turquoise.V600
        val AmountNegative = Salmon.V500
    }

    object Governance {
        val BannerIcon = White.Default
    }

    object Password {
        val UnfilledDigitIcon = Gray.V700
    }

    object Market {
        val NegativeValueBackground = Salmon.V600Alpha10
        val PositiveValueBackground = Turquoise.V500Alpha24
    }

    object Node {
        val ConnectedTestnetText = Yellow.V600
    }
}

interface PeraColor {
    interface Background {
        val primary: Color
        val secondary: Color
        val systemElements: Color
        val hero: Color
        val modality: Color
        val bottomSheetLine: Color
        val backdropModal: Color
    }

    interface Text {
        val main: Color
        val gray: Color
        val grayLighter: Color
    }

    interface Layer {
        val gray: Color
        val grayLighter: Color
        val grayLightest: Color
    }

    interface Link {
        val primary: Color
        val icon: Color
    }

    interface ButtonStyle {
        val background: Color
        val focusBackground: Color
        val disabledBackground: Color
        val text: Color
        val disabledText: Color
    }

    interface FloatButton {
        val background: Color
        val focusBackground: Color
        val iconMain: Color
        val iconLighter: Color
    }

    interface HelperButton {
        val background: Color
        val focusBackground: Color
        val disabledBackground: Color
        val icon: Color
        val disabledIcon: Color
        val peraIcon: Color
    }

    interface SquareButton {
        val background: Color
        val focusBackground: Color
        val secondaryBackground: Color
        val icon: Color
        val secondaryIcon: Color
    }

    interface Button {
        val primary: ButtonStyle
        val secondary: ButtonStyle
        val ghost: ButtonStyle
        val float: FloatButton
        val helper: HelperButton
        val square: SquareButton
        val strokeColor: Color
    }

    interface Status {
        val negative: Color
        val negativeLighter: Color
        val positive: Color
        val positiveLighter: Color
        val success: Color
        val successCheckmark: Color
    }

    interface Banner {
        val background: Color
        val button: Color
        val iconBackground: Color
        val text: Color
    }

    interface WalletStyle {
        val background: Color
        val icon: Color
    }

    interface Governor {
        val wallet1Icon: Color
        val wallet3Icon: Color
        val wallet4Icon: Color
    }

    interface Wallet {
        val wallet1: WalletStyle
        val wallet2: WalletStyle
        val wallet3: WalletStyle
        val wallet4: WalletStyle
        val wallet5: WalletStyle
        val placeholder: WalletStyle
        val governor: Governor
    }

    interface TabBar {
        val button: Color
        val background: Color
        val iconActive: Color
        val iconNonActive: Color
        val iconDisabled: Color
    }

    interface Switch {
        val background: Color
        val offBackground: Color
        val disabledBackground: Color
    }

    interface Nft {
        val iconBackground: Color
        val icon: Color
    }

    interface IconStyle {
        val background: Color
        val inline: Color
        val backgroundOpacity: Color
    }

    interface Icon {
        val trusted: IconStyle
        val verified: IconStyle
        val suspicious: IconStyle
    }

    interface Toast {
        val background: Color
        val title: Color
        val description: Color
    }

    interface Testnet {
        val background: Color
        val text: Color
    }

    interface Algo {
        val background: Color
        val icon: Color
        val iconBackground: Color
    }

    interface Discover {
        val helperPurple: Color
        val helperPurpleAlpha35: Color
        val helperText: Color
        val warning: Color
    }

    interface Verification {
        val badgeBackgroundGradientStart: Color
    }

    interface Notification {
        val iconPlaceholderTint: Color
        val iconPlaceholderBorder: Color
    }

    interface Backup {
        val bannerTitle: Color
        val bannerDescription: Color
        val bannerIcon: Color
        val bannerIconBackground: Color
    }

    interface Chart {
        val timeFrameButton: Color
        val timeFrameDefaultText: Color
    }

    interface WalletConnect {
        val accountSelectionBorder: Color
        val dappIconBackground: Color
        val dappIconBorder: Color
    }

    interface TextField {
        val defaultBackground: Color
        val typingColor: Color
        val focusedLine: Color
        val unfocusedLine: Color
        val searchBarIcon: Color
    }

    interface Transaction {
        val confirmedText: Color
        val amountPositive: Color
        val amountNegative: Color
    }

    interface Governance {
        val bannerIcon: Color
    }

    interface Password {
        val unfilledDigitIcon: Color
    }

    interface Market {
        val negativeValueBackground: Color
        val positiveValueBackground: Color
    }

    interface Node {
        val connectedTestnetText: Color
    }

    interface Divider {
        val dark: Color
    }

    val background: Background
    val text: Text
    val layer: Layer
    val link: Link
    val button: Button
    val status: Status
    val banner: Banner
    val wallet: Wallet
    val tabBar: TabBar
    val switch: Switch
    val nft: Nft
    val icon: Icon
    val toast: Toast
    val testnet: Testnet
    val algo: Algo
    val discover: Discover
    val verification: Verification
    val notification: Notification
    val backup: Backup
    val chart: Chart
    val walletConnect: WalletConnect
    val textField: TextField
    val transaction: Transaction
    val governance: Governance
    val password: Password
    val market: Market
    val node: Node
    val divider: Divider
}
