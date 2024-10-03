package com.algorand.android.module.asset.detail.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleMediaTypeExtensionMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleMediaTypeExtension
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeExtensionEntity
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
