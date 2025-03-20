@file:Suppress("MaxLineLength", "MagicNumber")
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

package com.algorand.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.algorand.android.models.Account
import com.algorand.android.models.AccountDetail
import com.algorand.android.models.AccountInformation
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.utils.toShortenedAddress
import java.math.BigInteger
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AccountDisplayNameTest {

    private lateinit var instrumentationContext: Context

    @Before
    fun setupTestEnvironment() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun isCreatingUnnamedAndUnmatchedNFDomainStandardAccountDisplayNameWorks() {
        val account = Account.create(
            publicKey = TEST_ACCOUNT_ADDRESS,
            detail = Account.Detail.Standard(byteArrayOf())
        )
        val accountInformation = createTestAccountInformation()
        val accountDetail = createTestAccountDetail(
            account = account,
            accountInformation = accountInformation,
            nameServiceName = null
        )
        val accountDisplayName = createAccountDisplayName(accountDetail)
        val isPrimaryNameMatches =
            accountDisplayName.primaryDisplayName == account.address.toShortenedAddress()
        val isSecondaryNameMatches =
            accountDisplayName.secondaryDisplayName == null
        assert(isPrimaryNameMatches && isSecondaryNameMatches)
    }

    @Test
    fun isCreatingUnnamedAndUnmatchedNFDomainWatchAccountDisplayNameWorks() {
        val account = Account.create(
            publicKey = TEST_ACCOUNT_ADDRESS,
            detail = Account.Detail.Watch
        )
        val accountInformation = createTestAccountInformation()
        val accountDetail = createTestAccountDetail(
            account = account,
            accountInformation = accountInformation,
            nameServiceName = null
        )
        val accountDisplayName = createAccountDisplayName(accountDetail)
        val isPrimaryNameMatches =
            accountDisplayName.primaryDisplayName == account.address.toShortenedAddress()
        val isSecondaryNameMatches =
            accountDisplayName.secondaryDisplayName == instrumentationContext.getString(
                R.string.watch_account
            )
        assert(isPrimaryNameMatches && isSecondaryNameMatches)
    }

    @Test
    fun isCreatingNamedAndUnmatchedNFDomainWatchAccountDisplayNameWorks() {
        val account = Account.create(
            publicKey = TEST_ACCOUNT_ADDRESS,
            accountName = "Spending Account",
            detail = Account.Detail.Watch
        )
        val accountInformation = createTestAccountInformation()
        val accountDetail = createTestAccountDetail(
            account = account,
            accountInformation = accountInformation,
            nameServiceName = null
        )
        val accountDisplayName = createAccountDisplayName(accountDetail)
        val isPrimaryNameMatches = accountDisplayName.primaryDisplayName == account.name
        val isSecondaryNameMatches =
            accountDisplayName.secondaryDisplayName == account.address.toShortenedAddress()
        assert(isPrimaryNameMatches && isSecondaryNameMatches)
    }

    @Test
    fun isCreatingUnnamedAndMatchedNFDomainLedgerAccountDisplayNameWorks() {
        val account = Account.create(
            publicKey = TEST_ACCOUNT_ADDRESS,
            detail = Account.Detail.Ledger("", null)
        )
        val accountInformation = createTestAccountInformation()
        val accountDetail = createTestAccountDetail(
            account = account,
            accountInformation = accountInformation,
            nameServiceName = "pera.algo"
        )
        val accountDisplayName = createAccountDisplayName(accountDetail)
        val isPrimaryNameMatches = accountDisplayName.primaryDisplayName == accountDetail.nameServiceName
        val isSecondaryNameMatches =
            accountDisplayName.secondaryDisplayName == account.address.toShortenedAddress()
        assert(isPrimaryNameMatches && isSecondaryNameMatches)
    }

    @Test
    fun isCreatingNamedAndMatchedNFDomainLedgerAccountDisplayNameWorks() {
        val account = Account.create(
            publicKey = TEST_ACCOUNT_ADDRESS,
            accountName = "TEST",
            detail = Account.Detail.Ledger("", "")
        )
        val accountInformation = createTestAccountInformation()
        val accountDetail = createTestAccountDetail(
            account = account,
            accountInformation = accountInformation,
            nameServiceName = "pera.algo"
        )
        val accountDisplayName = createAccountDisplayName(accountDetail)
        val isPrimaryNameMatches = accountDisplayName.primaryDisplayName == account.name
        val isSecondaryNameMatches =
            accountDisplayName.secondaryDisplayName == accountDetail.nameServiceName
        assert(isPrimaryNameMatches && isSecondaryNameMatches)
    }

    @Test
    fun isCreatingNamedAndMatchedNFDomainStandardAccountDisplayNameWorks() {
        val account = Account.create(
            publicKey = TEST_ACCOUNT_ADDRESS,
            accountName = "TEST",
            detail = Account.Detail.Standard(byteArrayOf())
        )
        val accountInformation = createTestAccountInformation()
        val accountDetail = createTestAccountDetail(
            account = account,
            accountInformation = accountInformation,
            nameServiceName = "pera.algo"
        )
        val accountDisplayName = createAccountDisplayName(accountDetail)
        val isPrimaryNameMatches = accountDisplayName.primaryDisplayName == account.name
        val isSecondaryNameMatches =
            accountDisplayName.secondaryDisplayName == accountDetail.nameServiceName
        assert(isPrimaryNameMatches && isSecondaryNameMatches)
    }

    @Test
    fun isCreatingNamedAndUnmatchedNFDomainStandardAccountDisplayNameWorks() {
        val account = Account.create(
            publicKey = TEST_ACCOUNT_ADDRESS,
            accountName = "TEST",
            detail = Account.Detail.Standard(byteArrayOf())
        )
        val accountInformation = createTestAccountInformation()
        val accountDetail = createTestAccountDetail(
            account = account,
            accountInformation = accountInformation,
            nameServiceName = null
        )
        val accountDisplayName = createAccountDisplayName(accountDetail)
        val isPrimaryNameMatches = accountDisplayName.primaryDisplayName == account.name
        val isSecondaryNameMatches =
            accountDisplayName.secondaryDisplayName == account.address.toShortenedAddress()
        assert(isPrimaryNameMatches && isSecondaryNameMatches)
    }

    @Test
    fun isCreatingUnnamedAndMatchedNFDomainStandardAccountDisplayNameWorks() {
        val account = Account.create(
            publicKey = TEST_ACCOUNT_ADDRESS,
            detail = Account.Detail.Standard(byteArrayOf())
        )
        val accountInformation = createTestAccountInformation()
        val accountDetail = createTestAccountDetail(
            account = account,
            accountInformation = accountInformation,
            nameServiceName = "pera.algo"
        )
        val accountDisplayName = createAccountDisplayName(accountDetail)
        val isPrimaryNameMatches = accountDisplayName.primaryDisplayName == accountDetail.nameServiceName
        val isSecondaryNameMatches =
            accountDisplayName.secondaryDisplayName == account.address.toShortenedAddress()
        assert(isPrimaryNameMatches && isSecondaryNameMatches)
    }

    @Test
    fun isAccountCopyableNameWorks() {
        val account = Account.create(
            publicKey = TEST_ACCOUNT_ADDRESS,
            detail = Account.Detail.Standard(byteArrayOf())
        )
        val accountInformation = createTestAccountInformation()
        val accountDetail = createTestAccountDetail(
            account = account,
            accountInformation = accountInformation,
            nameServiceName = "pera.algo"
        )
        val accountDisplayName = createAccountDisplayName(accountDetail)
        assert(accountDisplayName.accountAddress == account.address)
    }

    private fun createAccountDisplayName(accountDetail: AccountDetail): AccountDisplayName {
        with(accountDetail) {
            return AccountDisplayName(
                accountAddress = account.address,
                primaryDisplayName = account.name,
                secondaryDisplayName = nameServiceName
            )
        }
    }

    private fun createTestAccountInformation(): AccountInformation {
        return AccountInformation(
            address = TEST_ACCOUNT_ADDRESS,
            amount = BigInteger.ZERO,
            participation = null,
            rekeyAdminAddress = null,
            allAssetHoldingMap = hashMapOf(),
            createdAtRound = null,
            appsLocalState = null,
            appsTotalSchema = null,
            appsTotalExtraPages = null,
            totalCreatedApps = 0,
            lastFetchedRound = null
        )
    }

    private fun createTestAccountDetail(
        account: Account,
        accountInformation: AccountInformation,
        nameServiceName: String?
    ): AccountDetail {
        return AccountDetail(
            account = account,
            accountInformation = accountInformation,
            nameServiceName = nameServiceName
        )
    }

    companion object {
        private const val TEST_ACCOUNT_ADDRESS = "KFQMT4AK4ASIPAN23X36T3REP6D26LQDMAQNSAZM3DIEG2HTVKXEF76AP4"
    }
}
