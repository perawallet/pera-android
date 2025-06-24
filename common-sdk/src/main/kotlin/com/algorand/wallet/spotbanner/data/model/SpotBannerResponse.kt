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

package com.algorand.wallet.spotbanner.data.model

import com.google.gson.annotations.SerializedName

internal data class SpotBannerResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("text") val text: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("button_url_is_external") val isExternalButtonUrl: Boolean?
)
