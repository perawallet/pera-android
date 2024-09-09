package com.algorand.android.assetdetail.component.data.mapper.model

import com.algorand.android.assetdetail.component.data.model.AssetCreatorResponse
import com.algorand.android.assetdetail.component.domain.model.AssetCreator
import org.junit.Assert.*
import org.junit.Test

internal class AssetCreatorMapperImplTest {

    private val sut = AssetCreatorMapperImpl()

    @Test
    fun `EXPECT asset creator WHEN response fields are valid`() {
        val response = AssetCreatorResponse(publicKey = "address", id = 10L, isVerifiedAssetCreator = false)

        val result = sut(response)

        val expected = AssetCreator(publicKey = "address", id = 10L, isVerifiedAssetCreator = false)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT assert creator WHEN at least one response field is not null`() {
        val response = AssetCreatorResponse(publicKey = "address", id = null, isVerifiedAssetCreator = null)

        val result = sut(response)

        val expected = AssetCreator(publicKey = "address", id = null, isVerifiedAssetCreator = null)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN all response fields are null`() {
        val response = AssetCreatorResponse(publicKey = null, id = null, isVerifiedAssetCreator = null)

        val result = sut(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT asset creator WHEN parameters are valid`() {
        val result = sut(10L, "address", false)

        val expected = AssetCreator(publicKey = "address", id = 10L, isVerifiedAssetCreator = false)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN all parameters are null`() {
        val result = sut(null, null, null)

        assertNull(result)
    }

    @Test
    fun `EXPECT assert creator WHEN at least one parameter is not null`() {
        val result = sut(null, "address", null)

        val expected = AssetCreator(publicKey = "address", id = null, isVerifiedAssetCreator = null)
        assertEquals(expected, result)
    }
}
