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

import com.algorand.wallet.account.local.data.database.model.JointEntity
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class JointMapperImplTest {

    private val gson = Gson()
    private val mapper = JointMapperImpl(gson)

    @Test
    fun `EXPECT all fields mapped correctly WHEN invoke is called`() {
        val entity = createTestJointEntity()

        val localAccount = mapper(entity)

        assertEquals(TEST_ADDRESS, localAccount.algoAddress)
        assertEquals(TEST_PARTICIPANT_ADDRESSES, localAccount.participantAddresses)
        assertEquals(TEST_THRESHOLD, localAccount.threshold)
        assertEquals(TEST_VERSION, localAccount.version)
    }

    @Test
    fun `EXPECT participantAddresses mapped correctly WHEN invoke is called`() {
        val entity = createTestJointEntity()

        val localAccount = mapper(entity)

        assertEquals(TEST_PARTICIPANT_ADDRESSES, localAccount.participantAddresses)
    }

    @Test
    fun `EXPECT empty list WHEN participant addresses is empty json array`() {
        val emptySerializedAddresses = "[]"
        val entity = JointEntity(
            algoAddress = TEST_ADDRESS,
            participantAddresses = emptySerializedAddresses,
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION
        )

        val localAccount = mapper(entity)

        assertTrue(localAccount.participantAddresses.isEmpty())
    }

    @Test
    fun `EXPECT single address list WHEN participant addresses has one element`() {
        val singleAddress = "SINGLE_ADDR"
        val singleAddressSerialized = "[\"$singleAddress\"]"
        val entity = JointEntity(
            algoAddress = TEST_ADDRESS,
            participantAddresses = singleAddressSerialized,
            threshold = 1,
            version = TEST_VERSION
        )

        val localAccount = mapper(entity)

        assertEquals(listOf(singleAddress), localAccount.participantAddresses)
    }

    private fun createTestJointEntity() = JointEntity(
        algoAddress = TEST_ADDRESS,
        participantAddresses = SERIALIZED_ADDRESSES,
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
