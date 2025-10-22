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

package com.algorand.wallet.devoptions.di

import com.algorand.wallet.devoptions.data.cache.DefaultDeveloperOptionsFeatureFlagsCache
import com.algorand.wallet.devoptions.data.cache.DeveloperOptionsFeatureFlagsCache
import com.algorand.wallet.devoptions.data.repository.DefaultDeveloperOptionsRepository
import com.algorand.wallet.devoptions.domain.repository.DeveloperOptionsRepository
import com.algorand.wallet.devoptions.domain.usecase.ClearOverriddenFeatureFlag
import com.algorand.wallet.devoptions.domain.usecase.DisableDeveloperOptions
import com.algorand.wallet.devoptions.domain.usecase.EnableDeveloperOptions
import com.algorand.wallet.devoptions.domain.usecase.GetAllDeveloperOptionFeatureFlags
import com.algorand.wallet.devoptions.domain.usecase.GetAllDeveloperOptionFeatureFlagsUseCase
import com.algorand.wallet.devoptions.domain.usecase.GetOverriddenFeatureFlagStatus
import com.algorand.wallet.devoptions.domain.usecase.GetOverriddenFeatureFlags
import com.algorand.wallet.devoptions.domain.usecase.IsDeveloperOptionsEnabled
import com.algorand.wallet.devoptions.domain.usecase.OverrideFeatureFlagStatus
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DeveloperOptionsModule {

    @Provides
    fun provideDeveloperOptionsFeatureFlagsCache(
        cacheProvider: PersistentCacheProvider
    ): DeveloperOptionsFeatureFlagsCache {
        return DefaultDeveloperOptionsFeatureFlagsCache(
            cacheProvider.getFlowPersistentCache(
                type = Map::class.java,
                key = "developer_options_feature_flags_key",
                defaultValue = emptyMap()
            )
        )
    }

    @Provides
    fun provideDeveloperOptionsRepository(
        cacheProvider: PersistentCacheProvider,
        developerOptionsFeatureFlagsCache: DeveloperOptionsFeatureFlagsCache
    ): DeveloperOptionsRepository {
        return DefaultDeveloperOptionsRepository(
            cacheProvider.getFlowPersistentCache(
                type = Boolean::class.java,
                key = "developer_options_status_key",
                defaultValue = false
            ),
            developerOptionsFeatureFlagsCache
        )
    }

    @Provides
    fun provideEnableDeveloperOptions(repository: DeveloperOptionsRepository): EnableDeveloperOptions {
        return EnableDeveloperOptions(repository::enableDeveloperOptions)
    }

    @Provides
    fun provideDisableDeveloperOptions(repository: DeveloperOptionsRepository): DisableDeveloperOptions {
        return DisableDeveloperOptions(repository::disableDeveloperOptions)
    }

    @Provides
    fun provideIsDeveloperOptionsEnabled(repository: DeveloperOptionsRepository): IsDeveloperOptionsEnabled {
        return IsDeveloperOptionsEnabled(repository::isDeveloperOptionsEnabled)
    }

    @Provides
    fun provideOverrideFeatureFlagStatus(repository: DeveloperOptionsRepository): OverrideFeatureFlagStatus {
        return OverrideFeatureFlagStatus(repository::setFeatureFlagStatus)
    }

    @Provides
    fun provideGetOverriddenFeatureFlagStatus(repository: DeveloperOptionsRepository): GetOverriddenFeatureFlagStatus {
        return GetOverriddenFeatureFlagStatus(repository::isFeatureFlagEnabled)
    }

    @Provides
    fun provideClearOverriddenFeatureFlag(repository: DeveloperOptionsRepository): ClearOverriddenFeatureFlag {
        return ClearOverriddenFeatureFlag(repository::clearFeatureFlag)
    }

    @Provides
    fun provideGetOverriddenFeatureFlags(repository: DeveloperOptionsRepository): GetOverriddenFeatureFlags {
        return GetOverriddenFeatureFlags(repository::getFeatureFlags)
    }

    @Provides
    fun provideGetAllDeveloperOptionFeatureFlags(
        useCase: GetAllDeveloperOptionFeatureFlagsUseCase
    ): GetAllDeveloperOptionFeatureFlags = useCase
}
