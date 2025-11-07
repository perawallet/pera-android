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

package com.algorand.android.modules.transaction.csv.domain.usecase

import com.algorand.android.models.DateRange
import com.algorand.android.ui.transaction.csv.model.CreateCsvArgs
import com.algorand.android.ui.transaction.csv.usecase.CreateCsvFile
import com.algorand.android.utils.DataResource
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.flow

class CreateCsvUseCase @Inject constructor(private val createCsvFile: CreateCsvFile) {

    fun createTransactionHistoryCsvFile(
        cacheDirectory: File,
        publicKey: String,
        dateRange: DateRange?,
        assetId: Long?
    ) = flow<DataResource<File>> {
        emit(DataResource.Loading())
        val args = CreateCsvArgs(cacheDirectory, publicKey, dateRange, assetId)
        createCsvFile(args).use(
            onSuccess = {
                emit(DataResource.Success(it))
            },
            onFailed = { exception, code ->
                emit(DataResource.Error.Api(exception, code))
            }
        )
    }
}
