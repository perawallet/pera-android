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

import com.algorand.backup.data.api.model.BackupBatchReadRequest
import com.algorand.backup.data.api.model.BackupDeleteItemResponse
import com.algorand.backup.data.mapper.BackupBatchUpsertRequestMapper
import com.algorand.backup.data.mapper.BackupRegistrationProofRequestMapper
import com.algorand.backup.data.mapper.BackupUpsertItemRequestMapper
import com.algorand.backup.data.mapper.DeltaEntryResponseMapper
import com.algorand.backup.data.mapper.ManifestResponseMapper
import com.algorand.backup.data.service.BackupApiService
import com.algorand.backup.domain.model.BackupBatchUpsertInput
import com.algorand.backup.domain.model.BackupBatchUpsertItemResult
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.BackupItemStatus
import com.algorand.backup.domain.model.BackupItemType
import com.algorand.backup.domain.model.BackupUpsertItemResult
import com.algorand.backup.domain.model.DeltaEntry
import com.algorand.backup.domain.model.DerivedKeyMaterial
import com.algorand.backup.domain.model.DeviceId
import com.algorand.backup.domain.model.ItemHash
import com.algorand.backup.domain.model.BackupManifest
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.security.BackupRequestSigner
import com.algorand.backup.domain.security.NonceGenerator
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.network.utils.request
import javax.inject.Inject

internal class DefaultBackupRepository @Inject constructor(
    private val backupApiService: BackupApiService,
    private val manifestMapper: ManifestResponseMapper,
    private val deltaMapper: DeltaEntryResponseMapper,
    private val batchUpsertRequestMapper: BackupBatchUpsertRequestMapper,
    private val registrationProofRequestMapper: BackupRegistrationProofRequestMapper,
    private val upsertItemRequestMapper: BackupUpsertItemRequestMapper,
    private val requestSigner: BackupRequestSigner,
    private val nonceGenerator: NonceGenerator
) : BackupRepository {

    override suspend fun register(keyMaterial: DerivedKeyMaterial, deviceId: DeviceId): PeraResult<Unit> {
        val proof = requestSigner.createRegistrationProof(
            authPrivateKey = keyMaterial.authPrivateKey,
            authPublicKey = keyMaterial.authPublicKey,
            backupId = keyMaterial.backupId,
            deviceId = deviceId,
            nonce = nonceGenerator.generate()
        )
        val proofRequest = registrationProofRequestMapper.toRequest(proof)
        return request {
            backupApiService.register(proofRequest)
        }.map { }
    }

    override suspend fun getManifest(backupId: BackupId): PeraResult<BackupManifest> {
        return request {
            backupApiService.getManifest(backupId.value)
        }.use(
            onSuccess = { response ->
                val manifest = manifestMapper.toDomainModel(response)
                if (manifest != null) {
                    PeraResult.Success(manifest)
                } else {
                    PeraResult.Error(IllegalStateException("Failed to parse manifest"))
                }
            },
            onFailed = { exception, code -> PeraResult.Error(exception, code) }
        )
    }

    override suspend fun getDeltas(
        backupId: BackupId,
        fromSeq: Long,
        types: List<BackupItemType>?
    ): PeraResult<List<DeltaEntry>> {
        val typesQuery = types?.joinToString(",") { it.name }
        return request {
            backupApiService.getDeltas(backupId.value, fromSeq, typesQuery)
        }.map { response ->
            response.entries?.mapNotNull { deltaMapper.toDomainModel(it) }.orEmpty()
        }
    }

    override suspend fun getItem(backupId: BackupId, key: BackupItemKey): PeraResult<String> {
        return request {
            backupApiService.getItem(backupId.value, key.value)
        }
    }

    override suspend fun batchReadItems(
        backupId: BackupId,
        keys: List<BackupItemKey>
    ): PeraResult<Map<BackupItemKey, String>> {
        val batchReadRequest = BackupBatchReadRequest(keys.map { it.value })
        return request {
            backupApiService.batchReadItems(backupId.value, batchReadRequest)
        }.map { response ->
            response.items
                ?.filter { it.payload != null && it.key != null }
                ?.associate { BackupItemKey(it.key!!) to it.payload!! }.orEmpty()
        }
    }

    override suspend fun upsertItem(
        backupId: BackupId,
        key: BackupItemKey,
        type: BackupItemType,
        expectedVersion: Int,
        status: BackupItemStatus,
        deviceId: DeviceId,
        payload: String
    ): PeraResult<BackupUpsertItemResult> {
        val upsertRequest = upsertItemRequestMapper.toRequest(type, expectedVersion, status, deviceId, payload)
        return request {
            backupApiService.upsertItem(backupId.value, key.value, upsertRequest)
        }.map { response ->
            parseUpsertResponse(response.newVersion, response.seq, response.currentVersion, response.currentHash)
        }
    }

    override suspend fun batchUpsertItems(
        backupId: BackupId,
        deviceId: DeviceId,
        items: List<BackupBatchUpsertInput>
    ): PeraResult<List<BackupBatchUpsertItemResult>> {
        val batchRequest = batchUpsertRequestMapper.toRequest(deviceId, items)
        return request {
            backupApiService.batchUpsertItems(backupId.value, batchRequest)
        }.map { response ->
            response.results?.mapNotNull { item ->
                val key = item.key ?: return@mapNotNull null
                val result = parseUpsertResponse(item.newVersion, item.seq, item.currentVersion, item.currentHash)
                BackupBatchUpsertItemResult(key = BackupItemKey(key), result = result)
            }.orEmpty()
        }
    }

    private fun parseUpsertResponse(
        newVersion: Int?,
        seq: Long?,
        currentVersion: Int?,
        currentHash: String?
    ): BackupUpsertItemResult {
        return if (newVersion != null && seq != null) {
            BackupUpsertItemResult.Success(newVersion = newVersion, seq = seq)
        } else {
            BackupUpsertItemResult.Conflict(
                currentVersion = currentVersion ?: 0,
                currentHash = currentHash?.let { ItemHash(it) }
            )
        }
    }

    override suspend fun deleteItem(backupId: BackupId, key: BackupItemKey): PeraResult<Long> {
        return request(
            onFailed = { response ->
                if (response.code() == HTTP_NOT_FOUND) {
                    PeraResult.Success(BackupDeleteItemResponse(seq = ALREADY_DELETED_SEQ))
                } else {
                    PeraResult.Error(Exception(response.errorBody().toString()), response.code())
                }
            }
        ) {
            backupApiService.deleteItem(backupId.value, key.value)
        }.map { it.seq ?: ALREADY_DELETED_SEQ }
    }

    override suspend fun deleteBackup(backupId: BackupId): PeraResult<BackupId> {
        return request {
            backupApiService.deleteBackup(backupId.value)
        }.map { response ->
            response.backupId?.let { BackupId(it) } ?: backupId
        }
    }

    private companion object {
        const val HTTP_NOT_FOUND = 404
        const val ALREADY_DELETED_SEQ = -1L
    }
}
