/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.data.repository

import android.content.Context
import com.algorand.android.R
import com.algorand.android.exceptions.RetrofitErrorHandler
import com.algorand.android.models.Result
import com.algorand.android.modules.assetinbox.send.data.mapper.Arc59SendSummaryMapper
import com.algorand.android.modules.assetinbox.send.data.service.Arc59SendSummaryApiService
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59SendSummary
import com.algorand.android.modules.assetinbox.send.domain.repository.Arc59SendSummaryRepository
import com.algorand.android.network.requestWithHipoErrorHandler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Arc59SendSummaryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val arc59SendSummaryApi: Arc59SendSummaryApiService,
    private val retrofitErrorHandler: RetrofitErrorHandler,
    private val arc59SendSummaryMapper: Arc59SendSummaryMapper
) : Arc59SendSummaryRepository {

    override suspend fun getArc59SendSummary(
        address: String,
        assetId: Long
    ): Result<Arc59SendSummary> {
        val result = requestWithHipoErrorHandler(retrofitErrorHandler) {
            arc59SendSummaryApi.getArc59SendSummary(address, assetId)
        }
        if (result is Result.Error) return result
        val sendSummaryResponse = (result as Result.Success).data
        val sendSummary = arc59SendSummaryMapper(sendSummaryResponse)
        return if (sendSummary == null) {
            Result.Error(Exception(context.getString(R.string.failed_to_map_the_response)))
        } else {
            Result.Success(sendSummary)
        }
    }
}
