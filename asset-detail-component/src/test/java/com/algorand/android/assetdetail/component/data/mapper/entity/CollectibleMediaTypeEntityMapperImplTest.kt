package com.algorand.android.module.asset.detail.component.data.mapper.entity

import com.algorand.android.module.asset.detail.component.data.model.collectible.CollectibleMediaTypeResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleMediaTypeEntity
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleMediaTypeEntityMapperImplTest {

    private val sut = CollectibleMediaTypeEntityMapperImpl()

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        val responseList = listOf(
            CollectibleMediaTypeResponse.IMAGE,
            CollectibleMediaTypeResponse.VIDEO,
            CollectibleMediaTypeResponse.MIXED,
            CollectibleMediaTypeResponse.AUDIO,
            CollectibleMediaTypeResponse.UNKNOWN,
            null
        )

        val result = responseList.map { sut(it) }

        val expected = listOf(
            CollectibleMediaTypeEntity.IMAGE,
            CollectibleMediaTypeEntity.VIDEO,
            CollectibleMediaTypeEntity.MIXED,
            CollectibleMediaTypeEntity.AUDIO,
            CollectibleMediaTypeEntity.UNKNOWN,
            CollectibleMediaTypeEntity.UNKNOWN
        )
        assertEquals(expected, result)
    }
}
