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

package com.algorand.wallet.inbox.domain.usecase

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.model.InboxSearchInput
import com.algorand.wallet.jointaccount.domain.repository.JointAccountRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class FetchInboxMessagesUseCaseTest {

    private val repository: JointAccountRepository = mockk()
    private val sut = FetchInboxMessagesUseCase(repository)

    @Test
    fun `EXPECT success WHEN repository succeeds`() = runTest {
        val expectedMessages = createInboxMessages()
        coEvery { repository.getInboxMessages(TEST_DEVICE_ID, any()) } returns PeraResult.Success(expectedMessages)

        val result = sut(TEST_DEVICE_ID, TEST_ADDRESSES)

        assertTrue(result is PeraResult.Success)
        assertEquals(expectedMessages, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT error WHEN repository fails`() = runTest {
        val exception = Exception("Network error")
        coEvery { repository.getInboxMessages(TEST_DEVICE_ID, any()) } returns PeraResult.Error(exception)

        val result = sut(TEST_DEVICE_ID, TEST_ADDRESSES)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT correct device id passed to repository`() = runTest {
        coEvery { repository.getInboxMessages(any(), any()) } returns PeraResult.Success(createInboxMessages())

        sut(TEST_DEVICE_ID, TEST_ADDRESSES)

        coVerify { repository.getInboxMessages(TEST_DEVICE_ID, any()) }
    }

    @Test
    fun `EXPECT addresses wrapped in InboxSearchInput`() = runTest {
        val inputSlot = slot<InboxSearchInput>()
        coEvery { repository.getInboxMessages(any(), capture(inputSlot)) } returns PeraResult.Success(createInboxMessages())

        sut(TEST_DEVICE_ID, TEST_ADDRESSES)

        assertEquals(TEST_ADDRESSES, inputSlot.captured.addresses)
    }

    @Test
    fun `EXPECT success WHEN addresses list is empty`() = runTest {
        coEvery { repository.getInboxMessages(any(), any()) } returns PeraResult.Success(createInboxMessages())

        val result = sut(TEST_DEVICE_ID, emptyList())

        assertTrue(result is PeraResult.Success)
    }

    @Test
    fun `EXPECT success WHEN single address provided`() = runTest {
        val singleAddress = listOf("ADDR1")
        coEvery { repository.getInboxMessages(any(), any()) } returns PeraResult.Success(createInboxMessages())

        val result = sut(TEST_DEVICE_ID, singleAddress)

        assertTrue(result is PeraResult.Success)
    }

    private fun createInboxMessages() = InboxMessages(
        jointAccountImportRequests = emptyList(),
        jointAccountSignRequests = emptyList(),
        assetInboxes = emptyList()
    )

    private companion object {
        const val TEST_DEVICE_ID = 12345L
        val TEST_ADDRESSES = listOf("ADDR1", "ADDR2", "ADDR3")
    }
}
