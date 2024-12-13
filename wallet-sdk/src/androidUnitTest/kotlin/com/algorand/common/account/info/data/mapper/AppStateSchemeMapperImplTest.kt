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

package com.algorand.common.account.info.data.mapper

import com.algorand.common.account.info.data.model.AppStateSchemaResponse
import com.algorand.common.account.info.domain.model.AppStateScheme
import kotlin.test.Test
import kotlin.test.assertEquals

class AppStateSchemeMapperImplTest {

    private val sut = AppStateSchemeMapperImpl()

    @Test
    fun `EXPECT mapped scheme WHEN response fields are valid`() {
        val response = AppStateSchemaResponse(numByteSlice = 1, numUint = 2)

        val result = sut(response)

        val expected = AppStateScheme(numByteSlice = 1, numUint = 2)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT mapped scheme with default values WHEN response fields are null`() {
        val response = AppStateSchemaResponse(numByteSlice = null, numUint = null)

        val result = sut(response)

        val expected = AppStateScheme(numByteSlice = 0, numUint = 0)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT mapped scheme with given values WHEN args are valid`() {
        val result = sut(1, 2)

        val expected = AppStateScheme(numByteSlice = 1, numUint = 2)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT mapped scheme with default values WHEN args are null`() {
        val result = sut(null, null)

        val expected = AppStateScheme(numByteSlice = 0, numUint = 0)
        assertEquals(expected, result)
    }
}
