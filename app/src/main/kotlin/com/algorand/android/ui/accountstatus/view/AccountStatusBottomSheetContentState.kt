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

package com.algorand.android.ui.accountstatus.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.algorand.android.R
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content.AccountAction.RekeyToLedger
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content.AccountAction.RekeyToStandard
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content.AccountAction.RescanRekeyedAddresses
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content.DescriptionDetail
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.utils.AccountIconDrawable

@Composable
internal fun AccountStatusBottomSheetContentState(
    state: Content,
    listener: AccountStatusBottomSheetContentStateListener
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        MainTitleText(text = state.mainTitle)
        Spacer(modifier = Modifier.height(28.dp))
        AccountStatusAccountTypeCard(
            state,
            listener::onUndoRekeyClick,
            listener::onCopyAddress,
            listener::onScanRegisteredAddressesClick
        )
        Spacer(modifier = Modifier.height(28.dp))
        AccountTypeContainer(state, listener::onLearnMoreClick)
        Spacer(modifier = Modifier.height(12.dp))
        AccountActionsContainer(
            state.accountActions,
            listener::onRekeyToLedgerClick,
            listener::onRekeyToStandardClick,
            listener::onRescanRekeyedAddressesClick
        )
    }
}

@Composable
private fun MainTitleText(text: String) {
    Text(
        text = text,
        style = PeraTheme.typography.title.small.sansMedium,
        color = PeraTheme.colors.text.main,
    )
}

@Composable
private fun AccountTypeTitleText() {
    Text(
        text = stringResource(R.string.account_type),
        style = PeraTheme.typography.footnote.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun AccountTypeContainer(state: Content, onLearnMoreClick: (String) -> Unit) {
    AccountTypeTitleText()
    Spacer(modifier = Modifier.height(12.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier.size(32.dp),
            bitmap = AccountIconDrawable.create(
                context = LocalContext.current,
                accountIconDrawablePreview = state.accountTypeIconDrawablePreview,
                sizeResId = R.dimen.spacing_xxlarge
            ).toBitmap().asImageBitmap(),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = state.accountTypeDisplayName,
            style = PeraTheme.typography.body.regular.sansMedium,
            color = PeraTheme.colors.text.main
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    AccountTypeDescriptionText(state.description, onLearnMoreClick)
}

@Composable
private fun AccountTypeDescriptionText(descriptionDetail: DescriptionDetail, onLearnMoreClick: (String) -> Unit) {
    val annotatedString = buildAnnotatedString {
        append(descriptionDetail.description)
        val textLinkStyle = TextLinkStyles(
            SpanStyle(
                color = PeraTheme.colors.link.primary,
                fontStyle = PeraTheme.typography.footnote.sansMedium.fontStyle,
                fontWeight = PeraTheme.typography.footnote.sansMedium.fontWeight
            )
        )
        val link = LinkAnnotation.Url(descriptionDetail.hyperlinkUrl, textLinkStyle) {
            val url = (it as LinkAnnotation.Url).url
            onLearnMoreClick(url)
        }
        withLink(link) {
            append(" ")
            append(descriptionDetail.hyperlinkText)
        }
    }

    Text(
        text = annotatedString,
        style = PeraTheme.typography.footnote.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun AccountActionsContainer(
    accountActions: List<Content.AccountAction>,
    onRekeyToLedgerClick: () -> Unit,
    onRekeyToStandardClick: () -> Unit,
    onRescanRekeyedAddressesClick: () -> Unit
) {
    Column {
        accountActions.forEach { action ->
            when (action) {
                RekeyToLedger -> {
                    AccountActionItem(stringResource(R.string.rekey_to_ledger_account), onRekeyToLedgerClick)
                }

                RekeyToStandard -> {
                    AccountActionItem(stringResource(R.string.rekey_to_standard_account), onRekeyToStandardClick)
                }

                RescanRekeyedAddresses -> {
                    AccountActionItem(stringResource(R.string.rescan_rekeyed_accounts), onRescanRekeyedAddressesClick)
                }
            }
        }
    }
}

@Composable
private fun AccountActionItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = title,
            style = PeraTheme.typography.body.regular.sans,
            modifier = Modifier.weight(1f),
            color = PeraTheme.colors.text.main
        )
        Icon(
            painter = painterResource(R.drawable.ic_right_arrow),
            contentDescription = null,
            tint = PeraTheme.colors.text.grayLighter,
        )
    }
}

interface AccountStatusBottomSheetContentStateListener {
    fun onCopyAddress(address: String)
    fun onUndoRekeyClick()
    fun onRekeyToLedgerClick()
    fun onRekeyToStandardClick()
    fun onRescanRekeyedAddressesClick()
    fun onScanRegisteredAddressesClick()
    fun onLearnMoreClick(url: String)
}
