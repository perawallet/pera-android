package com.algorand.android.module.asset.detail.component.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.data.model.collectible.CollectibleStandardTypeResponse
import com.algorand.android.module.asset.detail.component.domain.model.CollectibleStandardType
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleStandardTypeEntity
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleStandardTypeMapperImplTest {

    private val sut = CollectibleStandardTypeMapperImpl()

    @Test
    fun `EXPECT response to be mapped successfully`() {
        val responseList = listOf(
            CollectibleStandardTypeResponse.ARC_3,
            CollectibleStandardTypeResponse.ARC_69,
            CollectibleStandardTypeResponse.UNKNOWN
        )

        val result = responseList.map { sut(it) }

        val expectedList = listOf(
            CollectibleStandardType.ARC_3,
            CollectibleStandardType.ARC_69,
            CollectibleStandardType.UNKNOWN
        )
        assertEquals(expectedList, result)
    }

    @Test
    fun `EXPECT entity to be mapped successfully`() {
        val entityList = listOf(
            CollectibleStandardTypeEntity.ARC_3,
            CollectibleStandardTypeEntity.ARC_69,
            CollectibleStandardTypeEntity.UNKNOWN
        )

        val result = entityList.map { sut(it) }

        val expectedList = listOf(
            CollectibleStandardType.ARC_3,
            CollectibleStandardType.ARC_69,
            CollectibleStandardType.UNKNOWN
        )
        assertEquals(expectedList, result)
    }
}
