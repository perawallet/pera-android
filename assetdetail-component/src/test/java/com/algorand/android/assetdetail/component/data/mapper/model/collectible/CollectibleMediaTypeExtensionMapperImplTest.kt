package com.algorand.android.assetdetail.component.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.android.assetdetail.component.domain.model.CollectibleMediaTypeExtension
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeExtensionEntity
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleMediaTypeExtensionMapperImplTest {

    private val sut = CollectibleMediaTypeExtensionMapperImpl()

    @Test
    fun `EXPECT response to be mapped successfully`() {
        val responseList = listOf(
            CollectibleMediaTypeExtensionResponse.GIF,
            CollectibleMediaTypeExtensionResponse.WEBP,
            CollectibleMediaTypeExtensionResponse.UNKNOWN
        )

        val result = responseList.map { sut(it) }

        val expectedList = listOf(
            CollectibleMediaTypeExtension.GIF,
            CollectibleMediaTypeExtension.WEBP,
            CollectibleMediaTypeExtension.UNKNOWN
        )
        assertEquals(expectedList, result)
    }

    @Test
    fun `EXPECT entity to be mapped successfully`() {
        val entityList = listOf(
            CollectibleMediaTypeExtensionEntity.GIF,
            CollectibleMediaTypeExtensionEntity.WEBP,
            CollectibleMediaTypeExtensionEntity.UNKNOWN
        )

        val result = entityList.map { sut(it) }

        val expectedList = listOf(
            CollectibleMediaTypeExtension.GIF,
            CollectibleMediaTypeExtension.WEBP,
            CollectibleMediaTypeExtension.UNKNOWN
        )
        assertEquals(expectedList, result)
    }
}
