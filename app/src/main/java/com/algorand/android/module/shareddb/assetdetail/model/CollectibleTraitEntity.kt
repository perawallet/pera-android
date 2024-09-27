package com.algorand.android.module.shareddb.assetdetail.model

import androidx.room.*
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleTraitEntity.Companion.COLLECTIBLE_TRAIT_ENTITY_NAME

@Entity(tableName = COLLECTIBLE_TRAIT_ENTITY_NAME)
data class CollectibleTraitEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long = 0L,

    @ColumnInfo("collectible_asset_id")
    val collectibleAssetId: Long,

    @ColumnInfo("display_name")
    val displayName: String?,

    @ColumnInfo("display_value")
    val displayValue: String?
) {
    internal companion object {
        const val COLLECTIBLE_TRAIT_ENTITY_NAME = "collectible_trait"
    }
}
