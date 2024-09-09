package com.algorand.android.assetdetail.component.data.mapper.entity

import com.algorand.android.assetdetail.component.data.model.collectible.CollectibleMediaTypeExtensionResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeExtensionEntity
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
