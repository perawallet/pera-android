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

package com.algorand.wallet.jointaccount.domain.usecase

import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class GetJointAccountParticipantCountUseCaseTest {

    private val getLocalAccount: GetLocalAccount = mockk()
    private val sut = GetJointAccountParticipantCountUseCase(getLocalAccount)

    @Test
    fun `EXPECT 0 WHEN account not found`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns null

        val result = sut(TEST_ADDRESS)

        assertEquals(0, result)
    }

    @Test
    fun `EXPECT 0 WHEN account is not joint account`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns LocalAccount.Algo25(algoAddress = TEST_ADDRESS)

        val result = sut(TEST_ADDRESS)

        assertEquals(0, result)
    }

    @Test
    fun `EXPECT 2 WHEN joint account has 2 participants`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns createJointAccount(listOf("ADDR1", "ADDR2"))

        val result = sut(TEST_ADDRESS)

        assertEquals(2, result)
    }

    @Test
    fun `EXPECT 5 WHEN joint account has 5 participants`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns createJointAccount(
            listOf("ADDR1", "ADDR2", "ADDR3", "ADDR4", "ADDR5")
        )

        val result = sut(TEST_ADDRESS)

        assertEquals(5, result)
    }

    @Test
    fun `EXPECT 0 WHEN joint account has empty participant list`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns createJointAccount(emptyList())

        val result = sut(TEST_ADDRESS)

        assertEquals(0, result)
    }

    @Test
    fun `EXPECT 0 WHEN account is LedgerBle`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns LocalAccount.LedgerBle(
            algoAddress = TEST_ADDRESS,
            deviceMacAddress = "00:11:22:33:44:55",
            bluetoothName = "Nano X",
            indexInLedger = 0
        )

        val result = sut(TEST_ADDRESS)

        assertEquals(0, result)
    }

    @Test
    fun `EXPECT 0 WHEN account is NoAuth`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns LocalAccount.NoAuth(algoAddress = TEST_ADDRESS)

        val result = sut(TEST_ADDRESS)

        assertEquals(0, result)
    }

    private fun createJointAccount(participants: List<String>) = LocalAccount.Joint(
        algoAddress = TEST_ADDRESS,
        participantAddresses = participants,
        threshold = 2,
        version = 1
    )

    private companion object {
        const val TEST_ADDRESS = "JOINT_ADDRESS"
    }
}
