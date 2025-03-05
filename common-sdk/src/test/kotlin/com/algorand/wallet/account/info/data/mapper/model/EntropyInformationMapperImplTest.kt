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

package com.algorand.wallet.account.info.data.mapper.model

import com.algorand.wallet.account.info.data.database.model.EntropyInformationEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class EntropyInformationMapperImplTest {

    private val sut = EntropyInformationMapperImpl()

    @Test
    fun `EXPECT correctly mapped domain model WHEN mapping from entity`() {
        val seedId = 123
        val entropyCustomName = "Test Entropy"

        val entity = EntropyInformationEntity(
            seedId = seedId,
            entropyCustomName = entropyCustomName
        )

        val result = sut.invoke(entity)

        assertEquals(seedId, result.seedId)
        assertEquals(entropyCustomName, result.entropyCustomName)
    }

    @Test
    fun `EXPECT domain model with empty custom name WHEN entity has empty name`() {
        val seedId = 456
        val entropyCustomName = ""

        val entity = EntropyInformationEntity(
            seedId = seedId,
            entropyCustomName = entropyCustomName
        )

        val result = sut.invoke(entity)

        assertEquals(seedId, result.seedId)
        assertEquals(entropyCustomName, result.entropyCustomName)
    }
}