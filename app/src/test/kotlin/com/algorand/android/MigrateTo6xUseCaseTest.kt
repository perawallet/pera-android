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

package com.algorand.android.modules.settings.ui.usecase

import com.algorand.android.models.Account
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.settings.domain.usecase.MigrateTo6xUseCase
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.usecase.GetLocalAccountsFromSharedPrefUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.analytics.domain.service.PeraExceptionLogger
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MigrateTo6xUseCaseTest {
    private var getLocalAccountsFromSharedPrefUseCase:
            GetLocalAccountsFromSharedPrefUseCase = mockk()
    private var aesPlatformManager: AESPlatformManager = mockk()
    private var accountAdditionUseCase: AccountAdditionUseCase = mockk()
    private var peraExceptionLogger: PeraExceptionLogger = mockk()
    private var sut: MigrateTo6xUseCase =
        MigrateTo6xUseCase(
            getLocalAccountsFromSharedPrefUseCase,
            aesPlatformManager,
            accountAdditionUseCase,
            peraExceptionLogger
        )

    @Test
    fun `EXPECT accounts migrated successfully WHEN local accounts exist`() = runTest {
        val localAccounts = listOf(standardAccount, watchAccount, ledgerAccount, rekeyedAccount)
        coEvery {
            getLocalAccountsFromSharedPrefUseCase.getLocalAccountsFromSharedPref()
        } returns localAccounts
        every { aesPlatformManager.encryptByteArray(SECRET_KEY) } returns ENCRYPTED_SECRET_KEY

        val capturedAccountCreations = mutableListOf<AccountCreation>()
        coEvery {
            accountAdditionUseCase.addNewAccount(capture(capturedAccountCreations))
        } returns Unit

        val result = sut.invoke()

        assertTrue(result is PeraResult.Success)
        assertEquals(4, (result as PeraResult.Success).data)
        assertEquals(4, capturedAccountCreations.size)
    }

    @Test
    fun `EXPECT standard account migrated correctly`() = runTest {
        coEvery {
            getLocalAccountsFromSharedPrefUseCase.getLocalAccountsFromSharedPref()
        } returns listOf(standardAccount)
        every { aesPlatformManager.encryptByteArray(SECRET_KEY) } returns ENCRYPTED_SECRET_KEY

        val capturedAccount = mutableListOf<AccountCreation>()
        coEvery {
            accountAdditionUseCase.addNewAccount(capture(capturedAccount))
        } returns Unit

        val result = sut.invoke()

        assertTrue(result is PeraResult.Success)
        assertEquals(1, (result as PeraResult.Success).data)

        val actualAccount = capturedAccount.first()
        val expectedAccountCreation = AccountCreation(
            address = "addr1",
            customName = "Account 1",
            type = AccountCreation.Type.Algo25(encryptedSecretKey = ENCRYPTED_SECRET_KEY),
            creationType = CreationType.RECOVER,
            isBackedUp = true
        )

        with(actualAccount) {
            assertEquals(expectedAccountCreation.address, address)
            assertEquals(expectedAccountCreation.customName, customName)
            assertTrue(type is AccountCreation.Type.Algo25)
            assertTrue((type as AccountCreation.Type.Algo25).encryptedSecretKey.contentEquals(ENCRYPTED_SECRET_KEY))
            assertEquals(expectedAccountCreation.creationType, creationType)
            assertEquals(expectedAccountCreation.isBackedUp, isBackedUp)
        }
    }

    @Test
    fun `EXPECT watch account migrated correctly`() = runTest {
        coEvery {
            getLocalAccountsFromSharedPrefUseCase.getLocalAccountsFromSharedPref()
        } returns listOf(watchAccount)

        val capturedAccount = mutableListOf<AccountCreation>()
        coEvery {
            accountAdditionUseCase.addNewAccount(capture(capturedAccount))
        } returns Unit

        val result = sut.invoke()

        assertTrue(result is PeraResult.Success)
        assertEquals(1, (result as PeraResult.Success).data)

        val actualAccount = capturedAccount.first()
        val expectedAccount = AccountCreation(
            address = "addr2",
            customName = "Account 2",
            type = AccountCreation.Type.NoAuth,
            creationType = CreationType.WATCH,
            isBackedUp = false
        )

        with(actualAccount) {
            assertEquals(expectedAccount.address, address)
            assertEquals(expectedAccount.customName, customName)
            assertTrue(actualAccount.type is AccountCreation.Type.NoAuth)
            assertEquals(expectedAccount.creationType, creationType)
            assertEquals(expectedAccount.isBackedUp, isBackedUp)
        }
    }

    @Test
    fun `EXPECT ledger account migrated correctly`() = runTest {
        coEvery {
            getLocalAccountsFromSharedPrefUseCase.getLocalAccountsFromSharedPref()
        } returns listOf(ledgerAccount)

        val capturedAccount = mutableListOf<AccountCreation>()
        coEvery {
            accountAdditionUseCase.addNewAccount(capture(capturedAccount))
        } returns Unit

        val result = sut.invoke()

        assertTrue(result is PeraResult.Success)
        assertEquals(1, (result as PeraResult.Success).data)

        val actualAccount = capturedAccount.first()
        val expectedAccount = AccountCreation(
            address = "addr3",
            customName = "Account 3",
            type = AccountCreation.Type.LedgerBle(
                deviceMacAddress = "AA:BB:CC:DD:EE:FF",
                indexInLedger = 0,
                bluetoothName = "Ledger Nano X"
            ),
            creationType = CreationType.LEDGER,
            isBackedUp = true
        )

        with(actualAccount) {
            assertEquals(expectedAccount.address, address)
            assertEquals(expectedAccount.customName, customName)
            assertTrue(type is AccountCreation.Type.LedgerBle)

            val ledgerType = type as AccountCreation.Type.LedgerBle
            val expectedLedgerType = expectedAccount.type as AccountCreation.Type.LedgerBle

            assertEquals(expectedLedgerType.deviceMacAddress, ledgerType.deviceMacAddress)
            assertEquals(expectedLedgerType.indexInLedger, ledgerType.indexInLedger)
            assertEquals(expectedLedgerType.bluetoothName, ledgerType.bluetoothName)

            assertEquals(expectedAccount.creationType, creationType)
            assertEquals(expectedAccount.isBackedUp, isBackedUp)
        }
    }

    @Test
    fun `EXPECT rekeyed account migrated correctly`() = runTest {
        coEvery {
            getLocalAccountsFromSharedPrefUseCase.getLocalAccountsFromSharedPref()
        } returns listOf(rekeyedAccount)

        val capturedAccount = mutableListOf<AccountCreation>()
        coEvery {
            accountAdditionUseCase.addNewAccount(capture(capturedAccount))
        } returns Unit

        val result = sut.invoke()

        assertTrue(result is PeraResult.Success)
        assertEquals(1, (result as PeraResult.Success).data)

        val actualAccount = capturedAccount.first()
        val expectedAccount = AccountCreation(
            address = "addr4",
            customName = "Account 4",
            type = AccountCreation.Type.NoAuth,
            creationType = CreationType.WATCH,
            isBackedUp = true
        )

        with(actualAccount) {
            assertEquals(expectedAccount.address, address)
            assertEquals(expectedAccount.customName, customName)
            assertTrue(type is AccountCreation.Type.NoAuth)
            assertEquals(expectedAccount.creationType, creationType)
            assertEquals(expectedAccount.isBackedUp, isBackedUp)
        }
    }

    @Test
    fun `EXPECT zero accounts migrated WHEN no local accounts exist`() = runTest {
        coEvery {
            getLocalAccountsFromSharedPrefUseCase.getLocalAccountsFromSharedPref()
        } returns emptyList()

        val result = sut.invoke()

        assertTrue(result is PeraResult.Success)
        assertEquals(0, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT error result WHEN exception is thrown`() = runTest {
        val exception = RuntimeException("Test exception")
        coEvery {
            getLocalAccountsFromSharedPrefUseCase.getLocalAccountsFromSharedPref()
        } throws exception

        coEvery {
            peraExceptionLogger.logException(exception)
        } returns Unit

        val result = sut.invoke()

        assertTrue(result is PeraResult.Error)
        assertEquals(exception, (result as PeraResult.Error).exception)
    }

    private companion object {
        val SECRET_KEY = byteArrayOf(1, 2, 3, 4)
        val ENCRYPTED_SECRET_KEY = byteArrayOf(5, 6, 7, 8)

        val standardAccount = Account(
            address = "addr1",
            name = "Account 1",
            index = 0,
            isBackedUp = true,
            detail = mockk<Account.Detail.Standard> {
                every { secretKey } returns SECRET_KEY
            }
        )

        val watchAccount = Account(
            address = "addr2",
            name = "Account 2",
            index = 1,
            isBackedUp = false,
            detail = mockk<Account.Detail.Watch>()
        )

        val ledgerAccount = Account(
            address = "addr3",
            name = "Account 3",
            index = 2,
            isBackedUp = true,
            detail = mockk<Account.Detail.Ledger>().apply {
                every { bluetoothAddress } returns "AA:BB:CC:DD:EE:FF"
                every { positionInLedger } returns 0
                every { bluetoothName } returns "Ledger Nano X"
            }
        )

        val rekeyedAccount = Account(
            address = "addr4",
            name = "Account 4",
            index = 3,
            isBackedUp = true,
            detail = mockk<Account.Detail.Rekeyed> {
                every { secretKey } returns null
            }
        )
    }
}
