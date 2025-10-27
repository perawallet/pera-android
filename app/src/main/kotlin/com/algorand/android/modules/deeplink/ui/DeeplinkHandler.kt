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

package com.algorand.android.modules.deeplink.ui

import com.algorand.android.models.AssetAction
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.User
import com.algorand.android.modules.webimport.common.data.model.WebImportQrCode
import com.algorand.android.utils.toBigIntegerOrZero
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.info.domain.usecase.IsAssetOptedInByAnyLocalAccount
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.asset.domain.util.getSafeAssetIdForResponse
import com.algorand.wallet.deeplink.model.DeepLink
import com.algorand.wallet.deeplink.model.NotificationGroupType
import com.algorand.wallet.deeplink.parser.CreateDeepLink
import com.algorand.wallet.deeplink.parser.CreateNewDeepLink
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("TooManyFunctions")
class DeeplinkHandler @Inject constructor(
    private val isAssetOptedInByAnyLocalAccount: IsAssetOptedInByAnyLocalAccount,
    private val createDeepLink: CreateDeepLink,
    private val createNewDeepLink: CreateNewDeepLink
) {

    private var listener: Listener? = null

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun parseDeeplink(uri: String): DeepLink {
        return if (isNewDeeplink(uri)) {
            createNewDeepLink(uri)
        } else {
            createDeepLink(uri)
        }
    }

    suspend fun handleDeepLink(uri: String) {
        withContext(Dispatchers.Main) {
            val parsedDeepLink = parseDeeplink(uri)
            handleDeepLink(parsedDeepLink)
        }
    }

    private suspend fun handleDeepLink(deepLink: DeepLink) {
        val isDeeplinkHandled: Boolean = when (deepLink) {
            is DeepLink.AccountAddress -> handleAccountAddressDeepLink(deepLink)
            is DeepLink.AssetOptIn -> handleAssetOptInDeepLink(deepLink)
            is DeepLink.AssetTransfer -> handleAssetTransferDeepLink(deepLink)
            is DeepLink.DiscoverBrowser -> handleDiscoverBrowserDeepLink(deepLink)
            is DeepLink.Discover -> handleDiscoverDeepLink(deepLink)
            is DeepLink.RecoverAccount -> handleRecoverAccountDeepLink(deepLink)
            is DeepLink.Notification -> handleNotificationDeepLink(deepLink)
            is DeepLink.Undefined -> handleUndefinedDeepLink()
            is DeepLink.WalletConnectConnection -> handleWalletConnectConnectionDeepLink(deepLink)
            is DeepLink.WebImportQrCode -> handleWebImportQrCodeDeepLink(deepLink)
            is DeepLink.KeyReg -> handleKeyRegDeepLink(deepLink)
            is DeepLink.AssetInbox -> handleAssetInboxDeepLink(deepLink)
            is DeepLink.Cards -> handleCardsDeepLink(deepLink)
            is DeepLink.Staking -> handleStakingDeepLink(deepLink)
            is DeepLink.AccountDetail -> handleAccountDetailDeepLink(deepLink)
            is DeepLink.AddContact -> handleAddContactDeepLink(deepLink)
            is DeepLink.EditContact -> handleEditContactDeepLink(deepLink)
            is DeepLink.AddWatchAccount -> handleAddWatchAccountDeepLink(deepLink)
            is DeepLink.ReceiverAccountSelection -> handleReceiverAccountSelectionDeepLink(deepLink)
            is DeepLink.AddressActions -> handleAddressActionsDeepLink(deepLink)
            is DeepLink.AssetDetail -> handleAssetDetailDeepLink(deepLink)
            is DeepLink.Sell -> handleSellDeepLink(deepLink)
            is DeepLink.Buy -> handleBuyDeepLink(deepLink)
            is DeepLink.InternalBrowser -> handleInternalBrowserDeepLink(deepLink)
            is DeepLink.Swap -> handleSwapDeepLink(deepLink)
            is DeepLink.Home -> handleHomeDeepLink()
            is DeepLink.Fido -> handleFidoDeepLink(deepLink)
        }
        if (isDeeplinkHandled) {
            listener?.onDeepLinkHandled()
        } else {
            listener?.onDeepLinkNotHandled(deepLink)
        }
    }

    private fun handleAccountAddressDeepLink(deepLink: DeepLink.AccountAddress): Boolean {
        return triggerListener { it.onAccountAddressDeeplink(deepLink.address, deepLink.label) }
    }

    private fun handleAssetOptInDeepLink(deeplink: DeepLink.AssetOptIn): Boolean {
        val assetAction = AssetAction(assetId = deeplink.assetId, publicKey = deeplink.address)
        return triggerListener { it.onAssetOptInDeepLink(assetAction) }
    }

    private fun handleRecoverAccountDeepLink(deepLink: DeepLink.RecoverAccount): Boolean {
        return triggerListener { it.onRecoverAccountDeepLink(deepLink.mnemonic) }
    }

    private fun handleWalletConnectConnectionDeepLink(deepLink: DeepLink.WalletConnectConnection): Boolean {
        return triggerListener {
            it.onWalletConnectConnectionDeeplink(wcUrl = deepLink.uri)
        }
    }

    private fun handleUndefinedDeepLink(): Boolean {
        return triggerListener { it.onUndefinedDeepLink(); true }
    }

    private fun handleKeyRegDeepLink(deepLink: DeepLink.KeyReg): Boolean {
        return triggerListener { it.onKeyRegDeeplink(deepLink); true }
    }

    private fun handleDiscoverBrowserDeepLink(deepLink: DeepLink.DiscoverBrowser): Boolean {
        return triggerListener { it.onDiscoverBrowserDeepLink(deepLink.webUrl); true }
    }

    private fun handleDiscoverDeepLink(deepLink: DeepLink.Discover): Boolean {
        return triggerListener { it.onDiscoverDeepLink(deepLink.path); true }
    }

    private fun handleCardsDeepLink(deepLink: DeepLink.Cards): Boolean {
        return triggerListener { it.onCardsDeepLink(deepLink.path); true }
    }

    private fun handleStakingDeepLink(deepLink: DeepLink.Staking): Boolean {
        return triggerListener { it.onStakingDeepLink(deepLink.path); true }
    }

    private fun handleWebImportQrCodeDeepLink(deepLink: DeepLink.WebImportQrCode): Boolean {
        return triggerListener {
            it.onWebImportQrCodeDeepLink(WebImportQrCode(deepLink.backupId, deepLink.encryptionKey))
        }
    }

    private suspend fun handleAssetTransferDeepLink(deepLink: DeepLink.AssetTransfer): Boolean {
        val safeAssetId = getSafeAssetIdForResponse(deepLink.assetId) ?: ALGO_ID
        val isAssetOptedInByAnyLocalAccount = if (safeAssetId == ALGO_ID) {
            true
        } else {
            withContext(Dispatchers.IO) {
                isAssetOptedInByAnyLocalAccount(safeAssetId)
            }
        }
        return if (isAssetOptedInByAnyLocalAccount) {
            with(deepLink) {
                val assetTransaction = AssetTransaction(
                    assetId = safeAssetId,
                    note = note, // normal note
                    xnote = xnote, // locked note
                    amount = amount.toBigIntegerOrZero(),
                    receiverUser = User(
                        publicKey = receiverAddress,
                        name = label ?: receiverAddress.toShortenedAddress(),
                        imageUriAsString = null
                    )
                )
                triggerListener { it.onAssetTransferDeepLink(assetTransaction) }
            }
        } else {
            triggerListener { it.onAssetTransferWithNotOptInDeepLink(safeAssetId) }
        }
    }

    private fun handleNotificationDeepLink(deepLink: DeepLink.Notification): Boolean {
        return triggerListener {
            it.onNotificationDeepLink(
                accountAddress = deepLink.address,
                assetId = deepLink.assetId,
                notificationGroupType = deepLink.notificationGroupType
            )
        }
    }

    private fun handleAssetInboxDeepLink(deepLink: DeepLink.AssetInbox): Boolean {
        return triggerListener {
            it.onAssetInboxDeepLink(
                accountAddress = deepLink.address
            )
        }
    }

    private fun handleAccountDetailDeepLink(deepLink: DeepLink.AccountDetail): Boolean {
        return triggerListener { it.onAccountDetailDeepLink(deepLink.address) }
    }

    private fun handleAddContactDeepLink(deepLink: DeepLink.AddContact): Boolean {
        return triggerListener { it.onAddContactDeepLink(deepLink.address, deepLink.label) }
    }

    private fun handleEditContactDeepLink(deepLink: DeepLink.EditContact): Boolean {
        return triggerListener { it.onEditContactDeepLink(deepLink.address, deepLink.label) }
    }

    private fun handleAddWatchAccountDeepLink(deepLink: DeepLink.AddWatchAccount): Boolean {
        return triggerListener { it.onAddWatchAccountDeepLink(deepLink.address, deepLink.label) }
    }

    private fun handleAddressActionsDeepLink(deepLink: DeepLink.AddressActions): Boolean {
        return triggerListener { it.onAddressActionsDeepLink(deepLink.address, deepLink.label) }
    }

    private fun handleAssetDetailDeepLink(deepLink: DeepLink.AssetDetail): Boolean {
        return triggerListener { it.onAssetDetailDeepLink(deepLink.address, deepLink.assetId) }
    }

    private fun handleSellDeepLink(deepLink: DeepLink.Sell): Boolean {
        return triggerListener { it.onSellDeepLink(deepLink.address) }
    }

    private fun handleBuyDeepLink(deepLink: DeepLink.Buy): Boolean {
        return triggerListener { it.onBuyDeepLink(deepLink.address) }
    }

    private fun handleInternalBrowserDeepLink(deepLink: DeepLink.InternalBrowser): Boolean {
        return triggerListener { it.onInternalBrowserDeepLink(deepLink.url) }
    }

    private fun handleReceiverAccountSelectionDeepLink(deepLink: DeepLink.ReceiverAccountSelection): Boolean {
        return triggerListener { it.onReceiverAccountSelectionDeepLink(deepLink.address) }
    }

    private fun handleSwapDeepLink(deepLink: DeepLink.Swap): Boolean {
        return triggerListener {
            it.onSwapDeepLink(
                deepLink.address,
                deepLink.assetInId,
                deepLink.assetOutId
            )
        }
    }

    private fun handleHomeDeepLink(): Boolean {
        return triggerListener {
            it.onHomeDeeplink()
        }
    }

    private fun handleFidoDeepLink(deepLink: DeepLink.Fido): Boolean {
        return triggerListener {
            it.onFidoDeepLink(deepLink.uri)
        }
    }

    private fun triggerListener(action: (Listener) -> Boolean): Boolean {
        return listener?.run(action) ?: false
    }

    private fun isNewDeeplink(uri: String): Boolean {
        return uri.startsWith(NEW_APPLINK_PREFIX) || uri.startsWith(NEW_DEEPLINK_PREFIX)
    }

    private companion object {
        private const val NEW_APPLINK_PREFIX = "https://perawallet.app/qr/perawallet/app"
        private const val NEW_DEEPLINK_PREFIX = "perawallet://app"
    }

    interface Listener {
        fun onAssetTransferDeepLink(assetTransaction: AssetTransaction): Boolean = false
        fun onAssetOptInDeepLink(assetAction: AssetAction): Boolean = false
        fun onRecoverAccountDeepLink(mnemonic: String): Boolean = false
        fun onAccountAddressDeeplink(address: String, label: String?): Boolean = false
        fun onWalletConnectConnectionDeeplink(wcUrl: String): Boolean = false
        fun onAssetTransferWithNotOptInDeepLink(assetId: Long): Boolean = false
        fun onWebImportQrCodeDeepLink(webImportQrCode: WebImportQrCode): Boolean = false
        fun onNotificationDeepLink(
            accountAddress: String,
            assetId: Long,
            notificationGroupType: NotificationGroupType
        ): Boolean = false

        fun onDiscoverBrowserDeepLink(webUrl: String): Boolean = false
        fun onDiscoverDeepLink(path: String): Boolean = false
        fun onAssetInboxDeepLink(accountAddress: String): Boolean = false
        fun onKeyRegDeeplink(deepLink: DeepLink.KeyReg): Boolean = false
        fun onCardsDeepLink(path: String?): Boolean = false
        fun onStakingDeepLink(path: String?): Boolean = false
        fun onAddContactDeepLink(address: String, label: String?): Boolean = false
        fun onReceiverAccountSelectionDeepLink(address: String): Boolean = false
        fun onAccountDetailDeepLink(address: String): Boolean = false
        fun onEditContactDeepLink(address: String, label: String?): Boolean = false
        fun onAddWatchAccountDeepLink(address: String, label: String?): Boolean = false
        fun onAddressActionsDeepLink(address: String, label: String?): Boolean = false
        fun onAssetDetailDeepLink(address: String, assetId: Long): Boolean = false
        fun onBuyDeepLink(address: String): Boolean = false
        fun onSellDeepLink(address: String): Boolean = false
        fun onInternalBrowserDeepLink(url: String): Boolean = false
        fun onSwapDeepLink(address: String, assetInId: Long?, assetOutId: Long?): Boolean = false
        fun onHomeDeeplink(): Boolean = false
        fun onDeepLinkHandled() = false
        fun onUndefinedDeepLink()
        fun onDeepLinkNotHandled(deepLink: DeepLink)
        fun onFidoDeepLink(uri: String): Boolean = false
    }
}
