package com.algorand.android.assetdetail.component.data.mapper.model

import com.algorand.android.assetdetail.component.data.model.VerificationTierResponse
import com.algorand.android.assetdetail.component.data.model.VerificationTierResponse.*
import com.algorand.android.assetdetail.component.domain.model.VerificationTier
import com.algorand.android.shared_db.assetdetail.model.VerificationTierEntity
import org.junit.Assert.assertEquals
import org.junit.Test

internal class VerificationTierMapperImplTest {

    private val sut = VerificationTierMapperImpl()

    @Test
    fun `EXPECT response to be mapped successfully`() {
        val responseList = listOf<VerificationTierResponse>(
            VERIFIED,
            UNVERIFIED,
            TRUSTED,
            SUSPICIOUS,
            UNKNOWN
        )

        val result = responseList.map { sut(it) }

        val expected = listOf(
            VerificationTier.VERIFIED,
            VerificationTier.UNVERIFIED,
            VerificationTier.TRUSTED,
            VerificationTier.SUSPICIOUS,
            VerificationTier.UNKNOWN
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT entity to be mapped successfully`() {
        val entityList = listOf<VerificationTierEntity>(
            VerificationTierEntity.VERIFIED,
            VerificationTierEntity.UNVERIFIED,
            VerificationTierEntity.TRUSTED,
            VerificationTierEntity.SUSPICIOUS,
            VerificationTierEntity.UNKNOWN
        )

        val result = entityList.map { sut(it) }

        val expected = listOf(
            VerificationTier.VERIFIED,
            VerificationTier.UNVERIFIED,
            VerificationTier.TRUSTED,
            VerificationTier.SUSPICIOUS,
            VerificationTier.UNKNOWN
        )
        assertEquals(expected, result)
    }
}
