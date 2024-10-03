package com.algorand.android.module.account.info.data.mapper.model

import com.algorand.android.module.account.info.data.model.AppStateSchemaResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class AppStateSchemeMapperImplTest {

    private val sut = AppStateSchemeMapperImpl()

    @Test
    fun `EXPECT mapped values to be correct`() {
        val response = AppStateSchemaResponse(
            numByteSlice = 10L,
            numUint = 0L
        )

        val result = sut(response)

        assertEquals(10L, result.numByteSlice)
        assertEquals(0L, result.numUint)
    }

    @Test
    fun `EXPECT mapped values to be correct with given values`() {
        val result = sut(
            numByteSlice = 21,
            numUint = 54
        )

        assertEquals(21, result.numByteSlice)
        assertEquals(54, result.numUint)
    }

    @Test
    fun `EXPECT default values WHEN fields are null`() {
        val response = AppStateSchemaResponse(
            numByteSlice = null,
            numUint = null
        )

        val result = sut(response)

        assertEquals(0L, result.numByteSlice)
        assertEquals(0L, result.numUint)
    }
}
