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

import com.algorand.wallet.jointaccount.transaction.domain.model.AddSignatureInput
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class AddSignatureInputMapperTest {

    private val mapper = AddSignatureInputMapper()

    @Test
    fun `EXPECT signed response type WHEN response is SIGNED`() {
        val input = createTestInput(response = SignRequestResponseType.SIGNED)

        val result = mapper.mapToSignRequestTransactionListResponseRequest(input)

        assertEquals("signed", result.response)
    }

    @Test
    fun `EXPECT declined response type WHEN response is DECLINED`() {
        val input = createTestInput(response = SignRequestResponseType.DECLINED)

        val result = mapper.mapToSignRequestTransactionListResponseRequest(input)

        assertEquals("declined", result.response)
    }

    @Test
    fun `EXPECT rejected response type WHEN response is REJECTED`() {
        val input = createTestInput(response = SignRequestResponseType.REJECTED)

        val result = mapper.mapToSignRequestTransactionListResponseRequest(input)

        assertEquals("rejected", result.response)
    }

    @Test
    fun `EXPECT address signatures and device id to be mapped correctly`() {
        val input = createTestInput()

        val result = mapper.mapToSignRequestTransactionListResponseRequest(input)

        assertEquals(TEST_ADDRESS, result.address)
        assertEquals(TEST_SIGNATURES, result.signatures)
        assertEquals(TEST_DEVICE_ID, result.deviceId)
    }

    @Test
    fun `EXPECT null signatures WHEN signatures is null`() {
        val input = AddSignatureInput(
            address = TEST_ADDRESS,
            response = SignRequestResponseType.SIGNED,
            signatures = null,
            deviceId = TEST_DEVICE_ID
        )

        val result = mapper.mapToSignRequestTransactionListResponseRequest(input)

        assertNull(result.signatures)
    }

    @Test
    fun `EXPECT null device id WHEN device id is null`() {
        val input = createTestInput().copy(deviceId = null)

        val result = mapper.mapToSignRequestTransactionListResponseRequest(input)

        assertNull(result.deviceId)
    }

    @Test
    fun `EXPECT empty signatures WHEN signatures list is empty`() {
        val input = createTestInput().copy(signatures = emptyList())

        val result = mapper.mapToSignRequestTransactionListResponseRequest(input)

        assertEquals(emptyList<List<String?>>(), result.signatures)
    }

    private fun createTestInput(
        response: SignRequestResponseType = SignRequestResponseType.SIGNED
    ) = AddSignatureInput(
        address = TEST_ADDRESS,
        response = response,
        signatures = TEST_SIGNATURES,
        deviceId = TEST_DEVICE_ID
    )

    private companion object {
        const val TEST_ADDRESS = "PARTICIPANT_ADDRESS"
        const val TEST_DEVICE_ID = "device_123"
        val TEST_SIGNATURES = listOf(listOf("sig_1", "sig_2"), listOf("sig_3", null))
    }
}
