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

package com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.repository

import android.content.Context
import com.algorand.android.R
import com.algorand.android.exceptions.RetrofitErrorHandler
import com.algorand.android.models.Result
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.mapper.AssetInboxOneAccountMapper
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.data.service.AssetInboxOneAccountApiService
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.model.AssetInboxOneAccountPaginated
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.domain.repository.AssetInboxOneAccountRepository
import com.algorand.android.network.requestWithHipoErrorHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AssetInboxOneAccountRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val assetInboxOneAccountApiService: AssetInboxOneAccountApiService,
    private val retrofitErrorHandler: RetrofitErrorHandler,
    private val assetInboxOneAccountMapper: AssetInboxOneAccountMapper
) : AssetInboxOneAccountRepository {

    override suspend fun getAssetInboxOneAccount(address: String): Result<AssetInboxOneAccountPaginated> {
        val result = requestWithHipoErrorHandler(retrofitErrorHandler) {
            assetInboxOneAccountApiService.getAssetInboxOneAccountRequests(address)
        }
        if (result is Result.Error) return result
        val assetInboxOneAccountPaginatedResponse = (result as Result.Success).data
        val assetInboxOneAccountPaginated =
            assetInboxOneAccountMapper(assetInboxOneAccountPaginatedResponse)
        return if (assetInboxOneAccountPaginated == null) {
            Result.Error(Exception(context.getString(R.string.failed_to_map_the_response)))
        } else {
            Result.Success(assetInboxOneAccountPaginated)
        }
    }

    override suspend fun getAssetInboxOneAccountMore(nextUrl: String): Result<AssetInboxOneAccountPaginated> {
        val result = requestWithHipoErrorHandler(retrofitErrorHandler) {
            assetInboxOneAccountApiService.getAssetInboxOneAccountRequestsMore(nextUrl)
        }
        if (result is Result.Error) return result
        val assetInboxOneAccountPaginatedResponse = (result as Result.Success).data
        val assetInboxOneAccountPaginated =
            assetInboxOneAccountMapper(assetInboxOneAccountPaginatedResponse)
        return if (assetInboxOneAccountPaginated == null) {
            Result.Error(Exception(context.getString(R.string.failed_to_map_the_response)))
        } else {
            Result.Success(assetInboxOneAccountPaginated)
        }
    }
}
