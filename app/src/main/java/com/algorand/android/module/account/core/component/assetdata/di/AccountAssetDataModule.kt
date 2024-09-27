@file:Suppress("TooManyFunctions")
/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.account.core.component.assetdata.di

import com.algorand.android.module.account.core.component.assetdata.mapper.PendingAdditionAssetDataMapper
import com.algorand.android.module.account.core.component.assetdata.mapper.PendingAdditionAssetDataMapperImpl
import com.algorand.android.module.account.core.component.assetdata.mapper.PendingDeletionAssetDataMapper
import com.algorand.android.module.account.core.component.assetdata.mapper.PendingDeletionAssetDataMapperImpl
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAccountAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAccountAssetDataUseCase
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAccountOwnedAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAccountOwnedAssetDataUseCase
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAccountPendingAdditionAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAccountPendingAdditionAssetDataUseCase
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAccountPendingDeletionAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAccountPendingDeletionAssetDataUseCase
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAlgoOwnedAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.CreateAlgoOwnedAssetDataUseCase
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountAssetDataFlow
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountAssetsData
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetDataUseCase
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetsData
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetsDataFlow
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetsDataFlowUseCase
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountOwnedAssetsDataUseCase
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountAssetDataFlowUseCase
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountAssetsDataUseCase
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountBaseOwnedAssetDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AccountAssetDataModule {

    @Provides
    @Singleton
    fun provideCreateAccountOwnedAssetData(
        useCase: CreateAccountOwnedAssetDataUseCase
    ): CreateAccountOwnedAssetData = useCase

    @Provides
    @Singleton
    fun provideCreateAccountPendingAdditionAssetData(
        useCase: CreateAccountPendingAdditionAssetDataUseCase
    ): CreateAccountPendingAdditionAssetData = useCase

    @Provides
    @Singleton
    fun providePendingDeletionAssetDataMapper(
        useCase: PendingDeletionAssetDataMapperImpl
    ): PendingDeletionAssetDataMapper = useCase

    @Provides
    @Singleton
    fun provideCreateAccountPendingDeletionAssetData(
        useCase: CreateAccountPendingDeletionAssetDataUseCase
    ): CreateAccountPendingDeletionAssetData = useCase

    @Provides
    @Singleton
    fun provideCreateAccountAssetData(useCase: CreateAccountAssetDataUseCase): CreateAccountAssetData = useCase

    @Provides
    @Singleton
    fun providePendingAdditionAssetDataMapper(
        mapper: PendingAdditionAssetDataMapperImpl
    ): PendingAdditionAssetDataMapper = mapper

    @Provides
    @Singleton
    fun provideCreateAlgoOwnedAssetData(useCase: CreateAlgoOwnedAssetDataUseCase): CreateAlgoOwnedAssetData = useCase

    @Provides
    @Singleton
    fun provideGetAccountBaseAssetData(
        useCase: GetAccountBaseOwnedAssetDataUseCase
    ): GetAccountBaseOwnedAssetData = useCase

    @Provides
    @Singleton
    fun provideGetAccountAssetsData(
        useCase: GetAccountAssetsDataUseCase
    ): GetAccountAssetsData = useCase

    @Provides
    @Singleton
    fun provideGetAccountAssetDataFlow(
        useCase: GetAccountAssetDataFlowUseCase
    ): GetAccountAssetDataFlow = useCase

    @Provides
    @Singleton
    fun provideGetAccountOwnedAssetsData(
        useCase: GetAccountOwnedAssetsDataUseCase
    ): GetAccountOwnedAssetsData = useCase

    @Provides
    @Singleton
    fun provideGetAccountOwnedAssetData(
        useCase: GetAccountOwnedAssetDataUseCase
    ): GetAccountOwnedAssetData = useCase

    @Provides
    @Singleton
    fun provideGetAccountOwnedAssetsDataFlow(
        useCase: GetAccountOwnedAssetsDataFlowUseCase
    ): GetAccountOwnedAssetsDataFlow = useCase
}
