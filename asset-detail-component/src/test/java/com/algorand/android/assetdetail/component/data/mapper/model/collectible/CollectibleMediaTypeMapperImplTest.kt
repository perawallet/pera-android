package com.algorand.android.module.asset.detail.component.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.android.module.asset.detail.component.domain.model.CollectibleMediaType
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeEntity
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleMediaTypeMapperImplTest {

    private val sut = CollectibleMediaTypeMapperImpl()

    @Test
    fun `EXPECT response to be mapped successfully`() {
        val responseList = listOf(
            CollectibleMediaTypeResponse.IMAGE,
            CollectibleMediaTypeResponse.VIDEO,
            CollectibleMediaTypeResponse.MIXED,
            CollectibleMediaTypeResponse.AUDIO,
            CollectibleMediaTypeResponse.UNKNOWN
        )

        val result = responseList.map { sut(it) }

        val expectedList = listOf(
            CollectibleMediaType.IMAGE,
            CollectibleMediaType.VIDEO,
            CollectibleMediaType.MIXED,
            CollectibleMediaType.AUDIO,
            CollectibleMediaType.UNKNOWN
        )
        assertEquals(expectedList, result)
    }

    @Test
    fun `EXPECT entity to be mapped successfully`() {
        val entityList = listOf(
            CollectibleMediaTypeEntity.IMAGE,
            CollectibleMediaTypeEntity.VIDEO,
            CollectibleMediaTypeEntity.MIXED,
            CollectibleMediaTypeEntity.AUDIO,
            CollectibleMediaTypeEntity.UNKNOWN
        )

        val result = entityList.map { sut(it) }

        val expectedList = listOf(
            CollectibleMediaType.IMAGE,
            CollectibleMediaType.VIDEO,
            CollectibleMediaType.MIXED,
            CollectibleMediaType.AUDIO,
            CollectibleMediaType.UNKNOWN
        )
        assertEquals(expectedList, result)
    }
}
