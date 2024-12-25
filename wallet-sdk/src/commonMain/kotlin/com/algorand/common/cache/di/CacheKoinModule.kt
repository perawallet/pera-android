/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.cache.di

import com.algorand.common.cache.LifecycleAwareCacheManager
import com.algorand.common.cache.LifecycleAwareCacheManagerImpl
import com.algorand.common.cache.domain.usecase.ClearPreviousSessionCache
import com.algorand.common.cache.domain.usecase.ClearPreviousSessionCacheUseCase
import com.algorand.common.cache.domain.usecase.GetAppCacheStatusFlow
import com.algorand.common.cache.domain.usecase.GetAppCacheStatusFlowUseCase
import com.algorand.common.cache.domain.usecase.InitializeAppCache
import com.algorand.common.cache.domain.usecase.InitializeAppCacheImpl
import com.algorand.common.cache.domain.usecase.UpdateAccountCache
import com.algorand.common.cache.domain.usecase.UpdateAccountCacheUseCase
import org.koin.dsl.module

internal val cacheKoinModule = module {
    factory<LifecycleAwareCacheManager> { LifecycleAwareCacheManagerImpl() }
    factory<ClearPreviousSessionCache> { ClearPreviousSessionCacheUseCase(get(), get()) }
    single<InitializeAppCache> { InitializeAppCacheImpl(get(), get(), get()) }
    single<GetAppCacheStatusFlow> { GetAppCacheStatusFlowUseCase(get(), get()) }
    factory<UpdateAccountCache> { UpdateAccountCacheUseCase(get(), get(), get()) }
}
