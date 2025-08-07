/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.swap.data.mapper

import com.algorand.test.peraFixture
import com.algorand.wallet.asset.data.database.model.VerificationTierEntity
import com.algorand.wallet.asset.data.mapper.model.VerificationTierMapper
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.swap.data.model.SwapSelectedAssetDto
import com.algorand.wallet.swap.domain.model.SwapSelectedAssetDetail
import io.mockk.every
import io.mockk.mockk
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultSwapSelectedAssetDetailMapperTest {

    private val verificationTierMapper: VerificationTierMapper = mockk {
        every { invoke(entity = VERIFICATION_TIER_ENTITY) } returns VERIFICATION_TIER
    }

    private val sut = DefaultSwapSelectedAssetDetailMapper(verificationTierMapper)

    @Test
    fun `EXPECT null WHEN dto assetId is null`() {
        val dto = VALID_DTO.copy(assetId = null)

        val result = sut(dto)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN dto decimal is null`() {
        val dto = VALID_DTO.copy(decimal = null)

        val result = sut(dto)

        assertNull(result)
    }

    @Test
    fun `EXPECT unknown verification tier WHEN dto verification tier is null`() {
        val dto = VALID_DTO.copy(verificationTier = null)

        val result = sut(dto)

        assertEquals(VerificationTier.UNKNOWN, result?.verificationTier)
    }

    @Test
    fun `EXPECT not opted in state WHEN dto asset holding amount is null`() {
        val dto = VALID_DTO.copy(assetHoldingAmount = null)

        val result = sut(dto)

        assertEquals(SwapSelectedAssetDetail.OptInState.NotOptedIn, result?.optInState)
    }

    @Test
    fun `EXPECT mapped detail WHEN dto is valid`() {
        val result = sut(VALID_DTO)

        assertEquals(VALID_DETAIL, result)
    }

    @Test
    fun `EXPECT zero amount WHEN dto asset holding amount is null`() {
        val dto = VALID_DTO.copy(assetHoldingAmount = null)

        val result = sut(dto)

        assertEquals(BigInteger.ZERO, result?.amount)
    }

    private companion object {
        val VERIFICATION_TIER_ENTITY = peraFixture<VerificationTierEntity>()
        val VERIFICATION_TIER = peraFixture<VerificationTier>()

        val VALID_DTO = SwapSelectedAssetDto(
            assetId = 123L,
            unitName = "TST",
            verificationTier = VERIFICATION_TIER_ENTITY,
            imageUrl = "https://example.com/image.png",
            assetHoldingAmount = 1000.toBigInteger(),
            decimal = 6
        )

        val VALID_DETAIL = SwapSelectedAssetDetail(
            assetId = 123L,
            unitName = "TST",
            verificationTier = VERIFICATION_TIER,
            decimal = 6,
            imageUrl = "https://example.com/image.png",
            amount = 1000.toBigInteger(),
            optInState = SwapSelectedAssetDetail.OptInState.OptedIn
        )
    }
}
