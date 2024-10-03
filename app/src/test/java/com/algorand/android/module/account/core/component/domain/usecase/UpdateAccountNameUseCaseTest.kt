package com.algorand.android.module.account.core.component.domain.usecase

import com.algorand.android.module.custominfo.domain.usecase.SetCustomName
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class UpdateAccountNameUseCaseTest {

    private val setCustomName: SetCustomName = mock()

    private val sut = UpdateAccountNameUseCase(setCustomName)

    @Test
    fun `EXPECT name to be updated WHEN it is not null`() = runTest {
        sut("ADDRESS", "NAME")

        verify(setCustomName).invoke("ADDRESS", "NAME")
    }

    @Test
    fun `EXPECT shortened address to be saved WHEN name is null`() = runTest {
        sut("VERYLONGADDRESS", null)

        verify(setCustomName).invoke("VERYLONGADDRESS", "VERYLO...DDRESS")
    }
}
