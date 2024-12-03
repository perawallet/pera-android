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

import com.algorand.common.account.local.data.database.model.NoAuthEntity
import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.encryption.AddressEncryptionManager
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

internal class NoAuthEntityMapperImplTest {

    private val encryptionManager: AddressEncryptionManager = mockk()

    private val sut = NoAuthEntityMapperImpl(encryptionManager)

    @Test
    fun `EXPECT mapped entity`() {
        coEvery { encryptionManager.encrypt("unencrypted_address") } returns "encrypted_address"

        val result = sut(LocalAccount.NoAuth(address = "unencrypted_address"))

        val expected = NoAuthEntity(encryptedAddress = "encrypted_address")
        assertEquals(expected, result)
    }
}
