package com.algorand.wallet.account.local.data.repository

import com.algorand.wallet.account.local.data.database.dao.HdSeedDao
import com.algorand.wallet.account.local.data.database.model.HdSeedEntity
import com.algorand.wallet.account.local.data.mapper.entity.HdSeedEntityMapper
import com.algorand.wallet.account.local.data.mapper.model.HdSeedMapper
import com.algorand.wallet.account.local.domain.model.HdSeed
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class HdSeedRepositoryImplTest {

    private val hdSeedDao = mockk<HdSeedDao>()
    private val hdSeedEntityMapper = mockk<HdSeedEntityMapper>()
    private val hdSeedMapper = mockk<HdSeedMapper>()
    private val aesPlatformManager = mockk<AESPlatformManager>()

    private val sut = HdSeedRepositoryImpl(
        hdSeedDao = hdSeedDao,
        hdSeedEntityMapper = hdSeedEntityMapper,
        hdSeedMapper = hdSeedMapper,
        aesPlatformManager = aesPlatformManager
    )

    @Test
    fun `EXPECT mapped seed list WHEN getting all seeds as flow`() = runTest {
        val entity1 = mockk<HdSeedEntity>()
        val entity2 = mockk<HdSeedEntity>()
        val entityList = listOf(entity1, entity2)

        val mappedSeed1 = mockk<HdSeed>()
        val mappedSeed2 = mockk<HdSeed>()
        val mappedList = listOf(mappedSeed1, mappedSeed2)

        every { hdSeedDao.getAllAsFlow() } returns flowOf(entityList)
        every { hdSeedMapper(entity1) } returns mappedSeed1
        every { hdSeedMapper(entity2) } returns mappedSeed2

        val result = sut.getAllAsFlow().first()

        assertEquals(mappedList, result)
    }

    @Test
    fun `EXPECT correct count WHEN getting seed count as flow`() = runTest {
        val count = 5
        every { hdSeedDao.getTableSizeAsFlow() } returns flowOf(count)

        val result = sut.getSeedCountAsFlow().first()

        assertEquals(count, result)
    }

    @Test
    fun `EXPECT account count WHEN getAccountCount is invoked`() = runTest {
        val expectedCount = 3
        coEvery { hdSeedDao.getTableSize() } returns expectedCount

        val result = sut.getHdSeedCount()

        assertEquals(expectedCount, result)
    }

    @Test
    fun `EXPECT highest id value WHEN getting max seed id from populated database`() = runTest {
        val maxId = 10
        coEvery { hdSeedDao.getMaxSeedId() } returns maxId

        val result = sut.getMaxSeedId()

        assertEquals(maxId, result)
    }

    @Test
    fun `EXPECT null WHEN getting max seed id from empty database`() = runTest {
        coEvery { hdSeedDao.getMaxSeedId() } returns null

        val result = sut.getMaxSeedId()

        assertNull(result)
    }

    @Test
    fun `EXPECT complete mapped list WHEN getting all seeds`() = runTest {
        val entity1 = mockk<HdSeedEntity>()
        val entity2 = mockk<HdSeedEntity>()
        val entityList = listOf(entity1, entity2)

        val mappedSeed1 = mockk<HdSeed>()
        val mappedSeed2 = mockk<HdSeed>()
        val mappedList = listOf(mappedSeed1, mappedSeed2)

        coEvery { hdSeedDao.getAll() } returns entityList
        every { hdSeedMapper(entity1) } returns mappedSeed1
        every { hdSeedMapper(entity2) } returns mappedSeed2

        val result = sut.getAllHdSeeds()

        assertEquals(mappedList, result)
    }

    @Test
    fun `EXPECT correctly mapped seed WHEN getting seed by existing id`() = runTest {
        val seedId = 123
        val seedEntity = mockk<HdSeedEntity>()
        val mappedSeed = mockk<HdSeed>()

        coEvery { hdSeedDao.get(seedId) } returns seedEntity
        every { hdSeedMapper(seedEntity) } returns mappedSeed

        val result = sut.getHdSeed(seedId)

        assertEquals(mappedSeed, result)
    }

    @Test
    fun `EXPECT null WHEN getting seed by non-existent id`() = runTest {
        val seedId = 123
        coEvery { hdSeedDao.get(seedId) } returns null

        val result = sut.getHdSeed(seedId)

        assertNull(result)
    }

    @Test
    fun `EXPECT generated id value WHEN adding new seed`() = runTest {
        val seedId = 123
        val entropy = ByteArray(32) { 1 }
        val seed = ByteArray(64) { 2 }
        val entity = mockk<HdSeedEntity>()
        val generatedId = 123L

        every { hdSeedEntityMapper(seedId, entropy, seed) } returns entity
        coEvery { hdSeedDao.insert(entity) } returns generatedId

        val result = sut.addHdSeed(seedId, entropy, seed)

        assertEquals(generatedId, result)
    }

    @Test
    fun `EXPECT successful completion WHEN deleting seed by id`() = runTest {
        val seedId = 123

        coEvery { hdSeedDao.delete(seedId) } returns Unit

        val result = sut.deleteHdSeed(seedId)

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT successful completion WHEN clearing all seeds`() = runTest {
        coEvery { hdSeedDao.clearAll() } returns Unit

        val result = sut.deleteAllHdSeeds()

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT decrypted entropy bytes WHEN getting entropy by existing id`() = runTest {
        val seedId = 123
        val encryptedEntropy = ByteArray(32) { 1 }
        val decryptedEntropy = ByteArray(32) { 2 }
        val entity = mockk<HdSeedEntity>()
        every { entity.encryptedEntropy } returns encryptedEntropy

        coEvery { hdSeedDao.get(seedId) } returns entity
        coEvery { aesPlatformManager.decryptByteArray(encryptedEntropy) } returns decryptedEntropy

        val result = sut.getEntropy(seedId)

        assertEquals(decryptedEntropy, result)
    }

    @Test
    fun `EXPECT null WHEN getting entropy by non-existent id`() = runTest {
        val seedId = 123

        coEvery { hdSeedDao.get(seedId) } returns null

        val result = sut.getEntropy(seedId)

        assertNull(result)
    }

    @Test
    fun `EXPECT decrypted seed bytes WHEN getting seed by existing id`() = runTest {
        val seedId = 123
        val encryptedSeed = ByteArray(64) { 1 }
        val decryptedSeed = ByteArray(64) { 2 }
        val entity = mockk<HdSeedEntity>()
        every { entity.encryptedSeed } returns encryptedSeed

        coEvery { hdSeedDao.get(seedId) } returns entity
        coEvery { aesPlatformManager.decryptByteArray(encryptedSeed) } returns decryptedSeed

        val result = sut.getSeed(seedId)

        assertEquals(decryptedSeed, result)
    }

    @Test
    fun `EXPECT true WHEN checking if any seed exists in table`() = runTest {
        coEvery { hdSeedDao.hasAnySeed() } returns true

        val result = sut.hasAnySeed()

        assertEquals(true, result)
    }

    @Test
    fun `EXPECT false WHEN checking if any seed exists in table and it is empty`() = runTest {
        coEvery { hdSeedDao.hasAnySeed() } returns false

        val result = sut.hasAnySeed()

        assertEquals(false, result)
    }
}
