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

package com.algorand.android.module.swap.component.reddot.di

import android.content.SharedPreferences
import com.algorand.android.module.swap.component.reddot.data.repository.SwapFeatureRedDotRepositoryImpl
import com.algorand.android.module.swap.component.reddot.data.storage.SwapFeatureRedDotPreferenceLocalSource
import com.algorand.android.module.swap.component.reddot.domain.repository.SwapFeatureRedDotRepository
import com.algorand.android.module.swap.component.reddot.domain.usecase.GetSwapFeatureRedDotVisibility
import com.algorand.android.module.swap.component.reddot.domain.usecase.SetSwapFeatureRedDotVisibility
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SwapFeatureRedDotModule {

    @Provides
    @Singleton
    fun provideSwapFeatureRedDotRepository(
        sharedPreferences: SharedPreferences
    ): SwapFeatureRedDotRepository {
        return SwapFeatureRedDotRepositoryImpl(
            SwapFeatureRedDotPreferenceLocalSource(sharedPreferences)
        )
    }

    @Provides
    @Singleton
    fun provideGetSwapFeatureRedDotVisibility(
        swapFeatureRedDotRepository: SwapFeatureRedDotRepository
    ): GetSwapFeatureRedDotVisibility {
        return GetSwapFeatureRedDotVisibility(swapFeatureRedDotRepository::getSwapFeatureRedDotVisibility)
    }

    @Provides
    @Singleton
    fun provideSetSwapFeatureRedDotVisibility(
        swapFeatureRedDotRepository: SwapFeatureRedDotRepository
    ): SetSwapFeatureRedDotVisibility {
        return SetSwapFeatureRedDotVisibility(swapFeatureRedDotRepository::setSwapFeatureRedDotVisibility)
    }
}
