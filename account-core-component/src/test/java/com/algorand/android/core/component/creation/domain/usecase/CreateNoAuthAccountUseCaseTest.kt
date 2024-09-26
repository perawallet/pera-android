package com.algorand.android.module.account.core.component.creation.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.AddNoAuthAccount
import com.algorand.android.module.account.core.component.caching.domain.usecase.CacheAccountDetail
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class CreateNoAuthAccountUseCaseTest {

    private val addNoAuthAccount: AddNoAuthAccount = mock()
    private val cacheAccountDetail: CacheAccountDetail = mock()

    private val sut = CreateNoAuthAccountUseCase(addNoAuthAccount, cacheAccountDetail)

    @Test
    fun `EXPECT no auth account to be created and cached successfully`() = runTest {
        sut("address")

        verify(addNoAuthAccount).invoke(LocalAccount.NoAuth("address"))
        verify(cacheAccountDetail).invoke("address")
    }
}
