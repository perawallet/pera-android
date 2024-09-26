package com.algorand.android.account.custominfo.domain.usecase.implementation

import com.algorand.android.module.custominfo.domain.repository.CustomInfoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class UpdateAccountNameUseCaseTest {

    private val customInfoRepository: com.algorand.android.module.custominfo.domain.repository.CustomInfoRepository =
        mock()

    private val sut = com.algorand.android.custominfo.component.domain.usecase.implementation.UpdateAccountNameUseCase(
        customInfoRepository
    )

    @Test
    fun `EXPECT name to be set WHEN it is not null`() = runTest {
        sut(ADDRESS, "name")

        verify(customInfoRepository).setCustomName(ADDRESS, "name")
    }

    @Test
    fun `EXPECT name to be set as address shortened WHEN it is null`() = runTest {
        sut(ADDRESS, null)

        val expected = ADDRESS.take(6) + "..." + ADDRESS.takeLast(6)
        verify(customInfoRepository).setCustomName(ADDRESS, expected)
    }

    @Test
    fun `EXPECT name to be set as address shortened WHEN it is blank`() = runTest {
        sut(ADDRESS, " ")

        val expected = ADDRESS.take(6) + "..." + ADDRESS.takeLast(6)
        verify(customInfoRepository).setCustomName(ADDRESS, expected)
    }

    companion object {
        private const val ADDRESS = "ADDRESS"
    }
}
