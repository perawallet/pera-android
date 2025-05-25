package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType

@PreviewLightDark
@Composable
fun StandardAccPreview() {
    PeraTheme {
        AccountStatusScreen(
            modifier = Modifier,
            title = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            subTitle = null,
            accountRegistrationType = AccountRegistrationType.Algo25,
            accountRekeyRegistrationType = null,
            icon = R.drawable.ic_wallet,
            iconBackgroundColor = PeraTheme.colors.wallet.wallet4.background,
            accountType = "Standard",
            accountTypeIcon = R.drawable.ic_wallet
        )
    }
}

@PreviewLightDark
@Composable
fun StandardRekeyedPreview() {
    PeraTheme {
        AccountStatusScreen(
            modifier = Modifier,
            title = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            accountRegistrationType = AccountRegistrationType.Algo25,
            accountRekeyRegistrationType = AccountRegistrationType.Algo25,
            icon = R.drawable.ic_wallet,
            iconBackgroundColor = PeraTheme.colors.wallet.wallet4.background,
            accountType = "Rekeyed",
            accountTypeIcon = R.drawable.ic_wallet
        )
    }
}

@PreviewLightDark
@Composable
fun HdToHdRekeyedPreview() {
    PeraTheme {
        AccountStatusScreen(
            modifier = Modifier,
            title = "Wallet #1",
            subTitle = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            accountRegistrationType = AccountRegistrationType.HdKey,
            accountRekeyRegistrationType = AccountRegistrationType.HdKey,
            icon = R.drawable.ic_wallet,
            iconBackgroundColor = PeraTheme.colors.wallet.wallet4.background,
            accountType = "Universal Wallet",
            accountTypeIcon = R.drawable.ic_wallet
        )
    }
}

@PreviewLightDark
@Composable
fun HdToStandardRekeyedPreview() {
    PeraTheme {
        AccountStatusScreen(
            modifier = Modifier,
            title = "Wallet #1",
            subTitle = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            accountRegistrationType = AccountRegistrationType.HdKey,
            accountRekeyRegistrationType = AccountRegistrationType.Algo25,
            icon = R.drawable.ic_wallet,
            iconBackgroundColor = PeraTheme.colors.wallet.wallet4.background,
            accountType = "Universal Wallet",
            accountTypeIcon = R.drawable.ic_wallet
        )
    }
}

@Composable
fun AccountStatusScreen(
    modifier: Modifier,
    title: String,
    subTitle: String? = null,
    accountRegistrationType: AccountRegistrationType,
    accountRekeyRegistrationType: AccountRegistrationType?,
    icon: Int,
    iconBackgroundColor: Color,
    accountType: String,
    accountTypeIcon: Int
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = PeraTheme.colors.layer.grayLightest)
    ) {
        AddressCard(
            modifier = Modifier,
            title = title,
            subTitle = subTitle,
            accountRegistrationType = accountRegistrationType,
            accountRekeyRegistrationType = accountRekeyRegistrationType,
            icon = icon,
            iconBackgroundColor = iconBackgroundColor,
        ) {}
        AccountTypeStatus(
            accountTypeIcon,
            accountType,
        ) {}
    }
}
