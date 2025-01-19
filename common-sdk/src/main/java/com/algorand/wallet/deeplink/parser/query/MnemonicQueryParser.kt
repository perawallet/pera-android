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

import com.algorand.wallet.deeplink.model.PeraUri
import com.algorand.wallet.foundation.json.JsonSerializer
import com.google.gson.annotations.SerializedName
import javax.inject.Inject

internal class MnemonicQueryParser @Inject constructor(
    private val jsonSerializer: JsonSerializer
) : DeepLinkQueryParser<String?> {

    override fun parseQuery(peraUri: PeraUri): String? {
        return try {
            jsonSerializer.fromJson(peraUri.rawUri, MnemonicPayload::class.java)?.mnemonic
        } catch (e: Exception) {
            null
        }
    }

    internal data class MnemonicPayload(
        @SerializedName("version") val version: Double? = null,
        @SerializedName("mnemonic") val mnemonic: String
    )
}
