/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.banner.data.model

import com.google.gson.annotations.SerializedName

internal data class BannerDetailResponse(
    @SerializedName("id") val bannerId: Long?,
    @SerializedName("type") val bannerTypeResponse: BannerTypeResponse?,
    @SerializedName("title") val title: String?,
    @SerializedName("subtitle") val description: String?,
    @SerializedName("button_label") val buttonText: String?,
    @SerializedName("button_url") val buttonUrl: String?
)