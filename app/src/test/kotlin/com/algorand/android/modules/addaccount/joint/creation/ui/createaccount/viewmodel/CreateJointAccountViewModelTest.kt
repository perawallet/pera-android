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

package com.algorand.android.modules.addaccount.joint.creation.ui.createaccount.viewmodel

import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.wallet.viewmodel.StateDelegate
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class CreateJointAccountViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `EXPECT empty selected accounts initially`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.selectedAccounts.isEmpty())
    }

    @Test
    fun `EXPECT isContinueEnabled false WHEN less than 2 accounts selected`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addSelectedAccount(createSelectedAccount("ADDR1"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isContinueEnabled)
    }

    @Test
    fun `EXPECT isContinueEnabled true WHEN 2 or more accounts selected`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addSelectedAccount(createSelectedAccount("ADDR1"))
        viewModel.addSelectedAccount(createSelectedAccount("ADDR2"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isContinueEnabled)
    }

    @Test
    fun `EXPECT true WHEN adding new account`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val result = viewModel.addSelectedAccount(createSelectedAccount("ADDR1"))

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN adding duplicate account`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addSelectedAccount(createSelectedAccount("ADDR1"))
        advanceUntilIdle()

        val result = viewModel.addSelectedAccount(createSelectedAccount("ADDR1"))

        assertFalse(result)
    }

    @Test
    fun `EXPECT account added to list WHEN addSelectedAccount succeeds`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addSelectedAccount(createSelectedAccount("ADDR1"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.selectedAccounts.size)
        assertEquals("ADDR1", state.selectedAccounts[0].accountDisplayName.accountAddress)
    }

    @Test
    fun `EXPECT account name updated WHEN updateAccountName called`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addSelectedAccount(createSelectedAccount("ADDR1", "Original Name"))
        advanceUntilIdle()

        viewModel.updateAccountName("ADDR1", "New Name")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("New Name", state.selectedAccounts[0].accountDisplayName.primaryDisplayName)
    }

    @Test
    fun `EXPECT no update WHEN updateAccountName with blank name`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addSelectedAccount(createSelectedAccount("ADDR1", "Original Name"))
        advanceUntilIdle()

        viewModel.updateAccountName("ADDR1", "   ")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Original Name", state.selectedAccounts[0].accountDisplayName.primaryDisplayName)
    }

    @Test
    fun `EXPECT account removed WHEN removeSelectedAccount called`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addSelectedAccount(createSelectedAccount("ADDR1"))
        viewModel.addSelectedAccount(createSelectedAccount("ADDR2"))
        advanceUntilIdle()

        viewModel.removeSelectedAccount("ADDR1")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.selectedAccounts.size)
        assertEquals("ADDR2", state.selectedAccounts[0].accountDisplayName.accountAddress)
    }

    @Test
    fun `EXPECT correct addresses WHEN getParticipantAddresses called`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addSelectedAccount(createSelectedAccount("ADDR1"))
        viewModel.addSelectedAccount(createSelectedAccount("ADDR2"))
        viewModel.addSelectedAccount(createSelectedAccount("ADDR3"))
        advanceUntilIdle()

        val addresses = viewModel.getParticipantAddresses()

        assertEquals(3, addresses.size)
        assertTrue(addresses.contains("ADDR1"))
        assertTrue(addresses.contains("ADDR2"))
        assertTrue(addresses.contains("ADDR3"))
    }

    @Test
    fun `EXPECT empty array WHEN getParticipantAddresses with no accounts`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val addresses = viewModel.getParticipantAddresses()

        assertTrue(addresses.isEmpty())
    }

    @Test
    fun `EXPECT only non-matching accounts remain WHEN removing specific account`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addSelectedAccount(createSelectedAccount("ADDR1"))
        viewModel.addSelectedAccount(createSelectedAccount("ADDR2"))
        viewModel.addSelectedAccount(createSelectedAccount("ADDR3"))
        advanceUntilIdle()

        viewModel.removeSelectedAccount("ADDR2")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(2, state.selectedAccounts.size)
        assertFalse(state.selectedAccounts.any { it.accountDisplayName.accountAddress == "ADDR2" })
    }

    @Test
    fun `EXPECT no change WHEN removing non-existent account`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addSelectedAccount(createSelectedAccount("ADDR1"))
        advanceUntilIdle()

        viewModel.removeSelectedAccount("NON_EXISTENT")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.selectedAccounts.size)
    }

    @Test
    fun `EXPECT multiple accounts can be added in sequence`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        repeat(5) { index ->
            viewModel.addSelectedAccount(createSelectedAccount("ADDR$index"))
        }
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(5, state.selectedAccounts.size)
        assertTrue(state.isContinueEnabled)
    }

    private fun createViewModel(): CreateJointAccountViewModel {
        return CreateJointAccountViewModel(
            stateDelegate = StateDelegate()
        )
    }

    private fun createSelectedAccount(
        address: String,
        displayName: String = "Account $address"
    ): SelectedJointAccountItem {
        return SelectedJointAccountItem(
            accountDisplayName = AccountDisplayName(
                accountAddress = address,
                primaryDisplayName = displayName,
                secondaryDisplayName = address.take(8) + "..."
            ),
            iconDrawablePreview = mockk<AccountIconDrawablePreview>()
        )
    }
}
