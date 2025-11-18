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

package com.algorand.wallet.cards.di

import com.algorand.wallet.cards.data.mapper.CardNftRewardStateMapper
import com.algorand.wallet.cards.data.mapper.DefaultCardNftRewardStateMapper
import com.algorand.wallet.cards.data.mapper.DefaultFundAddressMapper
import com.algorand.wallet.cards.data.mapper.FundAddressMapper
import com.algorand.wallet.cards.data.repository.DefaultCardRepository
import com.algorand.wallet.cards.data.service.CardApiService
import com.algorand.wallet.cards.domain.repository.CardRepository
import com.algorand.wallet.cards.domain.usecase.GetCardFundAddresses
import com.algorand.wallet.cards.domain.usecase.GetCardFundAddressesUseCase
import com.algorand.wallet.cards.domain.usecase.IsCountryWaitlistedForCards
import com.algorand.wallet.cards.domain.usecase.IsCountryWaitlistedForCardsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CardsModule {

    @Provides
    fun provideGetCardFundAddresses(useCase: GetCardFundAddressesUseCase): GetCardFundAddresses = useCase

    @Provides
    fun provideCardNftRewardStateMapper(mapper: DefaultCardNftRewardStateMapper): CardNftRewardStateMapper = mapper

    @Provides
    fun provideFundAddressMapper(mapper: DefaultFundAddressMapper): FundAddressMapper = mapper

    @Provides
    fun provideCardRepository(repository: DefaultCardRepository): CardRepository = repository

    @Provides
    @Singleton
    fun provideCardApiService(@Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit): CardApiService {
        return retrofit.create(CardApiService::class.java)
    }

    @Provides
    fun provideIsCountryWaitlistedForCards(
        useCase: IsCountryWaitlistedForCardsUseCase
    ): IsCountryWaitlistedForCards = useCase
}
