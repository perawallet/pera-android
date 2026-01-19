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

package com.algorand.android.modules.addaccount.intro.domain.usecase

import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.intro.domain.exception.AccountCreationException
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

internal class CreateAlgo25AccountUseCaseTest {

    private val algoAccountSdk: AlgoAccountSdk = mockk()
    private val aesPlatformManager: AESPlatformManager = mockk()

    private val sut = CreateAlgo25AccountUseCase(
        algoAccountSdk = algoAccountSdk,
        aesPlatformManager = aesPlatformManager
    )

    @Test
    fun `EXPECT error WHEN sdk returns null`() = runTest {
        every { algoAccountSdk.createAlgo25Account() } returns null

        val result = sut()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is AccountCreationException)
    }

    @Test
    fun `EXPECT AccountCreationException WHEN sdk fails`() = runTest {
        every { algoAccountSdk.createAlgo25Account() } returns null

        val result = sut()

        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).exception
        assertTrue(exception is AccountCreationException)
        assertTrue(exception.message?.contains("Failed to generate") == true)
    }
}
