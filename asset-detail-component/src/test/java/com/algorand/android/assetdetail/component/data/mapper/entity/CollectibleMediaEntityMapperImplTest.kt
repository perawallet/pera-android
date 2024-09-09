package com.algorand.android.assetdetail.component.data.mapper.entity

import com.algorand.android.assetdetail.component.data.model.AssetDetailResponse
import com.algorand.android.assetdetail.component.data.model.collectible.*
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeEntity.IMAGE
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeExtensionEntity.GIF
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

internal class CollectibleMediaEntityMapperImplTest {

    private val collectibleMediaTypeEntityMapper: CollectibleMediaTypeEntityMapper = mock()
    private val collectibleMediaTypeExtensionEntityMapper: CollectibleMediaTypeExtensionEntityMapper = mock()

    private val sut =
        com.algorand.android.assetdetail.component.asset.data.mapper.entity.CollectibleMediaEntityMapperImpl(
            collectibleMediaTypeEntityMapper,
            collectibleMediaTypeExtensionEntityMapper,
        )

    @Test
    fun `EXPECT null WHEN asset id is null`() {
        val assetDetailResponse = fixtureOf<AssetDetailResponse>().copy(assetId = null)

        val result = sut(assetDetailResponse)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        val mediaTypeResponse = fixtureOf<CollectibleMediaTypeResponse>()
        val mediaTypeExtensionResponse = fixtureOf<CollectibleMediaTypeExtensionResponse>()
        val collectibleMediaResponse = fixtureOf<CollectibleMediaResponse>().copy(
            mediaType = mediaTypeResponse,
            mediaTypeExtension = mediaTypeExtensionResponse
        )
        val collectibleResponse = fixtureOf<CollectibleResponse>().copy(
            collectibleMedias = listOf(collectibleMediaResponse)
        )
        val assetDetailResponse = fixtureOf<AssetDetailResponse>().copy(
            assetId = 1L,
            collectible = collectibleResponse
        )
        whenever(collectibleMediaTypeEntityMapper(mediaTypeResponse)).thenReturn(IMAGE)
        whenever(collectibleMediaTypeExtensionEntityMapper(mediaTypeExtensionResponse)).thenReturn(GIF)

        val result = sut(assetDetailResponse)

        val expected = listOf(
            CollectibleMediaEntity(
                collectibleAssetId = 1L,
                mediaType = IMAGE,
                downloadUrl = collectibleMediaResponse.downloadUrl,
                previewUrl = collectibleMediaResponse.previewUrl,
                mediaTypeExtension = GIF
            )
        )
        assertEquals(expected, result)
    }
}
