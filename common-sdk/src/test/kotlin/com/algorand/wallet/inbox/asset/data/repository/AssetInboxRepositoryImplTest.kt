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

package com.algorand.wallet.inbox.asset.data.repository

import com.algorand.test.test
import com.algorand.wallet.inbox.domain.model.AssetInbox
import com.algorand.wallet.inbox.domain.model.InboxMessages
import com.algorand.wallet.inbox.domain.repository.InboxRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AssetInboxRepositoryImplTest {

    private val inboxRepository: InboxRepository = mockk(relaxed = true)

    private val assetInboxRepositoryImpl = AssetInboxRepositoryImpl(inboxRepository)

    @Test
    fun `EXPECT request count flow to return sum of request counts`(): TestResult = runTest {
        val inboxMessagesFlow = MutableStateFlow<InboxMessages?>(
            InboxMessages(
                jointAccountImportRequests = null,
                jointAccountSignRequests = null,
                assetInboxes = listOf(
                    AssetInbox(ADDRESS_1, null, 1),
                    AssetInbox(ADDRESS_2, null, 4)
                )
            )
        )
        every { inboxRepository.getInboxMessagesFlow() } returns inboxMessagesFlow

        val result = assetInboxRepositoryImpl.getRequestCountFlow().test()

        result.assertValue(5)
    }

    @Test
    fun `EXPECT zero WHEN asset inboxes is null`(): TestResult = runTest {
        val inboxMessagesFlow = MutableStateFlow<InboxMessages?>(
            InboxMessages(
                jointAccountImportRequests = null,
                jointAccountSignRequests = null,
                assetInboxes = null
            )
        )
        every { inboxRepository.getInboxMessagesFlow() } returns inboxMessagesFlow

        val result = assetInboxRepositoryImpl.getRequestCountFlow().test()

        result.assertValue(0)
    }

    @Test
    fun `EXPECT zero WHEN inbox messages is null`(): TestResult = runTest {
        val inboxMessagesFlow = MutableStateFlow<InboxMessages?>(null)
        every { inboxRepository.getInboxMessagesFlow() } returns inboxMessagesFlow

        val result = assetInboxRepositoryImpl.getRequestCountFlow().test()

        result.assertValue(0)
    }

    @Test
    fun `EXPECT null WHEN getRequest is invoked but requested address is not in inbox`(): TestResult = runTest {
        coEvery { inboxRepository.getInboxMessages() } returns InboxMessages(
            jointAccountImportRequests = null,
            jointAccountSignRequests = null,
            assetInboxes = listOf(AssetInbox(ADDRESS_2, null, 4))
        )

        val result = assetInboxRepositoryImpl.getRequest(ADDRESS_1)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN getRequest is invoked but asset inboxes is null`(): TestResult = runTest {
        coEvery { inboxRepository.getInboxMessages() } returns InboxMessages(
            jointAccountImportRequests = null,
            jointAccountSignRequests = null,
            assetInboxes = null
        )

        val result = assetInboxRepositoryImpl.getRequest(ADDRESS_1)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN getRequest is invoked but inbox messages is null`(): TestResult = runTest {
        coEvery { inboxRepository.getInboxMessages() } returns null

        val result = assetInboxRepositoryImpl.getRequest(ADDRESS_1)

        assertNull(result)
    }

    @Test
    fun `EXPECT request detail WHEN getRequest is invoked and requested address is in inbox`(): TestResult = runTest {
        coEvery { inboxRepository.getInboxMessages() } returns InboxMessages(
            jointAccountImportRequests = null,
            jointAccountSignRequests = null,
            assetInboxes = listOf(
                AssetInbox(ADDRESS_1, null, 1),
                AssetInbox(ADDRESS_2, null, 4)
            )
        )

        val result = assetInboxRepositoryImpl.getRequest(ADDRESS_1)

        assertEquals(ADDRESS_1, result?.address)
        assertEquals(1, result?.requestCount)
    }

    private companion object {
        const val ADDRESS_1 = "address1"
        const val ADDRESS_2 = "address2"
    }
}
