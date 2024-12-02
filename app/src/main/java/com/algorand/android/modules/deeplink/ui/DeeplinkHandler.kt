/*
 * Copyright 2022 Pera Wallet, LDA
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
import com.algorand.android.models.AssetInformation
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.User
import com.algorand.android.modules.webimport.common.data.model.WebImportQrCode
import com.algorand.android.usecase.AccountDetailUseCase
import com.algorand.android.utils.toBigIntegerOrZero
import com.algorand.android.utils.toShortenedAddress
import com.algorand.common.deeplink.model.DeepLink
import com.algorand.common.deeplink.model.NotificationGroupType
import com.algorand.common.deeplink.parser.CreateDeepLink
import javax.inject.Inject

class DeeplinkHandler @Inject constructor(
    private val accountDetailUseCase: AccountDetailUseCase,
    private val createDeepLink: CreateDeepLink
) {

    private var listener: Listener? = null

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun handleDeepLink(uri: String) {
        val parsedDeepLink = createDeepLink(uri)
        handleDeeplink(parsedDeepLink)
    }

    private fun handleDeeplink(deepLink: DeepLink) {
        val isDeeplinkHandled: Boolean = when (deepLink) {
            is DeepLink.AccountAddress -> handleAccountAddressDeepLink(deepLink)
            is DeepLink.AssetOptIn -> handleAssetOptInDeepLink(deepLink.assetId)
            is DeepLink.AssetTransfer -> handleAssetTransferDeepLink(deepLink)
            is DeepLink.DiscoverBrowser -> handleDiscoverBrowserDeepLink(deepLink)
            is DeepLink.Mnemonic -> handleMnemonicDeepLink(deepLink)
            is DeepLink.Notification -> handleNotificationDeepLink(deepLink)
            is DeepLink.Undefined -> handleUndefinedDeepLink(deepLink)
            is DeepLink.WalletConnectConnection -> handleWalletConnectConnectionDeepLink(deepLink)
            is DeepLink.WebImportQrCode -> handleWebImportQrCodeDeepLink(deepLink)
            is DeepLink.KeyReg -> handleKeyRegDeepLink(deepLink)
            is DeepLink.AssetInbox -> handleAssetInboxDeepLink(deepLink)
        }
        if (!isDeeplinkHandled) listener?.onDeepLinkNotHandled(deepLink)
    }

    private fun handleAccountAddressDeepLink(deepLink: DeepLink.AccountAddress): Boolean {
        return triggerListener { it.onAccountAddressDeeplink(deepLink.address, deepLink.label) }
    }

    private fun handleAssetOptInDeepLink(assetId: Long): Boolean {
        val assetAction = AssetAction(assetId = assetId)
        return triggerListener { it.onAssetOptInDeepLink(assetAction) }
    }

    private fun handleMnemonicDeepLink(deepLink: DeepLink.Mnemonic): Boolean {
        return triggerListener { it.onImportAccountDeepLink(deepLink.mnemonic) }
    }

    private fun handleWalletConnectConnectionDeepLink(deepLink: DeepLink.WalletConnectConnection): Boolean {
        return triggerListener {
            it.onWalletConnectConnectionDeeplink(wcUrl = deepLink.uri)
        }
    }

    private fun handleUndefinedDeepLink(deepLink: DeepLink.Undefined): Boolean {
        return triggerListener { it.onUndefinedDeepLink(deepLink); true }
    }

    private fun handleKeyRegDeepLink(deepLink: DeepLink.KeyReg): Boolean {
        return triggerListener { it.onKeyRegDeeplink(deepLink); true }
    }

    private fun handleDiscoverBrowserDeepLink(deepLink: DeepLink.DiscoverBrowser): Boolean {
        return triggerListener { it.onDiscoverBrowserDeepLink(deepLink.webUrl); true }
    }

    private fun handleWebImportQrCodeDeepLink(deepLink: DeepLink.WebImportQrCode): Boolean {
        return triggerListener {
            it.onWebImportQrCodeDeepLink(WebImportQrCode(deepLink.backupId, deepLink.encryptionKey))
        }
    }

    private fun handleAssetTransferDeepLink(deepLink: DeepLink.AssetTransfer): Boolean {
        val assetId = deepLink.assetId
        val isAssetOwnedByAnyAccount = if (assetId == AssetInformation.ALGO_ID) {
            true
        } else {
            accountDetailUseCase.isAssetOwnedByAnyAccount(deepLink.assetId)
        }
        return if (isAssetOwnedByAnyAccount) {
            with(deepLink) {
                val assetTransaction = AssetTransaction(
                    assetId = assetId,
                    note = note, // normal note
                    xnote = xnote, // locked note
                    amount = amount.toBigIntegerOrZero(),
                    receiverUser = User(
                        publicKey = receiverAccountAddress,
                        name = label ?: receiverAccountAddress.toShortenedAddress(),
                        imageUriAsString = null
                    )
                )
                triggerListener { it.onAssetTransferDeepLink(assetTransaction) }
            }
        } else {
            triggerListener { it.onAssetTransferWithNotOptInDeepLink(assetId) }
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
                accountAddress = deepLink.address,
                notificationGroupType = deepLink.notificationGroupType
            )
        }
    }

    private fun triggerListener(action: (Listener) -> Boolean): Boolean {
        return listener?.run(action) ?: false
    }

    interface Listener {
        fun onAssetTransferDeepLink(assetTransaction: AssetTransaction): Boolean = false
        fun onAssetOptInDeepLink(assetAction: AssetAction): Boolean = false
        fun onImportAccountDeepLink(mnemonic: String): Boolean = false
        fun onAccountAddressDeeplink(accountAddress: String, label: String?): Boolean = false
        fun onWalletConnectConnectionDeeplink(wcUrl: String): Boolean = false
        fun onAssetTransferWithNotOptInDeepLink(assetId: Long): Boolean = false
        fun onWebImportQrCodeDeepLink(webImportQrCode: WebImportQrCode): Boolean = false
        fun onNotificationDeepLink(
            accountAddress: String,
            assetId: Long,
            notificationGroupType: NotificationGroupType
        ): Boolean = false

        fun onDiscoverBrowserDeepLink(webUrl: String): Boolean = false
        fun onAssetInboxDeepLink(accountAddress: String, notificationGroupType: NotificationGroupType): Boolean = false
        fun onKeyRegDeeplink(deepLink: DeepLink.KeyReg): Boolean = false
        fun onUndefinedDeepLink(deepLink: DeepLink.Undefined)
        fun onDeepLinkNotHandled(deepLink: DeepLink)
    }
}
