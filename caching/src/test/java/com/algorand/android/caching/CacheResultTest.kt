package com.algorand.android.caching

import com.algorand.android.caching.CacheResult
import org.junit.Assert.*
import org.junit.Test

class CacheResultTest {

    @Test
    fun `EXPECT Success to have creation timestamp WHEN it is created`() {
        val success = com.algorand.android.caching.CacheResult.Success.create("success")
        assertNotNull(success.creationTimestamp)
    }

    @Test
    fun `EXPECT Error to have creation timestamp WHEN it has successfully cached data previously`() {
        val previouslyCachedData = com.algorand.android.caching.CacheResult.Success.create<Any>("success")
        val error = com.algorand.android.caching.CacheResult.Error.create<Any>(Exception(), previouslyCachedData)
        assertNotNull(error.previouslyCachedDataCreationTimestamp)
    }

    @Test
    fun `EXPECT Success  to be equal WHEN data and creation timestamp are equal`() {
        val success1 = com.algorand.android.caching.CacheResult.Success.create("success")
        val success2 = com.algorand.android.caching.CacheResult.Success.create("success")
        assertEquals(success1, success2)
    }

    @Test
    fun `EXPECT Error to be equal WHEN exception, data and creation timestamp are equal`() {
        val previouslyCachedData = com.algorand.android.caching.CacheResult.Success.create<Any>("success")
        val error1 = com.algorand.android.caching.CacheResult.Error.create<Any>(Exception(), previouslyCachedData)
        val error2 = com.algorand.android.caching.CacheResult.Error.create<Any>(Exception(), previouslyCachedData)
        assertEquals(error1, error2)
    }

    @Test
    fun `EXPECT Success hashCode to be correct`() {
        val success = com.algorand.android.caching.CacheResult.Success.create("success")

        val expectedHashCode = success.creationTimestamp.hashCode() + success.data.hashCode()
        assertEquals(expectedHashCode, success.hashCode())
    }

    @Test
    fun `EXPECT Error hashCode to be correct`() {
        val error = com.algorand.android.caching.CacheResult.Error.create<Any>(Exception(), null)

        val expectedHashCode = with(error) {
            previouslyCachedData.hashCode() + previouslyCachedDataCreationTimestamp.hashCode() + exception.hashCode()
        }
        assertEquals(expectedHashCode, error.hashCode())
    }

    @Test
    fun `EXPECT null for previous data WHEN Error is created without it`() {
        val error = com.algorand.android.caching.CacheResult.Error.create<Any>(Exception())
        assertNull(error.previouslyCachedData)
        assertNull(error.previouslyCachedDataCreationTimestamp)
    }
}
