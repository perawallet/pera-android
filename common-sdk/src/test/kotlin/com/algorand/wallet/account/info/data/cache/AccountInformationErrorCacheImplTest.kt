package com.algorand.wallet.account.info.data.cache

import com.algorand.wallet.foundation.cache.CacheResult
import com.algorand.wallet.foundation.cache.SingleInMemoryLocalCache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountInformationErrorCacheImplTest {

    private val cache = SingleInMemoryLocalCache<Set<String>>()

    private val sut = AccountInformationErrorCacheImpl(cache)

    @Test
    fun `EXPECT address to be added to cache`() {
        cache.put(CacheResult.Success.create(setOf("address1")))

        sut.put("address2")

        val expected = setOf("address1", "address2")
        assertEquals(expected, cache.getOrNull()!!.getDataOrNull())
    }

    @Test
    fun `EXPECT address to be removed from cache`() {
        cache.put(CacheResult.Success.create(setOf("address1", "address2")))

        sut.remove("address1")

        val expected = setOf("address2")
        assertEquals(expected, cache.getOrNull()!!.getDataOrNull())
    }

    @Test
    fun `EXPECT all addresses`() {
        cache.put(CacheResult.Success.create(setOf("address1", "address2")))

        val result = sut.getAll()

        val expected = listOf("address1", "address2")
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT empty list when cache is empty`() {
        val result = sut.getAll()

        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun `EXPECT address to be added WHEN cache is empty`() {
        sut.put("address1")

        val expected = setOf("address1")
        assertEquals(expected, cache.getOrNull()!!.getDataOrNull())
    }

    @Test
    fun `EXPECT nothing to be removed WHEN cache is empty`() {
        sut.remove("address1")

        assertNotNull(cache.getOrNull())
        assertTrue(cache.getOrNull()!!.getDataOrNull()!!.isEmpty())
    }
}
