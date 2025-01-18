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

package com.algorand.wallet.account.local.data.mapper.model

import com.algorand.wallet.account.local.data.database.model.Algo25Entity
import com.algorand.wallet.account.local.domain.model.LocalAccount
import org.junit.Assert.assertEquals
import org.junit.Test

internal class Algo25MapperImplTest {

    private val sut = Algo25MapperImpl()

    @Test
    fun `EXPECT mapped model`() {

        val entity = Algo25Entity("unencrypted_address", byteArrayOf())
        val result = sut(entity)

        val expected = LocalAccount.Algo25("unencrypted_address")
        assertEquals(expected, result)
    }
}
