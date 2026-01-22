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

import com.algorand.wallet.jointaccount.transaction.domain.model.SearchSignRequestsInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class SearchSignRequestsInputMapperTest {

    private val mapper = SearchSignRequestsInputMapper()

    @Test
    fun `EXPECT all fields to be mapped correctly`() {
        val input = createTestInput()

        val result = mapper.mapToSearchSignRequestsRequest(input)

        assertEquals(TEST_DEVICE_ID, result.deviceId)
        assertEquals(TEST_SIGN_REQUEST_ID, result.signRequestId)
        assertEquals(TEST_PARTICIPANT_ADDRESSES, result.participantAddresses)
        assertEquals(TEST_STATUSES, result.statuses)
        assertEquals(TEST_JOINT_ACCOUNT_ADDRESSES, result.jointAccountAddress)
    }

    @Test
    fun `EXPECT null fields WHEN optional fields are null`() {
        val input = SearchSignRequestsInput(
            deviceId = TEST_DEVICE_ID,
            signRequestId = null,
            participantAddresses = null,
            statuses = null,
            jointAccountAddress = null
        )

        val result = mapper.mapToSearchSignRequestsRequest(input)

        assertEquals(TEST_DEVICE_ID, result.deviceId)
        assertNull(result.signRequestId)
        assertNull(result.participantAddresses)
        assertNull(result.statuses)
        assertNull(result.jointAccountAddress)
    }

    @Test
    fun `EXPECT empty lists WHEN lists are empty`() {
        val input = createTestInput().copy(
            participantAddresses = emptyList(),
            statuses = emptyList(),
            jointAccountAddress = emptyList()
        )

        val result = mapper.mapToSearchSignRequestsRequest(input)

        assertEquals(emptyList<String>(), result.participantAddresses)
        assertEquals(emptyList<String>(), result.statuses)
        assertEquals(emptyList<String>(), result.jointAccountAddress)
    }

    @Test
    fun `EXPECT only device id WHEN only device id is provided`() {
        val input = SearchSignRequestsInput(deviceId = TEST_DEVICE_ID)

        val result = mapper.mapToSearchSignRequestsRequest(input)

        assertEquals(TEST_DEVICE_ID, result.deviceId)
        assertNull(result.signRequestId)
        assertNull(result.participantAddresses)
        assertNull(result.statuses)
        assertNull(result.jointAccountAddress)
    }

    private fun createTestInput() = SearchSignRequestsInput(
        deviceId = TEST_DEVICE_ID,
        signRequestId = TEST_SIGN_REQUEST_ID,
        participantAddresses = TEST_PARTICIPANT_ADDRESSES,
        statuses = TEST_STATUSES,
        jointAccountAddress = TEST_JOINT_ACCOUNT_ADDRESSES
    )

    private companion object {
        const val TEST_DEVICE_ID = 12345L
        const val TEST_SIGN_REQUEST_ID = "sign_request_123"
        val TEST_PARTICIPANT_ADDRESSES = listOf("ADDR1", "ADDR2")
        val TEST_STATUSES = listOf("pending", "ready")
        val TEST_JOINT_ACCOUNT_ADDRESSES = listOf("JOINT_ADDR1", "JOINT_ADDR2")
    }
}
