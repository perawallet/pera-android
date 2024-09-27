package com.algorand.android.account.custominfo.data.mapper.model

import com.algorand.android.module.shareddb.assetdetail.model.CustomInfoEntity
import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.encryption.EncryptionManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

internal class CustomInfoMapperImplTest {

    private val encryptionManager: EncryptionManager = mock()
    private val sut =
        com.algorand.android.module.custominfo.data.mapper.model.CustomInfoMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapped custom info`() {
        whenever(encryptionManager.decrypt("encryptedAddress")).thenReturn("decryptedAddress")
        val customInfoEntity = CustomInfoEntity(
            encryptedAddress = "encryptedAddress",
            order = 1,
            isBackedUp = true,
            customName = "customName"
        )

        val result = sut(customInfoEntity)

        val expected = com.algorand.android.module.custominfo.domain.model.CustomInfo(
            address = "decryptedAddress",
            order = 1,
            isBackedUp = true,
            customName = "customName"
        )
        assertEquals(expected, result)
    }
}
