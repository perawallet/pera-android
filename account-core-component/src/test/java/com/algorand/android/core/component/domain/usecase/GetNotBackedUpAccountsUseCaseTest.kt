package com.algorand.android.core.component.domain.usecase

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.usecase.GetAllAccountInformation
import com.algorand.android.core.component.detail.domain.model.*
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.core.component.domain.model.AccountTotalValue
import com.algorand.android.custominfo.component.domain.model.CustomInfo
import com.algorand.android.custominfo.component.domain.usecase.GetCustomInfo
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.*
import java.math.BigDecimal

internal class GetNotBackedUpAccountsUseCaseTest {

    private val getAllAccountInformation: GetAllAccountInformation = mock()
    private val getCustomInfo: GetCustomInfo = mock()
    private val getAccountTotalValue: GetAccountTotalValue = mock()
    private val getAccountDetail: GetAccountDetail = mock()

    private val sut = GetNotBackedUpAccountsUseCase(
        getAllAccountInformation,
        getCustomInfo,
        getAccountTotalValue,
        getAccountDetail
    )

    @Test
    fun `EXPECT empty list WHEN getAllAccountInformation returns empty map`() = runTest {
        whenever(getAllAccountInformation()).thenReturn(emptyMap())

        val result = sut()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT empty list WHEN all accounts are backed up`() = runTest {
        whenever(getAllAccountInformation()).thenReturn(mapOf(ADDRESS to ACCOUNT_INFORMATION))
        whenever(getCustomInfo(ADDRESS)).thenReturn(CUSTOM_INFO)
        whenever(getAccountTotalValue(ADDRESS, true)).thenReturn(ACCOUNT_TOTAL_VALUE)
        whenever(getAccountDetail(ADDRESS)).thenReturn(ACCOUNT_DETAIL)

        val result = sut()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT not backed up WHEN there are accounts that are not backed up`() = runTest {
        whenever(getAllAccountInformation()).thenReturn(mapOf(ADDRESS to ACCOUNT_INFORMATION))
        whenever(getCustomInfo(ADDRESS)).thenReturn(CUSTOM_INFO)
        whenever(getAccountTotalValue(ADDRESS, true)).thenReturn(ACCOUNT_TOTAL_VALUE)
        whenever(getAccountDetail(ADDRESS)).thenReturn(ACCOUNT_DETAIL)
        whenever(getCustomInfo(ADDRESS)).thenReturn(CUSTOM_INFO.copy(isBackedUp = false))

        val result = sut()

        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `EXPECT empty list WHEN not backed up account is no auth account`() = runTest {
        whenever(getAllAccountInformation()).thenReturn(mapOf(ADDRESS to ACCOUNT_INFORMATION))
        whenever(getCustomInfo(ADDRESS)).thenReturn(CUSTOM_INFO)
        whenever(getAccountTotalValue(ADDRESS, true)).thenReturn(ACCOUNT_TOTAL_VALUE)
        whenever(getAccountDetail(ADDRESS)).thenReturn(ACCOUNT_DETAIL.copy(accountType = AccountType.NoAuth))
        whenever(getCustomInfo(ADDRESS)).thenReturn(CUSTOM_INFO.copy(isBackedUp = false))

        val result = sut()

        assertTrue(result.isEmpty())
    }

    private companion object {
        private const val ADDRESS = "address"
        private val ACCOUNT_INFORMATION = fixtureOf<AccountInformation>().copy(
            address = ADDRESS
        )
        private val CUSTOM_INFO = fixtureOf<CustomInfo>().copy(
            isBackedUp = true
        )
        private val ACCOUNT_TOTAL_VALUE = fixtureOf<AccountTotalValue>().copy(
            primaryAccountValue = BigDecimal.ONE
        )
        private val ACCOUNT_DETAIL = fixtureOf<AccountDetail>().copy(accountType = AccountType.Algo25)
    }
}
