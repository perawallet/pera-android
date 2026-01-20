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
import org.junit.Assert.assertNull
import org.junit.Test

internal class GetJointAccountUseCaseTest {

    private val getLocalAccount: GetLocalAccount = mockk()
    private val sut = GetJointAccountUseCase(getLocalAccount)

    @Test
    fun `EXPECT null WHEN no local account exists`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns null

        val result = sut(TEST_ADDRESS)

        assertNull(result)
    }

    @Test
    fun `EXPECT joint account WHEN joint account exists`() = runTest {
        val jointAccount = createJointAccount()
        coEvery { getLocalAccount(TEST_ADDRESS) } returns jointAccount

        val result = sut(TEST_ADDRESS)

        assertEquals(jointAccount, result)
    }

    @Test
    fun `EXPECT null WHEN Algo25 account exists`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns LocalAccount.Algo25(algoAddress = TEST_ADDRESS)

        val result = sut(TEST_ADDRESS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN LedgerBle account exists`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns LocalAccount.LedgerBle(
            algoAddress = TEST_ADDRESS,
            deviceMacAddress = "00:11:22:33:44:55",
            bluetoothName = "Ledger Nano X",
            indexInLedger = 0
        )

        val result = sut(TEST_ADDRESS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN NoAuth account exists`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns LocalAccount.NoAuth(algoAddress = TEST_ADDRESS)

        val result = sut(TEST_ADDRESS)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN HdKey account exists`() = runTest {
        coEvery { getLocalAccount(TEST_ADDRESS) } returns LocalAccount.HdKey(
            algoAddress = TEST_ADDRESS,
            publicKey = ByteArray(32),
            seedId = 1,
            account = 0,
            change = 0,
            keyIndex = 0,
            derivationType = 0
        )

        val result = sut(TEST_ADDRESS)

        assertNull(result)
    }

    private fun createJointAccount() = LocalAccount.Joint(
        algoAddress = TEST_ADDRESS,
        participantAddresses = listOf("ADDR1", "ADDR2", "ADDR3"),
        threshold = 2,
        version = 1
    )

    private companion object {
        const val TEST_ADDRESS = "JOINT_ADDRESS"
    }
}
