package com.algorand.android.modules.walletconnect.cards.di

import com.algorand.android.modules.walletconnect.cards.data.cache.WalletConnectCardsRequestAccountCache
import com.algorand.android.modules.walletconnect.cards.data.cache.WalletConnectCardsRequestAccountCacheImpl
import com.algorand.android.modules.walletconnect.cards.data.repository.WalletConnectCardsRepositoryImpl
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
}
