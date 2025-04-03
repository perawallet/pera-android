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

package com.algorand.wallet.account.info.data.mapper.model

import com.algorand.wallet.account.info.data.model.AccountFastLookupResponse
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountFastLookupMapperImplTest {

    private var sut: AccountFastLookupMapperImpl = AccountFastLookupMapperImpl()

    @Test
    fun `EXPECT correct account fast lookup WHEN mapping response`() {
        val response = AccountFastLookupResponse(
            algoValue = "1000000",
            usdValue = "150.25",
            calculationType = "estimated",
            accountExists = true
        )

        val expected = AccountFastLookup(
            algoValue = BigDecimal("1000000"),
            usdValue = BigDecimal("150.25"),
            accountExists = true
        )

        val result = sut.invoke(response)

        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT account does not exist WHEN response has accountExists false`() {
        val response = AccountFastLookupResponse(
            algoValue = "0.00",
            usdValue = "0.00",
            calculationType = "estimated",
            accountExists = false
        )

        val expected = AccountFastLookup(
            algoValue = BigDecimal.ZERO.setScale(2),
            usdValue = BigDecimal.ZERO.setScale(2),
            accountExists = false
        )

        val result = sut.invoke(response)

        assertEquals(expected, result)
    }
}
