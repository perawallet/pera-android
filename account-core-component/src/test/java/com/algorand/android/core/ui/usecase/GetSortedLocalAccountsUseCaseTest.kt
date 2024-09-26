package com.algorand.android.core.ui.usecase

import com.algorand.android.module.account.local.domain.model.LocalAccount
import com.algorand.android.module.account.local.domain.usecase.GetLocalAccounts
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountsDetail
import com.algorand.android.custominfo.component.domain.model.CustomInfo
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

internal class GetSortedLocalAccountsUseCaseTest {

    private val getAccountsDetail: GetAccountsDetail = mock()
    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = GetSortedLocalAccountsUseCase(getAccountsDetail, getLocalAccounts)

    @Test
    fun `EXPECT empty list WHEN there are no local accounts`() = runTest {
        whenever(getLocalAccounts()).thenReturn(emptyList())
        whenever(getAccountsDetail()).thenReturn(emptyList())

        val result = sut()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT empty list WHEN there are local accounts but they are not cached`() = runTest {
        whenever(getLocalAccounts()).thenReturn(fixtureOf())
        whenever(getAccountsDetail()).thenReturn(emptyList())

        val result = sut()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT sorted list WHEN there are multiple accounts and they are cached`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_ACCOUNT_1, LOCAL_ACCOUNT_2))
        whenever(getAccountsDetail()).thenReturn(listOf(ACCOUNT_DETAIL_2, ACCOUNT_DETAIL_1))

        val result = sut()

        assertEquals(listOf(LOCAL_ACCOUNT_2, LOCAL_ACCOUNT_1), result)
    }

    @Test
    fun `EXPECT not initialized accounts to be last WHEN there are order initialized accounts`() = runTest {
        whenever(getLocalAccounts()).thenReturn(listOf(LOCAL_ACCOUNT_3, LOCAL_ACCOUNT_1, LOCAL_ACCOUNT_2))
        whenever(getAccountsDetail())
            .thenReturn(listOf(NOT_INITIALIZED_ACCOUNT_DETAIL, ACCOUNT_DETAIL_1, ACCOUNT_DETAIL_2))

        val result = sut()

        assertEquals(listOf(LOCAL_ACCOUNT_2, LOCAL_ACCOUNT_1, LOCAL_ACCOUNT_3), result)
    }

    private companion object {
        private const val ADDRESS_1 = "ADDRESS_1"
        private const val ADDRESS_2 = "ADDRESS_2"
        private const val ADDRESS_3 = "ADDRESS_3"

        private val LOCAL_ACCOUNT_1 = fixtureOf<LocalAccount.Algo25>().copy(
            address = ADDRESS_1
        )
        private val LOCAL_ACCOUNT_2 = fixtureOf<LocalAccount.Algo25>().copy(
            address = ADDRESS_2
        )
        private val LOCAL_ACCOUNT_3 = fixtureOf<LocalAccount.Algo25>().copy(
            address = ADDRESS_3
        )

        private val ACCOUNT_DETAIL_1 = fixtureOf<AccountDetail>().copy(
            address = ADDRESS_1,
            customInfo = fixtureOf<CustomInfo>().copy(order = 2)
        )
        private val ACCOUNT_DETAIL_2 = fixtureOf<AccountDetail>().copy(
            address = ADDRESS_2,
            customInfo = fixtureOf<CustomInfo>().copy(order = 1)
        )
        private val NOT_INITIALIZED_ACCOUNT_DETAIL = fixtureOf<AccountDetail>().copy(
            address = ADDRESS_3,
            customInfo = fixtureOf<CustomInfo>().copy(order = -1)
        )
    }
}

/*
When getLocalAccounts() returns a list of accounts and getAccountsDetail() returns a list of account details, but none of the accounts match.
When getLocalAccounts() returns a list of accounts and getAccountsDetail() returns a list of account details, and some of the accounts match.
When getLocalAccounts() returns a list of accounts and getAccountsDetail() returns a list of account details, and all of the accounts match.
 */