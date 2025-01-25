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

package com.algorand.android.modules.algosdk.domain.mapper

import com.algorand.algosdk.util.Encoder
import com.algorand.android.models.TransactionParams
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

class TransactionParametersResponseMapperImplTest {

    private val sut = TransactionParametersResponseMapperImpl()

    @Test
    fun `EXPECT mapped TransactionParametersResponse`() {
        val params = TransactionParams(
            fee = 1000L,
            minFee = 1000L,
            genesisId = "genesisId",
            genesisHash = "genesisHash",
            lastRound = 1000L
        )

        val result = sut(params)

        assertEquals(1000L, result.fee)
        assertEquals(1000L, result.minFee)
        assertEquals("genesisId", result.genesisId)
        assertEquals(1000L, result.lastRound)
        assertTrue(Encoder.decodeFromBase64("genesisHash").contentEquals(result.genesisHash))
    }
}
