package com.algorand.android.core.ui.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.core.ui.usecase.implementation.GetAccountIconResourceByAccountTypeUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

internal class GetAccountIconResourceByAccountTypeUseCaseTest {

    private val sut = GetAccountIconResourceByAccountTypeUseCase()

    @Test
    fun `EXPECT AccountIconResource by AccountType`() {
        assertEquals(AccountIconResource.STANDARD, sut(AccountType.Algo25))
        assertEquals(AccountIconResource.LEDGER, sut(AccountType.LedgerBle))
        assertEquals(AccountIconResource.REKEYED, sut(AccountType.Rekeyed))
        assertEquals(AccountIconResource.REKEYED_AUTH, sut(AccountType.RekeyedAuth))
        assertEquals(AccountIconResource.WATCH, sut(AccountType.NoAuth))
        assertEquals(AccountIconResource.UNDEFINED, sut(null))
    }
}
