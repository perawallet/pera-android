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

package com.algorand.common.deeplink.parser.query

import com.algorand.common.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.common.deeplink.model.PeraUri
import com.algorand.common.deeplink.utils.isCoinbaseDeepLink

internal class AssetIdQueryParser : DeepLinkQueryParser<Long?> {

    override fun parseQuery(peraUri: PeraUri): Long? {
        val assetIdAsString = when {
            isCoinbaseDeepLink(peraUri) -> getAssetIdForCoinbase(peraUri)
            else -> peraUri.getQueryParam(ASSET_ID_QUERY_KEY)
        }
        return assetIdAsString?.toLongOrNull()
    }

    private fun getAssetIdForCoinbase(peraUri: PeraUri): String {
        // algo:31566704/transfer?address=KG2HXWIOQSBOBGJEXSIBNEVNTRD4G4EFIJGRKBG2ZOT7NQ
        val regexWithAssetId = COINBASE_ASSET_ID_REGEX.toRegex()
        val matchResultWithAssetId = regexWithAssetId.find(peraUri.rawUri)
        return matchResultWithAssetId?.destructured?.component1() ?: ALGO_ID.toString()
    }

    private companion object {
        const val ASSET_ID_QUERY_KEY = "asset"
        private const val COINBASE_ASSET_ID_REGEX = """algo:(\d+)"""
    }
}
