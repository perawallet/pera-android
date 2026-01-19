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
import com.algorand.wallet.inbox.domain.model.InboxMessagesDTO
import com.algorand.wallet.inbox.domain.model.InboxSearchDTO
import com.algorand.wallet.inbox.jointaccount.data.mapper.InboxSearchDTOMapper
import com.algorand.wallet.inbox.jointaccount.data.model.InboxSearchRequest
import com.algorand.wallet.inbox.jointaccount.data.model.InboxSearchResponse
import com.algorand.wallet.inbox.jointaccount.data.service.InboxApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

internal class JointAccountInboxApiUseCaseTest {

    private val inboxApiService: InboxApiService = mockk()
    private val peraApiErrorHandler: RetrofitErrorHandler = mockk()
    private val inboxSearchDTOMapper: InboxSearchDTOMapper = mockk()

    private val sut = JointAccountInboxApiUseCase(
        inboxApiService = inboxApiService,
        peraApiErrorHandler = peraApiErrorHandler,
        inboxSearchDTOMapper = inboxSearchDTOMapper
    )

    @Test
    fun `EXPECT success WHEN api call succeeds and mapping succeeds`() = runTest {
        val inboxSearchDTO = InboxSearchDTO(addresses = listOf("ADDR1"))
        val request = mockk<InboxSearchRequest>()
        val apiResponse = mockk<InboxSearchResponse>()
        val expectedDTO = mockk<InboxMessagesDTO>()

        every { inboxSearchDTOMapper.mapToInboxSearchRequest(inboxSearchDTO) } returns request
        coEvery { inboxApiService.getInboxMessages(TEST_DEVICE_ID, request) } returns Response.success(apiResponse)
        every { inboxSearchDTOMapper.mapToInboxMessagesDTO(apiResponse) } returns expectedDTO

        val result = sut.getInboxMessages(TEST_DEVICE_ID, inboxSearchDTO)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `EXPECT error WHEN mapping returns null`() = runTest {
        val inboxSearchDTO = InboxSearchDTO(addresses = listOf("ADDR1"))
        val request = mockk<InboxSearchRequest>()
        val apiResponse = mockk<InboxSearchResponse>()

        every { inboxSearchDTOMapper.mapToInboxSearchRequest(inboxSearchDTO) } returns request
        coEvery { inboxApiService.getInboxMessages(TEST_DEVICE_ID, request) } returns Response.success(apiResponse)
        every { inboxSearchDTOMapper.mapToInboxMessagesDTO(apiResponse) } returns null

        val result = sut.getInboxMessages(TEST_DEVICE_ID, inboxSearchDTO)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `EXPECT delete notification called with correct parameters`() = runTest {
        coEvery {
            inboxApiService.deleteInboxJointInvitationNotification(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)
        } returns Response.success(Unit)

        sut.deleteInboxJointInvitationNotification(TEST_DEVICE_ID, TEST_JOINT_ADDRESS)

        coVerify { inboxApiService.deleteInboxJointInvitationNotification(TEST_DEVICE_ID, TEST_JOINT_ADDRESS) }
    }

    private companion object {
        const val TEST_DEVICE_ID = 12345L
        const val TEST_JOINT_ADDRESS = "JOINT_ADDRESS_123"
    }
}
