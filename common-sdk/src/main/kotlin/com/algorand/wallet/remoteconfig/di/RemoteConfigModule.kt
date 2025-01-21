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

package com.algorand.wallet.remoteconfig.di

import com.algorand.wallet.remoteconfig.data.repository.FeatureToggleRepositoryImpl
import com.algorand.wallet.remoteconfig.data.service.FirebaseRemoteConfigService
import com.algorand.wallet.remoteconfig.data.service.FirebaseRemoteConfigServiceImpl
import com.algorand.wallet.remoteconfig.domain.repository.FeatureToggleRepository
import com.algorand.wallet.remoteconfig.domain.usecase.InitializeOperationalToggles
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RemoteConfigModule {

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfigService(impl: FirebaseRemoteConfigServiceImpl): FirebaseRemoteConfigService = impl

    @Provides
    @Singleton
    fun provideFeatureToggleRepository(impl: FeatureToggleRepositoryImpl): FeatureToggleRepository = impl

    @Provides
    @Singleton
    fun provideInitializeOperationalToggles(
        repository: FeatureToggleRepository
    ): InitializeOperationalToggles = InitializeOperationalToggles(repository::initializeOperationalToggles)

    @Provides
    @Singleton
    fun provideIsFeatureToggleEnabled(
        repository: FeatureToggleRepository
    ): IsFeatureToggleEnabled = IsFeatureToggleEnabled(repository::isFeatureEnabled)
}
