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

package com.algorand.wallet.cards.data.repository

import com.algorand.test.peraFixture
import com.algorand.wallet.cards.data.mapper.FundAddressMapper
import com.algorand.wallet.cards.data.model.CountryAvailabilityResponse
import com.algorand.wallet.cards.data.model.FundAddressResponse
import com.algorand.wallet.cards.data.model.FundAddressResultResponse
import com.algorand.wallet.cards.data.service.CardApiService
import com.algorand.wallet.cards.domain.model.FundAddress
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultCardRepositoryTest {

    private val cardApiService: CardApiService = mockk(relaxed = true)
    private val fundAddressMapper: FundAddressMapper = mockk(relaxed = true)

    private val sut = DefaultCardRepository(cardApiService, fundAddressMapper)

    @Test
    fun `EXPECT error WHEN fetching fund address throws exception`() = runTest {
        coEvery { cardApiService.getFundAddresses(ADDRESSES_QUERY) } throws Exception("Network error")

        val result = sut.getCardFundAddresses(ADDRESSES)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT fund addresses WHEN fetching fund address succeeds`() = runTest {
        val response = peraFixture<FundAddressResponse>()
        val fundAddress = peraFixture<FundAddress>()
        coEvery { cardApiService.getFundAddresses(ADDRESSES_QUERY) } returns FundAddressResultResponse(listOf(response))
        every { fundAddressMapper.map(response) } returns fundAddress

        val result = sut.getCardFundAddresses(ADDRESSES)

        val expected = PeraResult.Success(listOf(fundAddress))
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT error WHEN checking country waitlist throws exception`() = runTest {
        coEvery { cardApiService.isCountryAvailable(ADDRESSES_QUERY) } throws Exception("Network error")

        val result = sut.isCountryWaitlisted(ADDRESSES)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT waitlisted status true WHEN checking country waitlist succeeds and response is not null`() = runTest {
        coEvery { cardApiService.isCountryAvailable(ADDRESSES_QUERY) } returns CountryAvailabilityResponse(true)

        val result = sut.isCountryWaitlisted(ADDRESSES)

        val expected = PeraResult.Success(true)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT waitlisted status false WHEN checking country waitlist succeeds and response is null`() = runTest {
        coEvery { cardApiService.isCountryAvailable(ADDRESSES_QUERY) } returns CountryAvailabilityResponse(null)

        val result = sut.isCountryWaitlisted(ADDRESSES)

        val expected = PeraResult.Success(false)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT waitlisted status false WHEN checking country waitlist succeeds and response is false`() = runTest {
        coEvery { cardApiService.isCountryAvailable(ADDRESSES_QUERY) } returns CountryAvailabilityResponse(false)

        val result = sut.isCountryWaitlisted(ADDRESSES)

        val expected = PeraResult.Success(false)
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDRESSES_QUERY = "address1,address2"
        val ADDRESSES = listOf("address1", "address2")
    }
}
