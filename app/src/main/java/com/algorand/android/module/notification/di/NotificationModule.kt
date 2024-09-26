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

package com.algorand.android.module.notification.di

import android.content.SharedPreferences
import com.algorand.android.module.notification.data.mapper.NotificationHistoryMapper
import com.algorand.android.module.notification.data.mapper.NotificationHistoryMapperImpl
import com.algorand.android.module.notification.data.repository.NotificationRepositoryImpl
import com.algorand.android.module.notification.data.service.NotificationApiService
import com.algorand.android.module.notification.data.storage.NotificationRefreshTimeLocalSource
import com.algorand.android.module.notification.domain.repository.NotificationRepository
import com.algorand.android.module.notification.domain.usecase.DeleteNotificationFilterByAddress
import com.algorand.android.module.notification.domain.usecase.GetNotificationHistoryPagingFlow
import com.algorand.android.module.notification.domain.usecase.GetNotificationHistoryPagingFlowUseCase
import com.algorand.android.module.notification.domain.usecase.GetNotificationLastRefreshDateTime
import com.algorand.android.module.notification.domain.usecase.GetNotificationLastRefreshDateTimeUseCase
import com.algorand.android.module.notification.domain.usecase.SetNotificationFilter
import com.algorand.android.module.notification.domain.usecase.SetNotificationFilterUseCase
import com.algorand.android.module.notification.domain.usecase.SetNotificationLastRefreshDateTime
import com.algorand.android.module.notification.domain.usecase.SetNotificationLastRefreshDateTimeUseCase
import com.algorand.android.module.network.exceptions.RetrofitErrorHandler
import com.algorand.android.shared_db.notification.dao.NotificationFilterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        sharedPreferences: SharedPreferences,
        notificationFilterDao: NotificationFilterDao,
        notificationApiService: NotificationApiService,
        hipoErrorHandler: RetrofitErrorHandler,
        notificationHistoryMapper: NotificationHistoryMapper
    ): NotificationRepository {
        return NotificationRepositoryImpl(
            NotificationRefreshTimeLocalSource(sharedPreferences),
            notificationFilterDao,
            hipoErrorHandler,
            notificationApiService,
            notificationHistoryMapper
        )
    }

    @Provides
    @Singleton
    fun provideGetNotificationLastRefreshDateTime(
        useCase: GetNotificationLastRefreshDateTimeUseCase
    ): GetNotificationLastRefreshDateTime = useCase

    @Provides
    @Singleton
    fun provideSetNotificationLastRefreshDateTime(
        useCase: SetNotificationLastRefreshDateTimeUseCase
    ): SetNotificationLastRefreshDateTime = useCase

    @Provides
    @Singleton
    fun provideGetNotificationHistoryPagingFlow(
        useCase: GetNotificationHistoryPagingFlowUseCase
    ): GetNotificationHistoryPagingFlow = useCase

    @Provides
    @Singleton
    fun provideNotificationHistoryMapper(
        impl: NotificationHistoryMapperImpl
    ): NotificationHistoryMapper = impl

    @Provides
    @Singleton
    fun provideSetNotificationFilter(useCase: SetNotificationFilterUseCase): SetNotificationFilter = useCase

    @Provides
    @Singleton
    fun provideDeleteNotificationFilterByAddress(
        repository: NotificationRepository
    ): DeleteNotificationFilterByAddress = DeleteNotificationFilterByAddress(repository::deleteFilterByAddress)
}
