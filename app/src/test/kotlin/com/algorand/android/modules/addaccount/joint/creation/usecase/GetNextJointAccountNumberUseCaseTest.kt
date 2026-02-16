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

import com.algorand.wallet.account.core.domain.usecase.GetJointAccountCount
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class GetNextJointAccountNumberUseCaseTest {

    private val getJointAccountCount: GetJointAccountCount = mockk()
    private val sut = GetNextJointAccountNumberUseCase(getJointAccountCount)

    @Test
    fun `EXPECT 1 WHEN no joint accounts exist`() = runTest {
        coEvery { getJointAccountCount() } returns 0

        val result = sut()

        assertEquals(1, result)
    }

    @Test
    fun `EXPECT 2 WHEN one joint account exists`() = runTest {
        coEvery { getJointAccountCount() } returns 1

        val result = sut()

        assertEquals(2, result)
    }

    @Test
    fun `EXPECT 4 WHEN three joint accounts exist`() = runTest {
        coEvery { getJointAccountCount() } returns 3

        val result = sut()

        assertEquals(4, result)
    }
}
