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

import com.algorand.common.deeplink.model.PeraUri

internal class PeraUriParserImpl : PeraUriParser {

    override fun parseUri(uri: String): PeraUri {
        val matchResult = URI_REGEX.matchEntire(uri) ?: return createUriOnly(uri)
        return PeraUri(
            scheme = getScheme(matchResult),
            host = getHost(matchResult),
            path = getPath(matchResult),
            queryParams = getQueryParams(matchResult),
            fragment = getFragment(matchResult),
            rawUri = uri
        )
    }

    private fun createUriOnly(uri: String): PeraUri {
        return PeraUri(scheme = null, host = null, path = null, queryParams = emptyMap(), fragment = null, rawUri = uri)
    }

    private fun getScheme(matchResult: MatchResult): String? {
        return matchResult.groups[1]?.value
    }

    private fun getHost(matchResult: MatchResult): String? {
        return matchResult.groups[2]?.value
    }

    private fun getPath(matchResult: MatchResult): String? {
        return matchResult.groups[3]?.value?.removePrefix("/")
    }

    private fun getFragment(matchResult: MatchResult): String? {
        return matchResult.groups[5]?.value?.removePrefix("#")
    }

    private fun getQueryParams(matchResult: MatchResult): Map<String, String?> {
        val query = matchResult.groups[4]?.value?.removePrefix("?") ?: return emptyMap()
        return parseQueryParams(query)
    }

    private fun parseQueryParams(query: String): Map<String, String?> {
        return query.split("&").associate { param ->
            val (key, value) = param.split("=", limit = 2).let {
                it[0] to it.getOrNull(1)
            }
            key to value
        }
    }

    private companion object {
        val URI_REGEX = Regex("""^([a-zA-Z][a-zA-Z\d+.-]*):\/\/([^\/?#]+)(\/[^?#]*)?(\?[^#]*)?(#.*)?$""")
    }
}
