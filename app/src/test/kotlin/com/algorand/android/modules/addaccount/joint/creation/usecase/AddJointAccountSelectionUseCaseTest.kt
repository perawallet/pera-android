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

import com.algorand.android.models.BaseAccountSelectionListItem.BaseAccountItem.AccountItem
import com.algorand.android.models.BaseAccountSelectionListItem.BaseAccountItem.ContactItem
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionAccountItems
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionContactItems
import com.algorand.android.modules.accountcore.ui.accountselection.usecase.GetAccountSelectionNameServiceItems
import com.algorand.android.modules.addaccount.joint.creation.mapper.JointAccountSelectionListItemMapper
import com.algorand.android.modules.addaccount.joint.creation.model.JointAccountSelectionListItem
import com.algorand.android.utils.isValidAddress
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class AddJointAccountSelectionUseCaseTest {

    @Before
    fun setup() {
        mockkStatic("com.algorand.android.utils.AlgorandSDKUtilsKt")
    }

    @After
    fun tearDown() {
        unmockkStatic("com.algorand.android.utils.AlgorandSDKUtilsKt")
    }

    private val getAccountSelectionAccountItems: GetAccountSelectionAccountItems = mockk()
    private val getAccountSelectionContactItems: GetAccountSelectionContactItems = mockk()
    private val getAccountSelectionNameServiceItems: GetAccountSelectionNameServiceItems = mockk()
    private val jointAccountSelectionListItemMapper: JointAccountSelectionListItemMapper = mockk()
    private val getAccountRegistrationType: GetAccountRegistrationType = mockk()

    private val sut = AddJointAccountSelectionUseCase(
        getAccountSelectionAccountItems = getAccountSelectionAccountItems,
        getAccountSelectionContactItems = getAccountSelectionContactItems,
        getAccountSelectionNameServiceItems = getAccountSelectionNameServiceItems,
        jointAccountSelectionListItemMapper = jointAccountSelectionListItemMapper,
        getAccountRegistrationType = getAccountRegistrationType
    )

    @Test
    fun `EXPECT empty list WHEN no items match query`() = runTest {
        coEvery { getAccountSelectionAccountItems(showHoldings = true, showFailedAccounts = false) } returns emptyList()
        coEvery { getAccountSelectionContactItems() } returns emptyList()
        every { any<String>().isValidAddress() } returns false

        val result = sut.getAccountSelectionList("xyz")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT account items WHEN accounts match query`() = runTest {
        val mockAccountItem = mockk<AccountItem> {
            every { displayName } returns "Test Account"
            every { address } returns TEST_ADDRESS
            every { accountListItem.itemConfiguration.accountType } returns mockk(relaxed = true)
        }
        coEvery { getAccountRegistrationType(TEST_ADDRESS) } returns AccountRegistrationType.Algo25
        val mappedItem = JointAccountSelectionListItem.AccountItem(
            address = TEST_ADDRESS,
            displayName = "Test Account",
            secondaryDisplayName = null,
            iconDrawablePreview = mockk(),
            formattedAmount = "100 ALGO",
            formattedCurrencyValue = "$150.00"
        )

        coEvery { getAccountSelectionAccountItems(showHoldings = true, showFailedAccounts = false) } returns listOf(
            mockAccountItem
        )
        coEvery { getAccountSelectionContactItems() } returns emptyList()
        every { jointAccountSelectionListItemMapper.mapToAccountItem(mockAccountItem) } returns mappedItem
        every { any<String>().isValidAddress() } returns false

        val result = sut.getAccountSelectionList("Test")

        assertEquals(1, result.size)
        assertTrue(result.first() is JointAccountSelectionListItem.AccountItem)
    }

    @Test
    fun `EXPECT contact items WHEN contacts match query`() = runTest {
        val mockContactItem = mockk<ContactItem> {
            every { displayName } returns "Test Contact"
            every { address } returns TEST_ADDRESS
        }
        val mappedItem = JointAccountSelectionListItem.ContactItem(
            address = TEST_ADDRESS,
            displayName = "Test Contact",
            imageUri = null
        )

        coEvery { getAccountSelectionAccountItems(showHoldings = true, showFailedAccounts = false) } returns emptyList()
        coEvery { getAccountSelectionContactItems() } returns listOf(mockContactItem)
        every { jointAccountSelectionListItemMapper.mapToContactItem(mockContactItem) } returns mappedItem
        every { any<String>().isValidAddress() } returns false

        val result = sut.getAccountSelectionList("Test")

        assertEquals(1, result.size)
        assertTrue(result.first() is JointAccountSelectionListItem.ContactItem)
    }

    @Test
    fun `EXPECT external address WHEN valid address is queried`() = runTest {
        val externalItem = JointAccountSelectionListItem.ExternalAddressItem(
            address = VALID_ALGORAND_ADDRESS,
            shortenedAddress = "ABCD...UVU4",
            iconDrawablePreview = mockk()
        )

        coEvery { getAccountSelectionAccountItems(showHoldings = true, showFailedAccounts = false) } returns emptyList()
        coEvery { getAccountSelectionContactItems() } returns emptyList()
        every {
            jointAccountSelectionListItemMapper.mapToExternalAddressItem(
                VALID_ALGORAND_ADDRESS,
                any()
            )
        } returns externalItem
        every { VALID_ALGORAND_ADDRESS.isValidAddress() } returns true

        val result = sut.getAccountSelectionList(VALID_ALGORAND_ADDRESS)

        assertTrue(result.any { it is JointAccountSelectionListItem.ExternalAddressItem })
    }

    @Test
    fun `EXPECT no external address WHEN address already exists in accounts`() = runTest {
        val mockAccountItem = mockk<AccountItem> {
            every { displayName } returns "Existing Account"
            every { address } returns VALID_ALGORAND_ADDRESS
            every { accountListItem.itemConfiguration.accountType } returns mockk(relaxed = true)
        }
        coEvery { getAccountRegistrationType(VALID_ALGORAND_ADDRESS) } returns AccountRegistrationType.Algo25
        val mappedItem = JointAccountSelectionListItem.AccountItem(
            address = VALID_ALGORAND_ADDRESS,
            displayName = "Existing Account",
            secondaryDisplayName = null,
            iconDrawablePreview = mockk(),
            formattedAmount = "100 ALGO",
            formattedCurrencyValue = "$150.00"
        )

        val externalItem = JointAccountSelectionListItem.ExternalAddressItem(
            address = VALID_ALGORAND_ADDRESS,
            shortenedAddress = "ABCD...UVU4",
            iconDrawablePreview = mockk()
        )
        coEvery { getAccountSelectionAccountItems(showHoldings = true, showFailedAccounts = false) } returns listOf(
            mockAccountItem
        )
        coEvery { getAccountSelectionContactItems() } returns emptyList()
        every { jointAccountSelectionListItemMapper.mapToAccountItem(mockAccountItem) } returns mappedItem
        every {
            jointAccountSelectionListItemMapper.mapToExternalAddressItem(
                VALID_ALGORAND_ADDRESS,
                any()
            )
        } returns externalItem
        every { VALID_ALGORAND_ADDRESS.isValidAddress() } returns true

        val result = sut.getAccountSelectionList(VALID_ALGORAND_ADDRESS)

        // External address is filtered out because the account with same address already exists
        assertTrue(result.none { it is JointAccountSelectionListItem.ExternalAddressItem })
    }

    private companion object {
        const val TEST_ADDRESS = "TEST_ADDRESS"
        const val VALID_ALGORAND_ADDRESS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567ABCDEFGHIJKLMNOPQRSTUVU4"
    }
}
