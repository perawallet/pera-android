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
import com.algorand.wallet.account.local.data.database.model.JointParticipantEntity
import com.algorand.wallet.account.local.data.database.model.JointWithParticipants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class JointMapperImplTest {

    private val mapper = JointMapperImpl()

    @Test
    fun `EXPECT all fields mapped correctly WHEN invoke is called`() {
        val jointWithParticipants = createTestJointWithParticipants()

        val localAccount = mapper(jointWithParticipants)

        assertEquals(TEST_ADDRESS, localAccount.algoAddress)
        assertEquals(TEST_PARTICIPANT_ADDRESSES, localAccount.participantAddresses)
        assertEquals(TEST_THRESHOLD, localAccount.threshold)
        assertEquals(TEST_VERSION, localAccount.version)
    }

    @Test
    fun `EXPECT participants sorted by index WHEN invoke is called with unordered participants`() {
        val unorderedParticipants = listOf(
            JointParticipantEntity(TEST_ADDRESS, 2, "ADDR3"),
            JointParticipantEntity(TEST_ADDRESS, 0, "ADDR1"),
            JointParticipantEntity(TEST_ADDRESS, 1, "ADDR2")
        )
        val jointWithParticipants = JointWithParticipants(
            joint = TEST_ENTITY,
            participants = unorderedParticipants
        )

        val localAccount = mapper(jointWithParticipants)

        assertEquals(listOf("ADDR1", "ADDR2", "ADDR3"), localAccount.participantAddresses)
    }

    @Test
    fun `EXPECT empty list WHEN participant list is empty`() {
        val jointWithParticipants = JointWithParticipants(
            joint = TEST_ENTITY,
            participants = emptyList()
        )

        val localAccount = mapper(jointWithParticipants)

        assertTrue(localAccount.participantAddresses.isEmpty())
    }

    @Test
    fun `EXPECT single address list WHEN participant list has one element`() {
        val singleAddress = "SINGLE_ADDR"
        val jointWithParticipants = JointWithParticipants(
            joint = JointEntity(
                algoAddress = TEST_ADDRESS,
                threshold = 1,
                version = TEST_VERSION
            ),
            participants = listOf(
                JointParticipantEntity(TEST_ADDRESS, 0, singleAddress)
            )
        )

        val localAccount = mapper(jointWithParticipants)

        assertEquals(listOf(singleAddress), localAccount.participantAddresses)
    }

    private fun createTestJointWithParticipants() = JointWithParticipants(
        joint = TEST_ENTITY,
        participants = TEST_PARTICIPANT_ENTITIES
    )

    private companion object {
        const val TEST_ADDRESS = "JOINT_ADDRESS_123"
        val TEST_PARTICIPANT_ADDRESSES = listOf("ADDR1", "ADDR2", "ADDR3")
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1

        val TEST_ENTITY = JointEntity(
            algoAddress = TEST_ADDRESS,
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION
        )

        val TEST_PARTICIPANT_ENTITIES = listOf(
            JointParticipantEntity(TEST_ADDRESS, 0, "ADDR1"),
            JointParticipantEntity(TEST_ADDRESS, 1, "ADDR2"),
            JointParticipantEntity(TEST_ADDRESS, 2, "ADDR3")
        )
    }
}
