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

package com.algorand.wallet.account.local.di

import android.content.Context
import androidx.room.Room
import com.algorand.wallet.account.local.data.database.AddressDatabase
import com.algorand.wallet.account.local.data.mapper.entity.Algo25EntityMapper
import com.algorand.wallet.account.local.data.mapper.entity.Algo25EntityMapperImpl
import com.algorand.wallet.account.local.data.mapper.entity.HdKeyEntityMapper
import com.algorand.wallet.account.local.data.mapper.entity.HdKeyEntityMapperImpl
import com.algorand.wallet.account.local.data.mapper.entity.LedgerBleEntityMapper
import com.algorand.wallet.account.local.data.mapper.entity.LedgerBleEntityMapperImpl
import com.algorand.wallet.account.local.data.mapper.entity.NoAuthEntityMapper
import com.algorand.wallet.account.local.data.mapper.entity.NoAuthEntityMapperImpl
import com.algorand.wallet.account.local.data.mapper.model.Algo25Mapper
import com.algorand.wallet.account.local.data.mapper.model.Algo25MapperImpl
import com.algorand.wallet.account.local.data.mapper.model.HdKeyMapper
import com.algorand.wallet.account.local.data.mapper.model.HdKeyMapperImpl
import com.algorand.wallet.account.local.data.mapper.model.LedgerBleMapper
import com.algorand.wallet.account.local.data.mapper.model.LedgerBleMapperImpl
import com.algorand.wallet.account.local.data.mapper.model.NoAuthMapper
import com.algorand.wallet.account.local.data.mapper.model.NoAuthMapperImpl
import com.algorand.wallet.account.local.data.repository.Algo25AccountRepositoryImpl
import com.algorand.wallet.account.local.data.repository.HdKeyAccountRepositoryImpl
import com.algorand.wallet.account.local.data.repository.LedgerBleAccountRepositoryImpl
import com.algorand.wallet.account.local.data.repository.NoAuthAccountRepositoryImpl
import com.algorand.wallet.account.local.domain.repository.Algo25AccountRepository
import com.algorand.wallet.account.local.domain.repository.HdKeyAccountRepository
import com.algorand.wallet.account.local.domain.repository.LedgerBleAccountRepository
import com.algorand.wallet.account.local.domain.repository.NoAuthAccountRepository
import com.algorand.wallet.account.local.domain.usecase.DeleteLocalAccount
import com.algorand.wallet.account.local.domain.usecase.DeleteLocalAccountUseCase
import com.algorand.wallet.account.local.domain.usecase.GetAllLocalAccountAddressesAsFlow
import com.algorand.wallet.account.local.domain.usecase.GetAllLocalAccountAddressesAsFlowUseCase
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountCountFlow
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountCountFlowUseCase
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountUseCase
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsUseCase
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyAccountWithAddressUseCase
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyNoAuthAccountWithAddress
import com.algorand.wallet.account.local.domain.usecase.SaveAlgo25Account
import com.algorand.wallet.account.local.domain.usecase.SaveHdKeyAccount
import com.algorand.wallet.account.local.domain.usecase.SaveLedgerBleAccount
import com.algorand.wallet.account.local.domain.usecase.SaveNoAuthAccount
import com.algorand.wallet.account.local.domain.usecase.UpdateNoAuthAccountToAlgo25
import com.algorand.wallet.account.local.domain.usecase.UpdateNoAuthAccountToAlgo25UseCase
import com.algorand.wallet.account.local.domain.usecase.UpdateNoAuthAccountToLedgerBle
import com.algorand.wallet.account.local.domain.usecase.UpdateNoAuthAccountToLedgerBleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LocalAccountsModule {

    @Provides
    @Singleton
    fun provideAddressDatabase(@ApplicationContext context: Context): AddressDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AddressDatabase::class.java,
            name = AddressDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideAlgo25Dao(addressDatabase: AddressDatabase) = addressDatabase.algo25Dao()

    @Provides
    @Singleton
    fun provideHdKeyDao(addressDatabase: AddressDatabase) = addressDatabase.hdKeyDao()

    @Provides
    @Singleton
    fun provideHdSeedDao(addressDatabase: AddressDatabase) = addressDatabase.hdSeedDao()

    @Provides
    @Singleton
    fun provideLedgerBleDao(addressDatabase: AddressDatabase) = addressDatabase.ledgerBleDao()

    @Provides
    @Singleton
    fun provideNoAuthDao(addressDatabase: AddressDatabase) = addressDatabase.noAuthDao()

    @Provides
    fun provideHdKeyAccountRepository(repository: HdKeyAccountRepositoryImpl): HdKeyAccountRepository = repository

    @Provides
    fun provideAlgo25AccountRepository(repository: Algo25AccountRepositoryImpl): Algo25AccountRepository = repository

    @Provides
    fun provideLedgerBleAccountRepository(
        repository: LedgerBleAccountRepositoryImpl
    ): LedgerBleAccountRepository = repository

    @Provides
    fun provideNoAuthAccountRepository(repository: NoAuthAccountRepositoryImpl): NoAuthAccountRepository = repository

    @Provides
    fun provideHdKeyEntityMapper(impl: HdKeyEntityMapperImpl): HdKeyEntityMapper = impl

    @Provides
    fun provideAlgo25EntityMapper(impl: Algo25EntityMapperImpl): Algo25EntityMapper = impl

    @Provides
    fun provideLedgerBleEntityMapper(impl: LedgerBleEntityMapperImpl): LedgerBleEntityMapper = impl

    @Provides
    fun provideNoAuthEntityMapper(impl: NoAuthEntityMapperImpl): NoAuthEntityMapper = impl

    @Provides
    fun provideHdKeyMapper(impl: HdKeyMapperImpl): HdKeyMapper = impl

    @Provides
    fun provideAlgo25Mapper(impl: Algo25MapperImpl): Algo25Mapper = impl

    @Provides
    fun provideLedgerBleMapper(impl: LedgerBleMapperImpl): LedgerBleMapper = impl

    @Provides
    fun provideNoAuthMapper(impl: NoAuthMapperImpl): NoAuthMapper = impl

    @Provides
    fun provideSaveHdKeyAccount(repository: HdKeyAccountRepository): SaveHdKeyAccount {
        return SaveHdKeyAccount(repository::addAccount)
    }

    @Provides
    fun provideSaveAlgo25Account(repository: Algo25AccountRepository): SaveAlgo25Account {
        return SaveAlgo25Account(repository::addAccount)
    }

    @Provides
    fun provideSaveLedgerBleAccount(repository: LedgerBleAccountRepository): SaveLedgerBleAccount {
        return SaveLedgerBleAccount(repository::addAccount)
    }

    @Provides
    fun provideSaveNoAuthAccount(repository: NoAuthAccountRepository): SaveNoAuthAccount {
        return SaveNoAuthAccount(repository::addAccount)
    }

    @Provides
    fun provideGetAllLocalAccountAddressesAsFlow(
        useCase: GetAllLocalAccountAddressesAsFlowUseCase
    ): GetAllLocalAccountAddressesAsFlow = useCase

    @Provides
    fun provideDeleteLocalAccount(
        useCase: DeleteLocalAccountUseCase
    ): DeleteLocalAccount = useCase

    @Provides
    fun provideGetLocalAccounts(
        useCase: GetLocalAccountsUseCase
    ): GetLocalAccounts = useCase

    @Provides
    fun provideGetLocalAccount(useCase: GetLocalAccountUseCase): GetLocalAccount = useCase

    @Provides
    fun provideGetLocalAccountCountFlow(
        useCase: GetLocalAccountCountFlowUseCase
    ): GetLocalAccountCountFlow = useCase

    @Provides
    fun provideUpdateNoAuthAccountToAlgo25(
        useCase: UpdateNoAuthAccountToAlgo25UseCase
    ): UpdateNoAuthAccountToAlgo25 = useCase

    @Provides
    fun provideUpdateNoAuthAccountToLedgerBle(
        useCase: UpdateNoAuthAccountToLedgerBleUseCase
    ): UpdateNoAuthAccountToLedgerBle = useCase

    @Provides
    fun provideIsThereAnyAccountWithAddress(
        useCase: IsThereAnyAccountWithAddressUseCase
    ): IsThereAnyAccountWithAddress = useCase

    @Provides
    fun provideIsThereAnyNoAuthAccountWithAddress(
        repository: NoAuthAccountRepository
    ): IsThereAnyNoAuthAccountWithAddress {
        return IsThereAnyNoAuthAccountWithAddress(repository::isAddressExists)
    }
}
