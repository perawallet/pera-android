package com.algorand.wallet.account.local.data.repository

import com.algorand.wallet.account.local.data.database.dao.HdSeedDao
import com.algorand.wallet.account.local.data.database.model.HdSeedEntity
import com.algorand.wallet.account.local.data.mapper.entity.HdSeedEntityMapper
import com.algorand.wallet.account.local.data.mapper.model.HdSeedMapper
import com.algorand.wallet.account.local.domain.model.HdSeed
import com.algorand.wallet.encryption.domain.services.AESPlatformManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class HdSeedRepositoryImplTest {

    private val hdSeedDao: HdSeedDao = mockk()
    private val hdSeedEntityMapper: HdSeedEntityMapper = mockk()
    private val hdSeedMapper: HdSeedMapper = mockk()
    private val aesPlatformManager: AESPlatformManager = mockk()
    private val sut = HdSeedRepositoryImpl(hdSeedDao, hdSeedEntityMapper, hdSeedMapper, aesPlatformManager, Dispatchers.Unconfined)

    @Test
    fun given_getAllAsFlow_when_called_then_returnFlowOfHdSeedList() {
        every { hdSeedDao.getAllAsFlow() } returns flowOf(emptyList())
        sut.getAllAsFlow()
    }

    @Test
    fun given_getHdSeedCountAsFlow_when_called_then_returnFlowOfInt() {
        every { hdSeedDao.getTableSizeAsFlow() } returns flowOf(0)
        sut.getHdSeedCountAsFlow()
    }

    @Test
    fun given_getAllHdSeeds_when_called_then_returnListOfHdSeed() = runTest {
        coEvery { hdSeedDao.getAll() } returns emptyList()
        sut.getAllHdSeeds()
    }

    @Test
    fun given_seedId_when_getHdSeed_then_returnHdSeed() = runTest {
        coEvery { hdSeedDao.get(1) } returns null
        assertNull(sut.getHdSeed(1))
    }

    @Test
    fun given_encryptedEntropy_when_getHdSeed_then_returnHdSeed() = runTest {
        coEvery { hdSeedDao.get(byteArrayOf(1, 2, 3)) } returns null
        assertNull(sut.getHdSeed(byteArrayOf(1, 2, 3)))
    }

    @Test
    fun given_seedId_when_getEncryptedEntropy_then_returnByteArray() = runTest {
        coEvery { hdSeedDao.getEncryptedEntropy(1) } returns null
        assertNull(sut.getEncryptedEntropy(1))
    }

    @Test
    fun given_entropyCustomName_when_getAllHdSeed_then_returnListOfHdSeed() = runTest {
        coEvery { hdSeedDao.getAll("test") } returns emptyList()
        sut.getAllHdSeed("test")
    }

    @Test
    fun given_hdSeed_when_addHdSeed_then_returnLong() = runTest {
        val hdSeed = mockk<HdSeed>()
        val entropy = byteArrayOf(1, 2, 3)
        val seed = byteArrayOf(4, 5, 6)
        val entity = mockk<HdSeedEntity>()
        val generatedId = 123L

        every { hdSeedEntityMapper(hdSeed, entropy, seed) } returns entity
        coEvery { hdSeedDao.insert(entity) } returns generatedId

        val result = sut.addHdSeed(hdSeed, entropy, seed)
        assertEquals(generatedId, result)
    }

    @Test
    fun given_seedIdAndCustomName_when_setEntropyCustomName_then_success() = runTest {
        val seedId = 123
        val customName = "MyWallet"

        coEvery { hdSeedDao.update(seedId, customName) } returns Unit

        sut.setEntropyCustomName(seedId, customName)
    }

    @Test
    fun given_seedId_when_deleteHdSeed_then_success() = runTest {
        coEvery { hdSeedDao.delete(1) } returns Unit
        sut.deleteHdSeed(1)
    }

    @Test
    fun given_encryptedEntropy_when_deleteHdSeed_then_success() = runTest {
        coEvery { hdSeedDao.delete(byteArrayOf(1, 2, 3)) } returns Unit
        sut.deleteHdSeed(byteArrayOf(1, 2, 3))
    }

    @Test
    fun given_deleteAllHdSeeds_when_called_then_success() = runTest {
        coEvery { hdSeedDao.clearAll() } returns Unit
        sut.deleteAllHdSeeds()
    }

    @Test
    fun given_seedId_when_getEntropy_then_returnByteArray() = runTest {
        coEvery { hdSeedDao.get(1) } returns null
        assertNull(sut.getEntropy(1))
    }

    @Test
    fun given_seedId_when_getSeed_then_returnByteArray() = runTest {
        coEvery { hdSeedDao.get(1) } returns null
        assertNull(sut.getSeed(1))
    }
}