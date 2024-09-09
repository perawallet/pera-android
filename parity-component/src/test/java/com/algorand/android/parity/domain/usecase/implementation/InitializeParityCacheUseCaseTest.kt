package com.algorand.android.parity.domain.usecase.implementation

import com.algorand.android.parity.domain.usecase.FetchAndCacheParity
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.*

class InitializeParityCacheUseCaseTest {

    private val fetchAndCacheParity: FetchAndCacheParity = mock()

    private val sut = InitializeParityCacheUseCase(fetchAndCacheParity)

    @Test
    fun `EXPECT fetch and cache parity to be called WHEN invoked`() = runTest {
        sut()

        verify(fetchAndCacheParity).invoke()
    }
}
