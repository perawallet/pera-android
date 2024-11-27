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

package com.algorand.common.deeplink.di

import com.algorand.common.deeplink.builder.AccountAddressDeepLinkBuilder
import com.algorand.common.deeplink.builder.AssetOptInDeepLinkBuilder
import com.algorand.common.deeplink.builder.AssetTransferDeepLinkBuilder
import com.algorand.common.deeplink.builder.DiscoverBrowserDeepLinkBuilder
import com.algorand.common.deeplink.builder.MnemonicDeepLinkBuilder
import com.algorand.common.deeplink.builder.NotificationDeepLinkBuilder
import com.algorand.common.deeplink.builder.WalletConnectConnectionDeepLinkBuilder
import com.algorand.common.deeplink.builder.WebImportQrCodeDeepLinkBuilder
import com.algorand.common.deeplink.parser.CreateDeepLink
import com.algorand.common.deeplink.parser.CreateDeepLinkImpl
import com.algorand.common.deeplink.parser.ParseDeepLinkPayload
import com.algorand.common.deeplink.parser.ParseDeepLinkPayloadImpl
import com.algorand.common.deeplink.parser.PeraUriParser
import com.algorand.common.deeplink.parser.PeraUriParserImpl
import com.algorand.common.deeplink.parser.query.AccountAddressQueryParser
import com.algorand.common.deeplink.parser.query.AssetIdQueryParser
import com.algorand.common.deeplink.parser.query.MnemonicQueryParser
import com.algorand.common.deeplink.parser.query.NotificationGroupTypeQueryParser
import com.algorand.common.deeplink.parser.query.UrlQueryParser
import com.algorand.common.deeplink.parser.query.WalletConnectUrlQueryParser
import com.algorand.common.deeplink.parser.query.WebImportQrCodeQueryParser
import org.koin.dsl.module

val deepLinkModule = module {

    factory<PeraUriParser> { PeraUriParserImpl() }

    factory<ParseDeepLinkPayload> {
        ParseDeepLinkPayloadImpl(
            peraUriParser = get(),
            accountAddressQueryParser = AccountAddressQueryParser(),
            assetIdQueryParser = AssetIdQueryParser(),
            notificationGroupTypeQueryParser = NotificationGroupTypeQueryParser(),
            webImportQrCodeQueryParser = WebImportQrCodeQueryParser(),
            urlQueryParser = UrlQueryParser(get()),
            mnemonicQueryParser = MnemonicQueryParser(),
            walletConnectUrlQueryParser = WalletConnectUrlQueryParser()
        )
    }

    factory<CreateDeepLink> {
        CreateDeepLinkImpl(
            parseDeepLinkPayload = get(),
            accountAddressDeepLinkBuilder = AccountAddressDeepLinkBuilder(),
            assetOptInDeepLinkBuilder = AssetOptInDeepLinkBuilder(),
            assetTransferDeepLinkBuilder = AssetTransferDeepLinkBuilder(),
            mnemonicDeepLinkBuilder = MnemonicDeepLinkBuilder(),
            walletConnectConnectionDeepLinkBuilder = WalletConnectConnectionDeepLinkBuilder(),
            webImportQrCodeDeepLinkBuilder = WebImportQrCodeDeepLinkBuilder(),
            notificationGroupDeepLinkBuilder = NotificationDeepLinkBuilder(),
            discoverBrowserDeepLinkBuilder = DiscoverBrowserDeepLinkBuilder()
        )
    }
}
