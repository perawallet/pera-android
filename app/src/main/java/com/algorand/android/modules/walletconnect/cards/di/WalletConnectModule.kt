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

package com.algorand.android.modules.walletconnect.cards.di

import com.algorand.android.modules.walletconnect.cards.data.cache.WalletConnectCardsRequestAccountCache
import com.algorand.android.modules.walletconnect.cards.data.cache.WalletConnectCardsRequestAccountCacheImpl
import com.algorand.android.modules.walletconnect.cards.data.repository.WalletConnectCardsRepositoryImpl
import com.algorand.android.modules.walletconnect.cards.domain.helper.WalletConnectUrlSelectedAddressParser
import com.algorand.android.modules.walletconnect.cards.domain.helper.WalletConnectUrlSelectedAddressParserImpl
import com.algorand.android.modules.walletconnect.cards.domain.repository.WalletConnectCardsRepository
import com.algorand.android.modules.walletconnect.cards.domain.usecase.CacheWalletConnectRequestPreselectedAccountAddresses
import com.algorand.android.modules.walletconnect.cards.domain.usecase.CacheWalletConnectRequestPreselectedAccountAddressesUseCase
import com.algorand.android.modules.walletconnect.cards.domain.usecase.ClearWalletConnectPreselectedAddressCache
import com.algorand.android.modules.walletconnect.cards.domain.usecase.GetWalletConnectRequestPreselectedAccountAddresses
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object WalletConnectModule {

    @Provides
    @Singleton
    fun provideWalletConnectCardsRequestAccountCache(
        impl: WalletConnectCardsRequestAccountCacheImpl
    ): WalletConnectCardsRequestAccountCache = impl

    @Provides
    fun provideWalletConnectCardsRepository(
        impl: WalletConnectCardsRepositoryImpl
    ): WalletConnectCardsRepository = impl

    @Provides
    fun provideGetWalletConnectRequestPreselectedAccountAddresses(
        repository: WalletConnectCardsRepository
    ): GetWalletConnectRequestPreselectedAccountAddresses {
        return GetWalletConnectRequestPreselectedAccountAddresses(repository::getPreselectedAccountAddresses)
    }

    @Provides
    fun provideClearWalletConnectPreselectedAddressCacheById(
        repository: WalletConnectCardsRepository
    ): ClearWalletConnectPreselectedAddressCache {
        return ClearWalletConnectPreselectedAddressCache(repository::clearPreselectedAccountAddressesCache)
    }

    @Provides
    fun provideCacheWalletConnectRequestPreselectedAccountAddresses(
        useCase: CacheWalletConnectRequestPreselectedAccountAddressesUseCase
    ): CacheWalletConnectRequestPreselectedAccountAddresses = useCase

    @Provides
    fun provideWalletConnectUrlSelectedAddressParser(
        impl: WalletConnectUrlSelectedAddressParserImpl
    ): WalletConnectUrlSelectedAddressParser = impl
}
