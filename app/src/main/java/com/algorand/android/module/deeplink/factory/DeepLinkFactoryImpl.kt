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

package com.algorand.android.module.deeplink.factory

import com.algorand.android.module.deeplink.model.BaseDeepLink
import com.algorand.android.module.deeplink.model.RawDeepLink
import javax.inject.Inject

internal class DeepLinkFactoryImpl @Inject constructor(
    private val accountAddressDeepLinkCreator: AccountAddressDeepLinkCreator,
    private val assetOptInDeepLinkCreator: AssetOptInDeepLinkCreator,
    private val assetTransferDeepLinkCreator: com.algorand.android.module.deeplink.factory.AssetTransferDeepLinkCreator,
    private val mnemonicDeepLinkCreator: MnemonicDeepLinkCreator,
    private val notificationDeepLinkCreator: NotificationDeepLinkCreator,
    private val walletConnectConnectionDeepLinkCreator: WalletConnectConnectionDeepLinkCreator,
    private val webImportQrCodeDeepLinkCreator: WebImportQrCodeDeepLinkCreator,
    private val discoverBrowserDeepLinkCreator: DiscoverBrowserDeepLinkCreator
) : DeepLinkFactory {

    override suspend fun invoke(rawDeepLink: com.algorand.android.module.deeplink.model.RawDeepLink): com.algorand.android.module.deeplink.model.BaseDeepLink {
        return when {
            accountAddressDeepLinkCreator.doesDeeplinkMeetTheRequirements(rawDeepLink) -> {
                accountAddressDeepLinkCreator.createDeepLink(rawDeepLink)
            }
            assetOptInDeepLinkCreator.doesDeeplinkMeetTheRequirements(rawDeepLink) -> {
                assetOptInDeepLinkCreator.createDeepLink(rawDeepLink)
            }
            assetTransferDeepLinkCreator.doesDeeplinkMeetTheRequirements(rawDeepLink) -> {
                assetTransferDeepLinkCreator.createDeepLink(rawDeepLink)
            }
            mnemonicDeepLinkCreator.doesDeeplinkMeetTheRequirements(rawDeepLink) -> {
                mnemonicDeepLinkCreator.createDeepLink(rawDeepLink)
            }
            notificationDeepLinkCreator.doesDeeplinkMeetTheRequirements(rawDeepLink) -> {
                notificationDeepLinkCreator.createDeepLink(rawDeepLink)
            }
            walletConnectConnectionDeepLinkCreator.doesDeeplinkMeetTheRequirements(rawDeepLink) -> {
                walletConnectConnectionDeepLinkCreator.createDeepLink(rawDeepLink)
            }
            webImportQrCodeDeepLinkCreator.doesDeeplinkMeetTheRequirements(rawDeepLink) -> {
                webImportQrCodeDeepLinkCreator.createDeepLink(rawDeepLink)
            }
            discoverBrowserDeepLinkCreator.doesDeeplinkMeetTheRequirements(rawDeepLink) -> {
                discoverBrowserDeepLinkCreator.createDeepLink(rawDeepLink)
            }
            else -> com.algorand.android.module.deeplink.model.BaseDeepLink.UndefinedDeepLink(rawDeepLink.rawUrl)
        }
    }
}
