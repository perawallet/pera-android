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

package com.algorand.wallet.account.info.data.model

import com.google.gson.annotations.SerializedName

internal data class AccountInformationResponsePayloadResponse(
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("amount")
    val amount: String? = null,
    @SerializedName("participation")
    val participation: ParticipationResponse? = null,
    @SerializedName("auth-addr")
    val rekeyAdminAddress: String? = null,
    @SerializedName("assets")
    val allAssetHoldingList: List<AssetHoldingResponse>? = null,
    @SerializedName("created-at-round")
    val createdAtRound: Long? = null,
    @SerializedName("apps-total-schema")
    val appStateSchemaResponse: AppStateSchemaResponse? = null,
    @SerializedName("apps-total-extra-pages")
    val appsTotalExtraPages: Int? = null,
    @SerializedName("total-apps-opted-in")
    val totalAppsOptedIn: Int? = null,
    @SerializedName("total-assets-opted-in")
    val totalAssetsOptedIn: Int? = null,
    @SerializedName("total-created-apps")
    val totalCreatedApps: Int? = null,
    @SerializedName("total-created-assets")
    val totalCreatedAssets: Int? = null
)
