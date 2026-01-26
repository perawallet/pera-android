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

package com.algorand.wallet.jointaccount.data.service

import com.algorand.wallet.jointaccount.creation.data.model.CreateJointAccountRequest
import com.algorand.wallet.jointaccount.creation.data.model.JointAccountResponse
import com.algorand.wallet.jointaccount.transaction.data.model.JointSignRequestResponse
import com.algorand.wallet.jointaccount.transaction.data.model.ProposeJointSignRequestRequest
import com.algorand.wallet.jointaccount.transaction.data.model.SearchSignRequestsRequest
import com.algorand.wallet.jointaccount.transaction.data.model.SearchSignRequestsResponse
import com.algorand.wallet.jointaccount.transaction.data.model.SignRequestTransactionListResponseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

internal interface JointAccountApiService {

    @POST("v1/joint-accounts/accounts/")
    suspend fun createJointAccount(
        @Body createJointAccountRequest: CreateJointAccountRequest
    ): Response<JointAccountResponse>

    @POST("v1/joint-accounts/sign-requests/")
    suspend fun proposeSignRequest(
        @Body proposeSignRequestRequest: ProposeJointSignRequestRequest
    ): Response<JointSignRequestResponse>

    @POST("v1/joint-accounts/sign-requests/{sign_request_id}/responses/{participant_address}/")
    suspend fun addSignature(
        @Path("sign_request_id") signRequestId: String,
        @Path("participant_address") participantAddress: String,
        @Body signRequestTransactionListResponseRequest: SignRequestTransactionListResponseRequest
    ): Response<JointSignRequestResponse>

    @POST("v1/joint-accounts/sign-requests/search/")
    suspend fun searchSignRequests(
        @Body searchSignRequestsRequest: SearchSignRequestsRequest
    ): Response<SearchSignRequestsResponse>
}
