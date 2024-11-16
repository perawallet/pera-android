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

import com.algorand.common.deeplink.model.PeraUri
import com.algorand.common.encryption.Base64Manager

internal class UrlQueryParser(
    private val base64Manager: Base64Manager
) : DeepLinkQueryParser<String?> {

    override fun parseQuery(peraUri: PeraUri): String? {
        val urlQuery = peraUri.getQueryParam(URL_QUERY_KEY) ?: return null
        return try {
            base64Manager.decode(urlQuery).takeIf { it.isNotEmpty() }?.decodeToString()
        } catch (exception: Exception) {
            null
        }
    }

    private companion object {
        const val URL_QUERY_KEY = "url"
    }
}
