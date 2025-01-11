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

package com.algorand.common.nameservice.data.service

import com.algorand.common.foundation.PeraResult
import com.algorand.common.foundation.network.safeRequest
import com.algorand.common.nameservice.data.model.NameServiceSearchResponse
import com.algorand.common.nameservice.data.model.SearchNameServiceRequestBody
import com.algorand.common.nameservice.data.model.SearchNameServiceResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class NameServiceApiServiceImpl(private val client: HttpClient) : NameServiceApiService {

    override suspend fun fetchAccountsNameServices(
        body: SearchNameServiceRequestBody
    ): PeraResult<SearchNameServiceResponse> {
        return safeRequest {
            client.post("v1/accounts/names/bulk-read/") {
                setBody(body)
            }
        }
    }

    override suspend fun getNameServiceAccountAddresses(name: String): PeraResult<NameServiceSearchResponse> {
        return safeRequest {
            client.get("v1/name-services/search/") {
                parameter("name", name)
            }
        }
    }
}
