package com.algorand.android.module.account.core.component.detail.domain.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountRegistrationType.Algo25
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Rekeyed
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.GetAccountDetailUseCase
import com.algorand.android.module.asb.domain.usecase.GetAccountAsbBackUpStatus
import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.module.custominfo.domain.usecase.GetCustomInfo
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetAccountAssetInfoUseCaseTest {

    private val getAccountType: GetAccountType = mock()
    private val getAccountRegistrationType: GetAccountRegistrationType = mock()
    private val getCustomInfo: GetCustomInfo = mock()
    private val getAccountAsbBackUpStatus: GetAccountAsbBackUpStatus = mock()

    private val sut = GetAccountDetailUseCase(
        getAccountType,
        getAccountRegistrationType,
        getCustomInfo,
        getAccountAsbBackUpStatus
    )

    @Test
    fun `EXPECT account detail`() = runTest {
        whenever(getAccountType("address")).thenReturn(Rekeyed)
        whenever(getAccountRegistrationType("address")).thenReturn(Algo25)
        whenever(getCustomInfo("address")).thenReturn(CUSTOM_INFO)
        whenever(getAccountAsbBackUpStatus("address")).thenReturn(true)

        val result = sut("address")

        val expected = AccountDetail(
            address = "address",
            accountRegistrationType = Algo25,
            accountType = Rekeyed,
            customInfo = CUSTOM_INFO,
            isBackedUp = true
        )
        Assert.assertEquals(expected, result)
    }

    companion object {
        private val CUSTOM_INFO = fixtureOf<CustomInfo>()
    }
}
