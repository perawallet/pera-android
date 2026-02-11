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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.AccountIconDrawablePreviews
import com.algorand.android.modules.inbox.allaccounts.domain.model.InboxWithAccount
import com.algorand.android.modules.inbox.allaccounts.domain.model.SignatureRequestInboxItem
import com.algorand.android.modules.inbox.allaccounts.ui.InboxScreen
import com.algorand.android.modules.inbox.allaccounts.ui.InboxScreenListener
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxViewState
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import com.algorand.android.ui.compose.theme.ColorPalette
import com.algorand.android.ui.compose.theme.PeraTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.ZonedDateTime

@PeraPreviewLightDark
@Composable
fun InboxScreenContentPreview() {
    val state = remember { MutableStateFlow(getMockContentViewState()) }
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
                state = state,
                listener = NoOpInboxScreenListener
            )
        }
    }
}

@PeraPreviewLightDark
@Composable
fun InboxScreenEmptyPreview() {
    val state = remember { MutableStateFlow(InboxViewState.Empty) }
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
                state = state,
                listener = NoOpInboxScreenListener
            )
        }
    }
}

@PeraPreviewLightDark
@Composable
fun InboxScreenLoadingPreview() {
    val state = remember { MutableStateFlow(InboxViewState.Loading) }
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
                state = state,
                listener = NoOpInboxScreenListener
            )
        }
    }
}

private object NoOpInboxScreenListener : InboxScreenListener {
    override fun onAccountClick(accountAddress: String) = Unit
    override fun onSignatureRequestClick(signRequestId: String, canUserSign: Boolean) = Unit
    override fun onJointAccountInvitationClick(invitation: JointAccountInvitationInboxItem) = Unit
    override fun onInfoClick() = Unit
}

private fun getMockContentViewState() = InboxViewState.Content(
    inboxWithAccountList = getMockAccounts(),
    signatureRequestList = getMockSignatureRequests(),
    jointAccountInvitationList = getMockJointAccountInvitations()
)

private fun getMockAccounts(): List<InboxWithAccount> {
    return listOf(
        InboxWithAccount(
            address = "QKZ6V2AHIHHJA",
            requestCount = 3,
            accountDisplayName = AccountDisplayName(
                accountAddress = "QKZ6V2AHIHHJA",
                primaryDisplayName = "QKZ6V2...2IHHJA",
                secondaryDisplayName = null
            ),
            accountAddress = "QKZ6V2AHIHHJA",
            accountIconDrawablePreview = AccountIconDrawablePreviews.getDefaultIconDrawablePreview()
        ),
        InboxWithAccount(
            address = "DUA42ETI",
            requestCount = 1,
            accountDisplayName = AccountDisplayName(
                accountAddress = "DUA42ETI",
                primaryDisplayName = "Ledger Account",
                secondaryDisplayName = "DUA4...2ETI"
            ),
            accountAddress = "DUA42ETI",
            accountIconDrawablePreview = AccountIconDrawablePreviews.getLedgerBleDrawable()
        )
    )
}

private fun getMockSignatureRequests(): List<SignatureRequestInboxItem> {
    return listOf(
        SignatureRequestInboxItem(
            signRequestId = "mock-sign-request-id-1",
            jointAccountAddress = "QKZ6V2AHIHHJA",
            jointAccountAddressShortened = "QKZ6V2...2IHHJA",
            accountIconDrawablePreview = AccountIconDrawablePreviews.getJointDrawable(),
            description = "Signature request to sign for QKZ6V2...2IHHJA",
            timeAgo = "2 hours ago",
            signedCount = 1,
            totalCount = 2,
            timeLeft = "52m",
            isRead = false,
            isExpired = false,
            canUserSign = true
        ),
        SignatureRequestInboxItem(
            signRequestId = "mock-sign-request-id-2",
            jointAccountAddress = "ABC123XYZ",
            jointAccountAddressShortened = "ABC1...3XYZ",
            accountIconDrawablePreview = AccountIconDrawablePreviews.getJointDrawable(),
            description = "Signature request to sign for ABC1...3XYZ",
            timeAgo = "5 hours ago",
            signedCount = 2,
            totalCount = 3,
            timeLeft = "30m",
            isRead = true,
            isExpired = false,
            canUserSign = false
        )
    )
}

private fun getMockJointAccountInvitations(): List<JointAccountInvitationInboxItem> {
    return listOf(
        JointAccountInvitationInboxItem(
            id = "mock-invitation-1",
            accountAddress = "JOINT123ABC",
            accountAddressShortened = "JOINT...3ABC",
            threshold = 2,
            participantAddresses = listOf("ADDR1", "ADDR2", "ADDR3"),
            isRead = false,
            creationDateTime = ZonedDateTime.now().minusHours(1),
            timeDifference = 3600000L // 1 hour in milliseconds
        )
    )
}
