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

package com.algorand.wallet.jointaccount.transaction.data.mapper

import com.algorand.wallet.jointaccount.transaction.domain.model.SearchSignRequestsDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class SearchSignRequestsDTOMapperTest {

    private val mapper = SearchSignRequestsDTOMapper()

    private val testDeviceId = 12345L
    private val testSignRequestId = "sign_request_123"
    private val testParticipantAddresses = listOf("ADDR1", "ADDR2")
    private val testStatuses = listOf("pending", "ready")
    private val testJointAccountAddresses = listOf("JOINT_ADDR1", "JOINT_ADDR2")

    @Test
    fun `EXPECT device id to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToSearchSignRequestsRequest(dto)

        assertEquals(testDeviceId, result.deviceId)
    }

    @Test
    fun `EXPECT sign request id to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToSearchSignRequestsRequest(dto)

        assertEquals(testSignRequestId, result.signRequestId)
    }

    @Test
    fun `EXPECT participant addresses to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToSearchSignRequestsRequest(dto)

        assertEquals(testParticipantAddresses, result.participantAddresses)
    }

    @Test
    fun `EXPECT statuses to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToSearchSignRequestsRequest(dto)

        assertEquals(testStatuses, result.statuses)
    }

    @Test
    fun `EXPECT joint account addresses to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToSearchSignRequestsRequest(dto)

        assertEquals(testJointAccountAddresses, result.jointAccountAddress)
    }

    @Test
    fun `EXPECT null fields WHEN optional fields are null`() {
        val dto = SearchSignRequestsDTO(
            deviceId = testDeviceId,
            signRequestId = null,
            participantAddresses = null,
            statuses = null,
            jointAccountAddress = null
        )

        val result = mapper.mapToSearchSignRequestsRequest(dto)

        assertEquals(testDeviceId, result.deviceId)
        assertNull(result.signRequestId)
        assertNull(result.participantAddresses)
        assertNull(result.statuses)
        assertNull(result.jointAccountAddress)
    }

    @Test
    fun `EXPECT empty lists WHEN lists are empty`() {
        val dto = SearchSignRequestsDTO(
            deviceId = testDeviceId,
            signRequestId = testSignRequestId,
            participantAddresses = emptyList(),
            statuses = emptyList(),
            jointAccountAddress = emptyList()
        )

        val result = mapper.mapToSearchSignRequestsRequest(dto)

        assertEquals(emptyList<String>(), result.participantAddresses)
        assertEquals(emptyList<String>(), result.statuses)
        assertEquals(emptyList<String>(), result.jointAccountAddress)
    }

    @Test
    fun `EXPECT single address WHEN single participant address is provided`() {
        val dto = SearchSignRequestsDTO(
            deviceId = testDeviceId,
            signRequestId = testSignRequestId,
            participantAddresses = listOf("SINGLE_ADDR"),
            statuses = null,
            jointAccountAddress = null
        )

        val result = mapper.mapToSearchSignRequestsRequest(dto)

        assertEquals(listOf("SINGLE_ADDR"), result.participantAddresses)
    }

    @Test
    fun `EXPECT only device id and sign request id WHEN only those are provided`() {
        val dto = SearchSignRequestsDTO(
            deviceId = testDeviceId,
            signRequestId = testSignRequestId
        )

        val result = mapper.mapToSearchSignRequestsRequest(dto)

        assertEquals(testDeviceId, result.deviceId)
        assertEquals(testSignRequestId, result.signRequestId)
        assertNull(result.participantAddresses)
        assertNull(result.statuses)
        assertNull(result.jointAccountAddress)
    }

    private fun createTestDTO(): SearchSignRequestsDTO {
        return SearchSignRequestsDTO(
            deviceId = testDeviceId,
            signRequestId = testSignRequestId,
            participantAddresses = testParticipantAddresses,
            statuses = testStatuses,
            jointAccountAddress = testJointAccountAddresses
        )
    }
}
