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

package com.algorand.common.asset.data.service

import com.algorand.common.asset.data.model.AssetResponse
import com.algorand.common.foundation.PeraResult
import com.algorand.common.foundation.network.model.PaginationResponse
import com.algorand.common.foundation.network.safeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class AssetDetailApiServiceImpl(
    private val client: HttpClient
) : AssetDetailApiService {

    override suspend fun getAssetsByIds(
        assetIdsList: String,
        includeDeleted: Boolean?
    ): PeraResult<PaginationResponse<AssetResponse>> {
        return safeRequest {
            client.get("v1/assets/") {
                parameter("asset_ids", assetIdsList)
                includeDeleted?.let { parameter("include_deleted", it) }
            }
        }
    }

    override suspend fun getAssetDetail(nftAssetId: Long): PeraResult<AssetResponse> {
        return safeRequest {
            client.get("v1/assets/$nftAssetId/")
        }
    }
}
