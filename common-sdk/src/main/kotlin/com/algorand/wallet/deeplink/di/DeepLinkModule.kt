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

package com.algorand.wallet.deeplink.di

import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdkAddress
import com.algorand.wallet.deeplink.builder.AccountAddressDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AccountDetailNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AddContactNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AddWatchAccountNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AddressActionsNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetDetailNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetInboxDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetInboxNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetOptInDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetOptInNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetTransferDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetTransferNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.BuyNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.CardsDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.CardsPathNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.DiscoverBrowserDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.DiscoverBrowserNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.DiscoverDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.DiscoverPathNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.EditContactNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.InternalBrowserNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.KeyRegNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.KeyRegTransactionDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.NotificationDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.ReceiverAccountSelectionNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.RecoverAccountDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.RecoverAccountNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.SellNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.StakingDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.StakingPathNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.SwapNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.WalletConnectConnectionDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.WalletConnectNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.WebImportNewDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.WebImportQrCodeDeepLinkBuilder
import com.algorand.wallet.deeplink.parser.CreateDeepLink
import com.algorand.wallet.deeplink.parser.CreateDeepLinkImpl
import com.algorand.wallet.deeplink.parser.CreateNewDeepLink
import com.algorand.wallet.deeplink.parser.CreateNewDeepLinkImpl
import com.algorand.wallet.deeplink.parser.ParseDeepLinkPayload
import com.algorand.wallet.deeplink.parser.ParseDeepLinkPayloadImpl
import com.algorand.wallet.deeplink.parser.ParseNewDeepLinkPayload
import com.algorand.wallet.deeplink.parser.ParseNewDeepLinkPayloadImpl
import com.algorand.wallet.deeplink.parser.PeraNewUriParser
import com.algorand.wallet.deeplink.parser.PeraNewUriParserImpl
import com.algorand.wallet.deeplink.parser.PeraUriParser
import com.algorand.wallet.deeplink.parser.PeraUriParserImpl
import com.algorand.wallet.deeplink.parser.query.AccountAddressQueryParser
import com.algorand.wallet.deeplink.parser.query.AssetIdQueryParser
import com.algorand.wallet.deeplink.parser.query.MnemonicQueryParser
import com.algorand.wallet.deeplink.parser.query.NotificationGroupTypeQueryParser
import com.algorand.wallet.deeplink.parser.query.UrlQueryParser
import com.algorand.wallet.deeplink.parser.query.WalletConnectUrlQueryParser
import com.algorand.wallet.deeplink.parser.query.WebImportQrCodeQueryParser
import com.algorand.wallet.encryption.domain.manager.Base64Manager
import com.algorand.wallet.foundation.json.JsonSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DeepLinkModule {

    @Provides
    fun providePeraUriParser(impl: PeraUriParserImpl): PeraUriParser = impl

    @Provides
    fun providePeraNewUriParser(impl: PeraNewUriParserImpl): PeraNewUriParser = impl

    @Provides
    fun provideParseDeepLinkPayload(
        peraUriParser: PeraUriParser,
        algoSdkAddress: AlgoSdkAddress,
        jsonSerializer: JsonSerializer,
        base64Manager: Base64Manager
    ): ParseDeepLinkPayload {
        return ParseDeepLinkPayloadImpl(
            peraUriParser = peraUriParser,
            accountAddressQueryParser = AccountAddressQueryParser(algoSdkAddress),
            assetIdQueryParser = AssetIdQueryParser(),
            notificationGroupTypeQueryParser = NotificationGroupTypeQueryParser(),
            webImportQrCodeQueryParser = WebImportQrCodeQueryParser(jsonSerializer),
            urlQueryParser = UrlQueryParser(base64Manager),
            mnemonicQueryParser = MnemonicQueryParser(jsonSerializer),
            walletConnectUrlQueryParser = WalletConnectUrlQueryParser()
        )
    }

    @Provides
    fun provideParseNewDeepLinkPayload(
        peraNewUriParser: PeraNewUriParser,
    ): ParseNewDeepLinkPayload {
        return ParseNewDeepLinkPayloadImpl(
            peraNewUriParser = peraNewUriParser,
        )
    }

    @Provides
    fun provideCreateDeepLink(
        parseDeepLinkPayload: ParseDeepLinkPayload
    ): CreateDeepLink {
        return CreateDeepLinkImpl(
            parseDeepLinkPayload = parseDeepLinkPayload,
            accountAddressDeepLinkBuilder = AccountAddressDeepLinkBuilder(),
            assetOptInDeepLinkBuilder = AssetOptInDeepLinkBuilder(),
            assetTransferDeepLinkBuilder = AssetTransferDeepLinkBuilder(),
            recoverAccountDeepLinkBuilder = RecoverAccountDeepLinkBuilder(),
            walletConnectConnectionDeepLinkBuilder = WalletConnectConnectionDeepLinkBuilder(),
            webImportQrCodeDeepLinkBuilder = WebImportQrCodeDeepLinkBuilder(),
            notificationGroupDeepLinkBuilder = NotificationDeepLinkBuilder(),
            discoverBrowserDeepLinkBuilder = DiscoverBrowserDeepLinkBuilder(),
            discoverDeepLinkBuilder = DiscoverDeepLinkBuilder(),
            assetInboxDeepLinkBuilder = AssetInboxDeepLinkBuilder(),
            keyRegTransactionDeepLinkBuilder = KeyRegTransactionDeepLinkBuilder(),
            cardsDeepLinkBuilder = CardsDeepLinkBuilder(),
            stakingDeepLinkBuilder = StakingDeepLinkBuilder()
        )
    }

    @Provides
    fun provideCreateNewDeepLink(
        parseNewDeepLinkPayload: ParseNewDeepLinkPayload
    ): CreateNewDeepLink {
        return CreateNewDeepLinkImpl(
            parseNewDeepLinkPayload = parseNewDeepLinkPayload,
            addContactNewDeepLinkBuilder = AddContactNewDeepLinkBuilder(),
            editContactNewDeepLinkBuilder = EditContactNewDeepLinkBuilder(),
            addWatchAccountNewDeepLinkBuilder = AddWatchAccountNewDeepLinkBuilder(),
            receiverAccountSelectionNewDeepLinkBuilder = ReceiverAccountSelectionNewDeepLinkBuilder(),
            addressActionsNewDeepLinkBuilder = AddressActionsNewDeepLinkBuilder(),
            assetTransferNewDeepLinkBuilder = AssetTransferNewDeepLinkBuilder(),
            assetOptInNewDeepLinkBuilder = AssetOptInNewDeepLinkBuilder(),
            assetInboxNewDeepLinkBuilder = AssetInboxNewDeepLinkBuilder(),
            assetDetailNewDeepLinkBuilder = AssetDetailNewDeepLinkBuilder(),
            swapNewDeepLinkBuilder = SwapNewDeepLinkBuilder(),
            buyNewDeepLinkBuilder = BuyNewDeepLinkBuilder(),
            sellNewDeepLinkBuilder = SellNewDeepLinkBuilder(),
            keyRegNewDeepLinkBuilder = KeyRegNewDeepLinkBuilder(),
            recoverAccountNewDeepLinkBuilder = RecoverAccountNewDeepLinkBuilder(),
            webImportNewDeepLinkBuilder = WebImportNewDeepLinkBuilder(),
            walletConnectNewDeepLinkBuilder = WalletConnectNewDeepLinkBuilder(),
            discoverBrowserNewDeepLinkBuilder = DiscoverBrowserNewDeepLinkBuilder(),
            discoverPathNewDeepLinkBuilder = DiscoverPathNewDeepLinkBuilder(),
            cardsPathNewDeepLinkBuilder = CardsPathNewDeepLinkBuilder(),
            stakingPathNewDeepLinkBuilder = StakingPathNewDeepLinkBuilder(),
            accountDetailNewDeepLinkBuilder = AccountDetailNewDeepLinkBuilder(),
            internalBrowserNewDeepLinkBuilder = InternalBrowserNewDeepLinkBuilder(),
        )
    }
}
