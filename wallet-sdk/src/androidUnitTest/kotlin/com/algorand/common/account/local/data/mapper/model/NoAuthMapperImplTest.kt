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

package com.algorand.common.account.local.data.mapper.model

import com.algorand.common.account.local.data.database.model.NoAuthEntity
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.testing.peraFixture
import org.junit.Assert.assertEquals
import org.junit.Test

internal class NoAuthMapperImplTest {
    private val sut = NoAuthMapperImpl()

    @Test
    fun `EXPECT mapped model`() {
        val result = sut(NO_AUTH_ENTITY)
        val expected = LocalAccount.NoAuth("unencrypted_address")
        assertEquals(expected, result)
    }

    companion object {
        private val NO_AUTH_ENTITY = peraFixture<NoAuthEntity>().copy(algoAddress = "unencrypted_address")
    }
}
