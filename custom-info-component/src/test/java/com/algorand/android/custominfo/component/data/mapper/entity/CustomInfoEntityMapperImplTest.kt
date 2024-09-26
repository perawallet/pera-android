package com.algorand.android.account.custominfo.data.mapper.entity

import com.algorand.android.shared_db.assetdetail.model.CustomInfoEntity
import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.encryption.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

internal class CustomInfoEntityMapperImplTest {

    private val encryptionManager: EncryptionManager = mock()

    private val sut =
        com.algorand.android.module.custominfo.data.mapper.entity.CustomInfoEntityMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapped custom info entity`() {
        whenever(encryptionManager.encrypt("address")).thenReturn("encrypted_address")
        val customInfo = com.algorand.android.module.custominfo.domain.model.CustomInfo(
            address = "address",
            order = 8,
            isBackedUp = true,
            customName = "customName"
        )

        val result = sut(customInfo)

        val expected = CustomInfoEntity(
            encryptedAddress = "encrypted_address",
            order = 8,
            isBackedUp = true,
            customName = "customName"
        )
        assertEquals(expected, result)
    }
}
