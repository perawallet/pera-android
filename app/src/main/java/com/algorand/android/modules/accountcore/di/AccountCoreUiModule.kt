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

package com.algorand.android.modules.accountcore.di

import android.content.Context
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountTotalValue
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountTotalValueUseCase
import com.algorand.android.modules.accountcore.ui.mapper.AccountItemConfigurationMapper
import com.algorand.android.modules.accountcore.ui.mapper.AccountItemConfigurationMapperImpl
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDetailSummary
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDetailSummaryUseCase
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayNameUseCase
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreviewUseCase
import com.algorand.common.account.custom.domain.usecase.GetAccountCustomInfoOrNull
import com.algorand.common.account.detail.domain.usecase.GetAccountDetail
import com.algorand.common.nameservice.domain.usecase.GetAccountNameService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object AccountCoreUiModule {

    @Provides
    fun provideGetAccountIconDrawablePreview(
        useCase: GetAccountIconDrawablePreviewUseCase
    ): GetAccountIconDrawablePreview = useCase

    @Provides
    fun provideGetAccountDisplayName(
        getAccountCustomInfoOrNull: GetAccountCustomInfoOrNull,
        getAccountDetail: GetAccountDetail,
        @ApplicationContext context: Context,
        getAccountNameService: GetAccountNameService
    ): GetAccountDisplayName {
        return GetAccountDisplayNameUseCase(
            getCustomInfoOrNull = getAccountCustomInfoOrNull,
            getAccountDetail = getAccountDetail,
            resources = context.resources,
            getAccountNameService = getAccountNameService
        )
    }

    @Provides
    fun provideGetAccountTotalValue(useCase: GetAccountTotalValueUseCase): GetAccountTotalValue = useCase

    @Provides
    fun provideAccountItemConfigurationMapper(
        mapper: AccountItemConfigurationMapperImpl
    ): AccountItemConfigurationMapper = mapper

    @Provides
    fun provideGetAccountDetailSummary(
        useCase: GetAccountDetailSummaryUseCase
    ): GetAccountDetailSummary = useCase
}
