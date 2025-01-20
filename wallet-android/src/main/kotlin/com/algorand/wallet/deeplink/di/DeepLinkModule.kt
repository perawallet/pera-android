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

package com.algorand.wallet.deeplink.di

import com.algorand.wallet.algosdk.AlgoSdkUtils
import com.algorand.wallet.deeplink.builder.AccountAddressDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetInboxDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetOptInDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.AssetTransferDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.CardsDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.DiscoverBrowserDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.DiscoverDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.KeyRegTransactionDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.MnemonicDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.NotificationDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.StakingDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.WalletConnectConnectionDeepLinkBuilder
import com.algorand.wallet.deeplink.builder.WebImportQrCodeDeepLinkBuilder
import com.algorand.wallet.deeplink.parser.CreateDeepLink
import com.algorand.wallet.deeplink.parser.CreateDeepLinkImpl
import com.algorand.wallet.deeplink.parser.ParseDeepLinkPayload
import com.algorand.wallet.deeplink.parser.ParseDeepLinkPayloadImpl
import com.algorand.wallet.deeplink.parser.PeraUriParser
import com.algorand.wallet.deeplink.parser.PeraUriParserImpl
import com.algorand.wallet.deeplink.parser.query.AccountAddressQueryParser
import com.algorand.wallet.deeplink.parser.query.AssetIdQueryParser
import com.algorand.wallet.deeplink.parser.query.MnemonicQueryParser
import com.algorand.wallet.deeplink.parser.query.NotificationGroupTypeQueryParser
import com.algorand.wallet.deeplink.parser.query.UrlQueryParser
import com.algorand.wallet.deeplink.parser.query.WalletConnectUrlQueryParser
import com.algorand.wallet.deeplink.parser.query.WebImportQrCodeQueryParser
import com.algorand.wallet.encryption.Base64Manager
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
    fun provideParseDeepLinkPayload(
        peraUriParser: PeraUriParser,
        algoSdkUtils: AlgoSdkUtils,
        jsonSerializer: JsonSerializer,
        base64Manager: Base64Manager
    ): ParseDeepLinkPayload {
        return ParseDeepLinkPayloadImpl(
            peraUriParser = peraUriParser,
            accountAddressQueryParser = AccountAddressQueryParser(algoSdkUtils),
            assetIdQueryParser = AssetIdQueryParser(),
            notificationGroupTypeQueryParser = NotificationGroupTypeQueryParser(),
            webImportQrCodeQueryParser = WebImportQrCodeQueryParser(jsonSerializer),
            urlQueryParser = UrlQueryParser(base64Manager),
            mnemonicQueryParser = MnemonicQueryParser(jsonSerializer),
            walletConnectUrlQueryParser = WalletConnectUrlQueryParser()
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
            mnemonicDeepLinkBuilder = MnemonicDeepLinkBuilder(),
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
}
