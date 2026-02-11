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

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.algorand.android.modules.addaccount.joint.creation.ui.addaccount.viewmodel

import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.addaccount.joint.creation.mapper.SelectedJointAccountMapper
import com.algorand.android.modules.addaccount.joint.creation.model.JointAccountSelectionListItem
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.modules.addaccount.joint.creation.ui.addaccount.viewmodel.AddJointAccountViewModel.ViewState
import com.algorand.android.modules.addaccount.joint.creation.usecase.AddJointAccountSelectionUseCase
import com.algorand.android.modules.addaccount.joint.creation.usecase.CreateExternalAddressAsContact
import com.algorand.test.test
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.StateDelegate
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class AddJointAccountViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val addJointAccountSelectionUseCase: AddJointAccountSelectionUseCase = mockk()
    private val createExternalAddressAsContact: CreateExternalAddressAsContact = mockk()
    private val selectedJointAccountMapper: SelectedJointAccountMapper = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `EXPECT empty state initially`() = runTest {
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("") } returns emptyList()

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value as AddJointAccountViewModel.ViewState.Content
        assertEquals("", state.searchQuery)
        assertTrue(state.accountList.isEmpty())
    }

    @Test
    fun `EXPECT updated accountList WHEN search query changes`(): TestResult = runTest {
        val mockList = listOf(
            JointAccountSelectionListItem.AccountItem(
                address = TEST_ADDRESS,
                displayName = "Test Account",
                secondaryDisplayName = null,
                iconDrawablePreview = mockk(),
                formattedAmount = "100 ALGO",
                formattedCurrencyValue = "$150.00"
            )
        )
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("") } returns emptyList()
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("test") } returns mockList

        val sut = createViewModel()
        val stateObserver = sut.state.test(backgroundScope)

        advanceUntilIdle()
        sut.onSearchQueryUpdate("test")
        advanceTimeBy(350)
        advanceUntilIdle()

        val emptyContent = ViewState.Content(
            searchQuery = "",
            accountList = emptyList()
        ).withCategorizedLists()
        val searchingContent = ViewState.Content(
            searchQuery = "test",
            accountList = emptyList(),
            showEmptyState = true
        )
        val updatedContent = ViewState.Content(
            searchQuery = "test",
            accountList = mockList
        ).withCategorizedLists()
        stateObserver.assertValueHistory(ViewState.Loading, emptyContent, searchingContent, updatedContent)
    }

    @Test
    fun `EXPECT hasResults true WHEN accountList is not empty`() = runTest {
        val mockList = listOf(
            JointAccountSelectionListItem.AccountItem(
                address = TEST_ADDRESS,
                displayName = "Test",
                secondaryDisplayName = null,
                iconDrawablePreview = mockk(),
                formattedAmount = "100 ALGO",
                formattedCurrencyValue = "$150.00"
            )
        )
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("") } returns mockList

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value as AddJointAccountViewModel.ViewState.Content
        assertTrue(state.hasResults)
    }

    @Test
    fun `EXPECT showEmptyState true WHEN no results and query not empty`() = runTest {
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("") } returns emptyList()
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("xyz") } returns emptyList()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryUpdate("xyz")
        advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.state.value as AddJointAccountViewModel.ViewState.Content
        assertTrue(state.showEmptyState)
    }

    @Test
    fun `EXPECT query reset WHEN resetSearchQuery called`() = runTest {
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("") } returns emptyList()
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("test") } returns emptyList()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryUpdate("test")
        advanceTimeBy(350)
        advanceUntilIdle()

        viewModel.resetSearchQuery()
        advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.state.value as AddJointAccountViewModel.ViewState.Content
        assertEquals("", state.searchQuery)
    }

    @Test
    fun `EXPECT selected account WHEN createSelectedAccountFromItem finds match`() = runTest {
        val accountItem = JointAccountSelectionListItem.AccountItem(
            address = TEST_ADDRESS,
            displayName = "Test",
            secondaryDisplayName = null,
            iconDrawablePreview = mockk(),
            formattedAmount = "100 ALGO",
            formattedCurrencyValue = "$150.00"
        )
        val expectedSelected = createSelectedAccount()
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("") } returns listOf(accountItem)
        every {
            selectedJointAccountMapper.mapFromSelectionList(
                TEST_ADDRESS,
                listOf(accountItem)
            )
        } returns expectedSelected

        val viewModel = createViewModel()
        advanceUntilIdle()

        val result = viewModel.createSelectedAccountFromItem(TEST_ADDRESS)

        assertNotNull(result)
        assertEquals(TEST_ADDRESS, result?.accountDisplayName?.accountAddress)
    }

    @Test
    fun `EXPECT null WHEN createSelectedAccountFromItem finds no match`() = runTest {
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("") } returns emptyList()
        every { selectedJointAccountMapper.mapFromSelectionList("unknown", emptyList()) } returns null

        val viewModel = createViewModel()
        advanceUntilIdle()

        val result = viewModel.createSelectedAccountFromItem("unknown")

        assertNull(result)
    }

    @Test
    fun `EXPECT categorized lists populated WHEN accountList updates`() = runTest {
        val mockList = listOf(
            JointAccountSelectionListItem.AccountItem(
                address = "ADDR1",
                displayName = "Account",
                secondaryDisplayName = null,
                iconDrawablePreview = mockk(),
                formattedAmount = "100 ALGO",
                formattedCurrencyValue = "$150.00"
            ),
            JointAccountSelectionListItem.ContactItem(
                address = "ADDR2",
                displayName = "Contact",
                imageUri = null
            ),
            JointAccountSelectionListItem.NfdItem(
                address = "ADDR3",
                domainName = "test.algo",
                serviceLogoUrl = null
            )
        )
        coEvery { addJointAccountSelectionUseCase.getAccountSelectionList("") } returns mockList

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value as AddJointAccountViewModel.ViewState.Content
        assertEquals(1, state.accounts.size)
        assertEquals(1, state.contacts.size)
        assertEquals(1, state.nfds.size)
    }

    private fun createViewModel(): AddJointAccountViewModel {
        return AddJointAccountViewModel(
            stateDelegate = StateDelegate<AddJointAccountViewModel.ViewState>(),
            eventDelegate = EventDelegate<AddJointAccountViewModel.ViewEvent>(),
            addJointAccountSelectionUseCase = addJointAccountSelectionUseCase,
            createExternalAddressAsContact = createExternalAddressAsContact,
            selectedJointAccountMapper = selectedJointAccountMapper
        )
    }

    private fun createSelectedAccount(): SelectedJointAccountItem {
        return SelectedJointAccountItem(
            accountDisplayName = AccountDisplayName(
                accountAddress = TEST_ADDRESS,
                primaryDisplayName = "Test",
                secondaryDisplayName = null
            ),
            iconDrawablePreview = null
        )
    }

    private companion object {
        const val TEST_ADDRESS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567ABCDEFGHIJKLMNOPQRSTUVU4"
    }
}
