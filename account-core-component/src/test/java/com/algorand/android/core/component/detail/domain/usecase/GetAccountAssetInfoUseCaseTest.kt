package com.algorand.android.module.account.core.component.detail.domain.usecase

import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountRegistrationType.Algo25
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Rekeyed
import com.algorand.android.module.account.core.component.detail.domain.usecase.implementation.GetAccountDetailUseCase
import com.algorand.android.custominfo.component.domain.model.CustomInfo
import com.algorand.android.custominfo.component.domain.usecase.GetCustomInfo
import com.algorand.android.testutil.fixtureOf
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.mockito.kotlin.*

class GetAccountAssetInfoUseCaseTest {

    private val getAccountType: GetAccountType = mock()
    private val getAccountRegistrationType: GetAccountRegistrationType = mock()
    private val getCustomInfo: GetCustomInfo = mock()

    private val sut = GetAccountDetailUseCase(
        getAccountType,
        getAccountRegistrationType,
        getCustomInfo
    )

    @Test
    fun `EXPECT account detail`() = runTest {
        whenever(getAccountType("address")).thenReturn(Rekeyed)
        whenever(getAccountRegistrationType("address")).thenReturn(Algo25)
        whenever(getCustomInfo("address")).thenReturn(CUSTOM_INFO)

        val result = sut("address")

        val expected = AccountDetail(
            address = "address",
            accountRegistrationType = Algo25,
            accountType = Rekeyed,
            customInfo = CUSTOM_INFO
        )
        Assert.assertEquals(expected, result)
    }

    companion object {
        private val CUSTOM_INFO = fixtureOf<CustomInfo>()
    }
}
