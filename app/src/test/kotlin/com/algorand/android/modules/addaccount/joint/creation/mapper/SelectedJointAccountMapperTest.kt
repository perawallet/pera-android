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

package com.algorand.android.modules.addaccount.joint.creation.mapper

import android.net.Uri
import com.algorand.android.models.User
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.creation.model.JointAccountSelectionListItem
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class SelectedJointAccountMapperTest {

    private val sut = SelectedJointAccountMapper()

    @Before
    fun setup() {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `EXPECT correct mapping WHEN mapFromDetail with user`() {
        val user =
            User(name = "Test User", publicKey = TEST_ADDRESS, imageUriAsString = "https://example.com/image.png")
        val iconDrawablePreview = mockk<AccountIconDrawablePreview>()

        val result = sut.mapFromDetail(TEST_ADDRESS, user, iconDrawablePreview)

        assertEquals(TEST_ADDRESS, result.accountDisplayName.accountAddress)
        assertEquals("Test User", result.accountDisplayName.primaryDisplayName)
        assertNotNull(result.accountDisplayName.secondaryDisplayName)
        assertEquals(iconDrawablePreview, result.iconDrawablePreview)
        assertNotNull(result.imageUri)
        assertTrue(result.isContact)
    }

    @Test
    fun `EXPECT shortened address WHEN mapFromDetail without user`() {
        val result = sut.mapFromDetail(TEST_ADDRESS, null, null)

        assertEquals(TEST_ADDRESS, result.accountDisplayName.accountAddress)
        assertNotNull(result.accountDisplayName.primaryDisplayName)
        assertNull(result.iconDrawablePreview)
        assertNull(result.imageUri)
        assertFalse(result.isContact)
    }

    @Test
    fun `EXPECT correct mapping WHEN mapFromAccountItem`() {
        val iconDrawablePreview = mockk<AccountIconDrawablePreview>()
        val accountItem = JointAccountSelectionListItem.AccountItem(
            address = TEST_ADDRESS,
            displayName = "My Account",
            secondaryDisplayName = "Secondary",
            iconDrawablePreview = iconDrawablePreview,
            formattedAmount = "100 ALGO",
            formattedCurrencyValue = "$150.00"
        )
        val expected = SelectedJointAccountItem(
            accountDisplayName = AccountDisplayName(
                accountAddress = TEST_ADDRESS,
                primaryDisplayName = "My Account",
                secondaryDisplayName = "Secondary"
            ),
            iconDrawablePreview = iconDrawablePreview,
            isContact = false
        )

        val result = sut.mapFromAccountItem(accountItem)

        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT correct mapping WHEN mapFromContactItem`() {
        val contactItem = JointAccountSelectionListItem.ContactItem(
            address = TEST_ADDRESS,
            displayName = "Contact Name",
            imageUri = mockk()
        )

        val result = sut.mapFromContactItem(contactItem)

        assertEquals(TEST_ADDRESS, result.accountDisplayName.accountAddress)
        assertEquals("Contact Name", result.accountDisplayName.primaryDisplayName)
        assertNotNull(result.accountDisplayName.secondaryDisplayName)
        assertTrue(result.isContact)
    }

    @Test
    fun `EXPECT correct mapping WHEN mapFromNfdItem`() {
        val nfdItem = JointAccountSelectionListItem.NfdItem(
            address = TEST_ADDRESS,
            domainName = "test.algo",
            serviceLogoUrl = null
        )

        val result = sut.mapFromNfdItem(nfdItem)

        assertEquals(TEST_ADDRESS, result.accountDisplayName.accountAddress)
        assertEquals("test.algo", result.accountDisplayName.primaryDisplayName)
        assertNotNull(result.accountDisplayName.secondaryDisplayName)
        assertFalse(result.isContact)
    }

    @Test
    fun `EXPECT account item WHEN mapFromSelectionList finds matching account`() {
        val accountItem = JointAccountSelectionListItem.AccountItem(
            address = TEST_ADDRESS,
            displayName = "My Account",
            secondaryDisplayName = null,
            iconDrawablePreview = mockk(),
            formattedAmount = "100 ALGO",
            formattedCurrencyValue = "$150.00"
        )
        val list = listOf<JointAccountSelectionListItem>(accountItem)

        val result = sut.mapFromSelectionList(TEST_ADDRESS, list)

        assertNotNull(result)
        assertEquals(TEST_ADDRESS, result?.accountDisplayName?.accountAddress)
    }

    @Test
    fun `EXPECT null WHEN mapFromSelectionList finds no match`() {
        val accountItem = JointAccountSelectionListItem.AccountItem(
            address = "OTHER_ADDRESS",
            displayName = "Other Account",
            secondaryDisplayName = null,
            iconDrawablePreview = mockk(),
            formattedAmount = "100 ALGO",
            formattedCurrencyValue = "$150.00"
        )
        val list = listOf<JointAccountSelectionListItem>(accountItem)

        val result = sut.mapFromSelectionList(TEST_ADDRESS, list)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN mapFromSelectionList with empty list`() {
        val result = sut.mapFromSelectionList(TEST_ADDRESS, emptyList())

        assertNull(result)
    }

    @Test
    fun `EXPECT contact item WHEN mapFromSelectionList finds matching contact`() {
        val contactItem = JointAccountSelectionListItem.ContactItem(
            address = TEST_ADDRESS,
            displayName = "Contact",
            imageUri = null
        )
        val list = listOf<JointAccountSelectionListItem>(contactItem)

        val result = sut.mapFromSelectionList(TEST_ADDRESS, list)

        assertNotNull(result)
        assertTrue(result!!.isContact)
    }

    private companion object {
        const val TEST_ADDRESS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567ABCDEFGHIJKLMNOPQRSTUVU4"
    }
}
