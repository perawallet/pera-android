@file:Suppress("UnusedPrivateMember", "IllegalStateException", "EmptyFunctionBlock")
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

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.algorand.android.ui.compose.preview.PeraPreviewLightDark
import androidx.lifecycle.SavedStateHandle
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.AccountIconDrawablePreviews
import com.algorand.android.modules.inbox.allaccounts.domain.model.InboxWithAccount
import com.algorand.android.modules.inbox.allaccounts.domain.model.SignatureRequestInboxItem
import com.algorand.android.modules.inbox.allaccounts.ui.InboxScreen
import com.algorand.android.modules.inbox.allaccounts.ui.InboxScreenListener
import com.algorand.android.modules.inbox.allaccounts.ui.InboxViewModel
import com.algorand.android.modules.inbox.allaccounts.ui.mapper.InboxPreviewMapper
import com.algorand.android.modules.inbox.allaccounts.ui.mapper.InboxPreviewParams
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxPreview
import com.algorand.android.modules.inbox.allaccounts.ui.usecase.InboxPreviewUseCase
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.modules.inbox.data.local.InboxLastOpenedTimeLocalSource
import com.algorand.android.ui.compose.theme.ColorPalette
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessagesFlow
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddresses
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

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
                viewModel = getMockViewModel(),
                listener = object : InboxScreenListener {
                    override fun onAccountClick(accountAddress: String) {}
                    override fun onSignatureRequestClick(signRequestId: String, canUserSign: Boolean) {}
                    override fun onJointAccountInvitationClick(invitation: JointAccountInvitationInboxItem) {}
                    override fun onInfoClick() {}
                }
            )
        }
    }
}

private fun getMockViewModel(): InboxViewModel {
    val mockPreview = InboxPreview(
        isLoading = false,
        isEmptyStateVisible = false,
        showError = null,
        inboxWithAccountList = getMockAccounts(),
        signatureRequestList = getMockSignatureRequests()
    )

    val mockMapper = object : InboxPreviewMapper {
        override suspend fun invoke(params: InboxPreviewParams): InboxPreview = mockPreview
        override fun getInitialPreview(): InboxPreview = mockPreview
    }

    val mockGetInboxValidAddresses = GetInboxValidAddresses { emptyList() }

    val mockSharedPreferences = object : android.content.SharedPreferences {
        private val data = mutableMapOf<String, Any?>()
        override fun contains(key: String) = data.containsKey(key)
        override fun edit() = throw UnsupportedOperationException()
        override fun getAll() = data.toMap()
        override fun getBoolean(key: String, defValue: Boolean) = (data[key] as? Boolean) ?: defValue
        override fun getFloat(key: String, defValue: Float) = (data[key] as? Float) ?: defValue
        override fun getInt(key: String, defValue: Int) = (data[key] as? Int) ?: defValue
        override fun getLong(key: String, defValue: Long) = (data[key] as? Long) ?: defValue
        override fun getString(key: String, defValue: String?) = (data[key] as? String) ?: defValue
        override fun getStringSet(key: String, defValues: MutableSet<String>?) =
            (data[key] as? MutableSet<String>) ?: defValues

        override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {}
        override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {}
    }

    val mockInboxLastOpenedTimeLocalSource =
        InboxLastOpenedTimeLocalSource(mockSharedPreferences)

    val mockGetInboxMessagesFlow = GetInboxMessagesFlow {
        flowOf(null)
    }
    val mockRefreshInboxCache = RefreshInboxCache { }

    val mockUseCase = InboxPreviewUseCase(
        inboxPreviewMapper = mockMapper,
        getInboxValidAddresses = mockGetInboxValidAddresses,
        getInboxMessagesFlow = mockGetInboxMessagesFlow,
        refreshInboxCache = mockRefreshInboxCache,
        inboxLastOpenedTimeLocalSource = mockInboxLastOpenedTimeLocalSource
    )

    val mockIsFeatureToggleEnabled = IsFeatureToggleEnabled { true }

    val mockSavedStateHandle = SavedStateHandle()

    val viewModel = InboxViewModel(
        inboxPreviewUseCase = mockUseCase,
        isFeatureToggleEnabled = mockIsFeatureToggleEnabled,
        savedStateHandle = mockSavedStateHandle
    )

    val field = InboxViewModel::class.java.getDeclaredField("_viewStateFlow")
    field.isAccessible = true
    field.set(viewModel, MutableStateFlow(mockPreview))

    return viewModel
}

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
