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
import com.algorand.common.account.local.data.database.dao.LedgerBleDao
import com.algorand.common.account.local.data.database.dao.NoAuthDao
import com.algorand.common.account.local.data.mapper.entity.Algo25EntityMapper
import com.algorand.common.account.local.data.mapper.entity.Algo25EntityMapperImpl
import com.algorand.common.account.local.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.common.account.local.data.mapper.entity.LedgerBleEntityMapperImpl
import com.algorand.common.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.common.account.local.data.mapper.entity.NoAuthEntityMapperImpl
import com.algorand.common.account.local.data.mapper.model.algo25.Algo25Mapper
import com.algorand.common.account.local.data.mapper.model.algo25.Algo25MapperImpl
import com.algorand.common.account.local.data.mapper.model.ledgerble.LedgerBleMapper
import com.algorand.common.account.local.data.mapper.model.ledgerble.LedgerBleMapperImpl
import com.algorand.common.account.local.data.mapper.model.noauth.NoAuthMapper
import com.algorand.common.account.local.data.mapper.model.noauth.NoAuthMapperImpl
import com.algorand.common.account.local.data.repository.Algo25AccountRepositoryImpl
import com.algorand.common.account.local.data.repository.LedgerBleAccountRepositoryImpl
import com.algorand.common.account.local.data.repository.NoAuthAccountRepositoryImpl
import com.algorand.common.account.local.domain.repository.Algo25AccountRepository
import com.algorand.common.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.common.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.common.account.local.domain.usecase.AddAlgo25Account
import com.algorand.common.account.local.domain.usecase.AddLedgerBleAccount
import com.algorand.common.account.local.domain.usecase.AddNoAuthAccount
import com.algorand.common.account.local.domain.usecase.DeleteLocalAccount
import com.algorand.common.account.local.domain.usecase.DeleteLocalAccountUseCase
import com.algorand.common.account.local.domain.usecase.GetAllLocalAccountAddressesAsFlow
import com.algorand.common.account.local.domain.usecase.GetAllLocalAccountAddressesAsFlowUseCase
import org.koin.dsl.module

val localAccountsKoinModule = module {

    single<Algo25AccountRepository> {
        Algo25AccountRepositoryImpl(get(), get(), get(), get())
    }
    single<LedgerBleAccountRepository> {
        LedgerBleAccountRepositoryImpl(get(), get(), get(), get())
    }
    single<NoAuthAccountRepository> {
        NoAuthAccountRepositoryImpl(get(), get(), get(), get())
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

    factory<Algo25EntityMapper> { Algo25EntityMapperImpl(get(), get()) }
    factory<LedgerBleEntityMapper> { LedgerBleEntityMapperImpl(get()) }
    factory<NoAuthEntityMapper> { NoAuthEntityMapperImpl(get()) }
    factory<Algo25Mapper> { Algo25MapperImpl(get(), get()) }
    factory<LedgerBleMapper> { LedgerBleMapperImpl(get()) }
    factory<NoAuthMapper> { NoAuthMapperImpl(get()) }

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

    factory<GetAllLocalAccountAddressesAsFlow> { GetAllLocalAccountAddressesAsFlowUseCase(get(), get(), get()) }

    factory<DeleteLocalAccount> { DeleteLocalAccountUseCase(get(), get(), get()) }
}
