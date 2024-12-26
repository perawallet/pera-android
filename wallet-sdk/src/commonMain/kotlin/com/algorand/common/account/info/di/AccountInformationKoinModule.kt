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

package com.algorand.common.account.info.di

import com.algorand.common.account.info.data.database.dao.AccountInformationDao
import com.algorand.common.account.info.data.database.dao.AssetHoldingDao
import com.algorand.common.account.info.data.mapper.AccountInformationEntityMapper
import com.algorand.common.account.info.data.mapper.AccountInformationEntityMapperImpl
import com.algorand.common.account.info.data.mapper.AccountInformationErrorEntityMapper
import com.algorand.common.account.info.data.mapper.AccountInformationErrorEntityMapperImpl
import com.algorand.common.account.info.data.mapper.AccountInformationMapper
import com.algorand.common.account.info.data.mapper.AccountInformationMapperImpl
import com.algorand.common.account.info.data.mapper.AccountInformationResponseMapper
import com.algorand.common.account.info.data.mapper.AccountInformationResponseMapperImpl
import com.algorand.common.account.info.data.mapper.AppStateSchemeMapper
import com.algorand.common.account.info.data.mapper.AppStateSchemeMapperImpl
import com.algorand.common.account.info.data.mapper.AssetHoldingEntityMapper
import com.algorand.common.account.info.data.mapper.AssetHoldingEntityMapperImpl
import com.algorand.common.account.info.data.mapper.AssetHoldingMapper
import com.algorand.common.account.info.data.mapper.AssetHoldingMapperImpl
import com.algorand.common.account.info.data.repository.AccountAssetHoldingsFetchHelper
import com.algorand.common.account.info.data.repository.AccountAssetHoldingsFetchHelperImpl
import com.algorand.common.account.info.data.repository.AccountInformationCacheHelper
import com.algorand.common.account.info.data.repository.AccountInformationCacheHelperImpl
import com.algorand.common.account.info.data.repository.AccountInformationFetchHelper
import com.algorand.common.account.info.data.repository.AccountInformationFetchHelperImpl
import com.algorand.common.account.info.data.repository.AccountInformationRepositoryImpl
import com.algorand.common.account.info.data.repository.AssetHoldingCacheHelper
import com.algorand.common.account.info.data.repository.AssetHoldingCacheHelperImpl
import com.algorand.common.account.info.data.service.AccountInformationApiService
import com.algorand.common.account.info.data.service.AccountInformationApiServiceImpl
import com.algorand.common.account.info.domain.manager.AccountCacheManager
import com.algorand.common.account.info.domain.manager.AccountCacheManagerImpl
import com.algorand.common.account.info.domain.repository.AccountInformationRepository
import com.algorand.common.account.info.domain.usecase.ClearAccountInformationCache
import com.algorand.common.account.info.domain.usecase.FetchAndCacheAccountInformation
import com.algorand.common.account.info.domain.usecase.GetAccountDetailCacheStatusFlow
import com.algorand.common.account.info.domain.usecase.GetAccountDetailCacheStatusFlowUseCase
import com.algorand.common.account.info.domain.usecase.GetAccountInformation
import com.algorand.common.account.info.domain.usecase.GetAllAccountInformation
import com.algorand.common.account.info.domain.usecase.GetAllAccountInformationFlow
import com.algorand.common.account.info.domain.usecase.GetAllAssetHoldingIds
import com.algorand.common.account.info.domain.usecase.GetCachedAccountInformationCountFlow
import com.algorand.common.account.info.domain.usecase.GetEarliestLastFetchedRound
import com.algorand.common.foundation.database.PeraDatabase
import com.algorand.common.foundation.network.indexer.getIndexerApiHttpClient
import org.koin.dsl.module

internal val accountInformationKoinModule = module {
    single<AccountCacheManager> { AccountCacheManagerImpl(get(), get(), get(), get(), get(), get()) }

    single<AccountInformationApiService> {
        AccountInformationApiServiceImpl(getIndexerApiHttpClient(get()))
    }

    single<AccountInformationRepository> {
        AccountInformationRepositoryImpl(get(), get(), get(), get(), get(), get(), get())
    }

    single<AccountInformationCacheHelper> { AccountInformationCacheHelperImpl(get(), get(), get(), get(), get()) }
    single<AssetHoldingCacheHelper> { AssetHoldingCacheHelperImpl(get(), get(), get()) }
    single<AccountInformationFetchHelper> { AccountInformationFetchHelperImpl(get(), get(), get()) }
    single<AccountAssetHoldingsFetchHelper> { AccountAssetHoldingsFetchHelperImpl(get()) }

    factory<AccountInformationMapper> { AccountInformationMapperImpl(get(), get(), get()) }
    factory<AppStateSchemeMapper> { AppStateSchemeMapperImpl() }
    factory<AssetHoldingMapper> { AssetHoldingMapperImpl() }
    factory<AccountInformationDao> {
        get<PeraDatabase>().accountInformationDao()
    }
    factory<AssetHoldingDao> {
        get<PeraDatabase>().assetHoldingDao()
    }
    factory<AccountInformationResponseMapper> { AccountInformationResponseMapperImpl() }
    factory<AccountInformationEntityMapper> { AccountInformationEntityMapperImpl(get()) }
    factory<AccountInformationErrorEntityMapper> { AccountInformationErrorEntityMapperImpl(get()) }
    factory<AssetHoldingEntityMapper> { AssetHoldingEntityMapperImpl(get()) }
    factory<GetAccountDetailCacheStatusFlow> { GetAccountDetailCacheStatusFlowUseCase(get(), get()) }
    factory<FetchAndCacheAccountInformation> {
        FetchAndCacheAccountInformation { addresses ->
            get<AccountInformationRepository>().fetchAndCacheAccountInformation(addresses)
        }
    }
    factory<GetEarliestLastFetchedRound> {
        GetEarliestLastFetchedRound {
            get<AccountInformationRepository>().getEarliestLastFetchedRound()
        }
    }
    factory<GetAllAccountInformation> {
        GetAllAccountInformation {
            get<AccountInformationRepository>().getAllAccountInformation()
        }
    }
    factory<GetAllAssetHoldingIds> {
        GetAllAssetHoldingIds { accountAddresses ->
            get<AccountInformationRepository>().getAllAssetHoldingIds(accountAddresses)
        }
    }
    factory<GetCachedAccountInformationCountFlow> {
        GetCachedAccountInformationCountFlow {
            get<AccountInformationRepository>().getCachedAccountInformationCountFlow()
        }
    }
    factory<ClearAccountInformationCache> {
        ClearAccountInformationCache {
            get<AccountInformationRepository>().clearCache()
        }
    }
    factory<GetAllAccountInformationFlow> {
        GetAllAccountInformationFlow {
            get<AccountInformationRepository>().getAllAccountInformationFlow()
        }
    }
    factory<GetAccountInformation> {
        GetAccountInformation { address ->
            get<AccountInformationRepository>().getAccountInformation(address)
        }
    }
}
