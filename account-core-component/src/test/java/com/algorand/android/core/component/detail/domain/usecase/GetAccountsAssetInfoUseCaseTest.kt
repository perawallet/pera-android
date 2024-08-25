package com.algorand.android.core.component.detail.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.core.component.detail.domain.model.AccountDetail
import com.algorand.android.core.component.detail.domain.usecase.implementation.GetAccountsDetailUseCase
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

internal class GetAccountsAssetInfoUseCaseTest {

    private val getAccountDetail: GetAccountDetail = mock()
    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = GetAccountsDetailUseCase(getAccountDetail, getLocalAccounts)

    @Test
    fun `EXPECT empty list WHEN getLocalAccounts returns empty list`() = runTest {
        whenever(getLocalAccounts()).thenReturn(emptyList())

        val result = sut()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT mapped list WHEN there are local accounts`() = runTest {
        val localAccounts = listOf(LOCAL_ACCOUNT_1, LOCAL_ACCOUNT_2)
        whenever(getLocalAccounts()).thenReturn(localAccounts)
        whenever(getAccountDetail(LOCAL_ACCOUNT_1.address)).thenReturn(ACCOUNT_DETAIL_1)
        whenever(getAccountDetail(LOCAL_ACCOUNT_2.address)).thenReturn(ACCOUNT_DETAIL_2)

        val result = sut()

        assertEquals(listOf(ACCOUNT_DETAIL_1, ACCOUNT_DETAIL_2), result)
    }

    private companion object {
        val LOCAL_ACCOUNT_1 = fixtureOf<LocalAccount>()
        val LOCAL_ACCOUNT_2 = fixtureOf<LocalAccount>()
        val ACCOUNT_DETAIL_1 = fixtureOf<AccountDetail>()
        val ACCOUNT_DETAIL_2 = fixtureOf<AccountDetail>()
    }
}
