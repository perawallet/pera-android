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

package com.algorand.android.ui.transaction.csv.usecase

import com.algorand.android.modules.transaction.csv.domain.repository.CsvRepository
import com.algorand.android.ui.transaction.csv.model.CreateCsvArgs
import com.algorand.android.utils.recordException
import com.algorand.wallet.foundation.PeraResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Named

internal class CreateCsvFileUseCase @Inject constructor(
    @param:Named(CsvRepository.INJECTION_NAME)
    private val csvRepository: CsvRepository
) : CreateCsvFile {

    override suspend fun invoke(args: CreateCsvArgs): PeraResult<File> {
        var result: PeraResult<File>? = null
        csvRepository.getCsv(args.cacheDirectory, args.address, args.dateRange, args.assetId).use(
            onSuccess = { inputStream ->
                val csvFile = createCsvFile(args.cacheDirectory, args.address, inputStream)
                result = if (csvFile != null) PeraResult.Success(csvFile) else PeraResult.Error(IOException())
            },
            onFailed = { exception, code ->
                result = PeraResult.Error(exception, code)
            }
        )
        return result!!
    }

    private suspend fun createCsvFile(cacheDirectory: File, publicKey: String, inputStream: InputStream): File? {
        val csvDirectory = File(cacheDirectory, CSV_FILES_FOLDER)
        val tempCSVFile = File(csvDirectory, "$publicKey.csv")
        var csvFile: File? = null
        try {
            csvDirectory.mkdirs()
            try {
                withContext(Dispatchers.IO) {
                    val outputStream = FileOutputStream(tempCSVFile)
                    outputStream.use { output ->
                        val buffer = ByteArray(BYTEARRAY_SIZE)
                        var readData: Int
                        readData = inputStream.read(buffer)
                        while (readData != -1) {
                            output.write(buffer, 0, readData)
                            readData = inputStream.read(buffer)
                        }
                        output.flush()
                    }
                    csvFile = tempCSVFile
                }
            } catch (exception: Exception) {
                recordException(exception)
            } finally {
                withContext(Dispatchers.IO) {
                    inputStream.close()
                }
            }
        } catch (exception: Exception) {
            tempCSVFile.delete()
            recordException(exception)
        }
        return csvFile
    }

    private companion object {
        const val BYTEARRAY_SIZE = 4 * 1024
        const val CSV_FILES_FOLDER = "csvFiles"
    }
}
