/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.deeplink

import com.algorand.android.accountinfo.component.domain.usecase.IsAssetOwnedByAnyAccount
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.account.core.component.utils.toShortenedAddress
import com.algorand.android.module.deeplink.factory.DeepLinkFactory
import com.algorand.android.module.deeplink.model.BaseDeepLink
import com.algorand.android.module.deeplink.model.BaseDeepLink.AccountAddressDeepLink
import com.algorand.android.module.deeplink.model.BaseDeepLink.AssetOptInDeepLink
import com.algorand.android.module.deeplink.model.BaseDeepLink.AssetTransferDeepLink
import com.algorand.android.module.deeplink.model.BaseDeepLink.DiscoverBrowserDeepLink
import com.algorand.android.module.deeplink.model.BaseDeepLink.MnemonicDeepLink
import com.algorand.android.module.deeplink.model.BaseDeepLink.NotificationDeepLink
import com.algorand.android.module.deeplink.model.BaseDeepLink.UndefinedDeepLink
import com.algorand.android.module.deeplink.model.BaseDeepLink.WalletConnectConnectionDeepLink
import com.algorand.android.module.deeplink.model.BaseDeepLink.WebImportQrCodeDeepLink
import com.algorand.android.module.deeplink.usecase.ParseDeepLink
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class DeepLinkHandlerImpl @Inject constructor(
    private val parseDeepLink: ParseDeepLink,
    private val deepLinkFactory: DeepLinkFactory,
    private val isAssetOwnedByAnyAccount: IsAssetOwnedByAnyAccount
) : DeepLinkHandler {

    private var listener: DeepLinkHandler.Listener? = null

    override fun setListener(listener: DeepLinkHandler.Listener?) {
        this.listener = listener
    }

    override suspend fun handleDeepLink(uri: String) {
        val rawDeepLink = parseDeepLink(uri)
        val parsedDeepLink = deepLinkFactory(rawDeepLink)
        handleDeeplink(parsedDeepLink)
    }

    private suspend fun handleDeeplink(baseDeeplink: BaseDeepLink) {
        val isDeeplinkHandled: Boolean = when (baseDeeplink) {
            is AccountAddressDeepLink -> handleAccountAddressDeepLink(baseDeeplink)
            is AssetTransferDeepLink -> handleAssetTransferDeepLink(baseDeeplink)
            is AssetOptInDeepLink -> handleAssetOptInDeepLink(baseDeeplink.assetId)
            is MnemonicDeepLink -> handleMnemonicDeepLink(baseDeeplink)
            is WalletConnectConnectionDeepLink -> handleWalletConnectConnectionDeepLink(baseDeeplink)
            is UndefinedDeepLink -> handleUndefinedDeepLink(baseDeeplink)
            is WebImportQrCodeDeepLink -> handleWebImportQrCodeDeepLink(baseDeeplink)
            is NotificationDeepLink -> handleNotificationDeepLink(baseDeeplink)
            is DiscoverBrowserDeepLink -> handleDiscoverBrowserDeepLink(baseDeeplink)
        }
        if (!isDeeplinkHandled) listener?.onDeepLinkNotHandled(baseDeeplink)
    }

    private suspend fun handleAccountAddressDeepLink(deepLink: AccountAddressDeepLink): Boolean {
        return triggerListener { it.onAccountAddressDeeplink(deepLink.accountAddress, deepLink.label) }
    }

    private suspend fun handleAssetOptInDeepLink(assetId: Long): Boolean {
        return triggerListener { it.onAssetOptInDeepLink(assetId) }
    }

    private suspend fun handleMnemonicDeepLink(mnemonicDeeplink: MnemonicDeepLink): Boolean {
        return triggerListener { it.onImportAccountDeepLink(mnemonicDeeplink.mnemonic) }
    }

    private suspend fun handleWalletConnectConnectionDeepLink(wcConnectionDeeplink: WalletConnectConnectionDeepLink): Boolean {
        return triggerListener {
            it.onWalletConnectConnectionDeeplink(wcUrl = wcConnectionDeeplink.url)
        }
    }

    private suspend fun handleUndefinedDeepLink(undefinedDeeplink: UndefinedDeepLink): Boolean {
        return triggerListener { it.onUndefinedDeepLink(undefinedDeeplink); true }
    }

    private suspend fun handleWebImportQrCodeDeepLink(webImportQrCodeDeepLink: WebImportQrCodeDeepLink): Boolean {
        return triggerListener {
            it.onWebImportQrCodeDeepLink(
                webImportQrCode = webImportQrCodeDeepLink.webImportQrCode
            )
        }
    }

    private suspend fun handleAssetTransferDeepLink(assetTransferDeeplink: AssetTransferDeepLink): Boolean {
        val assetId = assetTransferDeeplink.assetId
        val isAssetOwnedByAnyAccount = if (assetId == ALGO_ASSET_ID) {
            true
        } else {
            isAssetOwnedByAnyAccount(assetTransferDeeplink.assetId)
        }
        return if (isAssetOwnedByAnyAccount) {
            with(assetTransferDeeplink) {
                triggerListener {
                    it.onAssetTransferDeepLink(
                        assetTransferDeeplink,
                        receiverAddress = receiverAccountAddress,
                        receiverName = label ?: receiverAccountAddress.toShortenedAddress()
                    )
                }
            }
        } else {
            triggerListener { it.onAssetTransferWithNotOptInDeepLink(assetId) }
        }
    }

    private suspend fun handleNotificationDeepLink(notificationDeepLink: NotificationDeepLink): Boolean {
        return triggerListener {
            it.onNotificationDeepLink(notificationDeepLink)
        }
    }

    private suspend fun handleDiscoverBrowserDeepLink(discoverBrowserDeepLink: DiscoverBrowserDeepLink): Boolean {
        return triggerListener { it.onDiscoverBrowserDeepLink(discoverBrowserDeepLink.webUrl); true }
    }

    private suspend fun triggerListener(action: (DeepLinkHandler.Listener) -> Boolean): Boolean {
        return withContext(Dispatchers.Main) {
            listener?.run(action) ?: false
        }
    }
}
