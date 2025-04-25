/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.parity.di

import com.algorand.android.modules.parity.data.repository.ParityRepositoryImpl
import com.algorand.android.modules.parity.domain.repository.ParityRepository
import com.algorand.android.modules.parity.domain.usecase.CalculateParityValue
import com.algorand.android.modules.parity.domain.usecase.CalculateParityValueUseCase
import com.algorand.android.modules.parity.domain.usecase.GetAlgoAmountValue
import com.algorand.android.modules.parity.domain.usecase.GetAlgoAmountValueUseCase
import com.algorand.android.modules.parity.domain.usecase.GetAlgoToUsdConversionRate
import com.algorand.android.modules.parity.domain.usecase.GetAlgoToUsdConversionRateUseCase
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryAlgoParityValue
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryAlgoParityValueUseCase
import com.algorand.android.modules.parity.domain.usecase.GetPrimaryCurrencyAssetParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryAlgoParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryAlgoParityValueUseCase
import com.algorand.android.modules.parity.domain.usecase.GetSecondaryCurrencyAssetParityValue
import com.algorand.android.modules.parity.domain.usecase.GetSelectedCurrencyDetailFlow
import com.algorand.android.modules.parity.domain.usecase.GetUsdToAlgoConversionRate
import com.algorand.android.modules.parity.domain.usecase.GetUsdToAlgoConversionRateUseCase
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRate
import com.algorand.android.modules.parity.domain.usecase.GetUsdToPrimaryCurrencyConversionRateUseCase
import com.algorand.android.modules.parity.domain.usecase.GetUsdToSecondaryCurrencyConversionRate
import com.algorand.android.modules.parity.domain.usecase.GetUsdToSecondaryCurrencyConversionRateUseCase
import com.algorand.android.modules.parity.domain.usecase.PrimaryCurrencyParityCalculationUseCase
import com.algorand.android.modules.parity.domain.usecase.SecondaryCurrencyParityCalculationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("TooManyFunctions")
@Module
@InstallIn(SingletonComponent::class)
internal object ParityModule {

    @Provides
    fun provideParityRepository(repository: ParityRepositoryImpl): ParityRepository = repository

    @Provides
    fun provideGetPrimaryCurrencyAssetParityValue(
        useCase: PrimaryCurrencyParityCalculationUseCase
    ): GetPrimaryCurrencyAssetParityValue {
        return GetPrimaryCurrencyAssetParityValue(useCase::getAssetParityValue)
    }

    @Provides
    fun provideGetSecondaryCurrencyAssetParityValue(
        useCase: SecondaryCurrencyParityCalculationUseCase
    ): GetSecondaryCurrencyAssetParityValue {
        return GetSecondaryCurrencyAssetParityValue(useCase::getAssetParityValue)
    }

    @Provides
    fun provideGetUsdToAlgoConversionRate(
        useCase: GetUsdToAlgoConversionRateUseCase
    ): GetUsdToAlgoConversionRate = useCase

    @Provides
    fun provideGetAlgoToUsdConversionRate(
        useCase: GetAlgoToUsdConversionRateUseCase
    ): GetAlgoToUsdConversionRate = useCase

    @Provides
    fun provideGetUsdToPrimaryCurrencyConversionRate(
        useCase: GetUsdToPrimaryCurrencyConversionRateUseCase
    ): GetUsdToPrimaryCurrencyConversionRate = useCase

    @Provides
    fun provideCalculateParityValue(useCase: CalculateParityValueUseCase): CalculateParityValue = useCase

    @Provides
    fun provideGetPrimaryAlgoParityValue(useCase: GetPrimaryAlgoParityValueUseCase): GetPrimaryAlgoParityValue = useCase

    @Provides
    fun provideGetAlgoAmountValue(useCase: GetAlgoAmountValueUseCase): GetAlgoAmountValue = useCase

    @Provides
    fun provideGetUsdToSecondaryCurrencyConversionRate(
        useCase: GetUsdToSecondaryCurrencyConversionRateUseCase
    ): GetUsdToSecondaryCurrencyConversionRate = useCase

    @Provides
    fun provideGetSecondaryAlgoParityValue(
        useCase: GetSecondaryAlgoParityValueUseCase
    ): GetSecondaryAlgoParityValue = useCase

    @Provides
    fun provideGetSelectedCurrencyDetailFlow(parityRepository: ParityRepository): GetSelectedCurrencyDetailFlow {
        return GetSelectedCurrencyDetailFlow(parityRepository::getSelectedCurrencyDetailCacheFlow)
    }
}
