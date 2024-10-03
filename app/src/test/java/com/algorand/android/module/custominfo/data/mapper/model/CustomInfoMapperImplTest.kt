package com.algorand.android.module.custominfo.data.mapper.model

import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.module.shareddb.assetdetail.model.CustomInfoEntity
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CustomInfoMapperImplTest {

    private val sut = CustomInfoMapperImpl()

    @Test
    fun `EXPECT mapped custom info`() {
        val customInfoEntity = CustomInfoEntity(
            encryptedAddress = "encryptedAddress",
            customName = "customName"
        )

        val result = sut("decryptedAddress", customInfoEntity)

        val expected = CustomInfo(
            address = "decryptedAddress",
            customName = "customName"
        )
        assertEquals(expected, result)
    }
}
