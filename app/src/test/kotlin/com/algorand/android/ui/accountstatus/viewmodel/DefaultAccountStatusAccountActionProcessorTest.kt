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

package com.algorand.android.ui.accountstatus.viewmodel

import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.parity.domain.model.AlgoAmountValue
import com.algorand.android.modules.parity.domain.model.ParityValue
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content.AccountAction
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.model.AccountType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

internal class DefaultAccountStatusAccountActionProcessorTest {

    private val processor = DefaultAccountStatusAccountActionProcessor()

    @Test
    fun `EXPECT RekeyToLedger and RekeyToStandard WHEN account type is Algo25`() = runTest {
        val accountLite = createAccountLite(AccountType.Algo25, AccountRegistrationType.Algo25)

        val result = processor.getAccountActions(accountLite)

        assertTrue(result.contains(AccountAction.RekeyToLedger))
        assertTrue(result.contains(AccountAction.RekeyToStandard))
        assertTrue(result.none { it is AccountAction.RekeyToJointAccount })
    }

    @Test
    fun `EXPECT RekeyToLedger and RekeyToStandard WHEN account type is HdKey`() = runTest {
        val accountLite = createAccountLite(AccountType.HdKey, AccountRegistrationType.HdKey)

        val result = processor.getAccountActions(accountLite)

        assertTrue(result.contains(AccountAction.RekeyToLedger))
        assertTrue(result.contains(AccountAction.RekeyToStandard))
        assertTrue(result.none { it is AccountAction.RekeyToJointAccount })
    }

    @Test
    fun `EXPECT RekeyToLedger and RekeyToStandard WHEN account type is LedgerBle`() = runTest {
        val accountLite = createAccountLite(AccountType.LedgerBle, AccountRegistrationType.LedgerBle)

        val result = processor.getAccountActions(accountLite)

        assertTrue(result.contains(AccountAction.RekeyToLedger))
        assertTrue(result.contains(AccountAction.RekeyToStandard))
        assertTrue(result.none { it is AccountAction.RekeyToJointAccount })
    }

    @Test
    fun `EXPECT only RekeyToJointAccount WHEN account type is Joint`() = runTest {
        val accountLite = createAccountLite(AccountType.Joint, AccountRegistrationType.Joint)

        val result = processor.getAccountActions(accountLite)

        assertTrue(result.contains(AccountAction.RekeyToJointAccount))
        assertTrue(result.none { it is AccountAction.RekeyToLedger })
        assertTrue(result.none { it is AccountAction.RekeyToStandard })
    }

    @Test
    fun `EXPECT only RekeyToJointAccount WHEN rekeyed joint account type is RekeyedAuth`() = runTest {
        val accountLite = createAccountLite(AccountType.RekeyedAuth, AccountRegistrationType.Joint)

        val result = processor.getAccountActions(accountLite)

        assertTrue(result.contains(AccountAction.RekeyToJointAccount))
        assertTrue(result.none { it is AccountAction.RekeyToLedger })
        assertTrue(result.none { it is AccountAction.RekeyToStandard })
    }

    @Test
    fun `EXPECT no rekey actions WHEN account type is Rekeyed`() = runTest {
        val accountLite = createAccountLite(AccountType.Rekeyed, AccountRegistrationType.NoAuth)

        val result = processor.getAccountActions(accountLite)

        assertTrue(result.none { it is AccountAction.RekeyToLedger })
        assertTrue(result.none { it is AccountAction.RekeyToStandard })
        assertTrue(result.none { it is AccountAction.RekeyToJointAccount })
    }

    @Test
    fun `EXPECT no rekey actions WHEN account type is NoAuth`() = runTest {
        val accountLite = createAccountLite(AccountType.NoAuth, AccountRegistrationType.NoAuth)

        val result = processor.getAccountActions(accountLite)

        assertTrue(result.none { it is AccountAction.RekeyToLedger })
        assertTrue(result.none { it is AccountAction.RekeyToStandard })
        assertTrue(result.none { it is AccountAction.RekeyToJointAccount })
    }

    @Test
    fun `EXPECT RescanRekeyedAddresses WHEN registration type has signer details`() = runTest {
        val accountLite = createAccountLite(AccountType.Algo25, AccountRegistrationType.Algo25)

        val result = processor.getAccountActions(accountLite)

        assertTrue(result.contains(AccountAction.RescanRekeyedAddresses))
    }

    @Test
    fun `EXPECT no RescanRekeyedAddresses WHEN registration type has no signer details`() = runTest {
        val accountLite = createAccountLite(AccountType.NoAuth, AccountRegistrationType.NoAuth)

        val result = processor.getAccountActions(accountLite)

        assertTrue(result.none { it is AccountAction.RescanRekeyedAddresses })
    }

    @Test
    fun `EXPECT RekeyToJointAccount and RescanRekeyedAddresses WHEN Joint account with signer details`() = runTest {
        val accountLite = createAccountLite(AccountType.Joint, AccountRegistrationType.Joint)

        val result = processor.getAccountActions(accountLite)

        assertEquals(2, result.size)
        assertTrue(result.contains(AccountAction.RekeyToJointAccount))
        assertTrue(result.contains(AccountAction.RescanRekeyedAddresses))
    }

    @Test
    fun `EXPECT no rekey actions WHEN cachedInfo is null`() = runTest {
        val accountLite = AccountLite(
            address = TEST_ADDRESS,
            customName = "Test",
            isBackedUp = true,
            cachedInfo = null,
            sortIndex = 0,
            registrationType = AccountRegistrationType.Algo25
        )

        val result = processor.getAccountActions(accountLite)

        assertTrue(result.none { it is AccountAction.RekeyToLedger })
        assertTrue(result.none { it is AccountAction.RekeyToStandard })
        assertTrue(result.none { it is AccountAction.RekeyToJointAccount })
    }

    private fun createAccountLite(
        accountType: AccountType,
        registrationType: AccountRegistrationType
    ): AccountLite {
        return AccountLite(
            address = TEST_ADDRESS,
            customName = "Test Account",
            isBackedUp = true,
            cachedInfo = AccountLite.CachedInfo(
                type = accountType,
                algoAmountValue = AlgoAmountValue(
                    amount = BigInteger.ZERO,
                    parityValueInSelectedCurrency = ParityValue(BigDecimal.ZERO, "$"),
                    parityValueInSecondaryCurrency = ParityValue(BigDecimal.ZERO, "ALGO"),
                    usdValue = BigDecimal.ZERO
                ),
                primaryAccountValue = BigDecimal.ZERO,
                secondaryAccountValue = BigDecimal.ZERO,
                assetCount = 0,
                minRequiredBalance = BigInteger.ZERO,
                rekeyAuthAddress = null,
                rekeyAuthRegistrationType = null
            ),
            sortIndex = 0,
            registrationType = registrationType
        )
    }

    private companion object {
        const val TEST_ADDRESS = "TEST_ADDRESS_12345"
    }
}
