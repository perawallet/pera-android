package com.algorand.android.module.asset.detail.data.mapper.model.collectible

import com.algorand.android.module.asset.detail.component.asset.data.mapper.model.collectible.CollectibleSearchMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleSearchResponse
import com.algorand.android.module.asset.detail.component.asset.domain.model.CollectibleSearch
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleSearchMapperImplTest {

    private val sut = CollectibleSearchMapperImpl()

    @Test
    fun `EXPECT response to be mapped successfully`() {
        val collectibleSearchResponse = CollectibleSearchResponse("primaryImageUrl", "title")

        val result = sut(collectibleSearchResponse)

        val expected = CollectibleSearch("primaryImageUrl", "title")
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN response fields are null`() {
        val collectibleSearchResponse = CollectibleSearchResponse(null, null)

        val result = sut(collectibleSearchResponse)

        val expected = CollectibleSearch(null, null)
        assertEquals(expected, result)
    }
}
