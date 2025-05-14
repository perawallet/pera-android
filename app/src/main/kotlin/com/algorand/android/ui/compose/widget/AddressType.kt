package com.algorand.android.ui.compose.widget

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
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
fun AccountTypeStatus(accountTypeString: String) {
    AccountTypeStatusImpl(
        accountTypeString
    )
}

@SuppressWarnings("LongMethod")
@Composable
fun AccountTypeStatusImpl(accountTypeString: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = PeraTheme.colors.layer.grayLightest)
            .padding(16.dp)
    ) {

        PeraFootNoteText(text = "Account type", color = PeraTheme.colors.text.gray)

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = PeraTheme.colors.icon.trusted.background, shape = CircleShape
                    ), contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.padding(6.dp),
                    painter = painterResource(R.drawable.ic_wallet),
                    contentDescription = "Wallet",
                    colorFilter = ColorFilter.tint(color = PeraTheme.colors.text.main)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            PeraBodyText(text = accountTypeString, color = PeraTheme.colors.text.main)
        }
        Spacer(modifier = Modifier.height(12.dp))

        PeraClickableText(
            text = stringResource(R.string.your_account_is_a_standard),
            style = PeraTheme.typography.footnote.sans,
            onClick = { Log.d("Mithilesh", "Click") })

        Spacer(modifier = Modifier.height(16.dp))
        RekeyOptionButton(stringResource(R.string.rekey_to_ledger_account))
        RekeyOptionButton(stringResource(R.string.rekey_to_standard_account))
    }
}

@Composable
fun RekeyOptionButton(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable {},
        verticalAlignment = Alignment.CenterVertically
    ) {
        PeraBodyText(
            modifier = Modifier.weight(1f), text = text, color = PeraTheme.colors.text.main
        )

        Image(painter = painterResource(R.drawable.ic_right), contentDescription = null)
    }
}

@SuppressWarnings("LongMethod")
@PreviewLightDark
@Composable
fun AddressTypePreview() {
    PeraTheme {
        AccountTypeStatus("Standard")
    }
}
