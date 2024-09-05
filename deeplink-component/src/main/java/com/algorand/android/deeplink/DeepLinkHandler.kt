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

package com.algorand.android.deeplink

import com.algorand.android.deeplink.model.BaseDeepLink
import com.algorand.android.deeplink.model.WebImportQrCode

interface DeepLinkHandler {

    fun setListener(listener: Listener?)

    suspend fun handleDeepLink(uri: String)

    interface Listener {
        fun onAssetTransferDeepLink(
            deepLink: BaseDeepLink.AssetTransferDeepLink,
            receiverAddress: String,
            receiverName: String
        ): Boolean = false

        fun onAssetOptInDeepLink(assetId: Long): Boolean = false
        fun onImportAccountDeepLink(mnemonic: String): Boolean = false
        fun onAccountAddressDeeplink(accountAddress: String, label: String?): Boolean = false
        fun onWalletConnectConnectionDeeplink(wcUrl: String): Boolean = false
        fun onAssetTransferWithNotOptInDeepLink(assetId: Long): Boolean = false
        fun onWebImportQrCodeDeepLink(webImportQrCode: WebImportQrCode): Boolean = false
        fun onNotificationDeepLink(deepLink: BaseDeepLink.NotificationDeepLink): Boolean = false
        fun onDiscoverBrowserDeepLink(webUrl: String): Boolean = false

        fun onUndefinedDeepLink(undefinedDeeplink: BaseDeepLink.UndefinedDeepLink)
        fun onDeepLinkNotHandled(deepLink: BaseDeepLink)
    }
}
