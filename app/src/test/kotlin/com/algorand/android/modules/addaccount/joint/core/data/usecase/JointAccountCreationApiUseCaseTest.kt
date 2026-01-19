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
import com.algorand.wallet.jointaccount.creation.data.mapper.CreateJointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.model.CreateJointAccountRequest
import com.algorand.wallet.jointaccount.creation.data.model.JointAccountResponse
import com.algorand.wallet.jointaccount.creation.domain.model.CreateJointAccountDTO
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccountDTO
import com.algorand.wallet.jointaccount.data.service.JointAccountApiService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

internal class JointAccountCreationApiUseCaseTest {

    private val jointAccountApiService: JointAccountApiService = mockk()
    private val peraApiErrorHandler: RetrofitErrorHandler = mockk()
    private val createJointAccountDTOMapper: CreateJointAccountDTOMapper = mockk()
    private val jointAccountDTOMapper: JointAccountDTOMapper = mockk()

    private val sut = JointAccountCreationApiUseCase(
        jointAccountApiService = jointAccountApiService,
        peraApiErrorHandler = peraApiErrorHandler,
        createJointAccountDTOMapper = createJointAccountDTOMapper,
        jointAccountDTOMapper = jointAccountDTOMapper
    )

    @Test
    fun `EXPECT success WHEN api call succeeds and mapping succeeds`() = runTest {
        val createDTO = mockk<CreateJointAccountDTO>()
        val request = mockk<CreateJointAccountRequest>()
        val apiResponse = mockk<JointAccountResponse>()
        val expectedDTO = mockk<JointAccountDTO>()

        every { createJointAccountDTOMapper.mapToCreateJointAccountRequest(createDTO) } returns request
        coEvery { jointAccountApiService.createJointAccount(request) } returns Response.success(apiResponse)
        every { jointAccountDTOMapper.mapToJointAccountDTO(apiResponse) } returns expectedDTO

        val result = sut(createDTO)

        assertTrue(result is Result.Success)
        assertEquals(expectedDTO, (result as Result.Success).data)
    }

    @Test
    fun `EXPECT error WHEN mapping fails`() = runTest {
        val createDTO = mockk<CreateJointAccountDTO>()
        val request = mockk<CreateJointAccountRequest>()
        val apiResponse = mockk<JointAccountResponse>()

        every { createJointAccountDTOMapper.mapToCreateJointAccountRequest(createDTO) } returns request
        coEvery { jointAccountApiService.createJointAccount(request) } returns Response.success(apiResponse)
        every { jointAccountDTOMapper.mapToJointAccountDTO(apiResponse) } returns null

        val result = sut(createDTO)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `EXPECT request to be mapped from DTO`() = runTest {
        val createDTO = mockk<CreateJointAccountDTO>()
        val request = mockk<CreateJointAccountRequest>()
        val apiResponse = mockk<JointAccountResponse>()
        val expectedDTO = mockk<JointAccountDTO>()

        every { createJointAccountDTOMapper.mapToCreateJointAccountRequest(createDTO) } returns request
        coEvery { jointAccountApiService.createJointAccount(request) } returns Response.success(apiResponse)
        every { jointAccountDTOMapper.mapToJointAccountDTO(apiResponse) } returns expectedDTO

        sut(createDTO)

        io.mockk.verify { createJointAccountDTOMapper.mapToCreateJointAccountRequest(createDTO) }
    }
}
