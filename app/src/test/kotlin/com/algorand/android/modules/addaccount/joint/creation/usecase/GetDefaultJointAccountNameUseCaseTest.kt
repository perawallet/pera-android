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

import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class GetDefaultJointAccountNameUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mockk()
    private val sut = GetDefaultJointAccountNameUseCase(getLocalAccounts)

    @Test
    fun `EXPECT Joint Account #1 WHEN no joint accounts exist`() = runTest {
        coEvery { getLocalAccounts() } returns emptyList()

        val result = sut()

        assertEquals("Joint Account #1", result)
    }

    @Test
    fun `EXPECT Joint Account #2 WHEN one joint account exists`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(createJointAccount("ADDR1"))

        val result = sut()

        assertEquals("Joint Account #2", result)
    }

    @Test
    fun `EXPECT Joint Account #4 WHEN three joint accounts exist`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(
            createJointAccount("ADDR1"),
            createJointAccount("ADDR2"),
            createJointAccount("ADDR3")
        )

        val result = sut()

        assertEquals("Joint Account #4", result)
    }

    @Test
    fun `EXPECT Joint Account #1 WHEN only non-joint accounts exist`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(
            LocalAccount.Algo25(algoAddress = "ADDR1"),
            LocalAccount.Algo25(algoAddress = "ADDR2")
        )

        val result = sut()

        assertEquals("Joint Account #1", result)
    }

    @Test
    fun `EXPECT Joint Account #3 WHEN mixed account types exist with two joint accounts`() = runTest {
        coEvery { getLocalAccounts() } returns listOf(
            LocalAccount.Algo25(algoAddress = "ADDR1"),
            createJointAccount("ADDR2"),
            LocalAccount.Algo25(algoAddress = "ADDR3"),
            createJointAccount("ADDR4")
        )

        val result = sut()

        assertEquals("Joint Account #3", result)
    }

    private fun createJointAccount(address: String) = LocalAccount.Joint(
        algoAddress = address,
        participantAddresses = listOf("PART1", "PART2"),
        threshold = 2,
        version = 1
    )
}
