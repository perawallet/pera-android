/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountcore.di

import com.algorand.android.modules.accountcore.domain.mapper.AlgoAssetDataMapper
import com.algorand.android.modules.accountcore.domain.mapper.AlgoAssetDataMapperImpl
import com.algorand.android.modules.accountcore.domain.mapper.OwnedAssetDataMapper
import com.algorand.android.modules.accountcore.domain.mapper.OwnedAssetDataMapperImpl
import com.algorand.android.modules.accountcore.domain.mapper.PendingAdditionAssetDataMapper
import com.algorand.android.modules.accountcore.domain.mapper.PendingAdditionAssetDataMapperImpl
import com.algorand.android.modules.accountcore.domain.mapper.PendingDeletionAssetDataMapper
import com.algorand.android.modules.accountcore.domain.mapper.PendingDeletionAssetDataMapperImpl
import com.algorand.android.modules.accountcore.domain.usecase.CreateAccountAssetData
import com.algorand.android.modules.accountcore.domain.usecase.CreateAccountAssetDataUseCase
import com.algorand.android.modules.accountcore.domain.usecase.CreateAccountOwnedAssetData
import com.algorand.android.modules.accountcore.domain.usecase.CreateAccountOwnedAssetDataUseCase
import com.algorand.android.modules.accountcore.domain.usecase.CreateAccountPendingAdditionAssetData
import com.algorand.android.modules.accountcore.domain.usecase.CreateAccountPendingAdditionAssetDataUseCase
import com.algorand.android.modules.accountcore.domain.usecase.CreateAccountPendingDeletionAssetData
import com.algorand.android.modules.accountcore.domain.usecase.CreateAccountPendingDeletionAssetDataUseCase
import com.algorand.android.modules.accountcore.domain.usecase.CreateAlgoOwnedAssetData
import com.algorand.android.modules.accountcore.domain.usecase.CreateAlgoOwnedAssetDataUseCase
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountAssetDataFlow
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountAssetDataFlowUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AccountCoreModule {

    @Provides
    fun provideAlgoAssetDataMapper(impl: AlgoAssetDataMapperImpl): AlgoAssetDataMapper = impl

    @Provides
    fun provideOwnedAssetDataMapper(impl: OwnedAssetDataMapperImpl): OwnedAssetDataMapper = impl

    @Provides
    fun provideCreateAccountOwnedAssetData(
        useCase: CreateAccountOwnedAssetDataUseCase
    ): CreateAccountOwnedAssetData = useCase

    @Provides
    fun providePendingDeletionAssetDataMapper(
        impl: PendingDeletionAssetDataMapperImpl
    ): PendingDeletionAssetDataMapper = impl

    @Provides
    fun providePendingAdditionAssetDataMapper(
        impl: PendingAdditionAssetDataMapperImpl
    ): PendingAdditionAssetDataMapper = impl

    @Provides
    fun provideCreateAccountPendingAdditionAssetData(
        useCase: CreateAccountPendingAdditionAssetDataUseCase
    ): CreateAccountPendingAdditionAssetData = useCase

    @Provides
    fun provideCreateAccountPendingDeletionAssetData(
        useCase: CreateAccountPendingDeletionAssetDataUseCase
    ): CreateAccountPendingDeletionAssetData = useCase

    @Provides
    fun provideCreateAccountAssetData(useCase: CreateAccountAssetDataUseCase): CreateAccountAssetData = useCase

    @Provides
    fun provideGetAccountAssetDataFlow(useCase: GetAccountAssetDataFlowUseCase): GetAccountAssetDataFlow = useCase

    @Provides
    fun provideCreateAlgoOwnedAssetData(useCase: CreateAlgoOwnedAssetDataUseCase): CreateAlgoOwnedAssetData = useCase
}
