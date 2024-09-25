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

package com.algorand.android.module.asset.action.ui.di

import com.algorand.android.module.asset.action.ui.mapper.AssetActionInformationMapper
import com.algorand.android.module.asset.action.ui.mapper.AssetActionInformationMapperImpl
import com.algorand.android.module.asset.action.ui.usecase.GetAssetActionAccountDetail
import com.algorand.android.module.asset.action.ui.usecase.GetAssetActionAccountDetailUseCase
import com.algorand.android.module.asset.action.ui.usecase.GetAssetActionInformation
import com.algorand.android.module.asset.action.ui.usecase.GetAssetActionInformationUseCase
import com.algorand.android.module.asset.action.ui.usecase.GetAssetActionPreview
import com.algorand.android.module.asset.action.ui.usecase.GetAssetActionPreviewUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AssetActionUiModule {

    @Provides
    @Singleton
    fun provideGetAssetActionPreview(useCase: GetAssetActionPreviewUseCase): GetAssetActionPreview = useCase

    @Provides
    @Singleton
    fun provideGetAssetActionInformation(useCase: GetAssetActionInformationUseCase): GetAssetActionInformation = useCase

    @Provides
    @Singleton
    fun provideGetAssetActionAccountDetail(
        useCase: GetAssetActionAccountDetailUseCase
    ): GetAssetActionAccountDetail = useCase

    @Provides
    @Singleton
    fun provideAssetActionInformationMapper(
        impl: AssetActionInformationMapperImpl
    ): AssetActionInformationMapper = impl
}
