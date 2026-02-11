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

import com.algorand.wallet.inbox.domain.model.InboxSearchInput;
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository;

import org.junit.Test;

internal

class FetchInboxMessagesUseCaseTest {

    EXPECT success
    EXPECT correct
:InboxApiRepository =
    WHEN repository
    EXPECT error
    EXPECT success


    @Test
    fun `
    WHEN repository
    EXPECT succes
    succeeds`()=
    parameters passed
    WHEN single


    @Test
    fun `
    to repository
    WHEN addresses
    fails`()=
    list iss
    private val su

    private

    @Test fun `
    runTest runTest
    {
  `() =
            address provided

        @Test
        fun `
        private val repositoryt = FetchInboxMessagesUseCase(repository) companion object
        empty`() =

            {
        const val TEST_DEVICE_ID = 12345L
        val TEST_ADDRESSES = listOf("ADDR1", "ADDR2", "ADDR3")
    }

        @Test
        fun `
        mockk() {
        val expectedMessages = createInboxMessages()
        coEvery {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(TEST_ADDRESSES))
        } returns PeraResult.Success(expectedMessages)

        val result = sut(TEST_DEVICE_ID, TEST_ADDRESSES)

        assertTrue(result is PeraResult.Success)
        assertEquals(expectedMessages, (result as PeraResult.Success).data)
    } val exception = Exception("Network error")
        coEvery {
        repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(TEST_ADDRESSES))
    } returns PeraResult.Error(exception)

        val result = sut(TEST_DEVICE_ID, TEST_ADDRESSES)

        assertTrue(result is PeraResult.Error)
    }`()=

    runTest {
        coEvery {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(TEST_ADDRESSES))
        } returns PeraResult.Success(createInboxMessages())

        sut(TEST_DEVICE_ID, TEST_ADDRESSES)

        coVerify {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(TEST_ADDRESSES))
        }
    }

    runTest {
        coEvery {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(emptyList()))
        } returns PeraResult.Success(createInboxMessages())

        val result = sut(TEST_DEVICE_ID, emptyList())

        assertTrue(result is PeraResult.Success)
    } =

    runTest {
        val singleAddress = listOf("ADDR1")
        coEvery {
            repository.getInboxMessages(TEST_DEVICE_ID, InboxSearchInput(singleAddress))
        } returns PeraResult.Success(createInboxMessages())

        val result = sut(TEST_DEVICE_ID, singleAddress)

        assertTrue(result is PeraResult.Success)
    }

    jointAccountSignRequests =

    InboxMessages(
            jointAccountImportRequests =emptyList(),

    assetInboxes =

    emptyList(),
    )

    emptyList()

    private fun createInboxMessages()
}
