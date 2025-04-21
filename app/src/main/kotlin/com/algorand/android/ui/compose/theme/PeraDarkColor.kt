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

object PeraDarkColor : PeraColor {
    override val background = object : PeraColor.Background {
        override val primary = ColorPalette.Gray.V900
        override val secondary = ColorPalette.Gray.V800
        override val systemElements = ColorPalette.White.Default
        override val hero = ColorPalette.Gray.V900
        override val modality = ColorPalette.Black.Default
        override val bottomSheetLine = ColorPalette.Gray.V200
        override val backdropModal = ColorPalette.Black.Alpha86
    }

    override val text = object : PeraColor.Text {
        override val main = ColorPalette.Gray.V100
        override val gray = ColorPalette.Gray.V400
        override val grayLighter = ColorPalette.Gray.V500
    }

    override val layer = object : PeraColor.Layer {
        override val gray = ColorPalette.Gray.V700
        override val grayLighter = ColorPalette.Gray.V800
        override val grayLightest = ColorPalette.Gray.V800
    }

    override val link = object : PeraColor.Link {
        override val primary = ColorPalette.Yellow.V400
        override val icon = ColorPalette.Gray.V100
    }

    override val button = object : PeraColor.Button {
        override val primary = object : PeraColor.ButtonStyle {
            override val background = ColorPalette.Yellow.V400
            override val focusBackground = ColorPalette.Yellow.V500
            override val disabledBackground = ColorPalette.Gray.V800
            override val text = ColorPalette.Gray.V900
            override val disabledText = ColorPalette.Gray.V500
        }

        override val secondary = object : PeraColor.ButtonStyle {
            override val background = ColorPalette.Gray.V800
            override val focusBackground = ColorPalette.Gray.V900
            override val disabledBackground = ColorPalette.Gray.V800
            override val text = ColorPalette.Gray.V100
            override val disabledText = ColorPalette.Gray.V500
        }

        override val ghost = object : PeraColor.ButtonStyle {
            override val background = ColorPalette.Gray.V900
            override val focusBackground = ColorPalette.Gray.V800
            override val disabledBackground = ColorPalette.Gray.V900
            override val text = ColorPalette.Gray.V100
            override val disabledText = ColorPalette.Gray.V500
        }

        override val float = object : PeraColor.FloatButton {
            override val background = ColorPalette.White.Default
            override val focusBackground = ColorPalette.Gray.V100
            override val iconMain = ColorPalette.Gray.V900
            override val iconLighter = ColorPalette.Gray.V900
        }

        override val helper = object : PeraColor.HelperButton {
            override val background = ColorPalette.Yellow.V400Alpha10
            override val focusBackground = ColorPalette.Yellow.V400Alpha20
            override val disabledBackground = ColorPalette.Yellow.V400Alpha5
            override val icon = ColorPalette.Yellow.V400
            override val disabledIcon = ColorPalette.Yellow.V400Alpha50
            override val peraIcon = ColorPalette.Yellow.V400
        }

        override val square = object : PeraColor.SquareButton {
            override val background = ColorPalette.Turquoise.V700Alpha12
            override val focusBackground = ColorPalette.Turquoise.V700Alpha28
            override val secondaryBackground = ColorPalette.Gray.V800
            override val icon = ColorPalette.Turquoise.V600
            override val secondaryIcon = ColorPalette.Gray.V500
        }

        override val strokeColor = ColorPalette.Gray.V800
    }

    override val status = object : PeraColor.Status {
        override val negative = ColorPalette.Salmon.V500
        override val negativeLighter = ColorPalette.Salmon.V500Alpha12
        override val positive = ColorPalette.Turquoise.V600
        override val positiveLighter = ColorPalette.Turquoise.V500Alpha12
        override val success = ColorPalette.Yellow.V400
        override val successCheckmark = ColorPalette.Gray.V900
    }

    override val banner = object : PeraColor.Banner {
        override val background = ColorPalette.Gray.V800
        override val button = ColorPalette.White.Alpha12
        override val iconBackground = ColorPalette.Turquoise.V700Alpha20
        override val text = ColorPalette.White.Default
    }

    override val wallet = object : PeraColor.Wallet {
        override val wallet1 = object : PeraColor.WalletStyle {
            override val background = ColorPalette.Blush.V600
            override val icon = ColorPalette.Blush.V900
        }

        override val wallet2 = object : PeraColor.WalletStyle {
            override val background = ColorPalette.Salmon.V500
            override val icon = ColorPalette.Yellow.V200
        }

        override val wallet3 = object : PeraColor.WalletStyle {
            override val background = ColorPalette.Purple.V500
            override val icon = ColorPalette.Pink.V900
        }

        override val wallet4 = object : PeraColor.WalletStyle {
            override val background = ColorPalette.Turquoise.V300
            override val icon = ColorPalette.Turquoise.V800
        }

        override val wallet5 = object : PeraColor.WalletStyle {
            override val background = ColorPalette.Salmon.V400
            override val icon = ColorPalette.Navy.V900
        }

        override val placeholder = object : PeraColor.WalletStyle {
            override val background = ColorPalette.Gray.V800
            override val icon = ColorPalette.Gray.V400
        }

        override val governor = object : PeraColor.Governor {
            override val wallet1Icon = ColorPalette.Blush.V900
            override val wallet3Icon = ColorPalette.Purple.V500
            override val wallet4Icon = ColorPalette.Turquoise.V800
        }
    }

