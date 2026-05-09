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

package com.algorand.backup.data.service

import com.algorand.backup.data.api.model.BackupBatchReadRequest
import com.algorand.backup.data.api.model.BackupBatchReadResponse
import com.algorand.backup.data.api.model.BackupBatchUpsertRequest
import com.algorand.backup.data.api.model.BackupBatchUpsertResponse
import com.algorand.backup.data.api.model.BackupDeleteItemResponse
import com.algorand.backup.data.api.model.BackupDeleteResponse
import com.algorand.backup.data.api.model.BackupDeltaResponse
import com.algorand.backup.data.api.model.BackupManifestResponse
import com.algorand.backup.data.api.model.BackupUpsertItemRequest
import com.algorand.backup.data.api.model.BackupUpsertItemResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

internal interface BackupApiService {

    @GET("backup/{backupId}/manifest")
    suspend fun getManifest(
        @Path("backupId") backupId: String
    ): Response<BackupManifestResponse>

    @GET("backup/{backupId}/delta")
    suspend fun getDeltas(
        @Path("backupId") backupId: String,
        @Query("from_seq") fromSeq: Long,
        @Query("types") types: String? = null
    ): Response<BackupDeltaResponse>

    @GET("backup/{backupId}/{key}")
    suspend fun getItem(
        @Path("backupId") backupId: String,
        @Path("key", encoded = true) key: String
    ): Response<String>

    @POST("backup/{backupId}/items/read")
    suspend fun batchReadItems(
        @Path("backupId") backupId: String,
        @Body request: BackupBatchReadRequest
    ): Response<BackupBatchReadResponse>

    @PUT("backup/{backupId}/{key}")
    suspend fun upsertItem(
        @Path("backupId") backupId: String,
        @Path("key", encoded = true) key: String,
        @Body request: BackupUpsertItemRequest
    ): Response<BackupUpsertItemResponse>

    @POST("backup/{backupId}/items/upsert")
    suspend fun batchUpsertItems(
        @Path("backupId") backupId: String,
        @Body request: BackupBatchUpsertRequest
    ): Response<BackupBatchUpsertResponse>

    @DELETE("backup/{backupId}/{key}")
    suspend fun deleteItem(
        @Path("backupId") backupId: String,
        @Path("key", encoded = true) key: String
    ): Response<BackupDeleteItemResponse>

    @DELETE("backup/{backupId}")
    suspend fun deleteBackup(
        @Path("backupId") backupId: String
    ): Response<BackupDeleteResponse>
}
