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

package com.algorand.android.modules.addaccount.joint.core.data.usecase

import com.algorand.android.exceptions.RetrofitErrorHandler
import com.algorand.android.models.Result
import com.algorand.wallet.jointaccount.data.service.JointAccountApiService
import com.algorand.wallet.jointaccount.transaction.data.mapper.JointSignRequestDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.ProposeJointSignRequestDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.SearchSignRequestsDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.mapper.SignRequestTransactionListResponseDTOMapper
import com.algorand.wallet.jointaccount.transaction.data.model.JointSignRequestResponse
import com.algorand.wallet.jointaccount.transaction.data.model.ProposeJointSignRequestRequest
import com.algorand.wallet.jointaccount.transaction.data.model.SearchSignRequestsRequest
import com.algorand.wallet.jointaccount.transaction.data.model.SignRequestTransactionListResponseRequest
import com.algorand.wallet.jointaccount.transaction.domain.model.JointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.ProposeJointSignRequestDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SearchSignRequestsDTO
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestTransactionListResponseDTO
import com.algorand.wallet.jointaccount.transaction.data.model.SearchSignRequestsResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

internal class JointSignRequestApiUseCaseTest {

    private val jointAccountApiService: JointAccountApiService = mockk()
    private val peraApiErrorHandler: RetrofitErrorHandler = mockk()
    private val proposeJointSignRequestDTOMapper: ProposeJointSignRequestDTOMapper = mockk()
    private val jointSignRequestDTOMapper: JointSignRequestDTOMapper = mockk()
    private val signRequestTransactionListResponseDTOMapper: SignRequestTransactionListResponseDTOMapper = mockk()
    private val searchSignRequestsDTOMapper: SearchSignRequestsDTOMapper = mockk()

    private val sut = JointSignRequestApiUseCase(
        jointAccountApiService = jointAccountApiService,
        peraApiErrorHandler = peraApiErrorHandler,
        proposeJointSignRequestDTOMapper = proposeJointSignRequestDTOMapper,
        jointSignRequestDTOMapper = jointSignRequestDTOMapper,
        signRequestTransactionListResponseDTOMapper = signRequestTransactionListResponseDTOMapper,
        searchSignRequestsDTOMapper = searchSignRequestsDTOMapper
    )

    @Test
    fun `EXPECT success WHEN proposeSignRequest succeeds`() = runTest {
        val proposeDTO = mockk<ProposeJointSignRequestDTO>()
        val request = mockk<ProposeJointSignRequestRequest>()
        val apiResponse = mockk<JointSignRequestResponse>()
        val expectedDTO = mockk<JointSignRequestDTO>()

        every { proposeJointSignRequestDTOMapper.mapToProposeJointSignRequestRequest(proposeDTO) } returns request
        coEvery { jointAccountApiService.proposeSignRequest(request) } returns Response.success(apiResponse)
        every { jointSignRequestDTOMapper.mapToJointSignRequestDTO(apiResponse) } returns expectedDTO

        val result = sut.proposeSignRequest(proposeDTO)

        assertTrue(result is Result.Success)
        assertEquals(expectedDTO, (result as Result.Success).data)
    }

    @Test
    fun `EXPECT error WHEN proposeSignRequest mapping fails`() = runTest {
        val proposeDTO = mockk<ProposeJointSignRequestDTO>()
        val request = mockk<ProposeJointSignRequestRequest>()
        val apiResponse = mockk<JointSignRequestResponse>()

        every { proposeJointSignRequestDTOMapper.mapToProposeJointSignRequestRequest(proposeDTO) } returns request
        coEvery { jointAccountApiService.proposeSignRequest(request) } returns Response.success(apiResponse)
        every { jointSignRequestDTOMapper.mapToJointSignRequestDTO(apiResponse) } returns null

        val result = sut.proposeSignRequest(proposeDTO)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `EXPECT success WHEN addSignature succeeds`() = runTest {
        val signRequestId = "sign_request_123"
        val responseDTO = mockk<SignRequestTransactionListResponseDTO> {
            every { address } returns TEST_ADDRESS
        }
        val request = mockk<SignRequestTransactionListResponseRequest>()
        val apiResponse = mockk<JointSignRequestResponse>()
        val expectedDTO = mockk<JointSignRequestDTO>()

        every {
            signRequestTransactionListResponseDTOMapper.mapToSignRequestTransactionListResponseRequest(responseDTO)
        } returns request
        coEvery {
            jointAccountApiService.addSignature(signRequestId, TEST_ADDRESS, request)
        } returns Response.success(apiResponse)
        every { jointSignRequestDTOMapper.mapToJointSignRequestDTO(apiResponse) } returns expectedDTO

        val result = sut.addSignature(signRequestId, responseDTO)

        assertTrue(result is Result.Success)
        assertEquals(expectedDTO, (result as Result.Success).data)
    }

    @Test
    fun `EXPECT list WHEN searchSignRequests succeeds`() = runTest {
        val searchDTO = mockk<SearchSignRequestsDTO>()
        val request = mockk<SearchSignRequestsRequest>()
        val responseItem = mockk<JointSignRequestResponse>()
        val searchResponse = SearchSignRequestsResponse(
            count = 1,
            next = null,
            previous = null,
            results = listOf(responseItem)
        )
        val expectedDTO = mockk<JointSignRequestDTO>()

        every { searchSignRequestsDTOMapper.mapToSearchSignRequestsRequest(searchDTO) } returns request
        coEvery { jointAccountApiService.searchSignRequests(request) } returns Response.success(searchResponse)
        every { jointSignRequestDTOMapper.mapToJointSignRequestDTO(responseItem) } returns expectedDTO

        val result = sut.searchSignRequests(searchDTO)

        assertTrue(result is Result.Success)
        assertEquals(1, (result as Result.Success).data.size)
        assertEquals(expectedDTO, result.data[0])
    }

    @Test
    fun `EXPECT empty list WHEN searchSignRequests returns null results`() = runTest {
        val searchDTO = mockk<SearchSignRequestsDTO>()
        val request = mockk<SearchSignRequestsRequest>()
        val searchResponse = SearchSignRequestsResponse(
            count = 0,
            next = null,
            previous = null,
            results = null
        )

        every { searchSignRequestsDTOMapper.mapToSearchSignRequestsRequest(searchDTO) } returns request
        coEvery { jointAccountApiService.searchSignRequests(request) } returns Response.success(searchResponse)

        val result = sut.searchSignRequests(searchDTO)

        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    private companion object {
        const val TEST_ADDRESS = "TEST_ADDRESS_123"
    }
}
