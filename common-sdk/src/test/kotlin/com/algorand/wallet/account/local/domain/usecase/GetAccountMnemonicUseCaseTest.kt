/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.account.local.domain.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.account.local.domain.model.AccountMnemonic
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAccountMnemonicUseCaseTest {

    private val getLocalAccount: GetLocalAccount = mockk()
    private val getAlgo25SecretKey: GetAlgo25SecretKey = mockk()
    private val getHdEntropy: GetHdEntropy = mockk()
    private val algoAccountSdk: AlgoAccountSdk = mockk()
    private val bip39Sdk: PeraBip39Sdk = mockk()

    private val sut = GetAccountMnemonicUseCase(
        getLocalAccount,
        getAlgo25SecretKey,
        getHdEntropy,
        algoAccountSdk,
        bip39Sdk
    )

    @Test
    fun `EXPECT error WHEN account type is neither Algo25 nor HdKey`() = runTest {
        coEvery { getLocalAccount(ADDRESS) } returns NO_AUTH

        val result = sut(ADDRESS)

        assertTrue(result.isFailed)
    }

    @Test
    fun `EXPECT error WHEN account type is Algo25 and secret key is null`() = runTest {
        coEvery { getLocalAccount(ADDRESS) } returns ALGO_25
        coEvery { getAlgo25SecretKey(ADDRESS) } returns null

        val result = sut(ADDRESS)

        assertTrue(result.isFailed)
    }

    @Test
    fun `EXPECT error WHEN account type is Algo25 and mnemonic is blank`() = runTest {
        coEvery { getLocalAccount(ADDRESS) } returns ALGO_25
        coEvery { getAlgo25SecretKey(ADDRESS) } returns SECRET_KEY
        coEvery { algoAccountSdk.getMnemonicFromAlgo25SecretKey(SECRET_KEY) } returns ""

        val result = sut(ADDRESS)

        assertTrue(result.isFailed)
    }

    @Test
    fun `EXPECT error WHEN account type is Algo25 and mnemonic is null`() = runTest {
        coEvery { getLocalAccount(ADDRESS) } returns ALGO_25
        coEvery { getAlgo25SecretKey(ADDRESS) } returns SECRET_KEY
        coEvery { algoAccountSdk.getMnemonicFromAlgo25SecretKey(SECRET_KEY) } returns null

        val result = sut(ADDRESS)

        assertTrue(result.isFailed)
    }

    @Test
    fun `EXPECT mnemonic WHEN account type is Algo25 and mnemonic is valid`() = runTest {
        coEvery { getLocalAccount(ADDRESS) } returns ALGO_25
        coEvery { getAlgo25SecretKey(ADDRESS) } returns SECRET_KEY
        coEvery { algoAccountSdk.getMnemonicFromAlgo25SecretKey(SECRET_KEY) } returns MNEMONIC

        val result = sut(ADDRESS)

        val expected = PeraResult.Success(AccountMnemonic(MNEMONIC_WORDS, AccountMnemonic.AccountType.Algo25))
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT error WHEN account type is HdKey and private key is null`() = runTest {
        coEvery { getLocalAccount(ADDRESS) } returns HD_KEY
        coEvery { getHdEntropy(HD_KEY.seedId) } returns null

        val result = sut(ADDRESS)

        assertTrue(result.isFailed)
    }

    @Test
    fun `EXPECT error WHEN account type is HdKey and mnemonic is blank`() = runTest {
        coEvery { getLocalAccount(ADDRESS) } returns HD_KEY
        coEvery { getHdEntropy(HD_KEY.seedId) } returns HD_ENTROPY
        coEvery { bip39Sdk.getMnemonicFromEntropy(HD_ENTROPY) } returns ""

        val result = sut(ADDRESS)

        assertTrue(result.isFailed)
    }

    @Test
    fun `EXPECT error WHEN account type is HdKey and mnemonic is null`() = runTest {
        coEvery { getLocalAccount(ADDRESS) } returns HD_KEY
        coEvery { getHdEntropy(HD_KEY.seedId) } returns HD_ENTROPY
        coEvery { bip39Sdk.getMnemonicFromEntropy(HD_ENTROPY) } returns null

        val result = sut(ADDRESS)

        assertTrue(result.isFailed)
    }

    @Test
    fun `EXPECT mnemonic WHEN account type is HdKey and mnemonic is valid`() = runTest {
        coEvery { getLocalAccount(ADDRESS) } returns HD_KEY
        coEvery { getHdEntropy(HD_KEY.seedId) } returns HD_ENTROPY
        coEvery { bip39Sdk.getMnemonicFromEntropy(HD_ENTROPY) } returns MNEMONIC

        val result = sut(ADDRESS)

        val expected = PeraResult.Success(AccountMnemonic(MNEMONIC_WORDS, AccountMnemonic.AccountType.HdKey))
        assertEquals(expected, result)
    }

    private companion object {
        const val ADDRESS = "ADDRESS"
        const val MNEMONIC = "word1 word2"
        val MNEMONIC_WORDS = listOf("word1", "word2")
        val SECRET_KEY = byteArrayOf(1, 2, 3)
        val NO_AUTH = peraFixture<LocalAccount.NoAuth>()
        val ALGO_25 = peraFixture<LocalAccount.Algo25>()
        val HD_KEY = peraFixture<LocalAccount.HdKey>()
        val HD_ENTROPY = byteArrayOf(1, 2, 3)
    }
}
