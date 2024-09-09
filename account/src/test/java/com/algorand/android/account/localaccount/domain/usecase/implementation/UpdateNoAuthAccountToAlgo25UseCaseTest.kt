package com.algorand.android.account.localaccount.domain.usecase.implementation

import com.algorand.android.account.localaccount.domain.usecase.CreateAlgo25Account
import com.algorand.android.account.localaccount.domain.usecase.DeleteLocalAccount
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class UpdateNoAuthAccountToAlgo25UseCaseTest {

    private val deleteLocalAccount: DeleteLocalAccount = mock()
    private val createAlgo25Account: CreateAlgo25Account = mock()

    private val sut = UpdateNoAuthAccountToAlgo25UseCase(deleteLocalAccount, createAlgo25Account)

    @Test
    fun `EXPECT noAuthAccount to be deleted and new Algo25Account to be created`() = runTest {
        sut(ADDRESS, SECRET_KEY)

        verify(deleteLocalAccount).invoke(ADDRESS)
        verify(createAlgo25Account).invoke(ADDRESS, SECRET_KEY)
    }

    companion object {
        private const val ADDRESS = "ADDRESS"
        private val SECRET_KEY = byteArrayOf()
    }
}
