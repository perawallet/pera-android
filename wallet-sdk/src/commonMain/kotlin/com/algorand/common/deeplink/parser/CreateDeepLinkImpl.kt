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

package com.algorand.common.deeplink.parser

import com.algorand.common.deeplink.builder.DeepLinkBuilder
import com.algorand.common.deeplink.model.DeepLink

internal class CreateDeepLinkImpl(
    private val parseDeepLinkPayload: ParseDeepLinkPayload,
    private val accountAddressDeepLinkBuilder: DeepLinkBuilder,
    private val assetOptInDeepLinkBuilder: DeepLinkBuilder,
    private val assetTransferDeepLinkBuilder: DeepLinkBuilder,
    private val mnemonicDeepLinkBuilder: DeepLinkBuilder,
    private val walletConnectConnectionDeepLinkBuilder: DeepLinkBuilder,
    private val webImportQrCodeDeepLinkBuilder: DeepLinkBuilder,
    private val notificationGroupDeepLinkBuilder: DeepLinkBuilder,
    private val discoverBrowserDeepLinkBuilder: DeepLinkBuilder,
    private val discoverDeepLinkBuilder: DeepLinkBuilder,
    private val assetInboxDeepLinkBuilder: DeepLinkBuilder,
    private val keyRegTransactionDeepLinkBuilder: DeepLinkBuilder,
    private val cardsDeepLinkBuilder: DeepLinkBuilder,
    private val stakingDeepLinkBuilder: DeepLinkBuilder
) : CreateDeepLink {

    override fun invoke(url: String): DeepLink {
        val payload = parseDeepLinkPayload(url)

        return when {
            accountAddressDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                accountAddressDeepLinkBuilder.createDeepLink(payload)
            }
            assetOptInDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                assetOptInDeepLinkBuilder.createDeepLink(payload)
            }
            assetTransferDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                assetTransferDeepLinkBuilder.createDeepLink(payload)
            }
            keyRegTransactionDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                keyRegTransactionDeepLinkBuilder.createDeepLink(payload)
            }
            walletConnectConnectionDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                walletConnectConnectionDeepLinkBuilder.createDeepLink(payload)
            }
            mnemonicDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                mnemonicDeepLinkBuilder.createDeepLink(payload)
            }
            webImportQrCodeDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                webImportQrCodeDeepLinkBuilder.createDeepLink(payload)
            }
            discoverBrowserDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                discoverBrowserDeepLinkBuilder.createDeepLink(payload)
            }
            discoverDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                discoverDeepLinkBuilder.createDeepLink(payload)
            }
            notificationGroupDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                notificationGroupDeepLinkBuilder.createDeepLink(payload)
            }
            assetInboxDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                assetInboxDeepLinkBuilder.createDeepLink(payload)
            }
            cardsDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                cardsDeepLinkBuilder.createDeepLink(payload)
            }
            stakingDeepLinkBuilder.doesDeeplinkMeetTheRequirements(payload) -> {
                stakingDeepLinkBuilder.createDeepLink(payload)
            }
            else -> DeepLink.Undefined(url)
        }
    }
}
