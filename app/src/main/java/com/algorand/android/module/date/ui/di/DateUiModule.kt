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

package com.algorand.android.module.date.ui.di

import com.algorand.android.module.date.ui.mapper.DateFilterPreviewMapper
import com.algorand.android.module.date.ui.mapper.DateFilterPreviewMapperImpl
import com.algorand.android.module.date.ui.usecase.GetCustomDateRangePreview
import com.algorand.android.module.date.ui.usecase.GetCustomDateRangePreviewUseCase
import com.algorand.android.module.date.ui.usecase.GetDateFilterList
import com.algorand.android.module.date.ui.usecase.GetDateFilterListUseCase
import com.algorand.android.module.date.ui.usecase.GetDefaultCustomRange
import com.algorand.android.module.date.ui.usecase.GetDefaultCustomRangeUseCase
import com.algorand.android.module.date.ui.usecase.GetUpdatedCustomRange
import com.algorand.android.module.date.ui.usecase.GetUpdatedCustomRangeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DateUiModule {

    @Provides
    @Singleton
    fun provideDateFilterPreviewMapper(impl: DateFilterPreviewMapperImpl): DateFilterPreviewMapper = impl

    @Provides
    @Singleton
    fun provideGetUpdatedCustomRange(useCase: GetUpdatedCustomRangeUseCase): GetUpdatedCustomRange = useCase

    @Provides
    @Singleton
    fun provideGetCustomDateRangePreview(
        useCase: GetCustomDateRangePreviewUseCase
    ): GetCustomDateRangePreview = useCase

    @Provides
    @Singleton
    fun provideGetDefaultCustomRange(useCase: GetDefaultCustomRangeUseCase): GetDefaultCustomRange = useCase

    @Provides
    @Singleton
    fun provideGetDateFilterList(useCase: GetDateFilterListUseCase): GetDateFilterList = useCase
}