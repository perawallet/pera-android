package com.algorand.android.ui.compose.widget

import android.content.Context
import android.graphics.drawable.Drawable
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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import coil.compose.AsyncImage
import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.AccountStatusDetailViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraFootNoteText
import com.algorand.android.utils.AccountIconDrawable

@SuppressWarnings("LongMethod")
@Composable
fun AccountTypeStatus(
    state: ViewState.Content,
    listener: (AccountTypeListener) -> Unit
) {
    AccountTypeStatusImpl(
        state,
        listener
    )
}

@SuppressWarnings("LongMethod")
@Composable
fun AccountTypeStatusImpl(
    state: ViewState.Content,
    listener: (AccountTypeListener) -> Unit
) {
    val context = LocalContext.current
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
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    modifier = Modifier.padding(6.dp),
                    model = getDrawable(context, state),
                    contentDescription = "Wallet"
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            PeraBodyText(
                text = state.accountTypeString.toString(),
                color = PeraTheme.colors.text.main
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        PeraClickableText(
            text = stringResource(R.string.your_account_is_a_standard),
            style = PeraTheme.typography.footnote.sans,
            onClick = { listener.invoke(AccountTypeListener.LearnMore) })

        Spacer(modifier = Modifier.height(16.dp))
        if (state.isRekeyToLedgerAccountVisible == true) {
            RekeyOptionButton(stringResource(R.string.rekey_to_ledger_account)) {
                listener.invoke(AccountTypeListener.RekeyToLedgerAccount)
            }
        }
        if (state.isRekeyToStandardAccountVisible == true) {
            RekeyOptionButton(stringResource(R.string.rekey_to_standard_account)) {
                listener.invoke(AccountTypeListener.RekeyToStandardAccount)
            }
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

fun getDrawable(context: Context, state: ViewState.Content): Drawable? {
    state.accountTypeDrawablePreview?.let { drawablePreview ->
       return AccountIconDrawable.create(
            context,
            R.dimen.spacing_xxxxlarge,
            drawablePreview
        )
    }
    return ResourcesCompat.getDrawable(context.resources,
         R.drawable.ic_wallet, null)
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
    val s = ViewState.Content(
        descriptionDetail = ViewState.Content.DescriptionDetail(
            annotatedString = AnnotatedString(R.string.address),
            hyperlinkUrl = ""
        )
    )
    PeraTheme {
        AccountTypeStatus(state = s) {
        }
    }
}
