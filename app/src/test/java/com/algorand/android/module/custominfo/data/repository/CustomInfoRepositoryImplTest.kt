package com.algorand.android.module.custominfo.data.repository

import com.algorand.android.module.custominfo.data.mapper.entity.CustomInfoEntityMapper
import com.algorand.android.module.custominfo.data.mapper.model.CustomInfoMapper
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.shareddb.assetdetail.model.CustomInfoEntity
import com.algorand.android.module.shareddb.custominfo.dao.CustomInfoDao
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

internal class CustomInfoRepositoryImplTest {

    private val customInfoDao: CustomInfoDao = mock()
    private val customInfoMapper: CustomInfoMapper = mock()
    private val customInfoEntityMapper: CustomInfoEntityMapper = mock()
    private val encryptionManager: EncryptionManager = mock {
        on { encrypt("address") } doReturn "encrypted_address"
    }

    private val sut = CustomInfoRepositoryImpl(
        customInfoDao,
        customInfoMapper,
        customInfoEntityMapper,
        encryptionManager
    )

    @Test
    fun `EXPECT custom info WHEN getCustomInfo is called`() = runTest {
        whenever(customInfoDao.getOrNull("encrypted_address")).thenReturn(CUSTOM_INFO_ENTITY)
        whenever(customInfoMapper("address", CUSTOM_INFO_ENTITY)).thenReturn(CUSTOM_INFO)

        val result = sut.getCustomInfo("address")

        Assert.assertEquals(CUSTOM_INFO, result)
    }

    @Test
    fun `EXPECT custom name to be updated WHEN setCustomName is called`() = runTest {
        sut.setCustomName("address", "name")

        verify(customInfoDao).updateCustomName("encrypted_address", "name")
    }

    @Test
    fun `EXPECT custom info to be inserted WHEN setCustomInfo is called`() = runTest {
        whenever(customInfoEntityMapper(CUSTOM_INFO)).thenReturn(CUSTOM_INFO_ENTITY)

        sut.setCustomInfo(CUSTOM_INFO)

        verify(customInfoDao).insert(CUSTOM_INFO_ENTITY)
    }

    companion object {
        private val CUSTOM_INFO = fixtureOf<com.algorand.android.module.custominfo.domain.model.CustomInfo>()
        private val CUSTOM_INFO_ENTITY = fixtureOf<CustomInfoEntity>()
    }
}
