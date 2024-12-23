/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.asset.data.mapper.entity

import com.algorand.common.asset.data.database.model.AssetDetailEntity
import com.algorand.common.asset.data.database.model.VerificationTierEntity
import com.algorand.common.asset.data.model.AssetCreatorResponse
import com.algorand.common.asset.data.model.AssetResponse
import com.algorand.common.testing.peraFixture
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

internal class AssetAssetInfoEntityMapperImplTest {

    private val verificationTierEntityMapper: VerificationTierEntityMapper = mockk()

    private val sut = AssetDetailEntityMapperImpl(verificationTierEntityMapper)

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        every { verificationTierEntityMapper(null) } returns VerificationTierEntity.UNKNOWN
        val result = sut(ASSET_DETAIL_RESPONSE)

        assertEquals(ASSET_DETAIL_ENTITY, result)
    }

    @Test
    fun `EXPECT null WHEN assetId is null`() {
        val assetDetailResponse = peraFixture<AssetResponse>().copy(assetId = null)

        val result = sut(assetDetailResponse)

        assertNull(result)
    }

    @Test
    fun `EXPECT default values when optional fields are null`() {
        every { verificationTierEntityMapper(null) } returns VerificationTierEntity.UNKNOWN
        val assetDetailResponse = peraFixture<AssetResponse>().copy(
            assetId = 1L,
            isAvailableOnDiscoverMobile = null,
            maxSupply = null,
            totalSupply = null,
            fractionDecimals = null,
            verificationTier = null
        )

        val result = sut(assetDetailResponse)

        assertEquals(0, result?.decimals)
        assertEquals("0", result?.maxSupply)
        assertEquals("0", result?.totalSupply)
        assertFalse(result?.availableOnDiscoverMobile!!)
    }

    companion object {
        private val ASSET_DETAIL_RESPONSE = AssetResponse(
            assetId = 1L,
            fullName = "fullName",
            shortName = "shortName",
            fractionDecimals = 2,
            usdValue = "10",
            maxSupply = "10",
            explorerUrl = "explorerUrl",
            projectUrl = "projectUrl",
            projectName = "projectName",
            logoSvgUri = "logoSvgUri",
            logoUri = "logoUri",
            description = "description",
            totalSupply = "10",
            url = "url",
            telegramUrl = "telegramUrl",
            twitterUsername = "twitterUsername",
            discordUrl = "discordUrl",
            isAvailableOnDiscoverMobile = true,
            last24HoursAlgoPriceChangePercentage = "10",
            verificationTier = null,
            assetCreator = AssetCreatorResponse(
                publicKey = "publicKey",
                id = 1L,
                isVerifiedAssetCreator = true
            ),
            collectible = null
        )

        private val ASSET_DETAIL_ENTITY = AssetDetailEntity(
            assetId = 1L,
            name = "fullName",
            unitName = "shortName",
            decimals = 2,
            usdValue = "10",
            maxSupply = "10",
            explorerUrl = "explorerUrl",
            projectUrl = "projectUrl",
            projectName = "projectName",
            logoSvgUrl = "logoSvgUri",
            logoUrl = "logoUri",
            description = "description",
            totalSupply = "10",
            url = "url",
            telegramUrl = "telegramUrl",
            twitterUsername = "twitterUsername",
            discordUrl = "discordUrl",
            availableOnDiscoverMobile = true,
            last24HoursAlgoPriceChangePercentage = "10",
            verificationTier = VerificationTierEntity.UNKNOWN,
            assetCreatorAddress = "publicKey",
            assetCreatorId = 1L,
            isVerifiedAssetCreator = true
        )
    }
}
