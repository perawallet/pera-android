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

package com.algorand.wallet.deeplink.parser.query

import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdkAddress
import com.algorand.wallet.deeplink.model.PeraUri
import com.algorand.wallet.deeplink.utils.getCoinbaseAddress
import com.algorand.wallet.deeplink.utils.isCoinbaseDeepLink

internal class AccountAddressQueryParser(
    private val algoSdkAddress: AlgoSdkAddress
) : DeepLinkQueryParser<String?> {

    override fun parseQuery(peraUri: PeraUri): String? {
        return when {
            peraUri.isAppLink() -> getAddressFromAppLink(peraUri)
            isCoinbaseDeepLink(peraUri.rawUri) -> getCoinbaseAddress(peraUri.rawUri)
            else -> peraUri.getQueryParam(ACCOUNT_ID_QUERY_KEY) ?: peraUri.host
        }?.takeIf { algoSdkAddress.isValid(it) } ?: peraUri.rawUri.takeIf { algoSdkAddress.isValid(it) }
    }

    private fun getAddressFromAppLink(uri: PeraUri): String? {
        return uri.path
            ?.split("/")
            ?.firstOrNull { algoSdkAddress.isValid(it) }
            ?: uri.getQueryParam(ACCOUNT_ID_QUERY_KEY)
    }

    private companion object {
        const val ACCOUNT_ID_QUERY_KEY = "account"
    }
}
