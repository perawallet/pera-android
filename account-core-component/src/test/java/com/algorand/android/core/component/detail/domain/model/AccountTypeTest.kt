package com.algorand.android.core.component.detail.domain.model

import com.algorand.android.core.component.detail.domain.model.AccountType.Companion.canSignTransaction
import org.junit.Assert.*
import org.junit.Test

internal class AccountTypeTest {

    @Test
    fun `EXPECT true WHEN account type can sign transaction`() {
        assertTrue(AccountType.Algo25.canSignTransaction())
        assertTrue(AccountType.LedgerBle.canSignTransaction())
        assertTrue(AccountType.RekeyedAuth.canSignTransaction())
    }

    @Test
    fun `EXPECT false WHEN account type can not sign transaction`() {
        assertFalse(AccountType.Rekeyed.canSignTransaction())
        assertFalse(AccountType.NoAuth.canSignTransaction())
    }
}
