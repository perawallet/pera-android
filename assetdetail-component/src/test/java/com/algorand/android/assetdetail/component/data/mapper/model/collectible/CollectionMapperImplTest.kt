package com.algorand.android.assetdetail.component.data.mapper.model.collectible

import com.algorand.android.assetdetail.component.data.model.collectible.CollectionResponse
import com.algorand.android.assetdetail.component.domain.model.Collection
import org.junit.Assert.*
import org.junit.Test

internal class CollectionMapperImplTest {

    private val sut = CollectionMapperImpl()

    @Test
    fun `EXPECT collection WHEN response fields are valid`() {
        val response = CollectionResponse(
            collectionId = 123L,
            collectionName = "collectionName",
            collectionDescription = "collectionDescription"
        )

        val result = sut(response)

        val expected = Collection(
            collectionId = 123L,
            collectionName = "collectionName",
            collectionDescription = "collectionDescription"
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN all response fields are null`() {
        val response = CollectionResponse(
            collectionId = null,
            collectionName = null,
            collectionDescription = null
        )

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT collection WHEN at least one response field is valid`() {
        val response = CollectionResponse(
            collectionId = 123L,
            collectionName = null,
            collectionDescription = null
        )

        val result = sut(response)

        val expected = Collection(
            collectionId = 123L,
            collectionName = null,
            collectionDescription = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT collection WHEN parameters are valid`() {
        val result = sut(123L, "collectionName", "collectionDescription")

        val expected = Collection(
            collectionId = 123L,
            collectionName = "collectionName",
            collectionDescription = "collectionDescription"
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN all parameters are null`() {
        val result = sut(null, null, null)

        assertNull(result)
    }

    @Test
    fun `EXPECT collection WHEN at least one parameter is valid`() {
        val result = sut(123L, null, null)

        val expected = Collection(
            collectionId = 123L,
            collectionName = null,
            collectionDescription = null
        )
        assertEquals(expected, result)
    }
}
