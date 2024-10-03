package com.algorand.android.module.caching

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CacheResultTest {

    @Test
    fun `EXPECT Success to have creation timestamp WHEN it is created`() {
        val success = CacheResult.Success.create("success")
        assertNotNull(success.creationTimestamp)
    }

    @Test
    fun `EXPECT Error to have creation timestamp WHEN it has successfully cached data previously`() {
        val previouslyCachedData = CacheResult.Success.create<Any>("success")
        val error = CacheResult.Error.create<Any>(Exception(), previouslyCachedData)
        assertNotNull(error.previouslyCachedDataCreationTimestamp)
    }

    @Test
    fun `EXPECT Success  to be equal WHEN data and creation timestamp are equal`() {
        val success1 = CacheResult.Success.create("success")
        val success2 = CacheResult.Success.create("success")
        assertEquals(success1, success2)
    }

    @Test
    fun `EXPECT Error to be equal WHEN exception, data and creation timestamp are equal`() {
        val previouslyCachedData = CacheResult.Success.create<Any>("success")
        val error1 = CacheResult.Error.create<Any>(Exception(), previouslyCachedData)
        val error2 = CacheResult.Error.create<Any>(Exception(), previouslyCachedData)
        assertEquals(error1, error2)
    }

    @Test
    fun `EXPECT Success hashCode to be correct`() {
        val success = CacheResult.Success.create("success")

        val expectedHashCode = success.creationTimestamp.hashCode() + success.data.hashCode()
        assertEquals(expectedHashCode, success.hashCode())
    }

    @Test
    fun `EXPECT Error hashCode to be correct`() {
        val error = CacheResult.Error.create<Any>(Exception(), null)

        val expectedHashCode = with(error) {
            previouslyCachedData.hashCode() + previouslyCachedDataCreationTimestamp.hashCode() + exception.hashCode()
        }
        assertEquals(expectedHashCode, error.hashCode())
    }

    @Test
    fun `EXPECT null for previous data WHEN Error is created without it`() {
        val error = CacheResult.Error.create<Any>(Exception())
        assertNull(error.previouslyCachedData)
        assertNull(error.previouslyCachedDataCreationTimestamp)
    }
}
