package com.algorand.android.assetdetail.component.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.data.model.collectible.CollectibleTraitResponse
import com.algorand.android.assetdetail.component.domain.model.CollectibleTrait
import com.algorand.android.shared_db.assetdetail.model.CollectibleTraitEntity
import org.junit.Assert.*
import org.junit.Test

internal class CollectibleTraitMapperImplTest {

    private val sut = CollectibleTraitMapperImpl()

    @Test
    fun `EXPECT collectible trait WHEN response fields are valid`() {
        val response = CollectibleTraitResponse(name = "traitName", value = "value")

        val result = sut(response)

        val expected = CollectibleTrait(name = "traitName", value = "value")
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN response fields are null`() {
        val response = CollectibleTraitResponse(name = null, value = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT collectible trait list WHEN entity list is valid`() {
        val entityList = listOf(
            CollectibleTraitEntity(
                displayName = "traitName1",
                displayValue = "value1",
                id = 1L,
                collectibleAssetId = 2L
            ),
            CollectibleTraitEntity(
                displayName = "traitName2",
                displayValue = "value2",
                id = 2L,
                collectibleAssetId = 2L
            )
        )

        val result = sut(entityList)

        val expectedList = listOf(
            CollectibleTrait(name = "traitName1", value = "value1"),
            CollectibleTrait(name = "traitName2", value = "value2")
        )
        assertEquals(expectedList, result)
    }
}
