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

package com.algorand.android.modules.walletconnect.cards.domain.helper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WalletConnectUrlSelectedAddressParserImplTest {

    private val sut = WalletConnectUrlSelectedAddressParserImpl()

    @Test
    fun `EXPECT selected address WHEN selected address query exists with other queries`() {
        val urlWithAddress = "$WC_REQUEST_URL?$SYM_KEY_QUERY&$SELECTED_ACCOUNT_KEY=$ADDRESS"

        val result = sut(urlWithAddress)

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT selected address WHEN only query is selected address`() {
        val urlWithAddress = "$WC_REQUEST_URL?$SELECTED_ACCOUNT_KEY=$ADDRESS"

        val result = sut(urlWithAddress)

        assertEquals(ADDRESS, result)
    }

    @Test
    fun `EXPECT null WHEN selected address query key does not exist`() {
        val url = "$WC_REQUEST_URL?$SYM_KEY_QUERY"

        val result = sut(url)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN url has no query`() {
        val result = sut(WC_REQUEST_URL)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN url is blank`() {
        val result = sut(" ")

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN url is empty`() {
        val result = sut("")

        assertNull(result)
    }

    private companion object {
        const val ADDRESS = "BGS5JPS56VNAU5WKWFR54RGB2IGKL66WULSYHNYY7QPGEXUMHKBY76OVWM"
        const val SELECTED_ACCOUNT_KEY = "selectedAccount"
        const val SYM_KEY_QUERY = "symKey=SOME_RANDOM_KEY"
        const val WC_REQUEST_URL = "wc:cbsbajsfqnwjsf7@2"
    }
}
