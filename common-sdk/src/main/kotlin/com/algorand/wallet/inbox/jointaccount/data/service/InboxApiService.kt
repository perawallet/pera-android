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

package com.algorand.wallet.inbox.jointaccount.data.service

import com.algorand.wallet.inbox.jointaccount.data.model.InboxSearchRequest
import com.algorand.wallet.inbox.jointaccount.data.model.InboxSearchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

internal interface InboxApiService {

    @POST("v1/inbox/{device_id}/")
    suspend fun getInboxMessages(
        @Path("device_id") deviceId: Long,
        @Body inboxSearchRequest: InboxSearchRequest
    ): Response<InboxSearchResponse>

    @DELETE("v1/joint-accounts/inbox/device-import/{device_id}/{multisig_address}/")
    suspend fun deleteInboxJointInvitationNotification(
        @Path("device_id") deviceId: Long,
        @Path("multisig_address") jointAddress: String
    ): Response<Unit>
}
