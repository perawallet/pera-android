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

package com.algorand.wallet.cards.data.mapper

import com.algorand.wallet.cards.data.model.CardNftRewardStateResponse
import com.algorand.wallet.cards.data.model.FundAddressResponse
import com.algorand.wallet.cards.domain.model.CardNftRewardState
import com.algorand.wallet.cards.domain.model.FundAddress
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultFundAddressMapperTest {

    private val nftRewardStateMapper: CardNftRewardStateMapper = mockk {
        every { map(CardNftRewardStateResponse.PROCESSED) } returns CardNftRewardState.PROCESSED
    }

    private val sut = DefaultFundAddressMapper(nftRewardStateMapper)

    @Test
    fun `EXPECT null WHEN owner address is null`() {
        val response = FUND_ADDRESS_RESPONSE.copy(ownerAddress = null)

        val result = sut.map(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN nft reward state is null`() {
        val response = FUND_ADDRESS_RESPONSE.copy(nftRewardStateResponse = null)

        val result = sut.map(response)

        assertNull(result)
    }

    @Test
    fun `EXPECT fund address WHEN all fields are valid`() {
        val result = sut.map(FUND_ADDRESS_RESPONSE)

        assertEquals(FUND_ADDRESS, result)
    }

    private companion object {
        val FUND_ADDRESS_RESPONSE = FundAddressResponse(
            ownerAddress = "ownerAddress",
            cardFundAddress = "cardFundAddress",
            nftRewardStateResponse = CardNftRewardStateResponse.PROCESSED
        )

        val FUND_ADDRESS = FundAddress(
            ownerAddress = "ownerAddress",
            fundAddress = "cardFundAddress",
            nftRewardState = CardNftRewardState.PROCESSED
        )
    }
}
