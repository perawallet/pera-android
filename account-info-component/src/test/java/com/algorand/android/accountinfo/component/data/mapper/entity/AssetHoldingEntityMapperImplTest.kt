package com.algorand.android.account.accountinformation.data.mapper.entity

import com.algorand.android.shared_db.accountinformation.model.AssetHoldingEntity
import com.algorand.android.account.accountinformation.data.model.AssetHoldingResponse
import com.algorand.android.encryption.EncryptionManager
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*
import java.math.BigInteger

internal class AssetHoldingEntityMapperImplTest {

    private val encryptionManager: EncryptionManager = mock {
        on { encrypt(com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ADDRESS) } doReturn com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ENCRYPTED_ADDRESS
    }

    private val sut = AssetHoldingEntityMapperImpl(encryptionManager)

    @Test
    fun `EXPECT asset holding to be mapped successfully`() {
        val result = sut(
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ADDRESS,
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ASSET_HOLDING_RESPONSE_1
        )

        assertEquals(
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ASSET_HOLDING_ENTITY_1,
            result
        )
    }

    @Test
    fun `EXPECT asset holding list to be mapped successfully`() {
        val result = sut(
            listOf(
                com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ADDRESS to com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ASSET_HOLDING_RESPONSE_1,
                com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ADDRESS to com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ASSET_HOLDING_RESPONSE_2
            )
        )

        val expected = listOf(
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ASSET_HOLDING_ENTITY_1,
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ASSET_HOLDING_ENTITY_2
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN asset id is missing`() {
        val assetHoldingResponse =
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ASSET_HOLDING_RESPONSE_1.copy(
                assetId = null
            )

        val result = sut(
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ADDRESS,
            assetHoldingResponse
        )

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN amount is missing`() {
        val assetHoldingResponse =
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ASSET_HOLDING_RESPONSE_1.copy(
                amount = null
            )

        val result = sut(
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ADDRESS,
            assetHoldingResponse
        )

        assertNull(result)
    }

    @Test
    fun `EXPECT default values WHEN optional fields are missing`() {
        val assetHoldingResponse =
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ASSET_HOLDING_RESPONSE_1.copy(
                isDeleted = null,
                isFrozen = null
            )

        val result = sut(
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ADDRESS,
            assetHoldingResponse
        )

        val expected =
            com.algorand.android.account.accountinformation.data.mapper.entity.AssetHoldingEntityMapperImplTest.Companion.ASSET_HOLDING_ENTITY_1.copy(
                isDeleted = false,
                isFrozen = false
            )
        assertEquals(expected, result)
    }

    companion object {
        private const val ADDRESS = "address"
        private const val ENCRYPTED_ADDRESS = "encrypted_address"

        private val ASSET_HOLDING_RESPONSE_1 = AssetHoldingResponse(
            assetId = 1,
            amount = BigInteger.TEN,
            isDeleted = false,
            isFrozen = false,
            optedInAtRound = 1,
            optedOutAtRound = 2
        )
        private val ASSET_HOLDING_ENTITY_1 = AssetHoldingEntity(
            id = 0,
            encryptedAddress = ENCRYPTED_ADDRESS,
            assetId = 1,
            amount = BigInteger.TEN,
            isDeleted = false,
            isFrozen = false,
            optedInAtRound = 1,
            optedOutAtRound = 2
        )
        private val ASSET_HOLDING_RESPONSE_2 = AssetHoldingResponse(
            assetId = 2,
            amount = BigInteger.ONE,
            isDeleted = true,
            isFrozen = false,
            optedInAtRound = 5,
            optedOutAtRound = 6
        )
        private val ASSET_HOLDING_ENTITY_2 = AssetHoldingEntity(
            id = 0,
            encryptedAddress = ENCRYPTED_ADDRESS,
            assetId = 2,
            amount = BigInteger.ONE,
            isDeleted = true,
            isFrozen = false,
            optedInAtRound = 5,
            optedOutAtRound = 6
        )
    }
}
