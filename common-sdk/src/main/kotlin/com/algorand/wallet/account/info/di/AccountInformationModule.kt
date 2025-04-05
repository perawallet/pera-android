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

package com.algorand.wallet.account.info.di

import com.algorand.wallet.account.info.data.cache.AccountInformationErrorCache
import com.algorand.wallet.account.info.data.cache.AccountInformationErrorCacheImpl
import com.algorand.wallet.account.info.data.database.dao.AccountInformationDao
import com.algorand.wallet.account.info.data.database.dao.AssetHoldingDao
import com.algorand.wallet.account.info.data.mapper.entity.AccountInformationEntityMapper
import com.algorand.wallet.account.info.data.mapper.entity.AccountInformationEntityMapperImpl
import com.algorand.wallet.account.info.data.mapper.entity.AssetHoldingEntityMapper
import com.algorand.wallet.account.info.data.mapper.entity.AssetHoldingEntityMapperImpl
import com.algorand.wallet.account.info.data.mapper.entity.AssetStatusEntityMapper
import com.algorand.wallet.account.info.data.mapper.entity.AssetStatusEntityMapperImpl
import com.algorand.wallet.account.info.data.mapper.model.AccountFastLookupMapper
import com.algorand.wallet.account.info.data.mapper.model.AccountFastLookupMapperImpl
import com.algorand.wallet.account.info.data.mapper.model.AccountInformationMapper
import com.algorand.wallet.account.info.data.mapper.model.AccountInformationMapperImpl
import com.algorand.wallet.account.info.data.mapper.model.AccountInformationResponseMapper
import com.algorand.wallet.account.info.data.mapper.model.AccountInformationResponseMapperImpl
import com.algorand.wallet.account.info.data.mapper.model.AppStateSchemeMapper
import com.algorand.wallet.account.info.data.mapper.model.AppStateSchemeMapperImpl
import com.algorand.wallet.account.info.data.mapper.model.AssetHoldingMapper
import com.algorand.wallet.account.info.data.mapper.model.AssetHoldingMapperImpl
import com.algorand.wallet.account.info.data.repository.AccountAssetHoldingsFetchHelper
import com.algorand.wallet.account.info.data.repository.AccountAssetHoldingsFetchHelperImpl
import com.algorand.wallet.account.info.data.repository.AccountFastLookupRepositoryImpl
import com.algorand.wallet.account.info.data.repository.AccountInformationCacheHelper
import com.algorand.wallet.account.info.data.repository.AccountInformationCacheHelperImpl
import com.algorand.wallet.account.info.data.repository.AccountInformationFetchHelper
import com.algorand.wallet.account.info.data.repository.AccountInformationFetchHelperImpl
import com.algorand.wallet.account.info.data.repository.AccountInformationRepositoryImpl
import com.algorand.wallet.account.info.data.repository.AssetHoldingCacheHelper
import com.algorand.wallet.account.info.data.repository.AssetHoldingCacheHelperImpl
import com.algorand.wallet.account.info.data.service.AccountFastLookupApiService
import com.algorand.wallet.account.info.data.service.AccountInformationApiService
import com.algorand.wallet.account.info.domain.manager.AccountCacheManager
import com.algorand.wallet.account.info.domain.manager.AccountCacheManagerImpl
import com.algorand.wallet.account.info.domain.repository.AccountFastLookupRepository
import com.algorand.wallet.account.info.domain.repository.AccountInformationRepository
import com.algorand.wallet.account.info.domain.usecase.AddAssetHoldingToAccountAsPending
import com.algorand.wallet.account.info.domain.usecase.ClearAccountInformationCache
import com.algorand.wallet.account.info.domain.usecase.DeleteAccountInformation
import com.algorand.wallet.account.info.domain.usecase.FetchAccountInformation
import com.algorand.wallet.account.info.domain.usecase.FetchAndCacheAccountInformation
import com.algorand.wallet.account.info.domain.usecase.FetchRekeyedAccounts
import com.algorand.wallet.account.info.domain.usecase.GetAccountAlgoBalance
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHoldingsFlow
import com.algorand.wallet.account.info.domain.usecase.GetAccountDetailCacheStatusFlow
import com.algorand.wallet.account.info.domain.usecase.GetAccountDetailCacheStatusFlowUseCase
import com.algorand.wallet.account.info.domain.usecase.GetAccountFastLookup
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformation
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.account.info.domain.usecase.GetAllAccountInformationFlow
import com.algorand.wallet.account.info.domain.usecase.GetAllAssetHoldingIds
import com.algorand.wallet.account.info.domain.usecase.GetAllFailedCachedAccountAddresses
import com.algorand.wallet.account.info.domain.usecase.GetAllSuccessfullyCachedAccountAddresses
import com.algorand.wallet.account.info.domain.usecase.GetCachedAccountInformationCountFlow
import com.algorand.wallet.account.info.domain.usecase.GetEarliestLastFetchedRound
import com.algorand.wallet.account.info.domain.usecase.GetRegisteredHdKeys
import com.algorand.wallet.account.info.domain.usecase.GetRegisteredHdKeysUseCase
import com.algorand.wallet.account.info.domain.usecase.IsAccountCachedSuccessfully
import com.algorand.wallet.account.info.domain.usecase.IsAccountCachedSuccessfullyUseCase
import com.algorand.wallet.account.info.domain.usecase.IsAssetOptedInByAnyLocalAccount
import com.algorand.wallet.account.info.domain.usecase.IsAssetOwnedByAccount
import com.algorand.wallet.account.info.domain.usecase.IsAssetOwnedByAccountUseCase
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyCachedErrorAccount
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyCachedErrorAccountUseCase
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyCachedSuccessAccount
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyCachedSuccessAccountUseCase
import com.algorand.wallet.account.info.domain.usecase.SetAccountAssetStatus
import com.algorand.wallet.foundation.cache.SingleInMemoryLocalCache
import com.algorand.wallet.foundation.database.PeraDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object AccountInformationModule {

    @Provides
    @Singleton
    fun provideAccountCacheManager(impl: AccountCacheManagerImpl): AccountCacheManager = impl

    @Provides
    @Singleton
    fun provideAccountFastLookupApiService(
        @Named("mobileAlgorandFastLookupRetrofitInterface") retrofit: Retrofit
    ): AccountFastLookupApiService {
        return retrofit.create(AccountFastLookupApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAccountInformationApiService(
        @Named("indexerRetrofitInterface") retrofit: Retrofit
    ): AccountInformationApiService {
        return retrofit.create(AccountInformationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAccountInformationRepository(impl: AccountInformationRepositoryImpl): AccountInformationRepository = impl

    @Provides
    @Singleton
    fun provideAssetHoldingCacheHelper(
        impl: AssetHoldingCacheHelperImpl
    ): AssetHoldingCacheHelper = impl

    @Provides
    @Singleton
    fun provideAccountFastLookupRepository(
        impl: AccountFastLookupRepositoryImpl
    ): AccountFastLookupRepository = impl

    @Provides
    @Singleton
    fun provideAccountInformationFetchHelper(
        impl: AccountInformationFetchHelperImpl
    ): AccountInformationFetchHelper = impl

    @Provides
    @Singleton
    fun provideAccountInformationErrorCache(): AccountInformationErrorCache {
        return AccountInformationErrorCacheImpl(SingleInMemoryLocalCache())
    }

    @Provides
    @Singleton
    fun provideAccountInformationCacheHelper(
        impl: AccountInformationCacheHelperImpl
    ): AccountInformationCacheHelper = impl

    @Provides
    @Singleton
    fun provideAccountAssetHoldingsFetchHelper(
        impl: AccountAssetHoldingsFetchHelperImpl
    ): AccountAssetHoldingsFetchHelper = impl

    @Provides
    fun provideAccountFastLookupMapper(impl: AccountFastLookupMapperImpl): AccountFastLookupMapper = impl

    @Provides
    fun provideAccountInformationMapper(impl: AccountInformationMapperImpl): AccountInformationMapper = impl

    @Provides
    fun provideAppStateSchemeMapper(impl: AppStateSchemeMapperImpl): AppStateSchemeMapper = impl

    @Provides
    fun provideAssetHoldingMapper(impl: AssetHoldingMapperImpl): AssetHoldingMapper = impl

    @Provides
    fun provideAccountInformationDao(database: PeraDatabase): AccountInformationDao = database.accountInformationDao()

    @Provides
    fun provideAssetHoldingDao(database: PeraDatabase): AssetHoldingDao = database.assetHoldingDao()

    @Provides
    fun provideAccountInformationResponseMapper(
        impl: AccountInformationResponseMapperImpl
    ): AccountInformationResponseMapper = impl

    @Provides
    fun provideAccountInformationEntityMapper(
        impl: AccountInformationEntityMapperImpl
    ): AccountInformationEntityMapper = impl

    @Provides
    fun provideAssetHoldingEntityMapper(
        impl: AssetHoldingEntityMapperImpl
    ): AssetHoldingEntityMapper = impl

    @Provides
    fun provideGetAccountDetailCacheStatusFlow(
        impl: GetAccountDetailCacheStatusFlowUseCase
    ): GetAccountDetailCacheStatusFlow = impl

    @Provides
    fun provideFetchAndCacheAccountInformation(
        repository: AccountInformationRepository
    ): FetchAndCacheAccountInformation {
        return FetchAndCacheAccountInformation(repository::fetchAndCacheAccountInformation)
    }

    @Provides
    fun provideGetEarliestLastFetchedRound(
        repository: AccountInformationRepository
    ): GetEarliestLastFetchedRound {
        return GetEarliestLastFetchedRound(repository::getEarliestLastFetchedRound)
    }

    @Provides
    fun provideGetAllSuccessfullyCachedAccountAddresses(
        repository: AccountInformationRepository
    ): GetAllSuccessfullyCachedAccountAddresses {
        return GetAllSuccessfullyCachedAccountAddresses(repository::getAllSuccessfullyCachedAccountAddresses)
    }

    @Provides
    fun provideGetAllAssetHoldingIds(
        repository: AccountInformationRepository
    ): GetAllAssetHoldingIds {
        return GetAllAssetHoldingIds(repository::getAllAssetHoldingIds)
    }

    @Provides
    fun provideGetCachedAccountInformationCountFlow(
        repository: AccountInformationRepository
    ): GetCachedAccountInformationCountFlow {
        return GetCachedAccountInformationCountFlow(repository::getCachedAccountInformationCountFlow)
    }

    @Provides
    fun provideClearAccountInformationCache(
        repository: AccountInformationRepository
    ): ClearAccountInformationCache {
        return ClearAccountInformationCache(repository::clearCache)
    }

    @Provides
    fun provideGetAllAccountInformationFlow(
        repository: AccountInformationRepository
    ): GetAllAccountInformationFlow {
        return GetAllAccountInformationFlow(repository::getAllAccountInformationFlow)
    }

    @Provides
    fun provideGetAccountFastLookup(
        repository: AccountFastLookupRepository
    ): GetAccountFastLookup {
        return GetAccountFastLookup(repository::fetchAccountFastLookup)
    }

    @Provides
    fun provideGetAccountInformation(
        repository: AccountInformationRepository
    ): GetAccountInformation {
        return GetAccountInformation(repository::getAccountInformation)
    }

    @Provides
    fun provideIsThereAnyCachedErrorAccount(
        useCase: IsThereAnyCachedErrorAccountUseCase
    ): IsThereAnyCachedErrorAccount = useCase

    @Provides
    fun provideIsThereAnyCachedSuccessAccount(
        useCase: IsThereAnyCachedSuccessAccountUseCase
    ): IsThereAnyCachedSuccessAccount = useCase

    @Provides
    fun provideIsAssetOwnedByAccount(useCase: IsAssetOwnedByAccountUseCase): IsAssetOwnedByAccount = useCase

    @Provides
    fun provideIsAssetOptedInByAnyLocalAccount(
        repository: AccountInformationRepository
    ): IsAssetOptedInByAnyLocalAccount {
        return IsAssetOptedInByAnyLocalAccount(repository::isAssetOptedInByAnyLocalAccount)
    }

    @Provides
    fun provideDeleteAccountInformation(
        repository: AccountInformationRepository
    ): DeleteAccountInformation {
        return DeleteAccountInformation(repository::deleteAccountInformation)
    }

    @Provides
    fun provideGetAccountInformationFlow(repository: AccountInformationRepository): GetAccountInformationFlow {
        return GetAccountInformationFlow(repository::getAccountInformationFlow)
    }

    @Provides
    fun provideFetchAccountInformation(repository: AccountInformationRepository): FetchAccountInformation {
        return FetchAccountInformation(repository::fetchAccountInformation)
    }

    @Provides
    fun provideFetchRekeyedAccounts(repository: AccountInformationRepository): FetchRekeyedAccounts {
        return FetchRekeyedAccounts(repository::fetchRekeyedAccounts)
    }

    @Provides
    fun provideAssetStatusEntityMapper(impl: AssetStatusEntityMapperImpl): AssetStatusEntityMapper = impl

    @Provides
    fun provideSetAccountAssetStatus(repository: AccountInformationRepository): SetAccountAssetStatus {
        return SetAccountAssetStatus(repository::setAssetStatus)
    }

    @Provides
    fun provideAddAssetHoldingToAccountAsPending(
        repository: AccountInformationRepository
    ): AddAssetHoldingToAccountAsPending {
        return AddAssetHoldingToAccountAsPending(repository::addAssetHoldingAsPending)
    }

    @Provides
    fun provideIsAccountCachedSuccessfully(
        useCase: IsAccountCachedSuccessfullyUseCase
    ): IsAccountCachedSuccessfully = useCase

    @Provides
    fun provideGetAllFailedCachedAccountAddresses(
        repository: AccountInformationRepository
    ): GetAllFailedCachedAccountAddresses {
        return GetAllFailedCachedAccountAddresses(repository::getFailedAccountInformation)
    }

    @Provides
    fun provideGetAccountRekeyAdminAddress(
        repository: AccountInformationRepository
    ): GetAccountRekeyAdminAddress = GetAccountRekeyAdminAddress(repository::getRekeyAuthAddress)

    @Provides
    fun provideGetAccountAssetHoldingsFlow(
        repository: AccountInformationRepository
    ): GetAccountAssetHoldingsFlow {
        return GetAccountAssetHoldingsFlow(repository::getAssetHoldingsFlow)
    }

    @Provides
    fun getAccountAlgoBalance(repository: AccountInformationRepository): GetAccountAlgoBalance {
        return GetAccountAlgoBalance(repository::getAccountAlgoBalance)
    }

    @Provides
    fun provideGetRegisteredHdKeys(useCase: GetRegisteredHdKeysUseCase): GetRegisteredHdKeys = useCase
}
