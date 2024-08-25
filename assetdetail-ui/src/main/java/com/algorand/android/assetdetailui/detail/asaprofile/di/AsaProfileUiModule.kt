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

package com.algorand.android.assetdetailui.detail.asaprofile.di

import com.algorand.android.assetdetailui.detail.asaprofile.mapper.AsaStatusPreviewMapper
import com.algorand.android.assetdetailui.detail.asaprofile.mapper.AsaStatusPreviewMapperImpl
import com.algorand.android.assetdetailui.detail.asaprofile.usecase.CreateAsaProfilePreviewFromAssetDetail
import com.algorand.android.assetdetailui.detail.asaprofile.usecase.CreateAsaProfilePreviewFromAssetDetailUseCase
import com.algorand.android.assetdetailui.detail.asaprofile.usecase.GetAlgoProfilePreviewWithAccountInformation
import com.algorand.android.assetdetailui.detail.asaprofile.usecase.GetAlgoProfilePreviewWithAccountInformationUseCase
import com.algorand.android.assetdetailui.detail.asaprofile.usecase.GetAsaProfilePreview
import com.algorand.android.assetdetailui.detail.asaprofile.usecase.GetAsaProfilePreviewUseCase
import com.algorand.android.assetdetailui.detail.asaprofile.usecase.GetAsaProfilePreviewWithAccountInformation
import com.algorand.android.assetdetailui.detail.asaprofile.usecase.GetAsaProfilePreviewWithAccountInformationUseCase
import com.algorand.android.assetdetailui.detail.asaprofile.usecase.GetAsaProfilePreviewWithoutAccountInformation
import com.algorand.android.assetdetailui.detail.asaprofile.usecase.GetAsaProfilePreviewWithoutAccountInformationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AsaProfileUiModule {

    @Provides
    @Singleton
    fun provideCreateAsaProfilePreviewFromAssetDetail(
        useCase: CreateAsaProfilePreviewFromAssetDetailUseCase
    ): CreateAsaProfilePreviewFromAssetDetail = useCase

    @Provides
    @Singleton
    fun provideAsaStatusPreviewMapper(impl: AsaStatusPreviewMapperImpl): AsaStatusPreviewMapper = impl

    @Provides
    @Singleton
    fun provideGetAsaProfilePreviewWithoutAccountInformation(
        useCase: GetAsaProfilePreviewWithoutAccountInformationUseCase
    ): GetAsaProfilePreviewWithoutAccountInformation = useCase

    @Provides
    @Singleton
    fun provideGetAlgoProfilePreviewWithAccountInformation(
        useCase: GetAlgoProfilePreviewWithAccountInformationUseCase
    ): GetAlgoProfilePreviewWithAccountInformation = useCase

    @Provides
    @Singleton
    fun provideGetAsaProfilePreviewWithAccountInformation(
        useCase: GetAsaProfilePreviewWithAccountInformationUseCase
    ): GetAsaProfilePreviewWithAccountInformation = useCase

    @Provides
    @Singleton
    fun provideGetAsaProfilePreview(useCase: GetAsaProfilePreviewUseCase): GetAsaProfilePreview = useCase
}
