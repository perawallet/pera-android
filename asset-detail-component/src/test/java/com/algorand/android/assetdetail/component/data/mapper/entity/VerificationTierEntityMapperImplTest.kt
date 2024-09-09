package com.algorand.android.assetdetail.component.data.mapper.entity

import com.algorand.android.assetdetail.component.data.model.VerificationTierResponse
import com.algorand.android.shared_db.assetdetail.model.VerificationTierEntity
import org.junit.Assert.assertEquals
import org.junit.Test

internal class VerificationTierEntityMapperImplTest {

    private val sut = VerificationTierEntityMapperImpl()

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        val responseList = listOf(
            VerificationTierResponse.VERIFIED,
            VerificationTierResponse.UNVERIFIED,
            VerificationTierResponse.TRUSTED,
            VerificationTierResponse.SUSPICIOUS,
            VerificationTierResponse.UNKNOWN,
            null
        )

        val result = responseList.map { sut(it) }

        val expected = listOf(
            VerificationTierEntity.VERIFIED,
            VerificationTierEntity.UNVERIFIED,
            VerificationTierEntity.TRUSTED,
            VerificationTierEntity.SUSPICIOUS,
            VerificationTierEntity.UNKNOWN,
            VerificationTierEntity.UNKNOWN
        )
        assertEquals(expected, result)
    }
}
