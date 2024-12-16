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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class MnemonicQueryParser : DeepLinkQueryParser<String?> {

    override fun parseQuery(peraUri: PeraUri): String? {
        return try {
            Json.decodeFromString<MnemonicPayload>(peraUri.rawUri).mnemonic
        } catch (e: Exception) {
            null
        }
    }

    @Serializable
    private data class MnemonicPayload(
        @SerialName("version") val version: Double? = null,
        @SerialName("mnemonic") val mnemonic: String
    )
}
