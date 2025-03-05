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

package com.algorand.wallet.account.custom.di

import com.algorand.wallet.account.custom.data.database.dao.CustomInfoDao
import com.algorand.wallet.account.custom.data.mapper.entity.CustomInfoEntityMapper
import com.algorand.wallet.account.custom.data.mapper.entity.CustomInfoEntityMapperImpl
import com.algorand.wallet.account.custom.data.mapper.model.CustomInfoMapper
import com.algorand.wallet.account.custom.data.mapper.model.CustomInfoMapperImpl
import com.algorand.wallet.account.custom.data.repository.CustomInfoRepositoryImpl
import com.algorand.wallet.account.custom.domain.repository.CustomInfoRepository
import com.algorand.wallet.account.custom.domain.usecase.DeleteAccountCustomInfo
import com.algorand.wallet.account.custom.domain.usecase.GetAccountAsbBackUpStatus
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomInfo
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomInfoOrNull
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomName
import com.algorand.wallet.account.custom.domain.usecase.GetAllAccountOrderIndexes
import com.algorand.wallet.account.custom.domain.usecase.GetBackedUpAccounts
import com.algorand.wallet.account.custom.domain.usecase.GetNotBackedUpAccounts
import com.algorand.wallet.account.custom.domain.usecase.SetAccountCustomInfo
import com.algorand.wallet.account.custom.domain.usecase.SetAccountCustomName
import com.algorand.wallet.account.custom.domain.usecase.SetAccountOrderIndex
import com.algorand.wallet.foundation.database.PeraDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CustomInfoModule {

    @Provides
    @Singleton
    fun provideCustomInfoDao(database: PeraDatabase): CustomInfoDao = database.customInfoDao()

    @Provides
    fun provideCustomEntityMapper(impl: CustomInfoEntityMapperImpl): CustomInfoEntityMapper = impl

    @Provides
    fun provideCustomInfoMapper(impl: CustomInfoMapperImpl): CustomInfoMapper = impl

    @Provides
    fun provideSetAccountCustomInfo(repository: CustomInfoRepository): SetAccountCustomInfo {
        return SetAccountCustomInfo(repository::setCustomInfo)
    }

    @Provides
    @Singleton
    fun provideCustomInfoRepository(repository: CustomInfoRepositoryImpl): CustomInfoRepository = repository

    @Provides
    fun provideSetAccountCustomName(repository: CustomInfoRepository): SetAccountCustomName {
        return SetAccountCustomName(repository::setCustomName)
    }

    @Provides
    fun provideGetAccountCustomName(repository: CustomInfoRepository): GetAccountCustomName {
        return GetAccountCustomName(repository::getCustomName)
    }

    @Provides
    fun provideGetAccountCustomInfoOrNull(repository: CustomInfoRepository): GetAccountCustomInfoOrNull {
        return GetAccountCustomInfoOrNull(repository::getCustomInfoOrNull)
    }

    @Provides
    fun provideGetAccountCustomInfo(repository: CustomInfoRepository): GetAccountCustomInfo {
        return GetAccountCustomInfo(repository::getCustomInfo)
    }

    @Provides
    fun provideSetAccountOrderIndex(repository: CustomInfoRepository): SetAccountOrderIndex {
        return SetAccountOrderIndex(repository::setOrderIndex)
    }

    @Provides
    fun provideDeleteAccountCustomInfo(repository: CustomInfoRepository): DeleteAccountCustomInfo {
        return DeleteAccountCustomInfo(repository::deleteCustomInfo)
    }

    @Provides
    fun provideGetAllAccountOrderIndexes(repository: CustomInfoRepository): GetAllAccountOrderIndexes {
        return GetAllAccountOrderIndexes(repository::getAllAccountOrderIndexes)
    }

    @Provides
    fun provideGetNotBackedUpAccounts(repository: CustomInfoRepository): GetNotBackedUpAccounts {
        return GetNotBackedUpAccounts(repository::getNotBackedUpAccounts)
    }

    @Provides
    fun provideGetAccountAsbBackUpStatus(repository: CustomInfoRepository): GetAccountAsbBackUpStatus {
        return GetAccountAsbBackUpStatus(repository::isAccountBackedUp)
    }

    @Provides
    fun provideGetBackedUpAccounts(repository: CustomInfoRepository): GetBackedUpAccounts {
        return GetBackedUpAccounts(repository::getBackedUpAccounts)
    }
}
