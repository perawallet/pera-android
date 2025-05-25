package com.algorand.android.ui.compose.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraFootNoteText

@SuppressWarnings("LongMethod")
@Composable
fun AccountTypeStatus(
    accountTypeIcon: Int,
    accountType: String,
    listener: (AccountTypeListener) -> Unit
) {
    AccountTypeStatusImpl(
        accountTypeIcon,
        accountType,
        listener
    )
}

@SuppressWarnings("LongMethod")
@Composable
fun AccountTypeStatusImpl(
    accountTypeIcon: Int,
    accountType: String,
    listener: (AccountTypeListener) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PeraTheme.colors.layer.grayLightest)
            .padding(16.dp)
    ) {

        PeraFootNoteText(text = "Account type", color = PeraTheme.colors.text.gray)

        Row(verticalAlignment = Alignment.CenterVertically) {
            AccountItemView(
                title = accountType,
                subTitle = null,
                accountItemButton = AccountItemButton.UNKNOWN,
                icon = accountTypeIcon,
                iconBackgroundColor = PeraTheme.colors.wallet.wallet4.background,
                iconTintColor = null,
            )
        }
        PeraClickableText(
            text = stringResource(R.string.your_account_is_a_standard),
            style = PeraTheme.typography.footnote.sans,
            onClick = { listener.invoke(AccountTypeListener.LearnMore) })

        Spacer(modifier = Modifier.height(16.dp))

        RekeyOptionButton(stringResource(R.string.rekey_to_ledger_account)) {
            listener.invoke(AccountTypeListener.RekeyToLedgerAccount)
        }
        RekeyOptionButton(stringResource(R.string.rekey_to_standard_account)) {
            listener.invoke(AccountTypeListener.RekeyToStandardAccount)
        }
    }
}

@Composable
fun RekeyOptionButton(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { onClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        PeraBodyText(
            modifier = Modifier.weight(1f), text = text, color = PeraTheme.colors.text.main
        )

        Image(painter = painterResource(R.drawable.ic_right), contentDescription = null)
    }
}

sealed class AccountTypeListener {
    data object RekeyToLedgerAccount : AccountTypeListener()
    data object RekeyToStandardAccount : AccountTypeListener()
    data object LearnMore : AccountTypeListener()
}

@SuppressWarnings("LongMethod")
@PreviewLightDark
@Composable
fun AddressTypePreview() {
    PeraTheme {
        AccountTypeStatus(
            accountType = "Standard",
            accountTypeIcon = R.drawable.ic_wallet
        ) {}
    }
}
