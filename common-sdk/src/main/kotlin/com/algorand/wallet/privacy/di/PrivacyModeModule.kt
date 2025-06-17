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

package com.algorand.wallet.privacy.di

import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import com.algorand.wallet.privacy.data.mapper.DefaultPrivacyModeCacheValueMapper
import com.algorand.wallet.privacy.data.mapper.DefaultPrivacyModeMapper
import com.algorand.wallet.privacy.data.mapper.PrivacyModeCacheValueMapper
import com.algorand.wallet.privacy.data.mapper.PrivacyModeMapper
import com.algorand.wallet.privacy.data.model.PrivacyModeCacheValue
import com.algorand.wallet.privacy.data.repository.DefaultPrivacyModeRepository
import com.algorand.wallet.privacy.domain.repository.PrivacyModeRepository
import com.algorand.wallet.privacy.domain.usecase.GetPrivacyModeFlow
import com.algorand.wallet.privacy.domain.usecase.SetPrivacyMode
import com.algorand.wallet.privacy.domain.usecase.TogglePrivacyMode
import com.algorand.wallet.privacy.domain.usecase.TogglePrivacyModeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object PrivacyModeModule {

    @Provides
    @Singleton
    fun providePrivacyModeRepository(
        persistentCacheProvider: PersistentCacheProvider,
        privacyModeCacheValueMapper: PrivacyModeCacheValueMapper,
        privacyModeMapper: PrivacyModeMapper
    ): PrivacyModeRepository {
        return DefaultPrivacyModeRepository(
            flowPersistentCache = persistentCacheProvider.getFlowPersistentCache(
                type = PrivacyModeCacheValue::class.java,
                key = "shared_pref_privacy_mode",
                defaultValue = PrivacyModeCacheValue.DISABLED
            ),
            privacyModeCacheValueMapper = privacyModeCacheValueMapper,
            privacyModeMapper = privacyModeMapper
        )
    }

    @Provides
    fun provideGetPrivacyModeFlow(repository: PrivacyModeRepository): GetPrivacyModeFlow {
        return GetPrivacyModeFlow(repository::getPrivacyModeFlow)
    }

    @Provides
    fun provideSetPrivacyMode(repository: PrivacyModeRepository): SetPrivacyMode {
        return SetPrivacyMode(repository::setPrivacyMode)
    }

    @Provides
    fun provideTogglePrivacyMode(useCase: TogglePrivacyModeUseCase): TogglePrivacyMode = useCase

    @Provides
    fun providePrivacyModeCacheValueMapper(mapper: DefaultPrivacyModeCacheValueMapper): PrivacyModeCacheValueMapper {
        return mapper
    }

    @Provides
    fun providePrivacyModeMapper(mapper: DefaultPrivacyModeMapper): PrivacyModeMapper = mapper
}
