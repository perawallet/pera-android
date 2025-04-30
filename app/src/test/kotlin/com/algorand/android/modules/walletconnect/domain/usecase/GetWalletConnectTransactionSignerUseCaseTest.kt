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

import com.algorand.android.models.WalletConnectAddress
import com.algorand.android.models.WalletConnectTransactionSigner
import com.algorand.test.peraFixture
import com.algorand.wallet.account.core.domain.model.TransactionSigner
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLedgerBleAccount
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetWalletConnectTransactionSignerUseCaseTest {

    private val getAccountRegistrationType: GetAccountRegistrationType = mock()
    private val getTransactionSigner: GetTransactionSigner = mock()
    private val getLedgerBleAccount: GetLedgerBleAccount = mock()

    private val sut = GetWalletConnectTransactionSignerUseCase(
        getAccountRegistrationType,
        getTransactionSigner,
        getLedgerBleAccount
    )

    @Test
    fun `EXPECT null WHEN signer is sender and decoded address is null`() = runTest {
        val signer = WalletConnectTransactionSigner.Sender(WC_ADDRESS.copy(decodedAddress = null))

        val result = sut(signer)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN signer is rekeyed and decoded address is null`() = runTest {
        val signer = WalletConnectTransactionSigner.Rekeyed(WC_ADDRESS.copy(decodedAddress = null))

        val result = sut(signer)

        assertNull(result)
    }

    @Test
    fun `EXPECT algo25 WHEN signer is rekeyed and registration type is algo25`() = runTest {
        whenever(getAccountRegistrationType(DECODED_ADDRESS)).thenReturn(AccountRegistrationType.Algo25)
        val signer = WalletConnectTransactionSigner.Rekeyed(WC_ADDRESS)

        val result = sut(signer)

        val expected = TransactionSigner.Algo25(DECODED_ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT ledger ble WHEN signer is rekeyed and registration type is ledger ble`() = runTest {
        whenever(getAccountRegistrationType(DECODED_ADDRESS)).thenReturn(AccountRegistrationType.LedgerBle)
        whenever(getLedgerBleAccount(DECODED_ADDRESS)).thenReturn(LOCAL_LEDGER_BLE)
        val signer = WalletConnectTransactionSigner.Rekeyed(WC_ADDRESS)

        val result = sut(signer)

        val expected = TransactionSigner.LedgerBle(
            address = DECODED_ADDRESS,
            bluetoothAddress = LOCAL_LEDGER_BLE.deviceMacAddress,
            positionInLedger = LOCAL_LEDGER_BLE.indexInLedger
        )
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN signer is rekeyed, registration type is ledger but ledger details is null`() = runTest {
        whenever(getAccountRegistrationType(DECODED_ADDRESS)).thenReturn(AccountRegistrationType.LedgerBle)
        whenever(getLedgerBleAccount(DECODED_ADDRESS)).thenReturn(null)
        val signer = WalletConnectTransactionSigner.Rekeyed(WC_ADDRESS)

        val result = sut(signer)

        assertNull(result)
    }

    @Test
    fun `EXPECT no auth WHEN signer is rekeyed and registration type is no auth`() = runTest {
        whenever(getAccountRegistrationType(DECODED_ADDRESS)).thenReturn(AccountRegistrationType.NoAuth)
        val signer = WalletConnectTransactionSigner.Rekeyed(WC_ADDRESS)

        val result = sut(signer)

        val expected = TransactionSigner.SignerNotFound.NoAuth(DECODED_ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT HdKey WHEN signer is rekeyed and registration type is hd key`() = runTest {
        whenever(getAccountRegistrationType(DECODED_ADDRESS)).thenReturn(AccountRegistrationType.HdKey)
        val signer = WalletConnectTransactionSigner.Rekeyed(WC_ADDRESS)

        val result = sut(signer)

        val expected = TransactionSigner.HdKey(DECODED_ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT null WHEN signer is rekeyed and auth account registration type is null`() = runTest {
        whenever(getAccountRegistrationType(DECODED_ADDRESS)).thenReturn(null)
        val signer = WalletConnectTransactionSigner.Rekeyed(WC_ADDRESS)

        val result = sut(signer)

        assertNull(result)
    }

    @Test
    fun `EXPECT sender signer WHEN wc signer is sender address`() = runTest {
        whenever(getTransactionSigner(DECODED_ADDRESS)).thenReturn(TransactionSigner.Algo25(DECODED_ADDRESS))
        val signer = WalletConnectTransactionSigner.Sender(WC_ADDRESS)

        val result = sut(signer)

        val expected = TransactionSigner.Algo25(DECODED_ADDRESS)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT nul WHEN wc signer is not rekeyed or sender`() = runTest {
        val unsignable = WalletConnectTransactionSigner.Unsignable(peraFixture())
        val multisig = WalletConnectTransactionSigner.Multisig(peraFixture())
        val displayOnly = WalletConnectTransactionSigner.DisplayOnly

        val resultUnsignable = sut(unsignable)
        val resultMultisig = sut(multisig)
        val resultDisplayOnly = sut(displayOnly)

        assertNull(resultUnsignable)
        assertNull(resultMultisig)
        assertNull(resultDisplayOnly)
    }

    private companion object {
        private val LOCAL_LEDGER_BLE = peraFixture<LocalAccount.LedgerBle>()

        private const val DECODED_ADDRESS = "decodedAddress"
        private const val ADDRESS_BASE64 = "encodedAddress"
        private val WC_ADDRESS = WalletConnectAddress(
            addressBase64 = ADDRESS_BASE64,
            decodedAddress = DECODED_ADDRESS
        )
    }
}
