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

import com.algorand.common.encryption.SecretKeyEncryptionManager
import io.mockk.mockk

internal class Algo25MapperImplTest {

    private val secretKeyEncryptionManager: SecretKeyEncryptionManager = mockk()

    private val sut = Algo25MapperImpl()

//    @Test
//    fun `EXPECT mapped model`() {
//        coEvery { secretKeyEncryptionManager.decrypt("encrypted_secret_key".toByteArray()) } returns byteArrayOf(1, 2, 3)
//
//        val entity = Algo25Entity("unencrypted_address", "encrypted_secret_key".toByteArray())
//        val result = sut(entity)
//
//        val expected = LocalAccount.Algo25("unencrypted_address", byteArrayOf(1, 2, 3))
//        assertEquals(expected, result)
//    }
}
