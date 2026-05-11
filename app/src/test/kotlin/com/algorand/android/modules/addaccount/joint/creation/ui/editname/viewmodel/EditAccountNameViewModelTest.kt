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

package com.algorand.android.modules.addaccount.joint.creation.ui.editname.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.algorand.android.models.User
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.creation.mapper.SelectedJointAccountMapper
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.modules.contact.base.domain.usecase.GetContactByAddress
import com.algorand.wallet.viewmodel.StateDelegate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class EditAccountNameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getContactByAddress: GetContactByAddress = mockk()
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview = mockk()
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
    fun `EXPECT Loading state initially`() = runTest {
        val stateDelegate = StateDelegate<EditAccountNameViewModel.ViewState>()
        coEvery { getContactByAddress(TEST_ADDRESS) } returns null
        coEvery { getAccountIconDrawablePreview(TEST_ADDRESS) } returns mockk()
        coEvery { selectedJointAccountMapper.mapFromDetail(TEST_ADDRESS, null, any()) } returns createSelectedAccount()

        createViewModel(stateDelegate)

        assertTrue(stateDelegate.state.value is EditAccountNameViewModel.ViewState.Loading)
    }

    @Test
    fun `EXPECT Content state WHEN account info loaded`() = runTest {
        val stateDelegate = StateDelegate<EditAccountNameViewModel.ViewState>()
        val expectedAccount = createSelectedAccount()
        coEvery { getContactByAddress(TEST_ADDRESS) } returns null
        coEvery { getAccountIconDrawablePreview(TEST_ADDRESS) } returns mockk()
        coEvery { selectedJointAccountMapper.mapFromDetail(TEST_ADDRESS, null, any()) } returns expectedAccount

        createViewModel(stateDelegate)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is EditAccountNameViewModel.ViewState.Content)
        assertEquals(expectedAccount, (state as EditAccountNameViewModel.ViewState.Content).account)
    }

    @Test
    fun `EXPECT Content with contact WHEN contact exists`() = runTest {
        val stateDelegate = StateDelegate<EditAccountNameViewModel.ViewState>()
        val contact = User(name = "Test Contact", publicKey = TEST_ADDRESS, imageUriAsString = null)
        val expectedAccount = createSelectedAccount(isContact = true)
        coEvery { getContactByAddress(TEST_ADDRESS) } returns contact
        coEvery { getAccountIconDrawablePreview(TEST_ADDRESS) } returns mockk()
        coEvery { selectedJointAccountMapper.mapFromDetail(TEST_ADDRESS, contact, any()) } returns expectedAccount

        createViewModel(stateDelegate)
        advanceUntilIdle()

        val state = stateDelegate.state.value
        assertTrue(state is EditAccountNameViewModel.ViewState.Content)
        assertTrue((state as EditAccountNameViewModel.ViewState.Content).account.isContact)
    }

    private fun createViewModel(
        stateDelegate: StateDelegate<EditAccountNameViewModel.ViewState>
    ): EditAccountNameViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("accountAddress" to TEST_ADDRESS))
        return EditAccountNameViewModel(
            savedStateHandle = savedStateHandle,
            stateDelegate = stateDelegate,
            getContactByAddress = getContactByAddress,
            getAccountIconDrawablePreview = getAccountIconDrawablePreview,
            selectedJointAccountMapper = selectedJointAccountMapper
        )
    }

    private fun createSelectedAccount(isContact: Boolean = false): SelectedJointAccountItem {
        return SelectedJointAccountItem(
            accountDisplayName = AccountDisplayName(
                accountAddress = TEST_ADDRESS,
                primaryDisplayName = "Test Account",
                secondaryDisplayName = "TEST...UVU4"
            ),
            iconDrawablePreview = mockk<AccountIconDrawablePreview>(),
            isContact = isContact
        )
    }

    private companion object {
        const val TEST_ADDRESS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567ABCDEFGHIJKLMNOPQRSTUVU4"
    }
}
