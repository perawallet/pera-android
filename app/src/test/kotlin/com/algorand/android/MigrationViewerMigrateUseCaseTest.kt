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

package com.algorand.android.ui.settings.migrationviewer

import com.algorand.android.models.Account
import com.algorand.android.models.AccountCreation
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.usecase.GetLocalAccountsFromSharedPrefUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.foundation.PeraResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MigrationViewerMigrateUseCaseTest {
    private lateinit var getLocalAccountsFromSharedPrefUseCase: GetLocalAccountsFromSharedPrefUseCase
    private lateinit var aesPlatformManager: AESPlatformManager
    private lateinit var accountAdditionUseCase: AccountAdditionUseCase

    private lateinit var sut: MigrationViewerMigrateUseCase

    @Before
    fun setup() {
        getLocalAccountsFromSharedPrefUseCase = mockk()
        aesPlatformManager = mockk()
        accountAdditionUseCase = mockk(relaxed = true)

        sut = MigrationViewerMigrateUseCase(
            getLocalAccountsFromSharedPrefUseCase,
            aesPlatformManager,
            accountAdditionUseCase
        )
    }

    @SuppressWarnings("LongMethod")
    @Test
    fun `EXPECT accounts migrated successfully WHEN local accounts exist`() = runTest {
        val secretKey = byteArrayOf(1, 2, 3, 4)
        val encryptedSecretKey = byteArrayOf(5, 6, 7, 8)

        val standardAccount = mockk<Account>()
        every { standardAccount.address } returns "addr1"
        every { standardAccount.name } returns "Account 1"
        every { standardAccount.index } returns 0
        every { standardAccount.isBackedUp } returns true
        every { standardAccount.detail } returns mockk<Account.Detail.Standard>()
        every { standardAccount.getSecretKey() } returns secretKey

        val watchAccount = mockk<Account>()
        every { watchAccount.address } returns "addr2"
        every { watchAccount.name } returns "Account 2"
        every { watchAccount.index } returns 1
        every { watchAccount.isBackedUp } returns false
        every { watchAccount.detail } returns mockk<Account.Detail.Watch>()
        every { watchAccount.getSecretKey() } returns null

        val ledgerDetail = mockk<Account.Detail.Ledger>()
        every { ledgerDetail.bluetoothAddress } returns "AA:BB:CC:DD:EE:FF"
        every { ledgerDetail.positionInLedger } returns 0
        every { ledgerDetail.bluetoothName } returns "Ledger Nano X"

        val ledgerAccount = mockk<Account>()
        every { ledgerAccount.address } returns "addr3"
        every { ledgerAccount.name } returns "Account 3"
        every { ledgerAccount.index } returns 2
        every { ledgerAccount.isBackedUp } returns true
        every { ledgerAccount.detail } returns ledgerDetail
        every { ledgerAccount.getSecretKey() } returns null

        val rekeyedAccount = mockk<Account>()
        every { rekeyedAccount.address } returns "addr4"
        every { rekeyedAccount.name } returns "Account 4"
        every { rekeyedAccount.index } returns 3
        every { rekeyedAccount.isBackedUp } returns true
        every { rekeyedAccount.detail } returns mockk<Account.Detail.Rekeyed>()
        every { rekeyedAccount.getSecretKey() } returns null

        val localAccounts = listOf(standardAccount, watchAccount, ledgerAccount, rekeyedAccount)

        coEvery { getLocalAccountsFromSharedPrefUseCase
            .getLocalAccountsFromSharedPref() } returns localAccounts

        every { aesPlatformManager.encryptByteArray(secretKey) } returns encryptedSecretKey

        val result = sut.invoke()

        assertTrue(result is PeraResult.Success)
        assertEquals(4, (result as PeraResult.Success).data)

        coVerify {
            accountAdditionUseCase.addNewAccount(match {
                it.address == "addr1" &&
                        it.type is AccountCreation.Type.Algo25
            })
        }

        coVerify {
            accountAdditionUseCase.addNewAccount(match {
                it.address == "addr2" &&
                        it.type is AccountCreation.Type.NoAuth &&
                        it.creationType == CreationType.WATCH
            })
        }

        coVerify {
            accountAdditionUseCase.addNewAccount(match {
                it.address == "addr3" &&
                        it.type is AccountCreation.Type.LedgerBle
            })
        }

        coVerify {
            accountAdditionUseCase.addNewAccount(match {
                it.address == "addr4" &&
                        it.type is AccountCreation.Type.NoAuth
            })
        }
    }

    @Test
    fun `EXPECT zero accounts migrated WHEN no local accounts exist`() = runTest {
        coEvery { getLocalAccountsFromSharedPrefUseCase
            .getLocalAccountsFromSharedPref() } returns emptyList()

        val result = sut.invoke()

        assertTrue(result is PeraResult.Success)
        assertEquals(0, (result as PeraResult.Success).data)
    }

    @Test
    fun `EXPECT error result WHEN exception is thrown`() = runBlocking {
        val exception = RuntimeException("Test exception")
        coEvery { getLocalAccountsFromSharedPrefUseCase
            .getLocalAccountsFromSharedPref() } throws exception

        val result = sut.invoke()

        assertTrue(result is PeraResult.Error)
        assertEquals(exception, (result as PeraResult.Error).exception)
    }
}
