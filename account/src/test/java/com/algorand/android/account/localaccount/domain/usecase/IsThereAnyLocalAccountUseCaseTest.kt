package com.algorand.android.account.localaccount.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.GetLocalAccounts
import com.algorand.android.account.localaccount.domain.usecase.implementation.IsThereAnyLocalAccountUseCase
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

class IsThereAnyLocalAccountUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mock()

    private val sut = IsThereAnyLocalAccountUseCase(getLocalAccounts)

    @Test
    fun `EXPECT true WHEN there are local accounts`() = runTest {
        whenever(getLocalAccounts()).thenReturn(LOCAL_ACCOUNTS)

        val result = sut()

        assertTrue(result)
    }

    @Test
    fun `EXPECT false WHEN there are not any local accounts`() = runTest {
        whenever(getLocalAccounts()).thenReturn(emptyList())

        val result = sut()

        assertFalse(result)
    }

    companion object {
        private val LOCAL_ACCOUNTS = fixtureOf<List<LocalAccount>>()
    }
}
