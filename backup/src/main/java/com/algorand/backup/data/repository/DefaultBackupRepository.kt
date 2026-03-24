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

package com.algorand.backup.data.repository

import com.algorand.backup.data.api.model.BatchReadRequest
import com.algorand.backup.data.mapper.DeltaEntryResponseMapper
import com.algorand.backup.data.mapper.ManifestResponseMapper
import com.algorand.backup.data.service.BackupApiService
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.backup.domain.model.DeltaEntry
import com.algorand.backup.domain.model.Manifest
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DefaultBackupRepository @Inject constructor(
    private val backupApiService: BackupApiService,
    private val manifestMapper: ManifestResponseMapper,
    private val deltaMapper: DeltaEntryResponseMapper
) : BackupRepository {

    override suspend fun getManifest(backupId: BackupId): PeraResult<Manifest> {
        return try {
            val response = backupApiService.getManifest(backupId.value)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val manifest = manifestMapper.toDomainModel(body)
                if (manifest != null) {
                    PeraResult.Success(manifest)
                } else {
                    PeraResult.Error(IllegalStateException("Failed to parse manifest"))
                }
            } else {
                PeraResult.Error(IllegalStateException("Manifest request failed"), response.code())
            }
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun getDeltas(
        backupId: BackupId,
        fromSeq: Long,
        types: List<BackupItemType>?
    ): PeraResult<List<DeltaEntry>> {
        return try {
            val typesQuery = types?.joinToString(",") { it.name }
            val response = backupApiService.getDeltas(backupId.value, fromSeq, typesQuery)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val deltas = body.mapNotNull { deltaMapper.toDomainModel(it) }
                PeraResult.Success(deltas)
            } else {
                PeraResult.Error(IllegalStateException("Delta request failed"), response.code())
            }
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun getItem(backupId: BackupId, key: BackupItemKey): PeraResult<String> {
        return try {
            val response = backupApiService.getItem(backupId.value, key.value)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                PeraResult.Success(body)
            } else {
                PeraResult.Error(IllegalStateException("Get item failed"), response.code())
            }
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun batchReadItems(
        backupId: BackupId,
        keys: List<BackupItemKey>
    ): PeraResult<Map<BackupItemKey, String>> {
        return try {
            val request = BatchReadRequest(keys.map { it.value })
            val response = backupApiService.batchReadItems(backupId.value, request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val items = body.items
                    ?.filter { it.payload != null && it.key != null }
                    ?.associate { BackupItemKey(it.key!!) to it.payload!! }.orEmpty()
                PeraResult.Success(items)
            } else {
                PeraResult.Error(IllegalStateException("Batch read failed"), response.code())
            }
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun deleteItem(backupId: BackupId, key: BackupItemKey): PeraResult<Long> {
        return try {
            val response = backupApiService.deleteItem(backupId.value, key.value)
            val body = response.body()
            if (response.isSuccessful && body?.seq != null) {
                PeraResult.Success(body.seq)
            } else {
                PeraResult.Error(IllegalStateException("Delete item failed"), response.code())
            }
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }
}
