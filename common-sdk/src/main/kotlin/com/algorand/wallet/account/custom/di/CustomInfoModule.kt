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

import com.algorand.wallet.account.custom.data.database.dao.CustomAccountInfoDao
import com.algorand.wallet.account.custom.data.database.dao.CustomHdSeedInfoDao
import com.algorand.wallet.account.custom.data.mapper.entity.CustomAccountInfoEntityMapper
import com.algorand.wallet.account.custom.data.mapper.entity.CustomAccountInfoEntityMapperImpl
import com.algorand.wallet.account.custom.data.mapper.entity.CustomHdSeedInfoEntityMapper
import com.algorand.wallet.account.custom.data.mapper.entity.CustomHdSeedInfoEntityMapperImpl
import com.algorand.wallet.account.custom.data.mapper.model.CustomAccountInfoMapper
import com.algorand.wallet.account.custom.data.mapper.model.CustomAccountInfoMapperImpl
import com.algorand.wallet.account.custom.data.mapper.model.CustomHdSeedInfoMapper
import com.algorand.wallet.account.custom.data.mapper.model.CustomHdSeedInfoMapperImpl
import com.algorand.wallet.account.custom.data.repository.CustomAccountInfoRepositoryImpl
import com.algorand.wallet.account.custom.data.repository.CustomHdSeedInfoRepositoryImpl
import com.algorand.wallet.account.custom.domain.repository.CustomAccountInfoRepository
import com.algorand.wallet.account.custom.domain.repository.CustomHdSeedInfoRepository
import com.algorand.wallet.account.custom.domain.usecase.DeleteAccountCustomInfo
import com.algorand.wallet.account.custom.domain.usecase.DeleteHdSeedCustomInfo
import com.algorand.wallet.account.custom.domain.usecase.GetAccountAsbBackUpStatus
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomInfo
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomInfoOrNull
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomName
import com.algorand.wallet.account.custom.domain.usecase.GetAllAccountOrderIndexes
import com.algorand.wallet.account.custom.domain.usecase.GetAllHdSeedOrderIndexes
import com.algorand.wallet.account.custom.domain.usecase.GetBackedUpAccounts
import com.algorand.wallet.account.custom.domain.usecase.GetHdSeedAsbBackUpStatus
import com.algorand.wallet.account.custom.domain.usecase.GetHdSeedCustomInfoOrNull
import com.algorand.wallet.account.custom.domain.usecase.GetHdSeedCustomName
import com.algorand.wallet.account.custom.domain.usecase.GetNotBackedUpAccounts
import com.algorand.wallet.account.custom.domain.usecase.SetAccountCustomInfo
import com.algorand.wallet.account.custom.domain.usecase.SetAccountCustomName
import com.algorand.wallet.account.custom.domain.usecase.SetAccountOrderIndex
import com.algorand.wallet.account.custom.domain.usecase.SetHdSeedCustomInfo
import com.algorand.wallet.account.custom.domain.usecase.SetHdSeedCustomName
import com.algorand.wallet.account.custom.domain.usecase.SetHdSeedOrderIndex
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
    fun provideCustomAccountInfoDao(database: PeraDatabase): CustomAccountInfoDao = database.customAccountInfoDao()

    @Provides
    fun provideCustomAccountEntityMapper(impl: CustomAccountInfoEntityMapperImpl): CustomAccountInfoEntityMapper = impl

    @Provides
    fun provideCustomAccountInfoMapper(impl: CustomAccountInfoMapperImpl): CustomAccountInfoMapper = impl

    @Provides
    fun provideSetAccountCustomInfo(repository: CustomAccountInfoRepository): SetAccountCustomInfo {
        return SetAccountCustomInfo(repository::setCustomInfo)
    }

    @Provides
    @Singleton
    fun provideCustomAccountInfoRepository(repository: CustomAccountInfoRepositoryImpl): CustomAccountInfoRepository = repository

    @Provides
    fun provideSetAccountCustomName(repository: CustomAccountInfoRepository): SetAccountCustomName {
        return SetAccountCustomName(repository::setCustomName)
    }

    @Provides
    fun provideGetAccountCustomName(repository: CustomAccountInfoRepository): GetAccountCustomName {
        return GetAccountCustomName(repository::getCustomName)
    }

    @Provides
    fun provideGetAccountCustomInfoOrNull(repository: CustomAccountInfoRepository): GetAccountCustomInfoOrNull {
        return GetAccountCustomInfoOrNull(repository::getCustomInfoOrNull)
    }

    @Provides
    fun provideGetAccountCustomInfo(repository: CustomAccountInfoRepository): GetAccountCustomInfo {
        return GetAccountCustomInfo(repository::getCustomInfo)
    }

    @Provides
    fun provideSetAccountOrderIndex(repository: CustomAccountInfoRepository): SetAccountOrderIndex {
        return SetAccountOrderIndex(repository::setOrderIndex)
    }

    @Provides
    fun provideDeleteAccountCustomInfo(repository: CustomAccountInfoRepository): DeleteAccountCustomInfo {
        return DeleteAccountCustomInfo(repository::deleteCustomInfo)
    }

    @Provides
    fun provideGetAllAccountOrderIndexes(repository: CustomAccountInfoRepository): GetAllAccountOrderIndexes {
        return GetAllAccountOrderIndexes(repository::getAllAccountOrderIndexes)
    }

    @Provides
    fun provideGetNotBackedUpAccounts(repository: CustomAccountInfoRepository): GetNotBackedUpAccounts {
        return GetNotBackedUpAccounts(repository::getNotBackedUpAccounts)
    }

    @Provides
    fun provideGetAccountAsbBackUpStatus(repository: CustomAccountInfoRepository): GetAccountAsbBackUpStatus {
        return GetAccountAsbBackUpStatus(repository::isAccountBackedUp)
    }

    @Provides
    fun provideGetBackedUpAccounts(repository: CustomAccountInfoRepository): GetBackedUpAccounts {
        return GetBackedUpAccounts(repository::getBackedUpAccounts)
    }

    @Provides
    @Singleton
    fun provideCustomHdSeedInfoDao(database: PeraDatabase): CustomHdSeedInfoDao = database.customHdSeedInfoDao()

    @Provides
    fun provideCustomHdSeedEntityMapper(impl: CustomHdSeedInfoEntityMapperImpl): CustomHdSeedInfoEntityMapper = impl

    @Provides
    fun provideCustomHdSeedInfoMapper(impl: CustomHdSeedInfoMapperImpl): CustomHdSeedInfoMapper = impl

    @Provides
    fun provideSetHdSeedCustomInfo(repository: CustomHdSeedInfoRepository): SetHdSeedCustomInfo {
        return SetHdSeedCustomInfo(repository::setCustomInfo)
    }

    @Provides
    @Singleton
    fun provideCustomHdSeedInfoRepository(repository: CustomHdSeedInfoRepositoryImpl): CustomHdSeedInfoRepository = repository

    @Provides
    fun provideSetHdSeedCustomName(repository: CustomHdSeedInfoRepository): SetHdSeedCustomName {
        return SetHdSeedCustomName(repository::setCustomName)
    }

    @Provides
    fun provideGetHdSeedCustomName(repository: CustomHdSeedInfoRepository): GetHdSeedCustomName {
        return GetHdSeedCustomName(repository::getCustomName)
    }

    @Provides
    fun provideGetHdSeedCustomInfoOrNull(repository: CustomHdSeedInfoRepository): GetHdSeedCustomInfoOrNull {
        return GetHdSeedCustomInfoOrNull(repository::getCustomInfoOrNull)
    }

    @Provides
    fun provideSetHdSeedOrderIndex(repository: CustomHdSeedInfoRepository): SetHdSeedOrderIndex {
        return SetHdSeedOrderIndex(repository::setOrderIndex)
    }

    @Provides
    fun provideDeleteHdSeedCustomInfo(repository: CustomHdSeedInfoRepository): DeleteHdSeedCustomInfo {
        return DeleteHdSeedCustomInfo(repository::deleteCustomInfo)
    }

    @Provides
    fun provideGetAllHdSeedOrderIndexes(repository: CustomHdSeedInfoRepository): GetAllHdSeedOrderIndexes {
        return GetAllHdSeedOrderIndexes(repository::getAllHdSeedOrderIndexes)
    }

    @Provides
    fun provideGetHdSeedAsbBackUpStatus(repository: CustomHdSeedInfoRepository): GetHdSeedAsbBackUpStatus {
        return GetHdSeedAsbBackUpStatus(repository::isHdSeedBackedUp)
    }
}
