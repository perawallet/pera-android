package com.algorand.android.module.custominfo.domain.usecase.implementation

import com.algorand.android.module.account.core.component.domain.usecase.UpdateAccountNameUseCase
import com.algorand.android.module.custominfo.domain.usecase.SetCustomName
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class UpdateAccountNameUseCaseTest {

    private val setCustomName: SetCustomName = mock()

    private val sut = UpdateAccountNameUseCase(setCustomName)

    @Test
    fun `EXPECT name to be set WHEN it is not null`() = runTest {
        sut(ADDRESS, "name")

        verify(setCustomName).invoke(ADDRESS, "name")
    }

    @Test
    fun `EXPECT name to be set as address shortened WHEN it is null`() = runTest {
        sut(ADDRESS, null)

        val expected = ADDRESS.take(6) + "..." + ADDRESS.takeLast(6)
        verify(setCustomName).invoke(ADDRESS, expected)
    }

    @Test
    fun `EXPECT name to be set as address shortened WHEN it is blank`() = runTest {
        sut(ADDRESS, " ")

        val expected = ADDRESS.take(6) + "..." + ADDRESS.takeLast(6)
        verify(setCustomName).invoke(ADDRESS, expected)
    }

    companion object {
        private const val ADDRESS = "ADDRESS"
    }
}
