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

package com.algorand.android.modules.accounts.lite.di

import com.algorand.android.modules.accounts.lite.domain.manager.AccountLiteManager
import com.algorand.android.modules.accounts.lite.domain.manager.AccountLiteManagerImpl
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheData
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheDataUseCase
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLitesFlow
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLitesFlowUseCase
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountLiteAppModule {

    @Provides
    fun provideGetAccountLitesUseCase(useCase: GetAccountLitesFlowUseCase): GetAccountLitesFlow = useCase

    @Provides
    @Singleton
    fun provideAccountLiteManager(impl: AccountLiteManagerImpl): AccountLiteManager = impl

    @Provides
    fun provideGetAccountLiteCacheFlow(accountLiteManager: AccountLiteManager): GetAccountLiteCacheFlow {
        return GetAccountLiteCacheFlow(accountLiteManager::localAccountLitesFlow)
    }

    @Provides
    fun provideGetAccountLiteCacheData(useCase: GetAccountLiteCacheDataUseCase): GetAccountLiteCacheData = useCase

    @Provides
    fun provideGetAccountLite(useCase: GetAccountLiteUseCase): GetAccountLite = useCase
}
