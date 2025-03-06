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

import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class HdSeedEntityMapperImplTest {

    private val aesPlatformManager = mockk<AESPlatformManager>()
    private val sut = HdSeedEntityMapperImpl(aesPlatformManager)

    @Test
    fun `EXPECT correctly mapped entity WHEN mapping seed data to entity`() {
        val seedId = 123
        val entropy = byteArrayOf(1, 2, 3)
        val seed = byteArrayOf(4, 5, 6)
        val encryptedEntropy = byteArrayOf(7, 8, 9)
        val encryptedSeed = byteArrayOf(10, 11, 12)

        every { aesPlatformManager.encryptByteArray(entropy) } returns encryptedEntropy
        every { aesPlatformManager.encryptByteArray(seed) } returns encryptedSeed

        val result = sut.invoke(seedId, entropy, seed)

        assertEquals(0, result.seedId) // Always 0 to let Room auto-generate
        assertEquals(encryptedEntropy, result.encryptedEntropy)
        assertEquals(encryptedSeed, result.encryptedSeed)
    }

    @Test
    fun `EXPECT encrypted values WHEN mapping with different entropy and seed values`() {
        val seedId = 456
        val entropy = byteArrayOf(20, 21, 22)
        val seed = byteArrayOf(23, 24, 25)
        val encryptedEntropy = byteArrayOf(26, 27, 28)
        val encryptedSeed = byteArrayOf(29, 30, 31)

        every { aesPlatformManager.encryptByteArray(entropy) } returns encryptedEntropy
        every { aesPlatformManager.encryptByteArray(seed) } returns encryptedSeed

        val result = sut.invoke(seedId, entropy, seed)

        assertEquals(0, result.seedId)
        assertEquals(encryptedEntropy, result.encryptedEntropy)
        assertEquals(encryptedSeed, result.encryptedSeed)
    }

    @Test
    fun `EXPECT seedId to be zero WHEN providing different seedId values`() {
        val seedId1 = 123
        val seedId2 = 456
        val entropy = byteArrayOf(1, 2, 3)
        val seed = byteArrayOf(4, 5, 6)
        val encryptedEntropy = byteArrayOf(7, 8, 9)
        val encryptedSeed = byteArrayOf(10, 11, 12)

        every { aesPlatformManager.encryptByteArray(entropy) } returns encryptedEntropy
        every { aesPlatformManager.encryptByteArray(seed) } returns encryptedSeed

        val result1 = sut.invoke(seedId1, entropy, seed)
        val result2 = sut.invoke(seedId2, entropy, seed)

        assertEquals(0, result1.seedId)
        assertEquals(0, result2.seedId)
    }
}