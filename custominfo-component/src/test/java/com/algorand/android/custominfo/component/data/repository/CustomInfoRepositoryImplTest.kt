package com.algorand.android.account.custominfo.data.repository

import com.algorand.android.shared_db.custominfo.dao.CustomInfoDao
import com.algorand.android.custominfo.component.data.mapper.entity.CustomInfoEntityMapper
import com.algorand.android.custominfo.component.data.mapper.model.CustomInfoMapper
import com.algorand.android.custominfo.component.domain.model.CustomInfo
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.shared_db.assetdetail.model.CustomInfoEntity
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.mockito.kotlin.*

internal class CustomInfoRepositoryImplTest {

    private val customInfoDao: CustomInfoDao = mock()
    private val customInfoMapper: com.algorand.android.custominfo.component.data.mapper.model.CustomInfoMapper = mock()
    private val customInfoEntityMapper: com.algorand.android.custominfo.component.data.mapper.entity.CustomInfoEntityMapper =
        mock()
    private val encryptionManager: EncryptionManager = mock {
        on { encrypt("address") } doReturn "encrypted_address"
    }

    private val sut = com.algorand.android.custominfo.component.data.repository.CustomInfoRepositoryImpl(
        customInfoDao,
        customInfoMapper,
        customInfoEntityMapper,
        encryptionManager
    )

    @Test
    fun `EXPECT custom info WHEN getCustomInfo is called`() = runTest {
        whenever(customInfoDao.get("encrypted_address")).thenReturn(CUSTOM_INFO_ENTITY)
        whenever(customInfoMapper(CUSTOM_INFO_ENTITY)).thenReturn(CUSTOM_INFO)

        val result = sut.getCustomInfo("address")

        Assert.assertEquals(CUSTOM_INFO, result)
    }

    @Test
    fun `EXPECT custom name to be updated WHEN setCustomName is called`() = runTest {
        sut.setCustomName("address", "name")

        verify(customInfoDao).updateCustomName("encrypted_address", "name")
    }

    @Test
    fun `EXPECT order to be updated WHEN setOrder is called`() = runTest {
        whenever(encryptionManager.encrypt("address1")).thenReturn("encrypted_address1")
        whenever(encryptionManager.encrypt("address2")).thenReturn("encrypted_address2")
        val accountOrderList = listOf("address1" to 0, "address2" to 1)

        sut.setOrders(accountOrderList)

        verify(customInfoDao).updateOrders(listOf("encrypted_address1" to 0, "encrypted_address2" to 1))
    }

    @Test
    fun `EXPECT backed up to be updated WHEN setBackedUp is called`() = runTest {
        sut.setBackedUp("address", true)

        verify(customInfoDao).updateBackedUp("encrypted_address", true)
    }

    @Test
    fun `EXPECT custom info to be inserted WHEN setCustomInfo is called`() = runTest {
        whenever(customInfoEntityMapper(CUSTOM_INFO)).thenReturn(CUSTOM_INFO_ENTITY)

        sut.setCustomInfo(CUSTOM_INFO)

        verify(customInfoDao).insert(CUSTOM_INFO_ENTITY)
    }

    companion object {
        private val CUSTOM_INFO = fixtureOf<com.algorand.android.custominfo.component.domain.model.CustomInfo>()
        private val CUSTOM_INFO_ENTITY = fixtureOf<CustomInfoEntity>()
    }
}
