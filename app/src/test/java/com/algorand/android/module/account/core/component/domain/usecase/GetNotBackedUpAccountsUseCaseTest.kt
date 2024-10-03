package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.component.domain.model.AccountTotalValue
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.usecase.GetAllAccountInformation
import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import com.algorand.android.module.asb.domain.usecase.GetBackedUpAccounts
import com.algorand.android.testutil.fixtureOf
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class GetNotBackedUpAccountsUseCaseTest {

    private val getAllAccountInformation: GetAllAccountInformation = mock()
    private val getAccountTotalValue: GetAccountTotalValue = mock()
    private val getAccountDetail: GetAccountDetail = mock()
    private val getBackedUpAccounts: GetBackedUpAccounts = mock()
    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = GetNotBackedUpAccountsUseCase(
        getAllAccountInformation,
        getBackedUpAccounts,
        getAccountTotalValue,
        getAccountDetail,
        getLocalAccounts
    )

    @Test
    fun `EXPECT empty list WHEN getAllAccountInformation returns empty map`() = runTest {
        whenever(getAllAccountInformation()).thenReturn(emptyMap())

        val result = sut()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT empty list WHEN all accounts are backed up`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_ACCOUNT))
        whenever(getAllAccountInformation()).thenReturn(mapOf(ADDRESS to ACCOUNT_INFORMATION))
        whenever(getBackedUpAccounts()).thenReturn(setOf(ADDRESS))
        whenever(getAccountTotalValue(ADDRESS, true)).thenReturn(ACCOUNT_TOTAL_VALUE)
        whenever(getAccountDetail(ADDRESS)).thenReturn(ACCOUNT_DETAIL)

        val result = sut()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT not backed up WHEN there are accounts that are not backed up`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_ACCOUNT))
        whenever(getAllAccountInformation()).thenReturn(mapOf(ADDRESS to ACCOUNT_INFORMATION))
        whenever(getBackedUpAccounts()).thenReturn(emptySet())
        whenever(getAccountTotalValue(ADDRESS, true)).thenReturn(ACCOUNT_TOTAL_VALUE)
        whenever(getAccountDetail(ADDRESS)).thenReturn(ACCOUNT_DETAIL)

        val result = sut()

        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `EXPECT empty list WHEN not backed up account is no auth account`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_ACCOUNT))
        whenever(getAllAccountInformation()).thenReturn(mapOf(ADDRESS to ACCOUNT_INFORMATION))
        whenever(getBackedUpAccounts()).thenReturn(setOf(ADDRESS))
        whenever(getAccountTotalValue(ADDRESS, true)).thenReturn(ACCOUNT_TOTAL_VALUE)
        whenever(getAccountDetail(ADDRESS)).thenReturn(ACCOUNT_DETAIL.copy(accountType = AccountType.NoAuth))

        val result = sut()

        assertTrue(result.isEmpty())
    }

    private companion object {
        private const val ADDRESS = "address"

        val LOCAL_ACCOUNT = fixtureOf<LocalAccount.Algo25>().copy(
            address = ADDRESS
        )
        private val ACCOUNT_INFORMATION = fixtureOf<AccountInformation>().copy(
            address = ADDRESS
        )
        private val ACCOUNT_TOTAL_VALUE = fixtureOf<AccountTotalValue>().copy(
            primaryAccountValue = BigDecimal.ONE
        )
        private val ACCOUNT_DETAIL = fixtureOf<AccountDetail>().copy(accountType = AccountType.Algo25)
    }
}
