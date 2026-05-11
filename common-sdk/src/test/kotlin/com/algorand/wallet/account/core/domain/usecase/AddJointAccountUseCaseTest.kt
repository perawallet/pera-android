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

package com.algorand.wallet.account.core.domain.usecase

import com.algorand.wallet.account.custom.domain.model.CustomAccountInfo
import com.algorand.wallet.account.custom.domain.usecase.SetAccountCustomInfo
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.SaveJointAccount
import com.algorand.wallet.foundation.PeraResult
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class AddJointAccountUseCaseTest {

    private val saveJointAccount: SaveJointAccount = mockk(relaxed = true)
    private val setAccountCustomInfo: SetAccountCustomInfo = mockk(relaxed = true)
    private val sut = AddJointAccountUseCase(saveJointAccount, setAccountCustomInfo)

    @Test
    fun `EXPECT joint account and custom info saved with correct parameters WHEN invoked`() = runTest {
        val accountSlot = slot<LocalAccount.Joint>()
        val customInfoSlot = slot<CustomAccountInfo>()
        coEvery { saveJointAccount(capture(accountSlot)) } returns Unit
        coEvery { setAccountCustomInfo(capture(customInfoSlot)) } returns Unit

        sut(TEST_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_NAME, TEST_ORDER)

        with(accountSlot.captured) {
            assertEquals(TEST_ADDRESS, algoAddress)
            assertEquals(TEST_PARTICIPANTS, participantAddresses)
            assertEquals(TEST_THRESHOLD, threshold)
            assertEquals(TEST_VERSION, version)
        }

        with(customInfoSlot.captured) {
            assertEquals(TEST_ADDRESS, address)
            assertEquals(TEST_NAME, customName)
            assertEquals(TEST_ORDER, orderIndex)
            assertTrue(isBackedUp)
        }
    }

    @Test
    fun `EXPECT null custom name WHEN name is null`() = runTest {
        val customInfoSlot = slot<CustomAccountInfo>()
        coEvery { setAccountCustomInfo(capture(customInfoSlot)) } returns Unit

        sut(TEST_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, null, TEST_ORDER)

        assertNull(customInfoSlot.captured.customName)
    }

    @Test
    fun `EXPECT saveJointAccount called before setAccountCustomInfo WHEN invoked`() = runTest {
        sut(TEST_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_NAME, TEST_ORDER)

        coVerify(ordering = Ordering.ORDERED) {
            saveJointAccount(any())
            setAccountCustomInfo(any())
        }
    }

    @Test
    fun `EXPECT empty participants saved correctly WHEN participant list is empty`() = runTest {
        val accountSlot = slot<LocalAccount.Joint>()
        coEvery { saveJointAccount(capture(accountSlot)) } returns Unit

        sut(TEST_ADDRESS, emptyList(), TEST_THRESHOLD, TEST_VERSION, TEST_NAME, TEST_ORDER)

        assertEquals(emptyList<String>(), accountSlot.captured.participantAddresses)
    }

    @Test
    fun `EXPECT exception propagated WHEN saveJointAccount fails`() = runTest {
        val expectedException = RuntimeException("Database error")
        coEvery { saveJointAccount(any()) } throws expectedException

        val result = sut(TEST_ADDRESS, TEST_PARTICIPANTS, TEST_THRESHOLD, TEST_VERSION, TEST_NAME, TEST_ORDER)

        assertTrue(result is PeraResult.Error)
        assertEquals(expectedException, (result as PeraResult.Error).exception)
        coVerify(exactly = 0) { setAccountCustomInfo(any()) }
    }

    private companion object {
        const val TEST_ADDRESS = "JOINT_ADDRESS_123"
        val TEST_PARTICIPANTS = listOf("ADDR1", "ADDR2", "ADDR3")
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1
        const val TEST_NAME = "My Joint Account"
        const val TEST_ORDER = 5
    }
}
