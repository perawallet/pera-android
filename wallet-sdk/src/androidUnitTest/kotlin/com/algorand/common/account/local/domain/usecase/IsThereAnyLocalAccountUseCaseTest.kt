/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.account.local.domain.usecase

import com.algorand.common.account.local.domain.model.LocalAccount
import com.algorand.common.testing.peraFixture
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IsThereAnyLocalAccountUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mockk()

    private val sut = IsThereAnyLocalAccountUseCase(getLocalAccounts)

    @Test
    fun `EXPECT true WHEN there are local accounts`() = runTest {
        coEvery { getLocalAccounts() } returns LOCAL_ACCOUNTS

        val result = sut()

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN there are not any local accounts`() = runTest {
        coEvery { getLocalAccounts() } returns emptyList()

        val result = sut()

        assertFalse(result)
    }

    companion object {
        private val LOCAL_ACCOUNTS = peraFixture<List<LocalAccount>>()
    }
}
