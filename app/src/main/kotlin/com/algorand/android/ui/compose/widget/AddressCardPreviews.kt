package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType

@PreviewLightDark
@Composable
fun PeraIconHighlightedTextPreview() {
    PeraTheme {
        PeraIconHighlightedText(
            modifier = Modifier.padding(start = 37.dp),
            text = stringResource(R.string.scan_new_addresses)
        )
    }
}

@PreviewLightDark
@Composable
fun StandardAccountPreview() {
    PeraTheme {
        Column {
            AddressCard(
                modifier = Modifier,
                title = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
                subTitle = null,
                accountRegistrationType = AccountRegistrationType.Algo25,
                accountRekeyRegistrationType = null,
                icon = R.drawable.ic_wallet,
                iconBackgroundColor = PeraTheme.colors.wallet.wallet4.background,
                onAccountItemButtonClick = { },
            )
        }
    }
}

@PreviewLightDark
@Composable
fun HDAccountPreview() {
    PeraTheme {
        AddressCard(
            modifier = Modifier,
            title = "HD Wallet #1",
            subTitle = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            accountRegistrationType = AccountRegistrationType.HdKey,
            accountRekeyRegistrationType = null,
            icon = R.drawable.ic_wallet_address,
            iconBackgroundColor = PeraTheme.colors.wallet.wallet4.background,
            onAccountItemButtonClick = { },
        )
    }
}

@PreviewLightDark
@Composable
fun StandardRekeyPreview() {
    PeraTheme {
        AddressCard(
            modifier = Modifier,
            title = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            subTitle = null,
            accountRegistrationType = AccountRegistrationType.Algo25,
            accountRekeyRegistrationType = AccountRegistrationType.Algo25,
            icon = R.drawable.ic_wallet,
            iconBackgroundColor = PeraTheme.colors.wallet.wallet4.background,
            onAccountItemButtonClick = { },
        )
    }
}

@PreviewLightDark
@Composable
fun HdRekeyPreview() {
    PeraTheme {
        AddressCard(
            modifier = Modifier,
            title = "HD Wallet #1",
            subTitle = "CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5CJR5",
            accountRegistrationType = AccountRegistrationType.HdKey,
            accountRekeyRegistrationType = AccountRegistrationType.Algo25,
            icon = R.drawable.ic_wallet,
            iconBackgroundColor = PeraTheme.colors.wallet.wallet4.background,
            onAccountItemButtonClick = { },
        )
    }
}
