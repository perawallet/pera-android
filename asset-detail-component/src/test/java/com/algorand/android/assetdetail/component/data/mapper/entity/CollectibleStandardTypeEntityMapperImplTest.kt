package com.algorand.android.assetdetail.component.data.mapper.entity

import com.algorand.android.assetdetail.component.data.model.collectible.CollectibleStandardTypeResponse
import com.algorand.android.shared_db.assetdetail.model.CollectibleStandardTypeEntity
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleStandardTypeEntityMapperImplTest {

    private val sut = CollectibleStandardTypeEntityMapperImpl()

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        val responseList = listOf(
            CollectibleStandardTypeResponse.ARC_3,
            CollectibleStandardTypeResponse.ARC_69,
            CollectibleStandardTypeResponse.UNKNOWN,
            null
        )

        val result = responseList.map { sut(it) }

        val expected = listOf(
            CollectibleStandardTypeEntity.ARC_3,
            CollectibleStandardTypeEntity.ARC_69,
            CollectibleStandardTypeEntity.UNKNOWN,
            CollectibleStandardTypeEntity.UNKNOWN
        )
        assertEquals(expected, result)
    }
}
