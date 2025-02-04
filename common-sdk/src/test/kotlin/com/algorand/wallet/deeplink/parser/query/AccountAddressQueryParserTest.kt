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

package com.algorand.wallet.deeplink.parser.query

import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdkAddress
import com.algorand.wallet.deeplink.PeraUriBuilder
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class AccountAddressQueryParserTest {

    private val algoSdkUtils: AlgoSdkAddress = mockk {
        every { isValid(ADDRESS) } returns true
        every { isValid("qr") } returns false
        every { isValid("1241231") } returns false
        every { isValid("") } returns false
    }

    private val sut = AccountAddressQueryParser(algoSdkUtils)

    @Test
    fun `EXPECT account address WHEN uri is perawallet deep link`() {
        val uri = PeraUriBuilder.create(
            scheme = "perawallet",
            host = ADDRESS
        )
        val result = sut.parseQuery(uri)

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT account address WHEN uri is perawallet with some queries`() {
        val uri = PeraUriBuilder.create(
            scheme = "perawallet",
            host = ADDRESS,
            queryParams = mapOf("amount" to "1000000", "note" to "1_ALGO_Transfer")
        )
        val result = sut.parseQuery(uri)

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT null WHEN uri is perawallet deep link but has no account address`() {
        val uri = PeraUriBuilder.create(
            scheme = "perawallet",
            host = "1241231"
        )
        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT account address WHEN uri is coinbase uri`() {
        val uri = PeraUriBuilder.create(
            scheme = "algo",
            host = ADDRESS,
            rawUri = "algo:$ADDRESS"
        )
        val result = sut.parseQuery(uri)

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT account address WHEN uri is coinbase uri with asset id`() {
        every { algoSdkUtils.isValid("31566704") } returns false
        val uri = PeraUriBuilder.create(
            scheme = "algo",
            host = "31566704",
            queryParams = mapOf("address" to ADDRESS, "note" to "1_ALGO_Transfer"),
            rawUri = "algo:31566704/transfer?address=$ADDRESS"
        )
        val result = sut.parseQuery(uri)

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT null WHEN uri is coinbase uri but has no account address`() {
        every { algoSdkUtils.isValid("algo:31566704/transfer?address=1241231") } returns false
        val uri = PeraUriBuilder.create(
            scheme = "algo",
            host = "31566704",
            path = "transfer",
            queryParams = mapOf("address" to "1241231"),
            rawUri = "algo:31566704/transfer?address=1241231"
        )
        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT account address WHEN uri is algorand deep link`() {
        val uri = PeraUriBuilder.create(
            scheme = "algorand",
            host = ADDRESS,
            rawUri = "algorand:$ADDRESS"
        )

        val result = sut.parseQuery(uri)

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT account address WHEN uri is algorand deep link with queries`() {
        val uri = PeraUriBuilder.create(
            scheme = "algorand",
            host = ADDRESS,
            queryParams = mapOf("amount" to "1000000", "note" to "1_ALGO_Transfer"),
            rawUri = "algorand:$ADDRESS?amount=1000000&note=1_ALGO_Transfer"
        )

        val result = sut.parseQuery(uri)

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT null WHEN uri is algorand deep link but has no account address`() {
        every { algoSdkUtils.isValid("31566704") } returns false
        every { algoSdkUtils.isValid("algorand:31566704/transfer?address=1241231") } returns false
        val uri = PeraUriBuilder.create(
            scheme = "algorand",
            host = "31566704",
            path = "transfer",
            queryParams = mapOf("address" to "1241231"),
            rawUri = "algorand:31566704/transfer?address=1241231"
        )

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    @Test
    fun `EXPECT account address WHEN uri is applink`() {
        val uri = PeraUriBuilder.create(
            scheme = "https",
            host = "perawallet.app",
            path = "qr/$ADDRESS",
            rawUri = "https://perawallet.app/qr/$ADDRESS"
        )

        val result = sut.parseQuery(uri)

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT account address WHEN uri is applink with queries`() {
        val uri = PeraUriBuilder.create(
            scheme = "https",
            host = "perawallet.app",
            path = "qr/$ADDRESS",
            queryParams = mapOf("amount" to "100"),
            rawUri = "https://perawallet.app/qr/$ADDRESS?amount=100"
        )

        val result = sut.parseQuery(uri)

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT null WHEN applink does not have account address`() {
        every { algoSdkUtils.isValid("https://perawallet.app/qr/1241231") } returns false
        val uri = PeraUriBuilder.create(
            scheme = "https",
            host = "perawallet.app",
            path = "qr/1241231",
            rawUri = "https://perawallet.app/qr/1241231"
        )

        val result = sut.parseQuery(uri)

        assertNull(result)
    }

    private companion object {
        const val ADDRESS = "Z7HJOZWPBM76GNERLD56IUMNMA7TNFMERU4KSDDXLUYGFBRLLVVGKGULCE"
    }
}
