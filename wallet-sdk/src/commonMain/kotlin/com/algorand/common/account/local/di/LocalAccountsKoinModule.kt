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

import com.algorand.common.account.local.data.database.AccountDatabase
import com.algorand.common.account.local.data.database.dao.Algo25Dao
import com.algorand.common.account.local.data.database.dao.Bip39Dao
import com.algorand.common.account.local.data.database.dao.LedgerBleDao
import com.algorand.common.account.local.data.database.dao.NoAuthDao
import com.algorand.common.account.local.data.mapper.entity.Algo25EntityMapper
import com.algorand.common.account.local.data.mapper.entity.Algo25EntityMapperImpl
import com.algorand.common.account.local.data.mapper.entity.Bip39EntityMapper
import com.algorand.common.account.local.data.mapper.entity.Bip39EntityMapperImpl
import com.algorand.common.account.local.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.common.account.local.data.mapper.entity.LedgerBleEntityMapperImpl
import com.algorand.common.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.common.account.local.data.mapper.entity.NoAuthEntityMapperImpl
import com.algorand.common.account.local.data.mapper.model.Algo25Mapper
import com.algorand.common.account.local.data.mapper.model.Algo25MapperImpl
import com.algorand.common.account.local.data.mapper.model.Bip39Mapper
import com.algorand.common.account.local.data.mapper.model.Bip39MapperImpl
import com.algorand.common.account.local.data.mapper.model.LedgerBleMapper
import com.algorand.common.account.local.data.mapper.model.LedgerBleMapperImpl
import com.algorand.common.account.local.data.mapper.model.NoAuthMapper
import com.algorand.common.account.local.data.mapper.model.NoAuthMapperImpl
import com.algorand.common.account.local.data.repository.Algo25AccountRepositoryImpl
import com.algorand.common.account.local.data.repository.Bip39AccountRepositoryImpl
import com.algorand.common.account.local.data.repository.LedgerBleAccountRepositoryImpl
import com.algorand.common.account.local.data.repository.NoAuthAccountRepositoryImpl
import com.algorand.common.account.local.domain.repository.Algo25AccountRepository
import com.algorand.common.account.local.domain.repository.Bip39AccountRepository
import com.algorand.common.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.common.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.common.account.local.domain.usecase.AddAlgo25Account
import com.algorand.common.account.local.domain.usecase.AddBip39Account
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
    single<Bip39AccountRepository> {
        Bip39AccountRepositoryImpl(get(), get(), get(), get())
    }
    single<Algo25AccountRepository> {
        Algo25AccountRepositoryImpl(get(), get(), get(), get())
    }
    single<LedgerBleAccountRepository> {
        LedgerBleAccountRepositoryImpl(get(), get(), get(), get())
    }
    single<NoAuthAccountRepository> {
        NoAuthAccountRepositoryImpl(get(), get(), get(), get())
    }

    single<Bip39Dao> {
        get<AccountDatabase>().bip39Dao()
    }
    single<Algo25Dao> {
        get<AccountDatabase>().algo25Dao()
    }
    single<LedgerBleDao> {
        get<AccountDatabase>().ledgerBleDao()
    }
    single<NoAuthDao> {
        get<AccountDatabase>().noAuthDao()
    }

    factory<Bip39EntityMapper> { Bip39EntityMapperImpl(get(), get()) }
    factory<Algo25EntityMapper> { Algo25EntityMapperImpl(get(), get()) }
    factory<LedgerBleEntityMapper> { LedgerBleEntityMapperImpl(get()) }
    factory<NoAuthEntityMapper> { NoAuthEntityMapperImpl(get()) }

    factory<Bip39Mapper> { Bip39MapperImpl(get(), get()) }
    factory<Algo25Mapper> { Algo25MapperImpl(get(), get()) }
    factory<LedgerBleMapper> { LedgerBleMapperImpl(get()) }
    factory<NoAuthMapper> { NoAuthMapperImpl(get()) }

    factory<AddBip39Account> {
        AddBip39Account { account ->
            get<Bip39AccountRepository>().addAccount(account)
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
