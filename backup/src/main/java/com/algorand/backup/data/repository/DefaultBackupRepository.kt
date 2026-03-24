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
import com.algorand.backup.data.api.model.BatchUpsertItemRequest
import com.algorand.backup.data.api.model.BatchUpsertRequest
import com.algorand.backup.data.api.model.UpsertItemRequest
import com.algorand.backup.data.mapper.DeltaEntryResponseMapper
import com.algorand.backup.data.mapper.ManifestResponseMapper
import com.algorand.backup.data.service.BackupApiService
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemStatus
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.backup.domain.model.BatchUpsertItemResult
import com.algorand.backup.domain.model.DeltaEntry
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.ItemHash
import com.algorand.backup.domain.model.Manifest
import com.algorand.backup.domain.model.UpsertItemResult
import com.algorand.backup.domain.model.BatchUpsertInput
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

    override suspend fun upsertItem(
        backupId: BackupId,
        key: BackupItemKey,
        expectedVersion: Int,
        status: BackupItemStatus,
        deviceId: DeviceId,
        payload: String
    ): PeraResult<UpsertItemResult> {
        return try {
            val request = UpsertItemRequest(
                expectedVersion = expectedVersion,
                status = status.name,
                deviceId = deviceId.value,
                payload = payload
            )
            val response = backupApiService.upsertItem(backupId.value, key.value, request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val result = parseUpsertResponse(body.newVersion, body.seq, body.currentVersion, body.currentHash)
                PeraResult.Success(result)
            } else {
                PeraResult.Error(IllegalStateException("Upsert item failed"), response.code())
            }
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun batchUpsertItems(
        backupId: BackupId,
        deviceId: DeviceId,
        items: List<BatchUpsertInput>
    ): PeraResult<List<BatchUpsertItemResult>> {
        return try {
            val request = BatchUpsertRequest(
                deviceId = deviceId.value,
                items = items.map { input ->
                    BatchUpsertItemRequest(
                        key = input.key.value,
                        expectedVersion = input.expectedVersion,
                        status = input.status.name,
                        payload = input.payload
                    )
                }
            )
            val response = backupApiService.batchUpsertItems(backupId.value, request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val results = body.results?.mapNotNull { item ->
                    val key = item.key ?: return@mapNotNull null
                    val result = parseUpsertResponse(item.newVersion, item.seq, item.currentVersion, item.currentHash)
                    BatchUpsertItemResult(key = BackupItemKey(key), result = result)
                }.orEmpty()
                PeraResult.Success(results)
            } else {
                PeraResult.Error(IllegalStateException("Batch upsert failed"), response.code())
            }
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    private fun parseUpsertResponse(
        newVersion: Int?,
        seq: Long?,
        currentVersion: Int?,
        currentHash: String?
    ): UpsertItemResult {
        return if (newVersion != null && seq != null) {
            UpsertItemResult.Success(newVersion = newVersion, seq = seq)
        } else {
            UpsertItemResult.Conflict(
                currentVersion = currentVersion ?: 0,
                currentHash = currentHash?.let { ItemHash(it) }
            )
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
