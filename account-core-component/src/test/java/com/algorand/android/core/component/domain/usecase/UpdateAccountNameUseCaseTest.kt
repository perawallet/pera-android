package com.algorand.android.core.component.domain.usecase

import com.algorand.android.custominfo.component.domain.usecase.SetCustomName
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

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
