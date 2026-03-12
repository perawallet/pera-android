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

package com.algorand.android.modules.walletconnect.domain.usecase

import com.algorand.android.models.WalletConnectArbitraryDataSigner
import com.algorand.android.models.WalletConnectArbitraryDataSigner.DisplayOnly
import com.algorand.android.modules.walletconnect.domain.WalletConnectErrorProvider
import com.algorand.android.modules.walletconnect.domain.model.WalletConnectError
import com.algorand.test.peraFixture
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateWalletConnectArbitraryDataSignerUseCaseTest {

    private val getLocalAccount: GetLocalAccount = mockk()
    private val errorProvider: WalletConnectErrorProvider = mockk {
        every { getUnableToSignError() } returns UNABLE_TO_SIGN_ERROR
        every { getMissingSignerError() } returns MISSING_SIGNER_ERROR
        every { getMultisigTransactionError() } returns MULTISIG_TRANSACTION_ERROR
    }

    private val sut = CreateWalletConnectArbitraryDataSignerUseCase(getLocalAccount, errorProvider)

    @Test
    fun `EXPECT DisplayOnly WHEN signer address is blank`() = runTest {
        val signerAddress = ""

        val result = sut(signerAddress)

        assertTrue(result is DisplayOnly)
    }

    @Test
    fun `EXPECT Signer with original address WHEN local account is Algo25`() = runTest {
        coEvery { getLocalAccount(SIGNER_ADDRESS) } returns LocalAccount.Algo25(SIGNER_ADDRESS)

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Signer)
        assertEquals(SIGNER_ADDRESS, result.address)
    }

    @Test
    fun `EXPECT Signer with original address WHEN local account is HdKey`() = runTest {
        coEvery { getLocalAccount(SIGNER_ADDRESS) } returns LocalAccount.HdKey(
            algoAddress = SIGNER_ADDRESS,
            publicKey = byteArrayOf(),
            seedId = 0,
            account = 0,
            change = 0,
            keyIndex = 0,
            derivationType = 0
        )

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Signer)
        assertEquals(SIGNER_ADDRESS, result.address)
    }

    @Test
    fun `EXPECT Unsignable WHEN local account is Joint`() = runTest {
        coEvery { getLocalAccount(SIGNER_ADDRESS) } returns LocalAccount.Joint(
            algoAddress = SIGNER_ADDRESS,
            participantAddresses = emptyList(),
            threshold = 1,
            version = 1
        )

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(MULTISIG_TRANSACTION_ERROR, result.error)
    }

    @Test
    fun `EXPECT Unsignable WHEN local account is LedgerBle`() = runTest {
        coEvery { getLocalAccount(SIGNER_ADDRESS) } returns LocalAccount.LedgerBle(
            algoAddress = SIGNER_ADDRESS,
            deviceMacAddress = "btAddress",
            bluetoothName = null,
            indexInLedger = 0
        )

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(UNABLE_TO_SIGN_ERROR, result.error)
    }

    @Test
    fun `EXPECT Unsignable WHEN local account is NoAuth`() = runTest {
        coEvery { getLocalAccount(SIGNER_ADDRESS) } returns LocalAccount.NoAuth(SIGNER_ADDRESS)

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(MISSING_SIGNER_ERROR, result.error)
    }

    @Test
    fun `EXPECT Unsignable WHEN local account is not found`() = runTest {
        coEvery { getLocalAccount(SIGNER_ADDRESS) } returns null

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(MISSING_SIGNER_ERROR, result.error)
    }

    private companion object {
        private const val SIGNER_ADDRESS = "ADDRESS"
        private val MISSING_SIGNER_ERROR = peraFixture<WalletConnectError>()
        private val UNABLE_TO_SIGN_ERROR = peraFixture<WalletConnectError>()
        private val MULTISIG_TRANSACTION_ERROR = peraFixture<WalletConnectError>()
    }
}
