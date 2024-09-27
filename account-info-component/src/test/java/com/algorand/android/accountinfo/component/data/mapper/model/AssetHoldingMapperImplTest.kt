package com.algorand.android.account.accountinformation.data.mapper.model

import com.algorand.android.module.shareddb.accountinformation.model.AssetHoldingEntity
import com.algorand.android.account.accountinformation.data.model.AssetHoldingResponse
import com.algorand.android.account.accountinformation.domain.model.AssetHolding
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.*
import org.junit.Test
import java.math.BigInteger

class AssetHoldingMapperImplTest {

    private val sut = AssetHoldingMapperImpl()

    @Test
    fun `EXPECT to return AssetHolding WHEN response is valid`() {
        val assetHoldingResponse = createResponse(
            assetId = 1001L,
            amount = BigInteger.TEN,
            isFrozen = true,
            isDeleted = false,
            optedInAtRound = 100L,
            optedOutAtRound = 200L
        )

        val result = sut(assetHoldingResponse)

        val expected = AssetHolding(
            assetId = 1001L,
            amount = BigInteger.TEN,
            isFrozen = true,
            isDeleted = false,
            optedInAtRound = 100L,
            optedOutAtRound = 200L
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT default values WHEN response is null`() {
        val assetHoldingResponse = createResponse(assetId = 1L)

        val result = sut(assetHoldingResponse)

        val expected = AssetHolding(
            assetId = 1L,
            amount = BigInteger.ZERO,
            isFrozen = false,
            isDeleted = false,
            optedInAtRound = null,
            optedOutAtRound = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN assetId is null`() {
        val assetHoldingResponse = createResponse()

        val result = sut(assetHoldingResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT entity to be mapped successfully`() {
        val entity = fixtureOf<AssetHoldingEntity>()

        val result = sut(entity)

        val expected = with(entity) {
            AssetHolding(amount, assetId, isDeleted, isFrozen, optedInAtRound, optedOutAtRound)
        }
        assertEquals(expected, result)
    }

    companion object {
        private fun createResponse(
            assetId: Long? = null,
            amount: BigInteger? = null,
            isFrozen: Boolean? = null,
            isDeleted: Boolean? = null,
            optedInAtRound: Long? = null,
            optedOutAtRound: Long? = null
        ): AssetHoldingResponse {
            return AssetHoldingResponse(
                assetId = assetId,
                amount = amount,
                isFrozen = isFrozen,
                isDeleted = isDeleted,
                optedInAtRound = optedInAtRound,
                optedOutAtRound = optedOutAtRound
            )
        }
    }
}
