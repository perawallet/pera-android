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

package com.algorand.common.account.info.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AccountInformationResponsePayloadResponse(
    @SerialName("address")
    val address: String? = null,
    @SerialName("amount")
    val amount: String? = null,
    @SerialName("participation")
    val participation: ParticipationResponse? = null,
    @SerialName("auth-addr")
    val rekeyAdminAddress: String? = null,
    @SerialName("assets")
    val allAssetHoldingList: List<AssetHoldingResponse>? = null,
    @SerialName("created-at-round")
    val createdAtRound: Long? = null,
    @SerialName("apps-total-schema")
    val appStateSchemaResponse: AppStateSchemaResponse? = null,
    @SerialName("apps-total-extra-pages")
    val appsTotalExtraPages: Int? = null,
    @SerialName("total-apps-opted-in")
    val totalAppsOptedIn: Int? = null,
    @SerialName("total-assets-opted-in")
    val totalAssetsOptedIn: Int? = null,
    @SerialName("total-created-apps")
    val totalCreatedApps: Int? = null,
    @SerialName("total-created-assets")
    val totalCreatedAssets: Int? = null
)
