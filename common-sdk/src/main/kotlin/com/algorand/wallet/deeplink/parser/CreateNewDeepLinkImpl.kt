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

package com.algorand.wallet.deeplink.parser

import com.algorand.wallet.deeplink.builder.AccountDetailNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AddContactNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AddWatchAccountNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AddressActionsNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetDetailNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetInboxNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetOptInNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetTransferNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.BuyNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.CardsPathNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.DiscoverBrowserNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.DiscoverPathNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.EditContactNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.HomeNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.InternalBrowserNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.JointAccountImportNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.KeyRegNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.ReceiverAccountSelectionNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.RecoverAccountNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.SellNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.SignRequestNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.StakingPathNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.SwapNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.WalletConnectNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.WebImportNewDeepLinkBuilder
import com.algorand.wallet.deeplink.model.DeepLink

internal class CreateNewDeepLinkImpl(
    private val parseNewDeepLinkPayload: ParseNewDeepLinkPayload,
    private val addContactNewDeepLinkBuilder: AddContactNewDeepLinkBuilder,
    private val editContactNewDeepLinkBuilder: EditContactNewDeepLinkBuilder,
    private val addWatchAccountNewDeepLinkBuilder: AddWatchAccountNewDeepLinkBuilder,
    private val receiverAccountSelectionNewDeepLinkBuilder: ReceiverAccountSelectionNewDeepLinkBuilder,
    private val addressActionsNewDeepLinkBuilder: AddressActionsNewDeepLinkBuilder,
    private val assetTransferNewDeepLinkBuilder: AssetTransferNewDeepLinkBuilder,
    private val assetOptInNewDeepLinkBuilder: AssetOptInNewDeepLinkBuilder,
    private val assetInboxNewDeepLinkBuilder: AssetInboxNewDeepLinkBuilder,
    private val assetDetailNewDeepLinkBuilder: AssetDetailNewDeepLinkBuilder,
    private val swapNewDeepLinkBuilder: SwapNewDeepLinkBuilder,
    private val buyNewDeepLinkBuilder: BuyNewDeepLinkBuilder,
    private val sellNewDeepLinkBuilder: SellNewDeepLinkBuilder,
    private val keyRegNewDeepLinkBuilder: KeyRegNewDeepLinkBuilder,
    private val recoverAccountNewDeepLinkBuilder: RecoverAccountNewDeepLinkBuilder,
    private val webImportNewDeepLinkBuilder: WebImportNewDeepLinkBuilder,
    private val walletConnectNewDeepLinkBuilder: WalletConnectNewDeepLinkBuilder,
    private val discoverBrowserNewDeepLinkBuilder: DiscoverBrowserNewDeepLinkBuilder,
    private val discoverPathNewDeepLinkBuilder: DiscoverPathNewDeepLinkBuilder,
    private val cardsPathNewDeepLinkBuilder: CardsPathNewDeepLinkBuilder,
    private val stakingPathNewDeepLinkBuilder: StakingPathNewDeepLinkBuilder,
    private val accountDetailNewDeepLinkBuilder: AccountDetailNewDeepLinkBuilder,
    private val internalBrowserNewDeepLinkBuilder: InternalBrowserNewDeepLinkBuilder,
    private val jointAccountImportNewDeepLinkBuilder: JointAccountImportNewDeepLinkBuilder,
    private val signRequestNewDeepLinkBuilder: SignRequestNewDeepLinkBuilder,
    private val homeNewDeepLinkBuilder: HomeNewDeepLinkBuilder,
) : CreateNewDeepLink {

    override fun invoke(url: String): DeepLink {
        val payload = parseNewDeepLinkPayload(url)
        val path = payload.lastPathSegment

        val deepLink = when (path) {
            "add-contact" -> addContactNewDeepLinkBuilder.createDeepLink(payload)
            "edit-contact" -> editContactNewDeepLinkBuilder.createDeepLink(payload)
            "add-watch-account" -> addWatchAccountNewDeepLinkBuilder.createDeepLink(payload)
            "receiver-account-selection" -> receiverAccountSelectionNewDeepLinkBuilder.createDeepLink(payload)
            "address-actions" -> addressActionsNewDeepLinkBuilder.createDeepLink(payload)
            "asset-transfer" -> assetTransferNewDeepLinkBuilder.createDeepLink(payload)
            "asset-opt-in" -> assetOptInNewDeepLinkBuilder.createDeepLink(payload)
            "asset-inbox" -> assetInboxNewDeepLinkBuilder.createDeepLink(payload)
            "asset-detail" -> assetDetailNewDeepLinkBuilder.createDeepLink(payload)
            "swap" -> swapNewDeepLinkBuilder.createDeepLink(payload)
            "buy" -> buyNewDeepLinkBuilder.createDeepLink(payload)
            "sell" -> sellNewDeepLinkBuilder.createDeepLink(payload)
            "keyreg" -> keyRegNewDeepLinkBuilder.createDeepLink(payload)
            "recover-address" -> recoverAccountNewDeepLinkBuilder.createDeepLink(payload)
            "web-import" -> webImportNewDeepLinkBuilder.createDeepLink(payload)
            "wallet-connect" -> walletConnectNewDeepLinkBuilder.createDeepLink(payload)
            "discover-browser" -> discoverBrowserNewDeepLinkBuilder.createDeepLink(payload)
            "discover-path" -> discoverPathNewDeepLinkBuilder.createDeepLink(payload)
            "cards-path" -> cardsPathNewDeepLinkBuilder.createDeepLink(payload)
            "staking-path" -> stakingPathNewDeepLinkBuilder.createDeepLink(payload)
            "account-detail" -> accountDetailNewDeepLinkBuilder.createDeepLink(payload)
            "internal-browser" -> internalBrowserNewDeepLinkBuilder.createDeepLink(payload)
            "joint-account-import" -> jointAccountImportNewDeepLinkBuilder.createDeepLink(payload)
            "joint-account-sign-request" -> signRequestNewDeepLinkBuilder.createDeepLink(payload)
            "app", "", null -> homeNewDeepLinkBuilder.createDeepLink(payload)
            else -> null
        }

        return deepLink ?: DeepLink.Undefined(url)
    }
}
