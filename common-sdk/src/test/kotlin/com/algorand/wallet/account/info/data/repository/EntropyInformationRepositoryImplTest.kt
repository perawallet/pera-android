package com.algorand.wallet.account.info.data.repository

import com.algorand.wallet.account.info.data.database.dao.EntropyInformationDao
import com.algorand.wallet.account.info.data.database.model.EntropyInformationEntity
import com.algorand.wallet.account.info.data.mapper.entity.EntropyInformationEntityMapper
import com.algorand.wallet.account.info.data.mapper.model.EntropyInformationMapper
import com.algorand.wallet.account.info.domain.model.EntropyInformation
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class EntropyInformationRepositoryImplTest {

    private val entropyInformationDao = mockk<EntropyInformationDao>()
    private val entropyInformationMapper = mockk<EntropyInformationMapper>()
    private val entropyInformationEntityMapper = mockk<EntropyInformationEntityMapper>()

    private val sut = EntropyInformationRepositoryImpl(
        entropyInformationDao = entropyInformationDao,
        entropyInformationMapper = entropyInformationMapper,
        entropyInformationEntityMapper = entropyInformationEntityMapper
    )

    @Test
    fun `EXPECT mapped information list WHEN getting all as flow`() = runTest {
        val entity1 = mockk<EntropyInformationEntity>()
        val entity2 = mockk<EntropyInformationEntity>()
        val entityList = listOf(entity1, entity2)

        val info1 = mockk<EntropyInformation>()
        val info2 = mockk<EntropyInformation>()
        val infoList = listOf(info1, info2)

        every { entropyInformationDao.getAllAsFlow() } returns flowOf(entityList)
        every { entropyInformationMapper(entity1) } returns info1
        every { entropyInformationMapper(entity2) } returns info2

        val result = sut.getAllAsFlow().first()

        assertEquals(infoList, result)
    }

    @Test
    fun `EXPECT correct count WHEN getting entropy information count as flow`() = runTest {
        val count = 5
        every { entropyInformationDao.getTableSizeAsFlow() } returns flowOf(count)

        val result = sut.getEntropyInformationCountAsFlow().first()

        assertEquals(count, result)
    }

    @Test
    fun `EXPECT complete mapped list WHEN getting all entropy information`() = runTest {
        val entity1 = mockk<EntropyInformationEntity>()
        val entity2 = mockk<EntropyInformationEntity>()
        val entityList = listOf(entity1, entity2)

        val info1 = mockk<EntropyInformation>()
        val info2 = mockk<EntropyInformation>()
        val infoList = listOf(info1, info2)

        coEvery { entropyInformationDao.getAll() } returns entityList
        every { entropyInformationMapper(entity1) } returns info1
        every { entropyInformationMapper(entity2) } returns info2

        val result = sut.getAllEntropyInformation()

        assertEquals(infoList, result)
    }

    @Test
    fun `EXPECT mapped information WHEN getting entropy information by existing id`() = runTest {
        val seedId = 123
        val entity = mockk<EntropyInformationEntity>()
        val info = mockk<EntropyInformation>()

        coEvery { entropyInformationDao.get(seedId) } returns entity
        every { entropyInformationMapper(entity) } returns info

        val result = sut.getEntropyInformation(seedId)

        assertEquals(info, result)
    }

    @Test
    fun `EXPECT null WHEN getting entropy information by non-existent id`() = runTest {
        val seedId = 123

        coEvery { entropyInformationDao.get(seedId) } returns null

        val result = sut.getEntropyInformation(seedId)

        assertNull(result)
    }

    @Test
    fun `EXPECT successful completion WHEN updating entropy custom name`() = runTest {
        val seedId = 123
        val customName = "MyCustomEntropy"

        coEvery { entropyInformationDao.update(seedId, customName) } returns Unit

        val result = sut.updateEntropyCustomName(seedId, customName)

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT successful completion WHEN adding entropy information`() = runTest {
        val entropyInfo = mockk<EntropyInformation>()
        val entity = mockk<EntropyInformationEntity>()

        every { entropyInformationEntityMapper(entropyInfo) } returns entity
        coEvery { entropyInformationDao.insert(entity) } returns Unit

        val result = sut.addEntropyInformation(entropyInfo)

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT successful completion WHEN deleting entropy information by id`() = runTest {
        val seedId = 123

        coEvery { entropyInformationDao.delete(seedId) } returns Unit

        val result = sut.deleteEntropyInformation(seedId)

        assertEquals(Unit, result)
    }

    @Test
    fun `EXPECT successful completion WHEN clearing all entropy information`() = runTest {
        coEvery { entropyInformationDao.clearAll() } returns Unit

        val result = sut.deleteAllEntropyInformation()

        assertEquals(Unit, result)
    }
}