package com.algorand.android.module.asset.detail.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleMediaTypeExtensionEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeExtensionEntity
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleMediaTypeExtensionEntityMapperImplTest {

    private val sut = CollectibleMediaTypeExtensionEntityMapperImpl()

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        val responseList = listOf(
            CollectibleMediaTypeExtensionResponse.GIF,
            CollectibleMediaTypeExtensionResponse.WEBP,
            CollectibleMediaTypeExtensionResponse.UNKNOWN,
            null
        )

        val result = responseList.map { sut(it) }

        val expected = listOf(
            CollectibleMediaTypeExtensionEntity.GIF,
            CollectibleMediaTypeExtensionEntity.WEBP,
            CollectibleMediaTypeExtensionEntity.UNKNOWN,
            CollectibleMediaTypeExtensionEntity.UNKNOWN
        )
        assertEquals(expected, result)
    }
}
