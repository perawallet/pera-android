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

package com.algorand.common.remoteconfig.di

import com.algorand.common.remoteconfig.data.repository.FeatureToggleRepositoryImpl
import com.algorand.common.remoteconfig.data.service.FirebaseRemoteConfigService
import com.algorand.common.remoteconfig.data.service.getFirebaseRemoteConfigService
import com.algorand.common.remoteconfig.domain.repository.FeatureToggleRepository
import com.algorand.common.remoteconfig.domain.usecase.InitializeOperationalToggles
import com.algorand.common.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import org.koin.dsl.module

val remoteConfigModule = module {
    single<FirebaseRemoteConfigService> { getFirebaseRemoteConfigService() }
    single<FeatureToggleRepository> { FeatureToggleRepositoryImpl(get()) }
    single<InitializeOperationalToggles> {
        InitializeOperationalToggles {
            get<FeatureToggleRepository>().initializeOperationalToggles()
        }
    }
    single<IsFeatureToggleEnabled> {
        IsFeatureToggleEnabled { toggleName ->
            get<FeatureToggleRepository>().isFeatureEnabled(toggleName)
        }
    }
}
