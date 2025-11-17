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

    val Transparent: Color = Color(0x00000000)

    object White {
        val Default: Color = Color(0xFFFFFFFF)
        val Alpha12: Color = Color(0x1FFFFFFF)
        val Alpha16: Color = Color(0x29FFFFFF)
        val Alpha60: Color = Color(0x99FFFFFF)
        val Alpha84: Color = Color(0xD6FFFFFF)
    }

    object Black {
        val Default: Color = Color(0xFF000000)
        val Alpha64: Color = Color(0xA3000000)
        val Alpha86: Color = Color(0xDB000000)
    }

    object Turquoise {
        val V900: Color = Color(0xFF0B4D68)
        val V800: Color = Color(0xFF136880)
        val V700: Color = Color(0xFF1F8E9D)
        val V700Alpha10: Color = V700.copy(alpha = 0.1f)
        val V700Alpha12: Color = V700.copy(alpha = 0.12f)
        val V700Alpha20: Color = V700.copy(alpha = 0.2f)
        val V700Alpha28: Color = V700.copy(alpha = 0.28f)
        val V600: Color = Color(0xFF2CB7BC)
        val V600Alpha12: Color = V600.copy(alpha = 0.12f)
        val V500: Color = Color(0xFF3EDBD2)
        val V500Alpha12: Color = V500.copy(alpha = 0.12f)
        val V500Alpha24: Color = V500.copy(alpha = 0.24f)
        val V400: Color = Color(0xFF6BE9D6)
        val V300: Color = Color(0xFF8BF4DB)
        val V200: Color = Color(0xFFB2FBE3)
        val V100: Color = Color(0xFFD8FDEE)
        val V50: Color = Color(0xFFEBFEF7)
    }

    object Purple {
        val V900: Color = Color(0xFF231566)
        val V800: Color = Color(0xFF34207B)
        val V700: Color = Color(0xFF4C2F99)
        val V600: Color = Color(0xFF6841B7)
        val V500: Color = Color(0xFF8755D5)
        val V400: Color = Color(0xFFAB7CE5)
        val V400Alpha35: Color = V400.copy(alpha = 0.35f)
        val V300: Color = Color(0xFFC499F1)
        val V200: Color = Color(0xFFDDBCF9)
        val V100: Color = Color(0xFFF0DDFC)
        val V50: Color = Color(0xFFF7EEFD)
    }

    object Salmon {
        val V900: Color = Color(0xFF7A1128)
        val V850: Color = Color(0xFFA91413)
        val V850Alpha20: Color = V850.copy(alpha = 0.2f)
        val V800: Color = Color(0xFF931D2D)
        val V700: Color = Color(0xFFB72D37)
        val V600: Color = Color(0xFFDB4645)
        val V600Alpha10: Color = V600.copy(alpha = 0.1f)
        val V500: Color = Color(0xFFFF6D5F)
        val V500Alpha12: Color = V500.copy(alpha = 0.12f)
        val V500Alpha24: Color = V500.copy(alpha = 0.24f)
        val V400: Color = Color(0xFFFF9B86)
        val V300: Color = Color(0xFFFFB69F)
        val V200: Color = Color(0xFFFFD3BE)
        val V100: Color = Color(0xFFFFECDF)
        val V50: Color = Color(0xFFFFF5EF)
    }

    object Blush {
        val V900: Color = Color(0xFF772552)
        val V800: Color = Color(0xFF8E3B63)
        val V700: Color = Color(0xFFB15D7D)
        val V600: Color = Color(0xFFD5859D)
        val V500: Color = Color(0xFFF8B7C4)
        val V400: Color = Color(0xFFFAC9CE)
        val V300: Color = Color(0xFFFCD5D5)
        val V200: Color = Color(0xFFFEE5E3)
        val V100: Color = Color(0xFFFEF3F1)
        val V50: Color = Color(0xFFFFF9F8)
    }

    object Gray {
        val V900: Color = Color(0xFF18181B)
        val V900Alpha60: Color = V900.copy(alpha = 0.6f)
        val V900Alpha90: Color = V900.copy(alpha = 0.9f)
        val V900Alpha12: Color = V900.copy(alpha = 0.12f)
        val V800: Color = Color(0xFF27272A)
        val V700: Color = Color(0xFF3F3F46)
        val V600: Color = Color(0xFF52525B)
        val V600Alpha92: Color = V600.copy(alpha = 0.92f)
        val V500: Color = Color(0xFF71717A)
        val V500Alpha50: Color = V500.copy(alpha = 0.5f)
        val V400: Color = Color(0xFFA1A1AA)
        val V400Alpha50: Color = V400.copy(alpha = 0.5f)
        val V300: Color = Color(0xFFD4D4D8)
        val V200: Color = Color(0xFFE4E4E7)
        val V100: Color = Color(0xFFF1F1F2)
        val V50: Color = Color(0xFFFAFAFA)
    }

    object Shimmer {
        val HighlightLight: Color = Color(0xFFE4E4E7)
        val BaseLight: Color = Color(0xFFF2F2F3)

        val HighlightDark: Color = Color(0xFF1F1F23)
        val BaseDark: Color = Color(0xFF25252A)
    }

    object Yellow {
        val V600: Color = Color(0xFFC77700)
        val V500: Color = Color(0xFFEDB21C)
        val V400: Color = Color(0xFFFFEE55)
        val V400Alpha50: Color = V400.copy(alpha = 0.5f)
        val V400Alpha20: Color = V400.copy(alpha = 0.2f)
        val V400Alpha10: Color = V400.copy(alpha = 0.1f)
        val V400Alpha5: Color = V400.copy(alpha = 0.05f)
        val V300: Color = Color(0xFFFFF387)
        val V200: Color = Color(0xFFFFF8BA)
        val V100: Color = Color(0xFFFFFBD4)
        val V50: Color = Color(0xFFFFFDEA)
    }

    object Blue {
        // These colors are not defined in Figma color palette
        val V900: Color = Color(0xFF1A304A)
        val V800: Color = Color(0xFF48A7FE)
        val V700: Color = Color(0xCC1A304A)
    }

    object Pink {
        // These colors are not defined in Figma color palette
        val V900: Color = Color(0xFFFFAEE3)
    }

    object Navy {
        val V900: Color = Color(0xFF171835)
        val V800: Color = Color(0xFF1A304A)
        val V800Alpha80: Color = V800.copy(alpha = 0.8f)
        val V800Alpha16: Color = V800.copy(alpha = 0.16f)
    }

    object Red {
        // These colors are not defined in Figma color palette
        val V900: Color = Color(0x29FF6D5F)
    }

    object Others {
        // These colors are not defined in Figma color palette
        val V100: Color = Color(0xFF424F76)
        val V200: Color = Color(0xFF9B0C48)
        val V300: Color = Color(0XFFFFEAC2)
    }

    object Discover {
        val HelperPurple: Color = Purple.V400
        val HelperPurpleAlpha35: Color = Purple.V400Alpha35
        val HelperText: Color = Gray.V900
        val Warning: Color = Salmon.V500
    }

    object Verification {
        val BadgeBackgroundGradientStart: Color = Gray.V800
    }

    object Notification {
        val IconPlaceholderTint: Color = Gray.V400
        val IconPlaceholderBorder: Color = Gray.V800
    }

    object Backup {
        val BannerTitle: Color = Black.Alpha64
        val BannerDescription: Color = Gray.V900
        val BannerIcon: Color = Gray.V900
        val BannerIconBackground: Color = Salmon.V850Alpha20
    }

    object Divider {
        val Dark: Color = Gray.V800
    }

    object Chart {
        val TimeFrameButton: Color = Gray.V800
        val TimeFrameDefaultText: Color = Gray.V500
    }

    object WalletConnect {
        val AccountSelectionBorder: Color = Gray.V800
        val DappIconBackground: Color = Gray.V800
        val DappIconBorder: Color = Transparent
    }

    object TextField {
        val DefaultBackground: Color = Gray.V700
        val TypingColor: Color = Gray.V100
        val FocusedLine: Color = Gray.V100
        val UnfocusedLine: Color = Gray.V700
        val SearchBarIcon: Color = Gray.V500
    }

    object Transaction {
        val ConfirmedText: Color = Turquoise.V600
        val AmountPositive: Color = Turquoise.V600
        val AmountNegative: Color = Salmon.V500
    }

    object Governance {
        val BannerIcon: Color = White.Default
    }

    object Password {
        val UnfilledDigitIcon: Color = Gray.V700
    }

    object Market {
        val NegativeValueBackground: Color = Salmon.V600Alpha10
        val PositiveValueBackground: Color = Turquoise.V500Alpha24
    }

    object Node {
        val ConnectedTestnetText: Color = Yellow.V600
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

    interface Helper {
        val positive: Color
        val positiveLighter: Color
        val negative: Color
        val negativeLighter: Color
        val success: Color
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

    interface Swap {
        val assetOutButtonBackground: Color
    }

    interface Shimmer {
        val highlight: Color
        val base: Color
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
    val helper: Helper
    val swap: Swap
    val shimmer: Shimmer
}
