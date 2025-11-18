package com.algorand.wallet.algosdk.transaction.mapper

import com.algorand.test.peraFixture
import com.algorand.wallet.algosdk.transaction.model.AssetConfigParameters
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionAssetConfigParametersPayload
import org.junit.Assert.assertEquals
import org.junit.Test

class AssetConfigParametersMapperImplTest {

    private val sut = AssetConfigParametersMapperImpl()

    @Test
    fun `EXPECT asset config parameters to be mapped`() {
        val result = sut(PAYLOAD)

        assertEquals(ASSET_CONFIG_PARAMETERS, result)
    }

    @Test
    fun `EXPECT asset config parameters to be null WHEN payload is null`() {
        val result = sut(null)

        val expected = AssetConfigParameters(
            unitName = null,
            managerAddress = null,
            reserveAddress = null,
            clawbackAddress = null,
            totalSupply = null,
            decimal = null,
            isFrozen = null,
            name = null,
            url = null,
            metadataHash = null,
            frozenAddress = null
        )
        assertEquals(expected, result)
    }

    private companion object {
        val TOTAL_SUPPLY = peraFixture<String?>()
        val DECIMAL = peraFixture<Long?>()
        val IS_FROZEN = peraFixture<Boolean?>()
        val UNIT_NAME = peraFixture<String?>()
        val NAME = peraFixture<String?>()
        val URL = peraFixture<String?>()
        val METADATA_HASH = peraFixture<String?>()
        val MANAGER_ADDRESS = peraFixture<String?>()
        val RESERVE_ADDRESS = peraFixture<String?>()
        val FROZEN_ADDRESS = peraFixture<String?>()
        val CLAWBACK_ADDRESS = peraFixture<String?>()

        val PAYLOAD = RawTransactionAssetConfigParametersPayload(
            totalSupply = TOTAL_SUPPLY,
            decimal = DECIMAL,
            isFrozen = IS_FROZEN,
            unitName = UNIT_NAME,
            name = NAME,
            url = URL,
            metadataHash = METADATA_HASH,
            managerAddress = MANAGER_ADDRESS,
            reserveAddress = RESERVE_ADDRESS,
            frozenAddress = FROZEN_ADDRESS,
            clawbackAddress = CLAWBACK_ADDRESS
        )

        val ASSET_CONFIG_PARAMETERS = AssetConfigParameters(
            totalSupply = TOTAL_SUPPLY,
            decimal = DECIMAL,
            isFrozen = IS_FROZEN,
            unitName = UNIT_NAME,
            name = NAME,
            url = URL,
            metadataHash = METADATA_HASH,
            managerAddress = MANAGER_ADDRESS,
            reserveAddress = RESERVE_ADDRESS,
            frozenAddress = FROZEN_ADDRESS,
            clawbackAddress = CLAWBACK_ADDRESS
        )
    }
}
