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

package com.algorand.wallet.deeplink.parser.query

import com.algorand.wallet.algosdk.AlgoSdkUtils
import com.algorand.wallet.deeplink.model.PeraUri
import com.algorand.wallet.deeplink.utils.isCoinbaseDeepLink
import javax.inject.Inject

internal class AccountAddressQueryParser @Inject constructor(
    private val algoSdkUtils: AlgoSdkUtils
) : DeepLinkQueryParser<String?> {

    override fun parseQuery(peraUri: PeraUri): String? {
        return when {
            peraUri.isAppLink() -> getAddressFromAppLink(peraUri)
            isCoinbaseDeepLink(peraUri) -> getAccountAddressForCoinbase(peraUri)
            else -> peraUri.getQueryParam(ACCOUNT_ID_QUERY_KEY) ?: peraUri.host
        }?.takeIf { algoSdkUtils.isValidAddress(it) } ?: peraUri.rawUri.takeIf { algoSdkUtils.isValidAddress(it) }
    }

    private fun getAddressFromAppLink(uri: PeraUri): String? {
        return uri.path
            ?.split("/")
            ?.firstOrNull { algoSdkUtils.isValidAddress(it) }
    }

    private fun getAccountAddressForCoinbase(uri: PeraUri): String? {
        // algo:31566704/transfer?address=KG2HXWIOQSBOBGJEXSIBNEVNTRD4G4EFIJGRKBG2ZOT7NQ
        val regexAddress = COINBASE_ACCOUNT_ADDRESS_WITH_ASSET_ID_REGEX.toRegex()
        regexAddress.find(uri.rawUri)?.destructured?.component1()?.let { return it }

        // algo:Z7HJOZWPBM76GNERLD56IUMNMA7TNFMERU4KSDDXLUYGFBRLLVVGKGULCE
        val regexWithoutAssetId = COINBASE_ACCOUNT_ADDRESS_REGEX.toRegex()
        regexWithoutAssetId.find(uri.rawUri)?.destructured?.component1()?.let { return it }
        return null
    }

    private companion object {
        const val COINBASE_ACCOUNT_ADDRESS_WITH_ASSET_ID_REGEX = """address=([A-Z0-9]+)"""
        const val COINBASE_ACCOUNT_ADDRESS_REGEX = """algo:([A-Z0-9]+)"""
        const val ACCOUNT_ID_QUERY_KEY = "account"
    }
}
