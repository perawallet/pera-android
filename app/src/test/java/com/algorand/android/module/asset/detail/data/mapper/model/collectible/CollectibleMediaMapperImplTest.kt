package com.algorand.android.module.asset.detail.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMediaMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMediaTypeExtensionMapper
import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMediaTypeMapper
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaResponse
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMedia
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMediaType
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMediaType.AUDIO
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMediaTypeExtension
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMediaTypeExtension.GIF
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeExtensionEntity
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class CollectibleMediaMapperImplTest {

    private val collectibleMediaTypeMapper: CollectibleMediaTypeMapper = mock {
        on { invoke(COLLECTIBLE_MEDIA_TYPE_RESPONSE) } doReturn COLLECTIBLE_MEDIA_TYPE
    }
    private val collectibleMediaTypeExtensionMapper: CollectibleMediaTypeExtensionMapper = mock {
        on { invoke(COLLECTIBLE_MEDIA_TYPE_EXTENSION_RESPONSE) } doReturn COLLECTIBLE_MEDIA_TYPE_EXTENSION
    }

    private val sut = CollectibleMediaMapperImpl(
        collectibleMediaTypeMapper,
        collectibleMediaTypeExtensionMapper
    )

    @Test
    fun `EXPECT collectible media WHEN response fields are valid`() {
        val result = sut(COLLECTIBLE_MEDIA_RESPONSE)

        assertEquals(COLLECTIBLE_MEDIA, result)
    }

    @Test
    fun `EXPECT null WHEN all response fields are null`() {
        val collectibleMediaResponse = CollectibleMediaResponse(
            mediaType = null,
            downloadUrl = null,
            previewUrl = null,
            mediaTypeExtension = null
        )

        val result = sut(collectibleMediaResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT collectible media list WHEN entity list is valid`() {
        whenever(collectibleMediaTypeMapper(CollectibleMediaTypeEntity.AUDIO)).thenReturn(AUDIO)
        whenever(collectibleMediaTypeExtensionMapper(CollectibleMediaTypeExtensionEntity.GIF)).thenReturn(GIF)
        val collectibleMediaEntity = CollectibleMediaEntity(
            id = 2L,
            collectibleAssetId = 1L,
            mediaType = CollectibleMediaTypeEntity.AUDIO,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = CollectibleMediaTypeExtensionEntity.GIF
        )
        val collectibleMediaEntities = listOf(collectibleMediaEntity)

        val result = sut(collectibleMediaEntities)

        val expected = CollectibleMedia(
            mediaType = AUDIO,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = GIF
        )
        assertEquals(listOf(expected), result)
    }

    companion object {
        private val COLLECTIBLE_MEDIA_TYPE = fixtureOf<CollectibleMediaType>()
        private val COLLECTIBLE_MEDIA_TYPE_RESPONSE = fixtureOf<CollectibleMediaTypeResponse>()
        private val COLLECTIBLE_MEDIA_TYPE_EXTENSION = fixtureOf<CollectibleMediaTypeExtension>()
        private val COLLECTIBLE_MEDIA_TYPE_EXTENSION_RESPONSE = fixtureOf<CollectibleMediaTypeExtensionResponse>()

        private val COLLECTIBLE_MEDIA = CollectibleMedia(
            mediaType = COLLECTIBLE_MEDIA_TYPE,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = COLLECTIBLE_MEDIA_TYPE_EXTENSION
        )

        private val COLLECTIBLE_MEDIA_RESPONSE = CollectibleMediaResponse(
            mediaType = COLLECTIBLE_MEDIA_TYPE_RESPONSE,
            downloadUrl = "downloadUrl",
            previewUrl = "previewUrl",
            mediaTypeExtension = COLLECTIBLE_MEDIA_TYPE_EXTENSION_RESPONSE
        )
    }
}
