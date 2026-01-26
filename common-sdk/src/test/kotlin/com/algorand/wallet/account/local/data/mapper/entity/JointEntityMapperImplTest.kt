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

package com.algorand.wallet.account.local.data.mapper.entity

import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.foundation.json.JsonSerializer
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

internal class JointEntityMapperImplTest {

    private val jsonSerializer: JsonSerializer = mockk()
    private val mapper = JointEntityMapperImpl(jsonSerializer)

    @Test
    fun `EXPECT all fields mapped correctly WHEN invoke is called`() {
        val participantSlot = slot<List<String>>()
        every { jsonSerializer.toJson(capture(participantSlot)) } returns SERIALIZED_ADDRESSES
        val localAccount = createTestJointAccount()

        val entity = mapper(localAccount)

        assertEquals(TEST_ADDRESS, entity.algoAddress)
        assertEquals(SERIALIZED_ADDRESSES, entity.participantAddresses)
        assertEquals(TEST_THRESHOLD, entity.threshold)
        assertEquals(TEST_VERSION, entity.version)
        assertEquals(TEST_PARTICIPANT_ADDRESSES, participantSlot.captured)
    }

    @Test
    fun `EXPECT jsonSerializer called with participant addresses WHEN invoke is called`() {
        every { jsonSerializer.toJson(any()) } returns SERIALIZED_ADDRESSES
        val localAccount = createTestJointAccount()

        mapper(localAccount)

        verify { jsonSerializer.toJson(TEST_PARTICIPANT_ADDRESSES) }
    }

    @Test
    fun `EXPECT empty serialization WHEN participant addresses is empty`() {
        val emptySerializedAddresses = "[]"
        every { jsonSerializer.toJson(emptyList<String>()) } returns emptySerializedAddresses
        val localAccount = LocalAccount.Joint(
            algoAddress = TEST_ADDRESS,
            participantAddresses = emptyList(),
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION
        )

        val entity = mapper(localAccount)

        assertEquals(emptySerializedAddresses, entity.participantAddresses)
        verify { jsonSerializer.toJson(emptyList<String>()) }
    }

    private fun createTestJointAccount() = LocalAccount.Joint(
        algoAddress = TEST_ADDRESS,
        participantAddresses = TEST_PARTICIPANT_ADDRESSES,
        threshold = TEST_THRESHOLD,
        version = TEST_VERSION
    )

    private companion object {
        const val TEST_ADDRESS = "JOINT_ADDRESS_123"
        val TEST_PARTICIPANT_ADDRESSES = listOf("ADDR1", "ADDR2", "ADDR3")
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1
        const val SERIALIZED_ADDRESSES = "[\"ADDR1\",\"ADDR2\",\"ADDR3\"]"
    }
}
