package com.algorand.android.shared_db.assetdetail.model

import androidx.room.*
import com.algorand.android.shared_db.assetdetail.model.CollectibleEntity.Companion.COLLECTIBLE_TABLE_NAME

@Entity(tableName = COLLECTIBLE_TABLE_NAME)
data class CollectibleEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long = 0L,

    @ColumnInfo("collectible_asset_id")
    val collectibleAssetId: Long,

    @ColumnInfo("standard_type")
    val standardType: CollectibleStandardTypeEntity?,

    @ColumnInfo("media_type")
    val mediaType: CollectibleMediaTypeEntity?,

    @ColumnInfo("primary_image_url")
    val primaryImageUrl: String?,

    @ColumnInfo("title")
    val title: String?,

    @ColumnInfo("description")
    val description: String?,

    @ColumnInfo("collection_id")
    val collectionId: Long?,

    @ColumnInfo("collection_name")
    val collectionName: String?,

    @ColumnInfo("collection_description")
    val collectionDescription: String?
) {

    internal companion object {
        const val COLLECTIBLE_TABLE_NAME = "collectible"
    }
}
