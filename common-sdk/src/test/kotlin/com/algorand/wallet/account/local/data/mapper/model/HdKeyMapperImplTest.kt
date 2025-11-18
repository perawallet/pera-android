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

package com.algorand.wallet.account.local.data.mapper.model

import com.algorand.wallet.account.local.data.database.model.HdKeyEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class HdKeyMapperImplTest {

    private val sut = HdKeyMapperImpl()

    @Test
    fun `EXPECT correctly mapped local account WHEN mapping from entity`() {
        val algoAddress = "ALGO123456"
        val publicKey = byteArrayOf(1, 2, 3)
        val encryptedPrivateKey = byteArrayOf(4, 5, 6)
        val seedId = 123
        val account = 0
        val change = 0
        val keyIndex = 5
        val derivationType = 1

        val entity = HdKeyEntity(
            algoAddress = algoAddress,
            publicKey = publicKey,
            encryptedPrivateKey = encryptedPrivateKey,
            seedId = seedId,
            account = account,
            change = change,
            keyIndex = keyIndex,
            derivationType = derivationType
        )

        val result = sut.invoke(entity)

        assertEquals(algoAddress, result.algoAddress)
        assertEquals(publicKey, result.publicKey)
        assertEquals(seedId, result.seedId)
        assertEquals(account, result.account)
        assertEquals(change, result.change)
        assertEquals(keyIndex, result.keyIndex)
        assertEquals(derivationType, result.derivationType)
    }

    @Test
    fun `EXPECT correct mapping WHEN entity has different values`() {
        val algoAddress = "ALGO789012"
        val publicKey = byteArrayOf(10, 11, 12)
        val encryptedPrivateKey = byteArrayOf(13, 14, 15)
        val seedId = 456
        val account = 1
        val change = 1
        val keyIndex = 10
        val derivationType = 2

        val entity = HdKeyEntity(
            algoAddress = algoAddress,
            publicKey = publicKey,
            encryptedPrivateKey = encryptedPrivateKey,
            seedId = seedId,
            account = account,
            change = change,
            keyIndex = keyIndex,
            derivationType = derivationType
        )

        val result = sut.invoke(entity)

        assertEquals(algoAddress, result.algoAddress)
        assertEquals(publicKey, result.publicKey)
        assertEquals(seedId, result.seedId)
        assertEquals(account, result.account)
        assertEquals(change, result.change)
        assertEquals(keyIndex, result.keyIndex)
        assertEquals(derivationType, result.derivationType)
    }
}