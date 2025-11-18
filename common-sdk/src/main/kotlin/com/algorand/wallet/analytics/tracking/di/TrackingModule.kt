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

package com.algorand.wallet.analytics.tracking.di

import com.algorand.wallet.analytics.tracking.domain.repository.PeraAnalyticsRepository
import com.algorand.wallet.analytics.tracking.domain.tracker.DefaultPeraAnalyticsEventTracker
import com.algorand.wallet.analytics.tracking.domain.tracker.PeraAnalyticsEventTracker
import com.algorand.wallet.analytics.tracking.domain.usecase.GetEventNameForSelectedNode
import com.algorand.wallet.analytics.tracking.domain.usecase.GetEventNameForSelectedNodeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TrackingModule {

    @Singleton
    @Provides
    fun providePeraAnalyticsEventTracker(
        repository: PeraAnalyticsRepository,
        getEventNameForSelectedNode: GetEventNameForSelectedNode
    ): PeraAnalyticsEventTracker {
        return DefaultPeraAnalyticsEventTracker(
            repository,
            getEventNameForSelectedNode,
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        )
    }

    @Provides
    fun provideGetEventNameForSelectedNode(
        useCase: GetEventNameForSelectedNodeUseCase
    ): GetEventNameForSelectedNode = useCase
}
