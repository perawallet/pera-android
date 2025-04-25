/*
 * Copyright 2025 Pera Wallet, LDA
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

import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class HdKeyEntityMapperImplTest {

    private val aesPlatformManager = mockk<AESPlatformManager>()
    private val sut = HdKeyEntityMapperImpl(aesPlatformManager)

    @Test
    fun `EXPECT correct entity mapping WHEN mapping hdkey account to entity`() {
        val algoAddress = "ALGO123456"
        val publicKey = byteArrayOf(1, 2, 3)
        val privateKey = byteArrayOf(4, 5, 6)
        val encryptedPrivateKey = byteArrayOf(7, 8, 9)
        val seedId = 123
        val account = 0
        val change = 0
        val keyIndex = 0
        val derivationType = 1

        val localAccount = LocalAccount.HdKey(
            algoAddress = algoAddress,
            publicKey = publicKey,
            seedId = seedId,
            account = account,
            change = change,
            keyIndex = keyIndex,
            derivationType = derivationType
        )

        every { aesPlatformManager.encryptByteArray(privateKey) } returns encryptedPrivateKey

        val result = sut.invoke(localAccount, privateKey)

        assertEquals(algoAddress, result.algoAddress)
        assertEquals(publicKey, result.publicKey)
        assertEquals(encryptedPrivateKey, result.encryptedPrivateKey)
        assertEquals(seedId, result.seedId)
        assertEquals(account, result.account)
        assertEquals(change, result.change)
        assertEquals(keyIndex, result.keyIndex)
        assertEquals(derivationType, result.derivationType)
    }

    @Test
    fun `EXPECT encrypted private key WHEN mapping with different private key values`() {
        val algoAddress = "ALGO123456"
        val publicKey = byteArrayOf(1, 2, 3)
        val privateKey = byteArrayOf(10, 11, 12)
        val encryptedPrivateKey = byteArrayOf(13, 14, 15)
        val seedId = 123

        val localAccount = LocalAccount.HdKey(
            algoAddress = algoAddress,
            publicKey = publicKey,
            seedId = seedId,
            account = 0,
            change = 0,
            keyIndex = 0,
            derivationType = 1
        )

        every { aesPlatformManager.encryptByteArray(privateKey) } returns encryptedPrivateKey

        val result = sut.invoke(localAccount, privateKey)

        assertEquals(encryptedPrivateKey, result.encryptedPrivateKey)
    }
}