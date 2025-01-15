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

package com.algorand.common.account.local.di

import com.algorand.common.account.local.data.database.AddressDatabase
import com.algorand.common.account.local.data.database.dao.Algo25Dao
import com.algorand.common.account.local.data.database.dao.HdKeyDao
import com.algorand.common.account.local.data.database.dao.HdSeedDao
import com.algorand.common.account.local.data.database.dao.LedgerBleDao
import com.algorand.common.account.local.data.database.dao.NoAuthDao
import com.algorand.common.account.local.data.mapper.entity.Algo25EntityMapper
import com.algorand.common.account.local.data.mapper.entity.Algo25EntityMapperImpl
import com.algorand.common.account.local.data.mapper.entity.HdKeyEntityMapper
import com.algorand.common.account.local.data.mapper.entity.HdKeyEntityMapperImpl
import com.algorand.common.account.local.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.common.account.local.data.mapper.entity.LedgerBleEntityMapperImpl
import com.algorand.common.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.common.account.local.data.mapper.entity.NoAuthEntityMapperImpl
import com.algorand.common.account.local.data.mapper.model.Algo25Mapper
import com.algorand.common.account.local.data.mapper.model.Algo25MapperImpl
import com.algorand.common.account.local.data.mapper.model.HdKeyMapper
import com.algorand.common.account.local.data.mapper.model.HdKeyMapperImpl
import com.algorand.common.account.local.data.mapper.model.LedgerBleMapper
import com.algorand.common.account.local.data.mapper.model.LedgerBleMapperImpl
import com.algorand.common.account.local.data.mapper.model.NoAuthMapper
import com.algorand.common.account.local.data.mapper.model.NoAuthMapperImpl
import com.algorand.common.account.local.data.repository.Algo25AccountRepositoryImpl
import com.algorand.common.account.local.data.repository.HdKeyAccountRepositoryImpl
import com.algorand.common.account.local.data.repository.LedgerBleAccountRepositoryImpl
import com.algorand.common.account.local.data.repository.NoAuthAccountRepositoryImpl
import com.algorand.common.account.local.domain.repository.Algo25AccountRepository
import com.algorand.common.account.local.domain.repository.HdKeyAccountRepository
import com.algorand.common.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.common.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.common.account.local.domain.usecase.AddAlgo25Account
import com.algorand.common.account.local.domain.usecase.AddHdKeyAccount
import com.algorand.common.account.local.domain.usecase.AddLedgerBleAccount
import com.algorand.common.account.local.domain.usecase.AddNoAuthAccount
import com.algorand.common.account.local.domain.usecase.DeleteLocalAccount
import com.algorand.common.account.local.domain.usecase.DeleteLocalAccountUseCase
import com.algorand.common.account.local.domain.usecase.GetAllLocalAccountAddressesAsFlow
import com.algorand.common.account.local.domain.usecase.GetAllLocalAccountAddressesAsFlowUseCase
import com.algorand.common.account.local.domain.usecase.GetLocalAccountCountFlow
import com.algorand.common.account.local.domain.usecase.GetLocalAccountCountFlowUseCase
import com.algorand.common.account.local.domain.usecase.GetLocalAccounts
import com.algorand.common.account.local.domain.usecase.GetLocalAccountsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

internal val localAccountsKoinModule = module {
    single<HdKeyAccountRepository> {
        HdKeyAccountRepositoryImpl(get(), get(), get(), Dispatchers.IO)
    }
    single<Algo25AccountRepository> {
        Algo25AccountRepositoryImpl(get(), get(), get(), Dispatchers.IO)
    }
    single<LedgerBleAccountRepository> {
        LedgerBleAccountRepositoryImpl(get(), get(), get(), Dispatchers.IO)
    }
    single<NoAuthAccountRepository> {
        NoAuthAccountRepositoryImpl(get(), get(), get(), Dispatchers.IO)
    }

    single<HdSeedDao> {
        get<AddressDatabase>().hdSeedDao()
    }
    single<HdKeyDao> {
        get<AddressDatabase>().hdKeyDao()
    }
    single<Algo25Dao> {
        get<AddressDatabase>().algo25Dao()
    }
    single<LedgerBleDao> {
        get<AddressDatabase>().ledgerBleDao()
    }
    single<NoAuthDao> {
        get<AddressDatabase>().noAuthDao()
    }

    factory<HdKeyEntityMapper> { HdKeyEntityMapperImpl() }
    factory<Algo25EntityMapper> { Algo25EntityMapperImpl() }
    factory<LedgerBleEntityMapper> { LedgerBleEntityMapperImpl() }
    factory<NoAuthEntityMapper> { NoAuthEntityMapperImpl() }

    factory<HdKeyMapper> { HdKeyMapperImpl() }
    factory<Algo25Mapper> { Algo25MapperImpl() }
    factory<LedgerBleMapper> { LedgerBleMapperImpl() }
    factory<NoAuthMapper> { NoAuthMapperImpl() }

    factory<AddHdKeyAccount> {
        AddHdKeyAccount { account ->
            get<HdKeyAccountRepository>().addAccount(account)
        }
    }

    factory<AddAlgo25Account> {
        AddAlgo25Account { account ->
            get<Algo25AccountRepository>().addAccount(account)
        }
    }

    factory<AddLedgerBleAccount> {
        AddLedgerBleAccount { account ->
            get<LedgerBleAccountRepository>().addAccount(account)
        }
    }

    factory<AddNoAuthAccount> {
        AddNoAuthAccount { account ->
            get<NoAuthAccountRepository>().addAccount(account)
        }
    }

    factory<GetAllLocalAccountAddressesAsFlow> { GetAllLocalAccountAddressesAsFlowUseCase(get(), get(), get(), get()) }

    factory<DeleteLocalAccount> { DeleteLocalAccountUseCase(get(), get(), get(), get()) }

    factory<GetLocalAccounts> {
        GetLocalAccountsUseCase(get(), get(), get(), get(), Dispatchers.IO)
    }
    factory<GetLocalAccountCountFlow> {
        GetLocalAccountCountFlowUseCase(get(), get(), get(), get())
    }
}
