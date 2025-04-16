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

package com.algorand.android.modules.currency.di

import com.algorand.android.modules.currency.domain.usecase.CurrencyUseCase
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencyId
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencyName
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencyNameUseCase
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrName
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolOrNameUseCase
import com.algorand.android.modules.currency.domain.usecase.GetPrimaryCurrencySymbolUseCase
import com.algorand.android.modules.currency.domain.usecase.GetSecondaryCurrencySymbol
import com.algorand.android.modules.currency.domain.usecase.GetSecondaryCurrencySymbolUseCase
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgo
import com.algorand.android.modules.currency.domain.usecase.IsPrimaryCurrencyAlgoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object CurrencyModule {

    @Provides
    fun provideGetPrimaryCurrencyId(currencyUseCase: CurrencyUseCase): GetPrimaryCurrencyId {
        return GetPrimaryCurrencyId(currencyUseCase::getPrimaryCurrencyId)
    }

    @Provides
    fun provideGetPrimaryCurrencyName(useCase: GetPrimaryCurrencyNameUseCase): GetPrimaryCurrencyName = useCase

    @Provides
    fun provideGetPrimaryCurrencySymbol(useCase: GetPrimaryCurrencySymbolUseCase): GetPrimaryCurrencySymbol = useCase

    @Provides
    fun provideGetPrimaryCurrencySymbolOrName(
        useCase: GetPrimaryCurrencySymbolOrNameUseCase
    ): GetPrimaryCurrencySymbolOrName = useCase

    @Provides
    fun provideIsPrimaryCurrencyAlgo(useCase: IsPrimaryCurrencyAlgoUseCase): IsPrimaryCurrencyAlgo = useCase

    @Provides
    fun provideGetSecondaryCurrencySymbol(
        useCase: GetSecondaryCurrencySymbolUseCase
    ): GetSecondaryCurrencySymbol = useCase
}
