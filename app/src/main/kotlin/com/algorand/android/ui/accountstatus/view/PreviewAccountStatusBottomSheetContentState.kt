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

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.algorand.android.models.AccountIconResource
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content
import com.algorand.android.ui.compose.theme.PeraTheme

@Suppress("EmptyFunctionBlock")
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun PreviewAccountStatusBottomSheetContentState(
    @PreviewParameter(AccountStatusBottomSheetPreviewProvider::class) content: Content
) {
    val listener = object : AccountStatusBottomSheetContentStateListener {
        override fun onCopyAddress(address: String) {}
        override fun onUndoRekeyClick() {}
        override fun onRekeyToLedgerClick() {}
        override fun onRekeyToStandardClick() {}
        override fun onRekeyToJointAccountClick() {}
        override fun onRescanRekeyedAddressesClick() {}
        override fun onScanRegisteredAddressesClick() {}
        override fun onLearnMoreClick(url: String) {}
    }
    PeraTheme {
        AccountStatusBottomSheetContentState(content, listener)
    }
}

private class AccountStatusBottomSheetPreviewProvider : PreviewParameterProvider<Content> {

    override val values: Sequence<Content>
        get() = listOf(
            createStandardPreviewContent(),
            createHdKeyPreviewContent(),
            createNoAuthPreviewContent()
        ).asSequence()
}

private fun createStandardPreviewContent(): Content {
    return Content(
        mainTitle = "Standard account",
        accountDisplayName = createAccountDisplayName(),
        iconDrawablePreview = createAccountIconDrawablePreview(AccountIconResource.STANDARD),
        accountTypeIconDrawablePreview = createAccountIconDrawablePreview(AccountIconResource.STANDARD),
        accountTypeDisplayName = "Standard",
        accountActions = createAccountActions(),
        description = createAlgo25Description(),
        detail = Content.AccountStatusTypeDetail.Algo25,
        rekeyAuthDetail = null,
    )
}

private fun createHdKeyPreviewContent(): Content {
    return Content(
        mainTitle = "Wallet Address",
        accountDisplayName = createAccountDisplayName(),
        iconDrawablePreview = createAccountIconDrawablePreview(AccountIconResource.HD),
        accountTypeIconDrawablePreview = createAccountIconDrawablePreview(AccountIconResource.HD),
        accountTypeDisplayName = "Universal Wallet",
        accountActions = createAccountActions(),
        description = createHdKeyDescription(),
        detail = createHdKeyDetail(),
        rekeyAuthDetail = createRekeyAuthDetail(),
    )
}

private fun createNoAuthPreviewContent(): Content {
    return Content(
        mainTitle = "No Auth account",
        accountDisplayName = createAccountDisplayName(),
        iconDrawablePreview = createAccountIconDrawablePreview(AccountIconResource.WATCH),
        accountTypeIconDrawablePreview = createAccountIconDrawablePreview(AccountIconResource.WATCH),
        accountTypeDisplayName = "No Auth",
        accountActions = emptyList(),
        description = createNoAuthDescription(),
        detail = Content.AccountStatusTypeDetail.NoAuth,
        rekeyAuthDetail = null,
    )
}

private fun createRekeyAuthDetail(): Content.RekeyAuthDetail {
    return Content.RekeyAuthDetail(
        AccountDisplayName(
            accountAddress = "AKPO54D7IIYXNOGDIKEV2UXPXWLGXC64SZKW35US7UVZFUIKCWW5DKPFBM",
            primaryDisplayName = "AKPO...PFBM",
            secondaryDisplayName = "Algo25"
        ),
        AccountIconDrawablePreview(
            backgroundColorResId = AccountIconResource.STANDARD.backgroundColorResId,
            iconTintResId = AccountIconResource.STANDARD.iconTintResId,
            iconResId = AccountIconResource.STANDARD.iconResId
        ),
        canSignTransaction = true
    )
}

private fun createAccountDisplayName(): AccountDisplayName {
    return AccountDisplayName(
        accountAddress = "AKPO54D7IIYXNOGDIKEV2UXPXWLGXC64SZKW35US7UVZFUIKCWW5DKPFBM",
        primaryDisplayName = "Main Account",
        secondaryDisplayName = "AKPO...PFBM"
    )
}

private fun createAccountIconDrawablePreview(resource: AccountIconResource): AccountIconDrawablePreview {
    return AccountIconDrawablePreview(
        backgroundColorResId = resource.backgroundColorResId,
        iconTintResId = resource.iconTintResId,
        iconResId = resource.iconResId
    )
}

private fun createAlgo25Description(): Content.DescriptionDetail {
    return Content.DescriptionDetail(
        description = """
                Your account is a standard Algorand account. It has not been rekeyed and 
                it is not associated with a Ledger device. It was added to your wallet with its keys.
            """.trimIndent(),
        hyperlinkText = "Learn More",
        hyperlinkUrl = ""
    )
}

private fun createHdKeyDescription(): Content.DescriptionDetail {
    return Content.DescriptionDetail(
        description = "Wallet that lets you derive new accounts, all using same key",
        hyperlinkText = "Learn More",
        hyperlinkUrl = ""
    )
}

private fun createNoAuthDescription(): Content.DescriptionDetail {
    return Content.DescriptionDetail(
        description = """
            This account was not added to your wallet with its keys. 
            It is read-only and cannot interact with other accounts or authorize transactions.
        """.trimIndent(),
        hyperlinkText = "Learn More",
        hyperlinkUrl = ""
    )
}

private fun createHdKeyDetail(): Content.AccountStatusTypeDetail.HdKey {
    return Content.AccountStatusTypeDetail.HdKey(
        seedId = 1,
        walletName = "HD Wallet #1",
        iconDrawablePreview = AccountIconDrawablePreview(
            backgroundColorResId = AccountIconResource.HD.backgroundColorResId,
            iconTintResId = AccountIconResource.HD.iconTintResId,
            iconResId = AccountIconResource.HD.iconResId
        ),
    )
}

private fun createAccountActions(): List<Content.AccountAction> {
    return listOf(
        Content.AccountAction.RekeyToLedger,
        Content.AccountAction.RekeyToStandard,
        Content.AccountAction.RescanRekeyedAddresses
    )
}
