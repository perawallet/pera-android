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

package com.algorand.wallet.account.local.data.mapper.entity

import com.algorand.wallet.account.local.data.database.model.Algo25Entity
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.encryption.AESPlatformManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

internal class Algo25EntityMapperImplTest {

    private val aesPlatformManager: AESPlatformManager = mockk(relaxed = true)

    private val sut = Algo25EntityMapperImpl(aesPlatformManager)

    @Test
    fun `EXPECT mapped entity`() {
        every { aesPlatformManager.encryptByteArray(byteArrayOf(1, 2, 3)) } returns byteArrayOf(4, 5, 6)

        val privateKey = byteArrayOf(1, 2, 3)

        val result = sut(LocalAccount.Algo25(algoAddress = "unencrypted_address"), privateKey)

        val expected = Algo25Entity(algoAddress = "unencrypted_address", byteArrayOf(4, 5, 6))
        assertEquals(expected, result)
    }
}
