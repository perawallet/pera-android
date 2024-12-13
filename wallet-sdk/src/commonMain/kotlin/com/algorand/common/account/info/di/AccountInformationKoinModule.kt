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
import com.algorand.common.account.info.data.service.IndexerApiService
import com.algorand.common.account.info.data.service.IndexerApiServiceImpl
import com.algorand.common.account.info.data.service.getIndexerApiHttpClient
import com.algorand.common.foundation.database.PeraDatabase
import org.koin.dsl.module

internal val accountInformationKoinModule = module {
    single<IndexerApiService> {
        IndexerApiServiceImpl(getIndexerApiHttpClient(get()))
    }

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
}
