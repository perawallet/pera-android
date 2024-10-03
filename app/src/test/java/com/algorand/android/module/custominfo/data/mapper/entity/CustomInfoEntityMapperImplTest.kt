package com.algorand.android.module.custominfo.data.mapper.entity

import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.shareddb.assetdetail.model.CustomInfoEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class CustomInfoEntityMapperImplTest {

    private val encryptionManager: EncryptionManager = mock()

    private val sut = CustomInfoEntityMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapped custom info entity`() {
        whenever(encryptionManager.encrypt("address")).thenReturn("encrypted_address")
        val customInfo = CustomInfo(
            address = "address",
            customName = "customName"
        )

        val result = sut(customInfo)

        val expected = CustomInfoEntity(
            encryptedAddress = "encrypted_address",
            customName = "customName"
        )
        assertEquals(expected, result)
    }
}
