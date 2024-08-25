package com.algorand.android.core.component.caching.domain.usecase

import com.algorand.android.testutil.test
import org.junit.Test

class AppCacheStatusManagerImplTest {

    private val appCacheStatusManagerImpl: AppCacheStatusManagerImpl = AppCacheStatusManagerImpl()

    private val testObserver = appCacheStatusManagerImpl.appCacheStatusFlow.test()

    @Test
    fun `EXPECT status to be set`() {

        appCacheStatusManagerImpl.setStatus(LOADING)
        appCacheStatusManagerImpl.setStatus(INITIALIZED)

        testObserver.assertValueHistory(NOT_INITIALIZED, LOADING, INITIALIZED)
    }
}
