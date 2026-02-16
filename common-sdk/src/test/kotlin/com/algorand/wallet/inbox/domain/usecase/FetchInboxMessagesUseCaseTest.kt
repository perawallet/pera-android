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
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class FetchInboxMessagesUseCaseTest {

    private val repository: InboxApiRepository = mockk()
    private val sut = FetchInboxMessagesUseCase(repository)

    private companion object {
        const val TEST_DEVICE_ID = 12345L
    }

    @Test
    fun `EXPECT success WHEN repository succeeds`() = runTest {
        val testAddresses = listOf("ADDR1", "ADDR2")
        val expectedMessages = createInboxMessages()
        coEvery {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(testAddresses))
        } returns PeraResult.Success(expectedMessages)

        val result = sut(TEST_DEVICE_ID, testAddresses)

        assertTrue(result is PeraResult.Success)
        assertEquals(expectedMessages, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT error WHEN repository fails`() = runTest {
        val testAddresses = listOf("ADDR1")
        val exception = Exception("Network error")
        coEvery {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(testAddresses))
        } returns PeraResult.Error(exception)

        val result = sut(TEST_DEVICE_ID, testAddresses)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT correct parameters passed to repository WHEN multiple addresses provided`() = runTest {
        val testAddresses = listOf("ADDR1", "ADDR2")
        coEvery {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(testAddresses))
        } returns PeraResult.Success(createInboxMessages())

        sut(TEST_DEVICE_ID, testAddresses)

        coVerify {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(testAddresses))
        }
    }

    @Test
    fun `EXPECT correct parameters passed to repository WHEN empty address list provided`() = runTest {
        val testAddresses = emptyList<String>()
        coEvery {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(testAddresses))
        } returns PeraResult.Success(createInboxMessages())

        sut(TEST_DEVICE_ID, testAddresses)

        coVerify {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(testAddresses))
        }
    }

    private fun createInboxMessages() = InboxMessages(
        jointAccountImportRequests = emptyList(),
        assetInboxes = emptyList(),
        jointAccountSignRequests = emptyList()
    )
}
