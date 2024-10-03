package com.algorand.android.module.account.local.domain.usecase.implementation

import com.algorand.android.module.account.local.domain.model.LocalAccount.Algo25
import com.algorand.android.module.account.local.domain.repository.Algo25AccountRepository
import com.algorand.android.testutil.fixtureOf
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.*

class GetSecretKeyUseCaseTest {

    private val algo25AccountRepository: Algo25AccountRepository = mock()

    private val sut = GetSecretKeyUseCase(algo25AccountRepository)

    @Test
    fun `EXPECT secret key WHEN account is found`() = runTest {
        whenever(algo25AccountRepository.getAccount(ALGO_25_ADDRESS)).thenReturn(ALGO_25_ACCOUNT)

        val result = sut(ALGO_25_ADDRESS)

        assertTrue(result.contentEquals(ALGO_25_ACCOUNT.secretKey))
    }

    @Test
    fun `EXPECT null WHEN account is not found`() = runTest {
        whenever(algo25AccountRepository.getAccount(ALGO_25_ADDRESS)).thenReturn(null)

        val result = sut(ALGO_25_ADDRESS)

        assertNull(result)
    }

    companion object {
        private const val ALGO_25_ADDRESS = "ADDRESS_1"

        private val ALGO_25_ACCOUNT = fixtureOf<Algo25>().copy(address = ALGO_25_ADDRESS)
    }
}
