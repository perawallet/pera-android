@file:Suppress("TooManyFunctions")
package com.algorand.android.module.account.info.di

import com.algorand.android.module.account.info.data.helper.fetch.AccountAssetHoldingsFetchHelper
import com.algorand.android.module.account.info.data.helper.fetch.AccountAssetHoldingsFetchHelperImpl
import com.algorand.android.module.account.info.data.helper.fetch.AccountInformationFetchHelper
import com.algorand.android.module.account.info.data.helper.fetch.AccountInformationFetchHelperImpl
import com.algorand.android.module.account.info.data.mapper.AccountInformationResponseMapper
import com.algorand.android.module.account.info.data.mapper.AccountInformationResponseMapperImpl
import com.algorand.android.module.account.info.data.mapper.entity.AccountInformationEntityMapper
import com.algorand.android.module.account.info.data.mapper.entity.AccountInformationEntityMapperImpl
import com.algorand.android.module.account.info.data.mapper.entity.AccountInformationErrorEntityMapper
import com.algorand.android.module.account.info.data.mapper.entity.AccountInformationErrorEntityMapperImpl
import com.algorand.android.module.account.info.data.mapper.entity.AssetHoldingEntityMapper
import com.algorand.android.module.account.info.data.mapper.entity.AssetHoldingEntityMapperImpl
import com.algorand.android.module.account.info.data.mapper.model.AccountInformationMapper
import com.algorand.android.module.account.info.data.mapper.model.AccountInformationMapperImpl
import com.algorand.android.module.account.info.data.mapper.model.AppStateSchemeMapper
import com.algorand.android.module.account.info.data.mapper.model.AppStateSchemeMapperImpl
import com.algorand.android.module.account.info.data.mapper.model.AssetHoldingMapper
import com.algorand.android.module.account.info.data.mapper.model.AssetHoldingMapperImpl
import com.algorand.android.module.account.info.data.repository.AccountInformationCacheHelper
import com.algorand.android.module.account.info.data.repository.AccountInformationCacheHelperImpl
import com.algorand.android.module.account.info.data.repository.AccountInformationRepositoryImpl
import com.algorand.android.module.account.info.data.repository.AssetHoldingCacheHelper
import com.algorand.android.module.account.info.data.repository.AssetHoldingCacheHelperImpl
import com.algorand.android.module.account.info.data.service.IndexerApi
import com.algorand.android.module.account.info.domain.mapper.AccountAssetsMapper
import com.algorand.android.module.account.info.domain.mapper.AccountAssetsMapperImpl
import com.algorand.android.module.account.info.domain.repository.AccountInformationRepository
import com.algorand.android.module.account.info.domain.usecase.AddAssetAdditionToAccountAssetHoldings
import com.algorand.android.module.account.info.domain.usecase.AddAssetRemovalToAccountAssetHoldings
import com.algorand.android.module.account.info.domain.usecase.ClearAccountInformationCache
import com.algorand.android.module.account.info.domain.usecase.DeleteAccountInformation
import com.algorand.android.module.account.info.domain.usecase.FetchAccountInformation
import com.algorand.android.module.account.info.domain.usecase.FetchAndCacheAccountInformation
import com.algorand.android.module.account.info.domain.usecase.FetchRekeyedAccounts
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.android.module.account.info.domain.usecase.GetAllAccountInformation
import com.algorand.android.module.account.info.domain.usecase.GetAllAccountInformationFlow
import com.algorand.android.module.account.info.domain.usecase.GetAllAssetHoldingIds
import com.algorand.android.module.account.info.domain.usecase.GetCachedAccountInformationCountFlow
import com.algorand.android.module.account.info.domain.usecase.GetEarliestLastFetchedRound
import com.algorand.android.module.account.info.domain.usecase.InitializeAccountInformation
import com.algorand.android.module.account.info.domain.usecase.IsAssetOwnedByAccount
import com.algorand.android.module.account.info.domain.usecase.IsAssetOwnedByAnyAccount
import com.algorand.android.module.account.info.domain.usecase.IsThereAnyCachedErrorAccount
import com.algorand.android.module.account.info.domain.usecase.IsThereAnyCachedSuccessAccount
import com.algorand.android.module.account.info.domain.usecase.implementation.GetAllAssetHoldingIdsUseCase
import com.algorand.android.module.account.info.domain.usecase.implementation.InitializeAccountInformationUseCase
import com.algorand.android.module.account.info.domain.usecase.implementation.IsAssetOwnedByAccountUseCase
import com.algorand.android.module.account.info.domain.usecase.implementation.IsAssetOwnedByAnyAccountUseCase
import com.algorand.android.module.account.info.domain.usecase.implementation.IsThereAnyCachedErrorAccountUseCase
import com.algorand.android.module.account.info.domain.usecase.implementation.IsThereAnyCachedSuccessAccountUseCase
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.encryption.di.DETERMINISTIC_ENCRYPTION_MANAGER
import com.algorand.android.module.shareddb.accountinformation.dao.AccountInformationDao
import com.algorand.android.module.shareddb.accountinformation.dao.AssetHoldingDao
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
    @Named("accountIndexerApi")
    fun provideIndexerApi(
        @Named("indexerRetrofitInterface") retrofit: Retrofit
    ): IndexerApi {
        return retrofit.create(IndexerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAccountInformationRepository(
        @Named("accountIndexerApi") indexerApi: IndexerApi,
        accountInformationMapper: AccountInformationMapper,
        accountInformationDao: AccountInformationDao,
        assetHoldingDao: AssetHoldingDao,
        assetHoldingMapper: AssetHoldingMapper,
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager,
        accountInformationResponseMapper: AccountInformationResponseMapper,
        accountInformationCacheHelper: AccountInformationCacheHelper,
        accountInformationFetchHelper: AccountInformationFetchHelper
    ): AccountInformationRepository {
        return AccountInformationRepositoryImpl(
            indexerApi,
            accountInformationMapper,
            accountInformationDao,
            assetHoldingDao,
            assetHoldingMapper,
            encryptionManager,
            accountInformationResponseMapper,
            accountInformationCacheHelper,
            accountInformationFetchHelper
        )
    }

    @Provides
    @Singleton
    fun provideAccountInformationEntityMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): AccountInformationEntityMapper {
        return AccountInformationEntityMapperImpl(
            encryptionManager
        )
    }

    @Provides
    @Singleton
    fun provideAssetHoldingEntityMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): AssetHoldingEntityMapper {
        return AssetHoldingEntityMapperImpl(encryptionManager)
    }

    @Provides
    @Singleton
    fun provideAccountInformationMapper(
        appStateSchemeMapper: AppStateSchemeMapper,
        assetHoldingMapper: AssetHoldingMapper,
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): AccountInformationMapper {
        return AccountInformationMapperImpl(appStateSchemeMapper, assetHoldingMapper, encryptionManager)
    }

    @Provides
    @Singleton
    fun provideAssetHoldingMapper(mapper: AssetHoldingMapperImpl): AssetHoldingMapper = mapper

    @Provides
    @Singleton
    fun provideAppStateSchemaMapper(mapper: AppStateSchemeMapperImpl): AppStateSchemeMapper = mapper

    @Provides
    @Singleton
    fun provideDeleteAccountInformation(
        accountInformationRepository: AccountInformationRepository
    ): DeleteAccountInformation {
        return DeleteAccountInformation(accountInformationRepository::deleteAccountInformation)
    }

    @Provides
    @Singleton
    fun provideAccountInformationErrorEntityMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) deterministicEncryptionManager: EncryptionManager
    ): AccountInformationErrorEntityMapper {
        return AccountInformationErrorEntityMapperImpl(deterministicEncryptionManager)
    }

    @Provides
    @Singleton
    fun provideAccountAssetsMapper(mapper: AccountAssetsMapperImpl): AccountAssetsMapper = mapper

    @Provides
    @Singleton
    fun provideInitializeAccountInformation(
        useCase: InitializeAccountInformationUseCase
    ): InitializeAccountInformation = useCase

    @Provides
    @Singleton
    fun provideIsThereAnyCachedErrorAccount(
        useCase: IsThereAnyCachedErrorAccountUseCase
    ): IsThereAnyCachedErrorAccount = useCase

    @Provides
    @Singleton
    fun provideIsThereAnyCachedSuccessAccount(
        useCase: IsThereAnyCachedSuccessAccountUseCase
    ): IsThereAnyCachedSuccessAccount = useCase

    @Provides
    fun provideIsAssetOwnedByAccount(
        useCase: IsAssetOwnedByAccountUseCase
    ): IsAssetOwnedByAccount = useCase

    @Provides
    fun provideGetAccountInformation(
        accountInformationRepository: AccountInformationRepository
    ): GetAccountInformation = GetAccountInformation(accountInformationRepository::getAccountInformation)

    @Provides
    fun provideFetchAndCacheAccountInformation(
        repository: AccountInformationRepository
    ): FetchAndCacheAccountInformation = FetchAndCacheAccountInformation(repository::fetchAndCacheAccountInformation)

    @Provides
    fun provideGetAllAccountInformation(
        repository: AccountInformationRepository
    ): GetAllAccountInformation = GetAllAccountInformation(repository::getAllAccountInformation)

    @Provides
    fun provideGetEarliestLastFetchedRound(
        repository: AccountInformationRepository
    ): GetEarliestLastFetchedRound = GetEarliestLastFetchedRound(repository::getEarliestLastFetchedRound)

    @Provides
    @Singleton
    fun provideGetAccountInformationFlow(
        repository: AccountInformationRepository
    ): GetAccountInformationFlow = GetAccountInformationFlow(repository::getAccountInformationFlow)

    @Provides
    @Singleton
    fun provideFetchAccountInformation(
        repository: AccountInformationRepository
    ): FetchAccountInformation = FetchAccountInformation(repository::fetchAccountInformation)

    @Provides
    @Singleton
    fun provideAccountInformationResponseMapper(
        mapper: AccountInformationResponseMapperImpl
    ): AccountInformationResponseMapper = mapper

    @Provides
    @Singleton
    fun provideFetchRekeyedAccounts(
        repository: AccountInformationRepository
    ): FetchRekeyedAccounts = FetchRekeyedAccounts(repository::fetchRekeyedAccounts)

    @Provides
    @Singleton
    fun provideIsAssetOwnedByAnyAccount(
        useCase: IsAssetOwnedByAnyAccountUseCase
    ): IsAssetOwnedByAnyAccount = useCase

    @Provides
    @Singleton
    fun provideGetCachedAccountInformationCountFlow(
        repository: AccountInformationRepository
    ): GetCachedAccountInformationCountFlow {
        return GetCachedAccountInformationCountFlow(repository::getCachedAccountInformationCountFlow)
    }

    @Provides
    @Singleton
    fun provideGetAllAssetHoldingIds(useCase: GetAllAssetHoldingIdsUseCase): GetAllAssetHoldingIds = useCase

    @Provides
    @Singleton
    fun provideClearAccountInformationCache(
        repository: AccountInformationRepository
    ): ClearAccountInformationCache = ClearAccountInformationCache(repository::clearCache)

    @Provides
    @Singleton
    fun provideGetAllAccountInformationFlow(
        repository: AccountInformationRepository
    ): GetAllAccountInformationFlow = GetAllAccountInformationFlow(repository::getAllAccountInformationFlow)

    @Provides
    @Singleton
    fun provideAddAssetAdditionToAccountAssetHoldings(
        repository: AccountInformationRepository
    ): AddAssetAdditionToAccountAssetHoldings {
        return AddAssetAdditionToAccountAssetHoldings(repository::addAssetAdditionToAccountAssetHoldings)
    }

    @Provides
    @Singleton
    fun provideAddAssetRemovalToAccountAssetHoldings(
        repository: AccountInformationRepository
    ): AddAssetRemovalToAccountAssetHoldings {
        return AddAssetRemovalToAccountAssetHoldings(repository::addAssetRemovalToAccountAssetHoldings)
    }

    @Provides
    @Singleton
    fun provideAccountInformationCacheHelper(
        impl: AccountInformationCacheHelperImpl
    ): AccountInformationCacheHelper = impl

    @Provides
    @Singleton
    fun provideAssetHoldingCacheHelper(impl: AssetHoldingCacheHelperImpl): AssetHoldingCacheHelper = impl

    @Provides
    @Singleton
    fun provideAccountInformationFetchHelper(
        @Named("accountIndexerApi") indexerApi: IndexerApi,
        accountAssetHoldingsFetchHelper: AccountAssetHoldingsFetchHelper,
        accountInformationResponseMapper: AccountInformationResponseMapper,
    ): AccountInformationFetchHelper {
        return AccountInformationFetchHelperImpl(
            indexerApi,
            accountAssetHoldingsFetchHelper,
            accountInformationResponseMapper
        )
    }

    @Provides
    @Singleton
    fun provideAccountAssetHoldingsFetchHelper(
        @Named("accountIndexerApi") indexerApi: IndexerApi,
    ): AccountAssetHoldingsFetchHelper = AccountAssetHoldingsFetchHelperImpl(indexerApi)
}
