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

package com.algorand.wallet.account.local.data.mapper.model

import com.algorand.wallet.account.local.data.database.model.HdKeyEntity
import com.algorand.wallet.account.local.domain.model.HdWalletSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class HdWalletSummaryMapperImplTest {

    private val sut = HdWalletSummaryMapperImpl()

    @Test
    fun `EXPECT HdWalletSummary WHEN mapper is invoked`() {
        val entity = HdKeyEntity(
            algoAddress = "addr1",
            publicKey = byteArrayOf(1),
            encryptedPrivateKey = byteArrayOf(2),
            seedId = 123,
            account = 5,
            change = 0,
            keyIndex = 0,
            derivationType = 1
        )

        val accountCount = 10

        val expected = HdWalletSummary(
            seedId = 123,
            accountCount = 10,
            maxAccountIndex = 5,
            primaryValue = "",
            secondaryValue = ""
        )

        val result = sut.invoke(entity, accountCount)

        assertEquals(expected.seedId, result.seedId)
        assertEquals(expected.accountCount, result.accountCount)
        assertEquals(expected.maxAccountIndex, result.maxAccountIndex)
        assertEquals(expected.primaryValue, result.primaryValue)
        assertEquals(expected.secondaryValue, result.secondaryValue)
    }
}
