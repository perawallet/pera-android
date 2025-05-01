/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.nameservice.data.repository

import com.algorand.wallet.foundation.cache.InMemoryLocalCache
import com.algorand.wallet.foundation.network.exceptions.PeraRetrofitErrorHandler
import com.algorand.wallet.nameservice.data.mapper.NameServiceMapper
import com.algorand.wallet.nameservice.data.mapper.NameServiceSearchResultMapper
import com.algorand.wallet.nameservice.data.service.NameServiceApiService
import com.algorand.wallet.nameservice.domain.model.NameService
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NameServiceRepositoryImplTest {

    private val nameServiceApiService: NameServiceApiService = mockk()
    private val nameServiceMapper: NameServiceMapper = mockk()
    private val inMemoryLocalCache: InMemoryLocalCache<String, NameService> = mockk(relaxed = true)
    private val nameServiceSearchResultMapper: NameServiceSearchResultMapper = mockk()
    private val peraApiErrorHandler: PeraRetrofitErrorHandler = mockk()

    private val sut = NameServiceRepositoryImpl(
        nameServiceApiService,
        nameServiceMapper,
        inMemoryLocalCache,
        nameServiceSearchResultMapper,
        peraApiErrorHandler
    )

    @Test
    fun `EXPECT local cache to be cleared WHEN clear cache is invoked`() = runTest {
        sut.clearCache()

        verify { inMemoryLocalCache.clear() }
    }
}