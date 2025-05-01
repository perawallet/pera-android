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

package com.algorand.android.modules.walletconnect.connectionrequest.ui.usecase

import com.algorand.android.R
import com.algorand.android.models.ui.AccountAssetItemButtonState.CHECKED
import com.algorand.android.models.ui.AccountAssetItemButtonState.UNCHECKED
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheData
import com.algorand.android.modules.walletconnect.connectionrequest.domain.usecase.WCDomainScammerStateUseCase
import com.algorand.android.modules.walletconnect.connectionrequest.ui.mapper.BaseWalletConnectConnectionItemMapper
import com.algorand.android.modules.walletconnect.connectionrequest.ui.mapper.WCSessionRequestResultMapper
import com.algorand.android.modules.walletconnect.connectionrequest.ui.mapper.WalletConnectConnectionPreviewMapper
import com.algorand.android.modules.walletconnect.connectionrequest.ui.mapper.WalletConnectNetworkItemMapper
import com.algorand.android.modules.walletconnect.connectionrequest.ui.model.BaseWalletConnectConnectionItem
import com.algorand.android.modules.walletconnect.connectionrequest.ui.model.WCSessionRequestResult
import com.algorand.android.modules.walletconnect.connectionrequest.ui.model.WalletConnectConnectionPreview
import com.algorand.android.modules.walletconnect.domain.model.WalletConnectBlockchain
import com.algorand.android.modules.walletconnect.ui.model.WalletConnectSessionProposal
import com.algorand.android.utils.Event
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import javax.inject.Inject

