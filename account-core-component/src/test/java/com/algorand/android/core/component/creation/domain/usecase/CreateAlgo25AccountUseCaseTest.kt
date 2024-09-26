package com.algorand.android.module.account.core.component.creation.domain.usecase

import com.algorand.android.account.localaccount.domain.model.LocalAccount
import com.algorand.android.account.localaccount.domain.usecase.AddAlgo25Account
import com.algorand.android.module.account.core.component.caching.domain.usecase.CacheAccountDetail
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class CreateAlgo25AccountUseCaseTest {

    private val addAlgo25Account: AddAlgo25Account = mock()
    private val cacheAccountDetail: CacheAccountDetail = mock()

    private val sut = CreateAlgo25AccountUseCase(addAlgo25Account, cacheAccountDetail)

    @Test
    fun `EXPECT algo25 account to be created and cached successfully`() = runTest {
        sut("address", ByteArray(0))

        verify(addAlgo25Account).invoke(LocalAccount.Algo25("address", ByteArray(0)))
        verify(cacheAccountDetail).invoke("address")
    }
}
