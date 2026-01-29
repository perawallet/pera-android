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
import com.algorand.android.ui.onboarding.creation.mapper.AccountCreationHdKeyTypeMapper
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Test

internal class CreateHdKeyAccountUseCaseTest {

    private val bip39WalletProvider: Bip39WalletProvider = mockk()
    private val accountCreationHdKeyTypeMapper: AccountCreationHdKeyTypeMapper = mockk()

    private val sut = CreateHdKeyAccountUseCase(
        bip39WalletProvider = bip39WalletProvider,
        accountCreationHdKeyTypeMapper = accountCreationHdKeyTypeMapper
    )

    @Test
    fun `EXPECT error WHEN wallet creation throws exception`() {
        every { bip39WalletProvider.createBip39Wallet() } throws RuntimeException("Wallet creation failed")

        val result = sut()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is AccountCreationException)
    }

    @Test
    fun `EXPECT error message contains exception details WHEN wallet creation fails`() {
        val errorMessage = "Entropy generation failed"
        every { bip39WalletProvider.createBip39Wallet() } throws RuntimeException(errorMessage)

        val result = sut()

        assertTrue(result is Result.Error)
        val exception = (result as Result.Error).exception
        assertTrue(exception is AccountCreationException)
        assertTrue(exception.message?.contains(errorMessage) == true)
    }
}
