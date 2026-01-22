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

package com.algorand.wallet.inbox.jointaccount.data.model

import com.algorand.wallet.jointaccount.creation.data.model.JointAccountResponse
import com.algorand.wallet.jointaccount.transaction.data.model.JointSignRequestResponse
import com.google.gson.annotations.SerializedName

internal data class InboxSearchRequest(
    @SerializedName("addresses")
    val addresses: List<String>
)

internal data class InboxSearchResponse(
    @SerializedName("joint_account_import_requests")
    val jointAccountImportRequests: List<JointAccountResponse>?,
    @SerializedName("joint_account_sign_requests")
    val jointAccountSignRequests: List<JointSignRequestResponse>?,
    @SerializedName("asa_inboxes")
    val asaInboxes: List<AssetInboxResponse>?
)

internal data class AssetInboxResponse(
    @SerializedName("address")
    val address: String?,
    @SerializedName("inbox_address")
    val inboxAddress: String?,
    @SerializedName("request_count")
    val requestCount: Int?
)
