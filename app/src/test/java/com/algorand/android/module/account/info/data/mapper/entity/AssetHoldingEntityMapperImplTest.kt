package com.algorand.android.module.account.info.data.mapper.entity

import com.algorand.android.module.account.info.data.model.AssetHoldingResponse
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.shareddb.accountinformation.model.AssetHoldingEntity
import com.algorand.android.module.shareddb.accountinformation.model.AssetStatusEntity
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

internal class AssetHoldingEntityMapperImplTest {

    private val encryptionManager: EncryptionManager = mock {
        on { encrypt(ADDRESS) } doReturn ENCRYPTED_ADDRESS
    }

    private val sut = AssetHoldingEntityMapperImpl(encryptionManager)

    @Test
    fun `EXPECT asset holding to be mapped successfully`() {
        val result = sut(
            ADDRESS,
            ASSET_HOLDING_RESPONSE_1
        )

        assertEquals(
            ASSET_HOLDING_ENTITY_1,
            result
        )
    }

    @Test
    fun `EXPECT asset holding list to be mapped successfully`() {
        val result = sut(
            listOf(
                ADDRESS to ASSET_HOLDING_RESPONSE_1,
                ADDRESS to ASSET_HOLDING_RESPONSE_2
            )
        )

        val expected = listOf(
            ASSET_HOLDING_ENTITY_1,
            ASSET_HOLDING_ENTITY_2
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN asset id is missing`() {
        val assetHoldingResponse =
            ASSET_HOLDING_RESPONSE_1.copy(
                assetId = null
            )

        val result = sut(
            ADDRESS,
            assetHoldingResponse
        )

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN amount is missing`() {
        val assetHoldingResponse =
            ASSET_HOLDING_RESPONSE_1.copy(
                amount = null
            )

        val result = sut(
            ADDRESS,
            assetHoldingResponse
        )

        assertNull(result)
    }

    @Test
    fun `EXPECT default values WHEN optional fields are missing`() {
        val assetHoldingResponse =
            ASSET_HOLDING_RESPONSE_1.copy(
                isDeleted = null,
                isFrozen = null
            )

        val result = sut(
            ADDRESS,
            assetHoldingResponse
        )

        val expected =
            ASSET_HOLDING_ENTITY_1.copy(
                isDeleted = false,
                isFrozen = false
            )
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDRESS = "address"
        const val ENCRYPTED_ADDRESS = "encrypted_address"

        val ASSET_STATUS_ENTITY = AssetStatusEntity.OWNED_BY_ACCOUNT

        val ASSET_HOLDING_RESPONSE_1 = AssetHoldingResponse(
            assetId = 1,
            amount = BigInteger.TEN,
            isDeleted = false,
            isFrozen = false,
            optedInAtRound = 1,
            optedOutAtRound = 2
        )
        val ASSET_HOLDING_ENTITY_1 = AssetHoldingEntity(
            id = 0,
            encryptedAddress = ENCRYPTED_ADDRESS,
            assetId = 1,
            amount = BigInteger.TEN,
            isDeleted = false,
            isFrozen = false,
            optedInAtRound = 1,
            optedOutAtRound = 2,
            assetStatusEntity = ASSET_STATUS_ENTITY
        )
        val ASSET_HOLDING_RESPONSE_2 = AssetHoldingResponse(
            assetId = 2,
            amount = BigInteger.ONE,
            isDeleted = true,
            isFrozen = false,
            optedInAtRound = 5,
            optedOutAtRound = 6
        )
        val ASSET_HOLDING_ENTITY_2 = AssetHoldingEntity(
            id = 0,
            encryptedAddress = ENCRYPTED_ADDRESS,
            assetId = 2,
            amount = BigInteger.ONE,
            isDeleted = true,
            isFrozen = false,
            optedInAtRound = 5,
            optedOutAtRound = 6,
            assetStatusEntity = ASSET_STATUS_ENTITY
        )
    }
}
