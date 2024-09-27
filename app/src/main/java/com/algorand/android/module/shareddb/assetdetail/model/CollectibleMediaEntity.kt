package com.algorand.android.module.shareddb.assetdetail.model

import androidx.room.*
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaEntity.Companion.COLLECTIBLE_MEDIA_TABLE_NAME

@Entity(tableName = COLLECTIBLE_MEDIA_TABLE_NAME)
data class CollectibleMediaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long = 0L,

    @ColumnInfo("collectible_asset_id")
    val collectibleAssetId: Long,

    @ColumnInfo("media_type")
    val mediaType: CollectibleMediaTypeEntity,

    @ColumnInfo("download_url")
    val downloadUrl: String?,

    @ColumnInfo("preview_url")
    val previewUrl: String?,

    @ColumnInfo("media_type_extension")
    val mediaTypeExtension: CollectibleMediaTypeExtensionEntity
) {

    internal companion object {
        const val COLLECTIBLE_MEDIA_TABLE_NAME = "collectible_media"
    }
}
