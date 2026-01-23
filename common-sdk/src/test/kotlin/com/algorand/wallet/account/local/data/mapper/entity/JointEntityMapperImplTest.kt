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
import org.junit.Assert.assertEquals
import org.junit.Test

internal class JointEntityMapperImplTest {

    private val mapper = JointEntityMapperImpl()

    @Test
    fun `EXPECT joint entity fields mapped correctly WHEN invoke is called`() {
        val localAccount = createTestJointAccount()

        val result = mapper(localAccount)

        assertEquals(TEST_ADDRESS, result.jointEntity.algoAddress)
        assertEquals(TEST_THRESHOLD, result.jointEntity.threshold)
        assertEquals(TEST_VERSION, result.jointEntity.version)
    }

    @Test
    fun `EXPECT participant entities created with correct indices WHEN invoke is called`() {
        val localAccount = createTestJointAccount()

        val result = mapper(localAccount)

        assertEquals(TEST_PARTICIPANT_ADDRESSES.size, result.participantEntities.size)
        result.participantEntities.forEachIndexed { index, entity ->
            assertEquals(TEST_ADDRESS, entity.jointAddress)
            assertEquals(index, entity.participantIndex)
            assertEquals(TEST_PARTICIPANT_ADDRESSES[index], entity.participantAddress)
        }
    }

    @Test
    fun `EXPECT empty participant entities WHEN participant addresses is empty`() {
        val localAccount = LocalAccount.Joint(
            algoAddress = TEST_ADDRESS,
            participantAddresses = emptyList(),
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION
        )

        val result = mapper(localAccount)

        assertEquals(TEST_ADDRESS, result.jointEntity.algoAddress)
        assertEquals(0, result.participantEntities.size)
    }

    @Test
    fun `EXPECT single participant entity WHEN participant addresses has one element`() {
        val singleAddress = "SINGLE_ADDR"
        val localAccount = LocalAccount.Joint(
            algoAddress = TEST_ADDRESS,
            participantAddresses = listOf(singleAddress),
            threshold = 1,
            version = TEST_VERSION
        )

        val result = mapper(localAccount)

        assertEquals(1, result.participantEntities.size)
        assertEquals(TEST_ADDRESS, result.participantEntities[0].jointAddress)
        assertEquals(0, result.participantEntities[0].participantIndex)
        assertEquals(singleAddress, result.participantEntities[0].participantAddress)
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
    }
}
