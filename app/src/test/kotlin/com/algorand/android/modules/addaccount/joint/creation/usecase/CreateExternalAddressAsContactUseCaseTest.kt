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

package com.algorand.android.modules.addaccount.joint.creation.usecase

import com.algorand.android.models.User
import com.algorand.android.repository.ContactRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class CreateExternalAddressAsContactUseCaseTest {

    private val contactRepository: ContactRepository = mockk(relaxed = true) {
        coEvery { getContactByAddress(any()) } returns null
    }
    private val sut = CreateExternalAddressAsContactUseCase(contactRepository)

    @Test
    fun `EXPECT contact added with correct fields WHEN invoke is called with displayName`() = runTest {
        val contactSlot = slot<User>()
        coEvery { contactRepository.addContact(capture(contactSlot)) } returns Unit

        sut(TEST_ADDRESS, TEST_DISPLAY_NAME)

        coVerify(exactly = 1) { contactRepository.addContact(any()) }
        assertEquals(TEST_ADDRESS, contactSlot.captured.publicKey)
        assertEquals(TEST_DISPLAY_NAME, contactSlot.captured.name)
        assertNull(contactSlot.captured.imageUriAsString)
    }

    @Test
    fun `EXPECT correct SelectedJointAccountItem WHEN invoke is called with displayName`() = runTest {
        val result = sut(TEST_ADDRESS, TEST_DISPLAY_NAME)

        assertNotNull(result)
        assertEquals(TEST_ADDRESS, result!!.accountDisplayName.accountAddress)
        assertEquals(TEST_DISPLAY_NAME, result.accountDisplayName.primaryDisplayName)
        assertNull(result.accountDisplayName.secondaryDisplayName)
        assertNull(result.iconDrawablePreview)
        assertTrue(result.isContact)
    }

    @Test
    fun `EXPECT displayName as contact name WHEN displayName is provided`() = runTest {
        val contactSlot = slot<User>()
        coEvery { contactRepository.addContact(capture(contactSlot)) } returns Unit
        val displayName = "nfd.algo"

        val result = sut(TEST_ADDRESS, displayName)

        assertNotNull(result)
        assertEquals(displayName, contactSlot.captured.name)
        assertEquals(displayName, result!!.accountDisplayName.primaryDisplayName)
    }

    @Test
    fun `EXPECT shortened address as contact name WHEN displayName is null`() = runTest {
        val contactSlot = slot<User>()
        coEvery { contactRepository.addContact(capture(contactSlot)) } returns Unit

        val result = sut(TEST_ADDRESS, null)

        assertNotNull(result)
        assertEquals(contactSlot.captured.name, result!!.accountDisplayName.primaryDisplayName)
    }

    @Test
    fun `EXPECT null WHEN repository throws exception`() = runTest {
        coEvery { contactRepository.addContact(any()) } throws RuntimeException("Database error")

        val result = sut(TEST_ADDRESS, TEST_DISPLAY_NAME)

        assertNull(result)
    }

    private companion object {
        const val TEST_ADDRESS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567ABCDEFGHIJKLMNOPQRSTUVU4"
        const val TEST_DISPLAY_NAME = "ABCD...UVU4"
    }
}
