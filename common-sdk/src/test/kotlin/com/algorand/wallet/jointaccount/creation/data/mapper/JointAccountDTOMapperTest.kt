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

import com.algorand.wallet.jointaccount.creation.data.model.JointAccountResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

internal class JointAccountDTOMapperTest {

    private val mapper = JointAccountDTOMapper()

    @Test
    fun `EXPECT null WHEN response is null`() {
        val result = mapper.mapToJointAccountDTO(null)

        assertNull(result)
    }

    @Test
    fun `EXPECT all fields mapped correctly WHEN response has all fields`() {
        val response = createTestResponse()

        val result = mapper.mapToJointAccountDTO(response)

        assertNotNull(result)
        assertEquals(TEST_CREATION_DATETIME, result?.creationDatetime)
        assertEquals(TEST_ADDRESS, result?.address)
        assertEquals(TEST_VERSION, result?.version)
        assertEquals(TEST_THRESHOLD, result?.threshold)
        assertEquals(TEST_PARTICIPANT_ADDRESSES, result?.participantAddresses)
    }

    @Test
    fun `EXPECT all null fields WHEN response has null fields`() {
        val response = JointAccountResponse(
            creationDatetime = null,
            address = null,
            version = null,
            threshold = null,
            participantAddresses = null
        )

        val result = mapper.mapToJointAccountDTO(response)

        assertNotNull(result)
        assertNull(result?.creationDatetime)
        assertNull(result?.address)
        assertNull(result?.version)
        assertNull(result?.threshold)
        assertNull(result?.participantAddresses)
    }

    @Test
    fun `EXPECT empty participant list WHEN response has empty participant addresses`() {
        val response = createTestResponse().copy(participantAddresses = emptyList())

        val result = mapper.mapToJointAccountDTO(response)

        assertEquals(emptyList<String>(), result?.participantAddresses)
    }

    @Test
    fun `EXPECT single participant WHEN response has single participant address`() {
        val response = createTestResponse().copy(
            participantAddresses = listOf(SINGLE_ADDRESS),
            threshold = 1
        )

        val result = mapper.mapToJointAccountDTO(response)

        assertEquals(listOf(SINGLE_ADDRESS), result?.participantAddresses)
    }

    private fun createTestResponse() = JointAccountResponse(
        creationDatetime = TEST_CREATION_DATETIME,
        address = TEST_ADDRESS,
        version = TEST_VERSION,
        threshold = TEST_THRESHOLD,
        participantAddresses = TEST_PARTICIPANT_ADDRESSES
    )

    private companion object {
        const val TEST_CREATION_DATETIME = "2024-01-01T00:00:00Z"
        const val TEST_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_VERSION = 1
        const val TEST_THRESHOLD = 2
        val TEST_PARTICIPANT_ADDRESSES = listOf("ADDR1", "ADDR2", "ADDR3")
        const val SINGLE_ADDRESS = "SINGLE_ADDR"
    }
}
