package com.algorand.android.module.account.core.component.detail.domain.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountRegistrationType
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.GetAccountDetailFlowUseCase
import com.algorand.android.module.account.info.domain.model.AccountInformation
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.android.module.asb.domain.usecase.GetAccountAsbBackUpStatus
import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.module.custominfo.domain.usecase.GetCustomInfo
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class GetAccountAssetInfoFlowUseCaseTest {

    private val getAccountInformationFlow: GetAccountInformationFlow = mock()
    private val getCustomInfo: GetCustomInfo = mock()
    private val getAccountType: GetAccountType = mock()
    private val getAccountRegistrationType: GetAccountRegistrationType = mock()
    private val getAccountAsbBackUpStatus: GetAccountAsbBackUpStatus = mock()

    private val sut = GetAccountDetailFlowUseCase(
        getAccountInformationFlow,
        getCustomInfo,
        getAccountType,
        getAccountRegistrationType,
        getAccountAsbBackUpStatus
    )

    @Test
    fun `EXPECT mapped account detail flow`() = runTest {
        whenever(getAccountInformationFlow(ADDRESS)).thenReturn(flowOf(ACCOUNT_INFORMATION))
        whenever(getCustomInfo(ADDRESS)).thenReturn(CUSTOM_INFO)
        whenever(getAccountType(ADDRESS)).thenReturn(AccountType.Algo25)
        whenever(getAccountRegistrationType(ADDRESS)).thenReturn(AccountRegistrationType.Algo25)
        whenever(getAccountAsbBackUpStatus(ADDRESS)).thenReturn(true)

        val result = sut(ADDRESS).first()

        assertEquals(ACCOUNT_DETAIL, result)
    }

    @Test
    fun `EXPECT null WHEN account information flow returns null`() = runTest {
        whenever(getAccountInformationFlow(ADDRESS)).thenReturn(flowOf(null))

        val result = sut(ADDRESS).first()

        assertNull(result)
    }

    private companion object {
        const val ADDRESS = "address"
        val ACCOUNT_INFORMATION = fixtureOf<AccountInformation>()
        val CUSTOM_INFO = fixtureOf<CustomInfo>()

        val ACCOUNT_DETAIL = AccountDetail(
            address = ADDRESS,
            customInfo = CUSTOM_INFO,
            accountType = AccountType.Algo25,
            accountRegistrationType = AccountRegistrationType.Algo25,
            isBackedUp = true
        )
    }
}
