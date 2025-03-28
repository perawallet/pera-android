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

package com.algorand.android.modules.assetinbox.send.summary.data.model

import com.google.gson.annotations.SerializedName

data class Arc59WarningMessageResponse(
    @SerializedName("title")
    val title: String?,
    @SerializedName("detail")
    val detail: String?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("link_text")
    val linkText: String?
)
