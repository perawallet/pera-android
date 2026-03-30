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

package com.algorand.wallet.jointaccount.transaction.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class SignRequestTypeTest {

    @Test
    fun `EXPECT ASYNC WHEN value is async`() {
        assertEquals(SignRequestType.ASYNC, SignRequestType.fromValue("async"))
    }

    @Test
    fun `EXPECT SYNC WHEN value is sync`() {
        assertEquals(SignRequestType.SYNC, SignRequestType.fromValue("sync"))
    }

    @Test
    fun `EXPECT null WHEN value is unknown`() {
        assertNull(SignRequestType.fromValue("unknown"))
    }

    @Test
    fun `EXPECT null WHEN value is null`() {
        assertNull(SignRequestType.fromValue(null))
    }

    @Test
    fun `EXPECT null WHEN value is empty`() {
        assertNull(SignRequestType.fromValue(""))
    }

    @Test
    fun `EXPECT null WHEN value has wrong case`() {
        assertNull(SignRequestType.fromValue("ASYNC"))
    }
}
