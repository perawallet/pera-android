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

package com.algorand.wallet.analytics.di

import android.content.Context
import com.algorand.wallet.analytics.PeraReferrerQueryParamParserImpl
import com.algorand.wallet.analytics.data.repository.ReferrerRepositoryImpl
import com.algorand.wallet.analytics.data.service.PeraEventTrackerImpl
import com.algorand.wallet.analytics.data.service.PeraReferrerManagerImpl
import com.algorand.wallet.analytics.domain.repository.ReferrerRepository
import com.algorand.wallet.analytics.domain.service.PeraEventTracker
import com.algorand.wallet.analytics.domain.service.PeraReferrerManager
import com.algorand.wallet.analytics.domain.service.PeraReferrerQueryParamParser
import com.algorand.wallet.analytics.domain.usecase.GetReferrerData
import com.algorand.wallet.analytics.domain.usecase.SaveReferrerData
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AnalyticsModule {

    @Singleton
    @Provides
    fun providePeraEventTracker(
        firebaseAnalytics: FirebaseAnalytics,
        getReferrerData: GetReferrerData
    ): PeraEventTracker {
        return PeraEventTrackerImpl(firebaseAnalytics, getReferrerData)
    }

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(@ApplicationContext appContext: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(appContext)
    }

    @Provides
    @Singleton
    fun provideReferrerManager(impl: PeraReferrerManagerImpl): PeraReferrerManager = impl

    @Provides
    @Singleton
    fun provideReferrerRepository(impl: ReferrerRepositoryImpl): ReferrerRepository = impl

    @Provides
    fun provideReferrerQueryParamParser(impl: PeraReferrerQueryParamParserImpl): PeraReferrerQueryParamParser = impl

    @Provides
    fun provideGetReferrerData(repository: ReferrerRepository): GetReferrerData = GetReferrerData(repository::getReferrerData)

    @Provides
    fun provideSaveReferrerData(repository: ReferrerRepository): SaveReferrerData = SaveReferrerData(repository::saveReferrerData)
}
