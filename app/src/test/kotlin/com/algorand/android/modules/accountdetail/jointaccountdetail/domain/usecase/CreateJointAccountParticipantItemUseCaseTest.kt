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

package com.algorand.android.modules.accountdetail.jointaccountdetail.domain.usecase

import com.algorand.android.models.User
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.repository.ContactRepository
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class CreateJointAccountParticipantItemUseCaseTest {

    private val getAccountDisplayName: GetAccountDisplayName = mockk()
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview = mockk()
    private val getLocalAccounts: GetLocalAccounts = mockk()
    private val contactRepository: ContactRepository = mockk()

    private val sut = CreateJointAccountParticipantItemUseCase(
        getAccountDisplayName = getAccountDisplayName,
        getAccountIconDrawablePreview = getAccountIconDrawablePreview,
        getLocalAccounts = getLocalAccounts,
        contactRepository = contactRepository
    )

    @Test
    fun `EXPECT local account addresses WHEN getLocalAccountAddresses called`() = runTest {
        val localAccounts = listOf(
            mockk<LocalAccount> { every { algoAddress } returns "ADDR1" },
            mockk<LocalAccount> { every { algoAddress } returns "ADDR2" }
        )
        coEvery { getLocalAccounts() } returns localAccounts

        val result = sut.getLocalAccountAddresses()

        assertEquals(listOf("ADDR1", "ADDR2"), result)
    }

    @Test
    fun `EXPECT participant item with isLocalAccount true WHEN address is local`() = runTest {
        val displayName = mockk<AccountDisplayName> {
            every { primaryDisplayName } returns "Test Account"
            every { secondaryDisplayName } returns "ADDR1...XYZ"
        }
        val iconPreview = mockk<AccountIconDrawablePreview>()

        coEvery { getAccountDisplayName(TEST_ADDRESS) } returns displayName
        coEvery { getAccountIconDrawablePreview(TEST_ADDRESS) } returns iconPreview
        coEvery { contactRepository.getContactByAddress(TEST_ADDRESS) } returns null

        val result = sut(TEST_ADDRESS, listOf(TEST_ADDRESS))

        assertTrue(result.isLocalAccount)
        assertFalse(result.isContact)
    }

    @Test
    fun `EXPECT participant item with isContact true WHEN address is contact but not local`() = runTest {
        val displayName = mockk<AccountDisplayName> {
            every { primaryDisplayName } returns "Contact Name"
            every { secondaryDisplayName } returns "ADDR...XYZ"
        }
        val iconPreview = mockk<AccountIconDrawablePreview>()
        val contact = mockk<User> {
            every { imageUriAsString } returns null
        }

        coEvery { getAccountDisplayName(TEST_ADDRESS) } returns displayName
        coEvery { getAccountIconDrawablePreview(TEST_ADDRESS) } returns iconPreview
        coEvery { contactRepository.getContactByAddress(TEST_ADDRESS) } returns contact

        val result = sut(TEST_ADDRESS, emptyList())

        assertFalse(result.isLocalAccount)
        assertTrue(result.isContact)
    }

    @Test
    fun `EXPECT isContact false WHEN address is both local and contact`() = runTest {
        val displayName = mockk<AccountDisplayName> {
            every { primaryDisplayName } returns "Account Name"
            every { secondaryDisplayName } returns "ADDR...XYZ"
        }
        val iconPreview = mockk<AccountIconDrawablePreview>()
        val contact = mockk<User> {
            every { imageUriAsString } returns null
        }

        coEvery { getAccountDisplayName(TEST_ADDRESS) } returns displayName
        coEvery { getAccountIconDrawablePreview(TEST_ADDRESS) } returns iconPreview
        coEvery { contactRepository.getContactByAddress(TEST_ADDRESS) } returns contact

        val result = sut(TEST_ADDRESS, listOf(TEST_ADDRESS))

        assertTrue(result.isLocalAccount)
        assertFalse(result.isContact)
    }

    @Test
    fun `EXPECT imageUri null WHEN contact has no image`() = runTest {
        val displayName = mockk<AccountDisplayName> {
            every { primaryDisplayName } returns "Contact Name"
            every { secondaryDisplayName } returns "ADDR...XYZ"
        }
        val iconPreview = mockk<AccountIconDrawablePreview>()
        val contact = mockk<User> {
            every { imageUriAsString } returns null
        }

        coEvery { getAccountDisplayName(TEST_ADDRESS) } returns displayName
        coEvery { getAccountIconDrawablePreview(TEST_ADDRESS) } returns iconPreview
        coEvery { contactRepository.getContactByAddress(TEST_ADDRESS) } returns contact

        val result = sut(TEST_ADDRESS, emptyList())

        assertNull(result.imageUri)
    }

    @Test
    fun `EXPECT multiple participant items WHEN createParticipantItems called`() = runTest {
        val addresses = listOf("ADDR1", "ADDR2", "ADDR3")
        val localAccounts = listOf(
            mockk<LocalAccount> { every { algoAddress } returns "ADDR1" }
        )
        val displayName = mockk<AccountDisplayName> {
            every { primaryDisplayName } returns "Name"
            every { secondaryDisplayName } returns "Secondary"
        }
        val iconPreview = mockk<AccountIconDrawablePreview>()

        coEvery { getLocalAccounts() } returns localAccounts
        coEvery { getAccountDisplayName(any<String>()) } returns displayName
        coEvery { getAccountIconDrawablePreview(any<String>()) } returns iconPreview
        coEvery { contactRepository.getContactByAddress(any()) } returns null

        val result = sut.createParticipantItems(addresses)

        assertEquals(3, result.size)
        assertTrue(result[0].isLocalAccount)
        assertFalse(result[1].isLocalAccount)
        assertFalse(result[2].isLocalAccount)
    }

    private companion object {
        const val TEST_ADDRESS = "TEST_ADDRESS_123"
    }
}
