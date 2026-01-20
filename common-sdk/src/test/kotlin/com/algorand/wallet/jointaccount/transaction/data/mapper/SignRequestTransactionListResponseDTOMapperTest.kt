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

import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestTransactionListResponseDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class SignRequestTransactionListResponseDTOMapperTest {

    private val mapper = SignRequestTransactionListResponseDTOMapper()

    private val testAddress = "PARTICIPANT_ADDRESS"
    private val testDeviceId = "device_123"
    private val testSignatures = listOf(listOf("sig_1", "sig_2"), listOf("sig_3", null))

    @Test
    fun `EXPECT signed response type WHEN response is SIGNED`() {
        val dto = createTestDTO(response = SignRequestResponseType.SIGNED)

        val result = mapper.mapToSignRequestTransactionListResponseRequest(dto)

        assertEquals("signed", result.response)
    }

    @Test
    fun `EXPECT declined response type WHEN response is DECLINED`() {
        val dto = createTestDTO(response = SignRequestResponseType.DECLINED)

        val result = mapper.mapToSignRequestTransactionListResponseRequest(dto)

        assertEquals("declined", result.response)
    }

    @Test
    fun `EXPECT rejected response type WHEN response is REJECTED`() {
        val dto = createTestDTO(response = SignRequestResponseType.REJECTED)

        val result = mapper.mapToSignRequestTransactionListResponseRequest(dto)

        assertEquals("rejected", result.response)
    }

    @Test
    fun `EXPECT signatures to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToSignRequestTransactionListResponseRequest(dto)

        assertEquals(testSignatures, result.signatures)
    }

    @Test
    fun `EXPECT device id to be mapped correctly`() {
        val dto = createTestDTO()

        val result = mapper.mapToSignRequestTransactionListResponseRequest(dto)

        assertEquals(testDeviceId, result.deviceId)
    }

    @Test
    fun `EXPECT null signatures WHEN signatures is null`() {
        val dto = SignRequestTransactionListResponseDTO(
            address = testAddress,
            response = SignRequestResponseType.SIGNED,
            signatures = null,
            deviceId = testDeviceId
        )

        val result = mapper.mapToSignRequestTransactionListResponseRequest(dto)

        assertNull(result.signatures)
    }

    @Test
    fun `EXPECT null device id WHEN device id is null`() {
        val dto = SignRequestTransactionListResponseDTO(
            address = testAddress,
            response = SignRequestResponseType.SIGNED,
            signatures = testSignatures,
            deviceId = null
        )

        val result = mapper.mapToSignRequestTransactionListResponseRequest(dto)

        assertNull(result.deviceId)
    }

    @Test
    fun `EXPECT empty signatures WHEN signatures list is empty`() {
        val dto = SignRequestTransactionListResponseDTO(
            address = testAddress,
            response = SignRequestResponseType.SIGNED,
            signatures = emptyList(),
            deviceId = testDeviceId
        )

        val result = mapper.mapToSignRequestTransactionListResponseRequest(dto)

        assertEquals(emptyList<List<String?>>(), result.signatures)
    }

    @Test
    fun `EXPECT single signature WHEN single signature list is provided`() {
        val singleSignatureList = listOf(listOf("single_sig"))
        val dto = SignRequestTransactionListResponseDTO(
            address = testAddress,
            response = SignRequestResponseType.SIGNED,
            signatures = singleSignatureList,
            deviceId = testDeviceId
        )

        val result = mapper.mapToSignRequestTransactionListResponseRequest(dto)

        assertEquals(singleSignatureList, result.signatures)
    }

    @Test
    fun `EXPECT all null signatures WHEN all signatures in list are null`() {
        val nullSignatures = listOf(listOf<String?>(null, null, null))
        val dto = SignRequestTransactionListResponseDTO(
            address = testAddress,
            response = SignRequestResponseType.SIGNED,
            signatures = nullSignatures,
            deviceId = testDeviceId
        )

        val result = mapper.mapToSignRequestTransactionListResponseRequest(dto)

        assertEquals(nullSignatures, result.signatures)
    }

    private fun createTestDTO(
        response: SignRequestResponseType = SignRequestResponseType.SIGNED
    ): SignRequestTransactionListResponseDTO {
        return SignRequestTransactionListResponseDTO(
            address = testAddress,
            response = response,
            signatures = testSignatures,
            deviceId = testDeviceId
        )
    }
}
