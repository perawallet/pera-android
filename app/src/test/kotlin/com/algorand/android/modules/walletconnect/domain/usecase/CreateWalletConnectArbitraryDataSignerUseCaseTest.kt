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
import com.algorand.wallet.account.core.domain.model.TransactionSigner
import com.algorand.wallet.account.core.domain.model.TransactionSigner.SignerNotFound
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CreateWalletConnectArbitraryDataSignerUseCaseTest {

    private val getTransactionSigner: GetTransactionSigner = mockk()
    private val errorProvider: WalletConnectErrorProvider = mockk {
        every { getUnableToSignError() } returns UNABLE_TO_SIGN_ERROR
        every { getMissingSignerError() } returns MISSING_SIGNER_ERROR
    }

    private val sut = CreateWalletConnectArbitraryDataSignerUseCase(getTransactionSigner, errorProvider)

    @Test
    fun `EXPECT DisplayOnly WHEN signer address is blank`() = runTest {
        val signerAddress = ""

        val result = sut(signerAddress)

        assertTrue(result is DisplayOnly)
    }

    @Test
    fun `EXPECT Unsignable WHEN transaction signer is NoAuth`() = runTest {
        coEvery { getTransactionSigner(SIGNER_ADDRESS) } returns SignerNotFound.NoAuth(SIGNER_ADDRESS)

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(MISSING_SIGNER_ERROR, result.error)
    }

    @Test
    fun `EXPECT Unsignable WHEN transaction signer is Rekeyed`() = runTest {
        coEvery { getTransactionSigner(SIGNER_ADDRESS) } returns SignerNotFound.Rekeyed(SIGNER_ADDRESS)

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(MISSING_SIGNER_ERROR, result.error)
    }

    @Test
    fun `EXPECT Unsignable WHEN transaction signer is AccountNotFound`() = runTest {
        coEvery { getTransactionSigner(SIGNER_ADDRESS) } returns SignerNotFound.AccountNotFound(SIGNER_ADDRESS)

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(MISSING_SIGNER_ERROR, result.error)
    }

    @Test
    fun `EXPECT Unsignable WHEN transaction signer is AuthAccountIsNoAuth`() = runTest {
        coEvery { getTransactionSigner(SIGNER_ADDRESS) } returns SignerNotFound.AuthAccountIsNoAuth(SIGNER_ADDRESS)

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(MISSING_SIGNER_ERROR, result.error)
    }

    @Test
    fun `EXPECT Unsignable WHEN transaction signer is AuthAccountSigningDetailsNotFound`() = runTest {
        coEvery { getTransactionSigner(SIGNER_ADDRESS) } returns SignerNotFound.AuthAccountSigningDetailsNotFound(
            address = SIGNER_ADDRESS,
            authAddress = "authAddress"
        )

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(MISSING_SIGNER_ERROR, result.error)
    }

    @Test
    fun `EXPECT Unsignable WHEN transaction signer is AuthAddressNotFound`() = runTest {
        coEvery { getTransactionSigner(SIGNER_ADDRESS) } returns SignerNotFound.AuthAddressNotFound(SIGNER_ADDRESS)

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(MISSING_SIGNER_ERROR, result.error)
    }

    @Test
    fun `EXPECT Unsignable WHEN transaction signer is LedgerBLE`() = runTest {
        coEvery { getTransactionSigner(SIGNER_ADDRESS) } returns TransactionSigner.LedgerBle(
            address = SIGNER_ADDRESS,
            bluetoothAddress = "btAddress"
        )

        val result = sut(SIGNER_ADDRESS)

        assertTrue(result is WalletConnectArbitraryDataSigner.Unsignable)
        assertEquals(UNABLE_TO_SIGN_ERROR, result.error)
    }

    @Test
    fun `EXPECT Signer WHEN transaction signer is Algo25`() = runTest {
        coEvery { getTransactionSigner(SIGNER_ADDRESS) } returns TransactionSigner.Algo25(AUTH_ADDRESS)

        val result = sut(SIGNER_ADDRESS)

        assertEquals(AUTH_ADDRESS, result.address)
    }

    @Test
    fun `EXPECT Signer WHEN transaction signer is HdKey`() = runTest {
        coEvery { getTransactionSigner(SIGNER_ADDRESS) } returns TransactionSigner.HdKey(AUTH_ADDRESS)

        val result = sut(SIGNER_ADDRESS)

        assertEquals(AUTH_ADDRESS, result.address)
    }

    private companion object {
        private const val SIGNER_ADDRESS = "ADDRESS"
        private const val AUTH_ADDRESS = "AUTH_ADDRESS"
        private val MISSING_SIGNER_ERROR = peraFixture<WalletConnectError>()
        private val UNABLE_TO_SIGN_ERROR = peraFixture<WalletConnectError>()
    }
}
