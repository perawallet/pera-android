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

package com.algorand.wallet.swap.data.model

import com.google.gson.annotations.SerializedName

private const val TINYMAN_SERIALIZE_VALUE = "tinyman"
private const val TINYMAN_V2_SERIALIZE_VALUE = "tinyman-v2"
private const val TINYMAN_SWAP_ROUTER_SERIALIZE_VALUE = "tinyman-swap-router"
private const val VESTIGE_V3_SERIALIZE_VALUE = "vestige-v3"
private const val VESTIGE_V4_SERIALIZE_VALUE = "vestige-v4"
private const val FOLKS_ROUTER_SERIALIZE_VALUE = "folks-router"
private const val DEFLEX_SERIALIZE_VALUE = "deflex"

internal enum class SwapQuoteProviderResponse(val value: String?) {

    @SerializedName(TINYMAN_SERIALIZE_VALUE)
    TINYMAN(TINYMAN_SERIALIZE_VALUE),

    @SerializedName(TINYMAN_V2_SERIALIZE_VALUE)
    TINYMAN_V2(TINYMAN_V2_SERIALIZE_VALUE),

    @SerializedName(TINYMAN_SWAP_ROUTER_SERIALIZE_VALUE)
    TINYMAN_SWAP_ROUTER(TINYMAN_SWAP_ROUTER_SERIALIZE_VALUE),

    @SerializedName(VESTIGE_V3_SERIALIZE_VALUE)
    VESTIGE_V3(VESTIGE_V3_SERIALIZE_VALUE),

    @SerializedName(VESTIGE_V4_SERIALIZE_VALUE)
    VESTIGE_V4(VESTIGE_V4_SERIALIZE_VALUE),

    @SerializedName(FOLKS_ROUTER_SERIALIZE_VALUE)
    FOLKS_ROUTER(FOLKS_ROUTER_SERIALIZE_VALUE),

    @SerializedName(DEFLEX_SERIALIZE_VALUE)
    DEFLEX(DEFLEX_SERIALIZE_VALUE),

    UNKNOWN(null);

    companion object {

        fun getValues(): List<SwapQuoteProviderResponse> {
            return entries.filterNot { it.value == null }
        }
    }
}