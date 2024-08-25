package com.algorand.android.shared_db.accountinformation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.algorand.android.shared_db.accountinformation.model.AssetHoldingEntity
import com.algorand.android.shared_db.accountinformation.model.AssetStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetHoldingDao {

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
    fun getAllAsFlow(): Flow<AssetHoldingEntity>

    @Query("SELECT * FROM asset_holding_table WHERE encrypted_address = :encryptedAddress")
    fun getAssetsByAddressAsFlow(encryptedAddress: String): Flow<List<AssetHoldingEntity>>

    @Query("DELETE FROM asset_holding_table")
    suspend fun clearAll()
}