    override val tabBar = object : PeraColor.TabBar {
        override val button = ColorPalette.Gray.V800
        override val background = ColorPalette.Gray.V900
        override val iconActive = ColorPalette.Gray.V50
        override val iconNonActive = ColorPalette.Gray.V500
        override val iconDisabled = ColorPalette.Gray.V500Alpha50
    }

    override val switch = object : PeraColor.Switch {
        override val background = ColorPalette.Yellow.V500
        override val offBackground = ColorPalette.Gray.V800
        override val disabledBackground = ColorPalette.Gray.V400Alpha50
    }

    override val nft = object : PeraColor.Nft {
        override val iconBackground = ColorPalette.Gray.V900Alpha60
        override val icon = ColorPalette.White.Default
    }

    override val icon = object : PeraColor.Icon {
        override val trusted = object : PeraColor.IconStyle {
            override val background = ColorPalette.Turquoise.V600
            override val inline = ColorPalette.Gray.V900
            override val backgroundOpacity = ColorPalette.Gray.V900
        }

        override val verified = object : PeraColor.IconStyle {
            override val background = ColorPalette.Blue.V900
            override val inline = ColorPalette.Blue.V800
            override val backgroundOpacity = ColorPalette.Blue.V700
        }

        override val suspicious = object : PeraColor.IconStyle {
            override val background = ColorPalette.Salmon.V500
            override val inline = ColorPalette.Gray.V900
            override val backgroundOpacity = ColorPalette.Red.V900
        }
    }

    override val toast = object : PeraColor.Toast {
        override val background = ColorPalette.Gray.V600Alpha92
        override val title = ColorPalette.White.Default
        override val description = ColorPalette.White.Alpha60
    }

    override val testnet = object : PeraColor.Testnet {
        override val background = ColorPalette.Yellow.V500
        override val text = ColorPalette.Gray.V900
    }

    override val algo = object : PeraColor.Algo {
        override val background = ColorPalette.Black.Default
        override val icon = ColorPalette.White.Default
        override val iconBackground = ColorPalette.Black.Default
    }

    override val discover = object : PeraColor.Discover {
        override val helperPurple = ColorPalette.Discover.HelperPurple
        override val helperPurpleAlpha35 = ColorPalette.Discover.HelperPurpleAlpha35
        override val helperText = ColorPalette.Discover.HelperText
        override val warning = ColorPalette.Discover.Warning
    }

    override val verification = object : PeraColor.Verification {
        override val badgeBackgroundGradientStart = ColorPalette.Verification.BadgeBackgroundGradientStart
    }

    override val notification = object : PeraColor.Notification {
        override val iconPlaceholderTint = ColorPalette.Notification.IconPlaceholderTint
        override val iconPlaceholderBorder = ColorPalette.Notification.IconPlaceholderBorder
    }

    override val backup = object : PeraColor.Backup {
        override val bannerTitle = ColorPalette.Backup.BannerTitle
        override val bannerDescription = ColorPalette.Backup.BannerDescription
        override val bannerIcon = ColorPalette.Backup.BannerIcon
        override val bannerIconBackground = ColorPalette.Backup.BannerIconBackground
    }

    override val chart = object : PeraColor.Chart {
        override val timeFrameButton = ColorPalette.Chart.TimeFrameButton
        override val timeFrameDefaultText = ColorPalette.Chart.TimeFrameDefaultText
    }

    override val walletConnect = object : PeraColor.WalletConnect {
        override val accountSelectionBorder = ColorPalette.WalletConnect.AccountSelectionBorder
        override val dappIconBackground = ColorPalette.WalletConnect.DappIconBackground
        override val dappIconBorder = ColorPalette.WalletConnect.DappIconBorder
    }

    override val textField = object : PeraColor.TextField {
        override val defaultBackground = ColorPalette.TextField.DefaultBackground
        override val typingColor = ColorPalette.TextField.TypingColor
        override val focusedLine = ColorPalette.TextField.FocusedLine
        override val unfocusedLine = ColorPalette.TextField.UnfocusedLine
        override val searchBarIcon = ColorPalette.TextField.SearchBarIcon
    }

    override val transaction = object : PeraColor.Transaction {
        override val confirmedText = ColorPalette.Transaction.ConfirmedText
        override val amountPositive = ColorPalette.Transaction.AmountPositive
        override val amountNegative = ColorPalette.Transaction.AmountNegative
    }

    override val governance = object : PeraColor.Governance {
        override val bannerIcon = ColorPalette.Governance.BannerIcon
    }

    override val password = object : PeraColor.Password {
        override val unfilledDigitIcon = ColorPalette.Password.UnfilledDigitIcon
    }

    override val market = object : PeraColor.Market {
        override val negativeValueBackground = ColorPalette.Market.NegativeValueBackground
        override val positiveValueBackground = ColorPalette.Market.PositiveValueBackground
    }

    override val node = object : PeraColor.Node {
        override val connectedTestnetText = ColorPalette.Node.ConnectedTestnetText
    }

    override val divider = object : PeraColor.Divider {
        override val dark = ColorPalette.Divider.Dark
    }
}
