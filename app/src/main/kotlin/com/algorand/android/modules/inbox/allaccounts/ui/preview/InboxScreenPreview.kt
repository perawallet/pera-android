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

package com.algorand.android.modules.inbox.allaccounts.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.AccountIconDrawablePreviews
import com.algorand.android.modules.inbox.allaccounts.domain.model.InboxWithAccount
import com.algorand.android.modules.inbox.allaccounts.domain.model.SignatureRequestInboxItem
import com.algorand.android.modules.inbox.allaccounts.ui.InboxScreen
import com.algorand.android.modules.inbox.allaccounts.ui.InboxScreenListener
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxPreview
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.ColorPalette
import com.algorand.android.ui.compose.theme.PeraTheme
import kotlinx.coroutines.flow.MutableStateFlow

@PeraPreviewLightDark
@Composable
fun InboxScreenPreview() {
    PeraTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isSystemInDarkTheme()) {
                        ColorPalette.Gray.V900
                    } else {
                        ColorPalette.White.Default
                    }
                )
        ) {
            InboxScreen(
                viewStateFlow = MutableStateFlow(getMockPreview()),
                listener = object : InboxScreenListener {
                    override fun onAccountClick(accountAddress: String) = Unit
                    override fun onSignatureRequestClick(signRequestId: String, canUserSign: Boolean) = Unit
                    override fun onJointAccountInvitationClick(invitation: JointAccountInvitationInboxItem) = Unit
                    override fun onInfoClick() = Unit
                }
            )
        }
    }
}

private fun getMockPreview() = InboxPreview(
    isLoading = false,
    isEmptyStateVisible = false,
    showError = null,
    inboxWithAccountList = getMockAccounts(),
    signatureRequestList = getMockSignatureRequests()
)

private fun getMockAccounts(): List<InboxWithAccount> {
    return listOf(
        InboxWithAccount(
            address = "QKZ6V2...2IHHJA",
            requestCount = 3,
            accountDisplayName = AccountDisplayName(
                accountAddress = "QKZ6V2...2IHHJA",
                primaryDisplayName = "QKZ6V2...2IHHJA",
                secondaryDisplayName = null
            ),
            accountAddress = "QKZ6V2...2IHHJA",
            accountIconDrawablePreview = AccountIconDrawablePreviews.getDefaultIconDrawablePreview()
        ),
        InboxWithAccount(
            address = "DUA4...2ETI",
            requestCount = 1,
            accountDisplayName = AccountDisplayName(
                accountAddress = "DUA4...2ETI",
                primaryDisplayName = "Ledger Account",
                secondaryDisplayName = "DUA4...2ETI"
            ),
            accountAddress = "DUA4...2ETI",
            accountIconDrawablePreview = AccountIconDrawablePreviews.getLedgerBleDrawable()
        )
    )
}

private fun getMockSignatureRequests(): List<SignatureRequestInboxItem> {
    return listOf(
        SignatureRequestInboxItem(
            signRequestId = "mock-sign-request-id-1",
            jointAccountAddress = "QKZ6V2...2IHHJA",
            jointAccountAddressShortened = "QKZ6V2...2IHHJA",
            accountIconDrawablePreview = AccountIconDrawablePreviews.getJointDrawable(),
            description = "Signature request to sign for QKZ6V2...2IHHJA",
            timeAgo = "2 hours ago",
            signedCount = 1,
            totalCount = 2,
            timeLeft = "52m"
        )
    )
}
