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

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateNoAuthAccountToAlgo25UseCaseTest {

    private val deleteLocalAccount: DeleteLocalAccount = mockk(relaxed = true)
    private val createAlgo25Account: CreateAlgo25Account = mockk(relaxed = true)

    private val sut = UpdateNoAuthAccountToAlgo25UseCase(deleteLocalAccount, createAlgo25Account)

    @Test
    fun `EXPECT noAuthAccount to be deleted and new Algo25Account to be created`() = runTest {
        sut(ADDRESS, SECRET_KEY)

        coVerify { deleteLocalAccount(ADDRESS) }
        coVerify { createAlgo25Account(ADDRESS, SECRET_KEY) }
    }

    companion object {
        private const val ADDRESS = "ADDRESS"
        private val SECRET_KEY = byteArrayOf()
    }
}