@SuppressWarnings("LongParameterList")
class WalletConnectConnectionPreviewUseCase @Inject constructor(
    private val baseWalletConnectConnectionItemMapper: BaseWalletConnectConnectionItemMapper,
    private val walletConnectConnectionPreviewMapper: WalletConnectConnectionPreviewMapper,
    private val wcSessionRequestResultMapper: WCSessionRequestResultMapper,
    private val walletConnectNetworkItemMapper: WalletConnectNetworkItemMapper,
    private val wcDomainScammerStateUseCase: WCDomainScammerStateUseCase,
    private val getAccountLiteCacheData: GetAccountLiteCacheData,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountDisplayName: GetAccountDisplayName
) {

    suspend fun getWalletConnectConnectionPreview(
        sessionProposal: WalletConnectSessionProposal
    ): WalletConnectConnectionPreview {

        val accountItems = getAccountItems()

        val dAppInfoItem = baseWalletConnectConnectionItemMapper.mapToDappInfoItem(
            name = sessionProposal.peerMeta.name,
            url = sessionProposal.peerMeta.url,
            peerIconUri = sessionProposal.peerMeta.peerIconUri.toString()
        )

        val accountsTitleItem = baseWalletConnectConnectionItemMapper.mapToTitleItem(
            memberCount = accountItems.count(),
            titleTextResId = R.plurals.select_accounts
        )
        val algorandNamepace = sessionProposal.requiredNamespaces[WalletConnectBlockchain.ALGORAND]
        val eventList = algorandNamepace?.events?.map { it.value }.orEmpty()
        val networkList = algorandNamepace?.chains?.map {
            walletConnectNetworkItemMapper.mapToWalletConnectConnectionNetworkItem(it)
        }.orEmpty()

        val networkCount = networkList.count()
        val networkItem = baseWalletConnectConnectionItemMapper.mapToWalletConnectConnectionNetworkItem(
            networkCount = networkCount,
            walletConnectConnectionNetworkList = networkList
        )

        val eventCount = eventList.count()
        val eventItem = baseWalletConnectConnectionItemMapper.mapToEventItem(
            eventCount = eventCount,
            eventList = eventList
        )

        val requestedPermissionTitle = baseWalletConnectConnectionItemMapper.mapToTitleItem(
            titleTextResId = R.plurals.requested_permission,
            memberCount = eventCount + networkCount
        )

        val baseWalletConnectConnectionItems = mutableListOf<BaseWalletConnectConnectionItem>().apply {
            add(dAppInfoItem)
            if (networkList.isNotEmpty() || eventList.isNotEmpty()) {
                add(requestedPermissionTitle)
            }
            if (networkList.isNotEmpty()) add(networkItem)
            if (eventList.isNotEmpty()) add(eventItem)
            add(accountsTitleItem)
            addAll(accountItems)
        }

        return walletConnectConnectionPreviewMapper.mapToWalletConnectConnectionPreview(
            baseWalletConnectConnectionItems = baseWalletConnectConnectionItems,
            isConfirmationButtonEnabled = accountItems.any { it.isChecked }
        )
    }

    private suspend fun getAccountItems(): List<BaseWalletConnectConnectionItem.AccountItem> {
        val sortedAccountList = createSortedAccountList()
        val isThereOnlyOneAccount = sortedAccountList.count() == 1
        val preSelectedButtonState = if (isThereOnlyOneAccount) CHECKED else UNCHECKED

        return sortedAccountList.map { accountLite ->
            with(accountLite) {
                baseWalletConnectConnectionItemMapper.mapToAccountItem(
                    accountAddress = address,
                    accountIconDrawablePreview = getAccountIconDrawablePreview(this),
                    accountDisplayName = getAccountDisplayName(this),
                    buttonState = preSelectedButtonState,
                    isChecked = isThereOnlyOneAccount
                )
            }
        }
    }

    private fun createSortedAccountList(): Collection<AccountLite> {
        return getAccountLiteCacheData()?.accountLites
            ?.filter { it.value.cachedInfo?.type?.canSignTransaction() == true }
            ?.values
            .orEmpty()
    }

    fun updatePreviewStateAccordingToAccountSelection(
        preview: WalletConnectConnectionPreview,
        accountAddress: String
    ): WalletConnectConnectionPreview {
        val baseWalletConnectConnectionItems = preview.baseWalletConnectConnectionItems.map {
            when (it) {
                is BaseWalletConnectConnectionItem.TitleItem,
                is BaseWalletConnectConnectionItem.EventItem,
                is BaseWalletConnectConnectionItem.NetworkItem,
                is BaseWalletConnectConnectionItem.DappInfoItem -> it
                is BaseWalletConnectConnectionItem.AccountItem -> updateSelectedItemButtonState(it, accountAddress)
            }
        }
        val isConfirmationButtonEnabled = baseWalletConnectConnectionItems
            .filterIsInstance<BaseWalletConnectConnectionItem.AccountItem>()
            .any { it.isChecked }

        return walletConnectConnectionPreviewMapper.mapToWalletConnectConnectionPreview(
            baseWalletConnectConnectionItems = baseWalletConnectConnectionItems,
            isConfirmationButtonEnabled = isConfirmationButtonEnabled
        )
    }

    private fun updateSelectedItemButtonState(
        accountItem: BaseWalletConnectConnectionItem.AccountItem,
        updatedAccountAddress: String
    ): BaseWalletConnectConnectionItem.AccountItem {
        return if (accountItem.accountAddress == updatedAccountAddress) {
            val oppositeState = !accountItem.isChecked
            accountItem.copy(isChecked = oppositeState, buttonState = if (oppositeState) CHECKED else UNCHECKED)
        } else {
            accountItem
        }
    }

    suspend fun getApprovedWalletConnectSessionResult(
        preview: WalletConnectConnectionPreview?,
        sessionProposal: WalletConnectSessionProposal
    ): WalletConnectConnectionPreview? {
        val isDomainScammer = wcDomainScammerStateUseCase(sessionProposal.peerMeta.url)
        return if (isDomainScammer.not()) {
            val selectedAccounts = getSelectedAccountFromPreview(preview)
            val approveRequest = wcSessionRequestResultMapper.mapToApproveRequest(
                accountAddresses = selectedAccounts,
                sessionProposal = sessionProposal
            )
            preview?.copy(approveWalletConnectSessionRequest = Event(approveRequest))
        } else {
            val rejectedRequest = wcSessionRequestResultMapper.mapToRejectScamRequest(sessionProposal = sessionProposal)
            preview?.copy(
                rejectScamWalletConnectSessionRequest = Event(rejectedRequest)
            )
        }
    }

    fun getRejectedWalletConnectSessionResult(
        sessionProposal: WalletConnectSessionProposal
    ): WCSessionRequestResult.RejectRequest {
        return wcSessionRequestResultMapper.mapToRejectRequest(sessionProposal = sessionProposal)
    }

    private fun getSelectedAccountFromPreview(preview: WalletConnectConnectionPreview?): List<String> {
        return preview?.baseWalletConnectConnectionItems
            ?.filterIsInstance<BaseWalletConnectConnectionItem.AccountItem>()
            ?.filter { it.isChecked }
            ?.map { it.accountAddress }
            .orEmpty()
    }
}
