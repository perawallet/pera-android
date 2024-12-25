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

package com.algorand.common.account.info.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.algorand.common.account.info.data.database.model.AssetHoldingEntity
import com.algorand.common.account.info.data.database.model.AssetStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AssetHoldingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AssetHoldingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<AssetHoldingEntity>)

    @Query("SELECT * FROM asset_holding_table WHERE encrypted_address = :encryptedAddress AND asset_id = :assetId")
    suspend fun get(encryptedAddress: String, assetId: Long): AssetHoldingEntity

    @Query("SELECT * FROM asset_holding_table WHERE encrypted_address = :encryptedAddress")
    suspend fun getAssetsByAddress(encryptedAddress: String): List<AssetHoldingEntity>

    @Query("DELETE FROM asset_holding_table WHERE encrypted_address = :encryptedAddress AND asset_id = :assetId")
    suspend fun delete(encryptedAddress: String, assetId: Long)

    @Query("DELETE FROM asset_holding_table WHERE encrypted_address = :encryptedAddress")
    suspend fun deleteByAddress(encryptedAddress: String)

    @Query("SELECT asset_id FROM asset_holding_table WHERE encrypted_address IN (:encryptedAddressList)")
    suspend fun getAssetIdsByAddresses(encryptedAddressList: List<String>): List<Long>

    @Query(
        """
        UPDATE asset_holding_table 
        SET asset_status = :status 
        WHERE encrypted_address = :encryptedAddress AND asset_id = :assetId
    """
    )
    suspend fun updateStatus(encryptedAddress: String, assetId: Long, status: AssetStatusEntity)

    @Query(
        """
        DELETE FROM asset_holding_table 
        WHERE encrypted_address = :encryptedAddress AND asset_id NOT IN (:assetIds)
    """
    )
    suspend fun deleteAssetsNotInList(encryptedAddress: String, assetIds: List<Long>)

    @Transaction
    suspend fun updateAssetHoldings(encryptedAddress: String, assetHoldingEntities: List<AssetHoldingEntity>) {
        val assetIds = assetHoldingEntities.map { it.assetId }
        deleteAssetsNotInList(encryptedAddress, assetIds)
        insertAll(assetHoldingEntities)
    }

    @Query("SELECT * FROM asset_holding_table")
    fun getAllAsFlow(): Flow<List<AssetHoldingEntity>>

    @Query("SELECT * FROM asset_holding_table WHERE encrypted_address = :encryptedAddress")
    fun getAssetsByAddressAsFlow(encryptedAddress: String): Flow<List<AssetHoldingEntity>>

    @Query("DELETE FROM asset_holding_table")
    suspend fun clearAll()
}
