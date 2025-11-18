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
import com.algorand.wallet.cards.domain.model.CardNftRewardState
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultCardNftRewardStateMapperTest {

    private val sut = DefaultCardNftRewardStateMapper()

    @Test
    fun `EXPECT processed state WHEN response is processed`() {
        val result = sut.map(CardNftRewardStateResponse.PROCESSED)

        assertEquals(CardNftRewardState.PROCESSED, result)
    }

    @Test
    fun `EXPECT is processing state WHEN response is isProcessing`() {
        val result = sut.map(CardNftRewardStateResponse.IS_PROCESSING)

        assertEquals(CardNftRewardState.IS_PROCESSING, result)
    }

    @Test
    fun `EXPECT not processed state WHEN response is notProcessed`() {
        val result = sut.map(CardNftRewardStateResponse.NOT_PROCESSED)

        assertEquals(CardNftRewardState.NOT_PROCESSED, result)
    }
}
