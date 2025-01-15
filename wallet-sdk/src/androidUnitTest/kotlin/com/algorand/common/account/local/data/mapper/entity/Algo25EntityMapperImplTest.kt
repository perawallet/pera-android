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

package com.algorand.common.account.local.data.mapper.entity

import com.algorand.common.encryption.SecretKeyEncryptionManager
import io.mockk.mockk

internal class Algo25EntityMapperImplTest {

    private val secretKeyEncryptionManager: SecretKeyEncryptionManager = mockk()

    private val sut = Algo25EntityMapperImpl()

//    @Test
//    fun `EXPECT mapped entity`() {
//        coEvery { secretKeyEncryptionManager.encrypt(byteArrayOf(1, 2, 3)) } returns "encrypted_secret_key".toByteArray()
//
//        val result = sut(LocalAccount.Algo25(algoAddress = "unencrypted_address", encryptedSecretKey = byteArrayOf(1, 2, 3)))
//
//        val expected = Algo25Entity(algoAddress = "unencrypted_address", "encrypted_secret_key".toByteArray())
//        assertEquals(expected, result)
//    }
}
