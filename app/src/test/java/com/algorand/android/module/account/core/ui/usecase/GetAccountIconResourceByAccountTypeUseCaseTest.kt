package com.algorand.android.module.account.core.ui.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.ui.model.AccountIconResource
import com.algorand.android.module.account.core.ui.usecase.implementation.GetAccountIconResourceByAccountTypeUseCase
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class GetAccountIconResourceByAccountTypeUseCaseTest {

    private val getAccountDetail: GetAccountDetail = mock()

    private val sut = GetAccountIconResourceByAccountTypeUseCase(getAccountDetail)

    @Test
    fun `EXPECT AccountIconResource by AccountType`() {
        assertEquals(AccountIconResource.STANDARD, sut(AccountType.Algo25))
        assertEquals(AccountIconResource.LEDGER, sut(AccountType.LedgerBle))
        assertEquals(AccountIconResource.REKEYED, sut(AccountType.Rekeyed))
        assertEquals(AccountIconResource.REKEYED_AUTH, sut(AccountType.RekeyedAuth))
        assertEquals(AccountIconResource.WATCH, sut(AccountType.NoAuth))
        assertEquals(AccountIconResource.UNDEFINED, sut(null))
    }

    @Test
    fun `EXPECT AccountIconResource by account address`() = runTest {
        whenever(getAccountDetail(ADDRESS)).thenReturn(ACCOUNT_DETAIL)

        val result = sut(ADDRESS)

        assertEquals(AccountIconResource.STANDARD, result)
    }

    private companion object {
        const val ADDRESS = "address"
        val ACCOUNT_DETAIL = fixtureOf<AccountDetail>().copy(
            address = ADDRESS,
            accountType = AccountType.Algo25
        )
    }
}
