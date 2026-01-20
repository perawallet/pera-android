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

import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class GetJointAccountProposerAddressUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mockk()
    private val getAccountType: GetAccountType = mockk()
    private val sut = GetJointAccountProposerAddressUseCase(getLocalAccounts, getAccountType)

    @Test
    fun `EXPECT null WHEN no local accounts exist`() = runTest {
        coEvery { getLocalAccounts() } returns emptyList()

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertNull(result)
    }

    @Test
    fun `EXPECT address WHEN participant is Algo25`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(LocalAccount.Algo25(algoAddress = PARTICIPANT_1))
        coEvery { getAccountType(PARTICIPANT_1) } returns AccountType.Algo25

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertEquals(PARTICIPANT_1, result)
    }

    @Test
    fun `EXPECT address WHEN participant is HdKey`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(createHdKeyAccount(PARTICIPANT_1))
        coEvery { getAccountType(PARTICIPANT_1) } returns AccountType.HdKey

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertEquals(PARTICIPANT_1, result)
    }

    @Test
    fun `EXPECT address WHEN participant is LedgerBle`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(createLedgerAccount(PARTICIPANT_1))
        coEvery { getAccountType(PARTICIPANT_1) } returns AccountType.LedgerBle

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertEquals(PARTICIPANT_1, result)
    }

    @Test
    fun `EXPECT address WHEN participant is RekeyedAuth`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(LocalAccount.Algo25(algoAddress = PARTICIPANT_1))
        coEvery { getAccountType(PARTICIPANT_1) } returns AccountType.RekeyedAuth

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertEquals(PARTICIPANT_1, result)
    }

    @Test
    fun `EXPECT null WHEN participant is NoAuth`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(LocalAccount.NoAuth(algoAddress = PARTICIPANT_1))
        coEvery { getAccountType(PARTICIPANT_1) } returns AccountType.NoAuth

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN participant is Rekeyed without auth`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(LocalAccount.Algo25(algoAddress = PARTICIPANT_1))
        coEvery { getAccountType(PARTICIPANT_1) } returns AccountType.Rekeyed

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN participant is Joint account`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(createJointAccount(listOf("INNER_1", "INNER_2"), PARTICIPANT_1))
        coEvery { getAccountType(PARTICIPANT_1) } returns AccountType.Joint

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertNull(result)
    }

    @Test
    fun `EXPECT second address WHEN first cannot sign`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(
            LocalAccount.NoAuth(algoAddress = PARTICIPANT_1),
            LocalAccount.Algo25(algoAddress = PARTICIPANT_2)
        )
        coEvery { getAccountType(PARTICIPANT_1) } returns AccountType.NoAuth
        coEvery { getAccountType(PARTICIPANT_2) } returns AccountType.Algo25

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2, PARTICIPANT_3)))

        assertEquals(PARTICIPANT_2, result)
    }

    @Test
    fun `EXPECT null WHEN account type is null`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(LocalAccount.Algo25(algoAddress = PARTICIPANT_1))
        coEvery { getAccountType(PARTICIPANT_1) } returns null

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertNull(result)
    }

    @Test
    fun `EXPECT first address WHEN multiple participants can sign`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(
            LocalAccount.Algo25(algoAddress = PARTICIPANT_1),
            LocalAccount.Algo25(algoAddress = PARTICIPANT_2)
        )
        coEvery { getAccountType(PARTICIPANT_1) } returns AccountType.Algo25
        coEvery { getAccountType(PARTICIPANT_2) } returns AccountType.Algo25

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertEquals(PARTICIPANT_1, result)
    }

    @Test
    fun `EXPECT second WHEN first not in local wallet`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(LocalAccount.Algo25(algoAddress = PARTICIPANT_2))
        coEvery { getAccountType(PARTICIPANT_2) } returns AccountType.Algo25

        val result = sut(createJointAccount(listOf(PARTICIPANT_1, PARTICIPANT_2)))

        assertEquals(PARTICIPANT_2, result)
    }

    private fun createJointAccount(
        participants: List<String>,
        address: String = "JOINT_ADDRESS"
    ) = LocalAccount.Joint(
        algoAddress = address,
        participantAddresses = participants,
        threshold = 2,
        version = 1
    )

    private fun createHdKeyAccount(address: String) = LocalAccount.HdKey(
        algoAddress = address,
        publicKey = ByteArray(32),
        seedId = 1,
        account = 0,
        change = 0,
        keyIndex = 0,
        derivationType = 0
    )

    private fun createLedgerAccount(address: String) = LocalAccount.LedgerBle(
        algoAddress = address,
        deviceMacAddress = "00:11:22:33:44:55",
        bluetoothName = "Nano X",
        indexInLedger = 0
    )

    private companion object {
        const val PARTICIPANT_1 = "PARTICIPANT_1"
        const val PARTICIPANT_2 = "PARTICIPANT_2"
        const val PARTICIPANT_3 = "PARTICIPANT_3"
    }
}
