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

package com.algorand.wallet.jointaccount.creation.data.mapper

import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountDTO
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CreateJointAccountDTOMapperTest {

    private val mapper = CreateJointAccountDTOMapperImpl()

    @Test
    fun `EXPECT all fields mapped correctly WHEN mapToCreateJointAccountRequest is called`() {
        val dto = createTestDTO()

        val result = mapper.mapToCreateJointAccountRequest(dto)

        assertEquals(TEST_PARTICIPANT_ADDRESSES, result.participantAddresses)
        assertEquals(TEST_THRESHOLD, result.threshold)
        assertEquals(TEST_VERSION, result.version)
    }

    @Test
    fun `EXPECT empty participant list WHEN participant addresses is empty`() {
        val dto = CreateJointAccountDTO(
            participantAddresses = emptyList(),
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION
        )

        val result = mapper.mapToCreateJointAccountRequest(dto)

        assertEquals(emptyList<String>(), result.participantAddresses)
    }

    @Test
    fun `EXPECT single participant WHEN participant addresses has one element`() {
        val dto = CreateJointAccountDTO(
            participantAddresses = listOf(SINGLE_ADDRESS),
            threshold = 1,
            version = TEST_VERSION
        )

        val result = mapper.mapToCreateJointAccountRequest(dto)

        assertEquals(listOf(SINGLE_ADDRESS), result.participantAddresses)
    }

    @Test
    fun `EXPECT order preserved WHEN mapping participant addresses`() {
        val orderedAddresses = listOf("FIRST", "SECOND", "THIRD")
        val dto = CreateJointAccountDTO(
            participantAddresses = orderedAddresses,
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION
        )

        val result = mapper.mapToCreateJointAccountRequest(dto)

        assertEquals("FIRST", result.participantAddresses[0])
        assertEquals("SECOND", result.participantAddresses[1])
        assertEquals("THIRD", result.participantAddresses[2])
    }

    @Test
    fun `EXPECT many participants mapped WHEN participant list is large`() {
        val manyParticipants = (1..10).map { "ADDR$it" }
        val dto = CreateJointAccountDTO(
            participantAddresses = manyParticipants,
            threshold = 5,
            version = TEST_VERSION
        )

        val result = mapper.mapToCreateJointAccountRequest(dto)

        assertEquals(manyParticipants, result.participantAddresses)
        assertEquals(10, result.participantAddresses.size)
    }

    private fun createTestDTO() = CreateJointAccountDTO(
        participantAddresses = TEST_PARTICIPANT_ADDRESSES,
        threshold = TEST_THRESHOLD,
        version = TEST_VERSION
    )

    private companion object {
        val TEST_PARTICIPANT_ADDRESSES = listOf("ADDR1", "ADDR2", "ADDR3")
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1
        const val SINGLE_ADDRESS = "SINGLE_ADDR"
    }
}
