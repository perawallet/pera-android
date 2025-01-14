@file:Suppress("TooManyFunctions")
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

package com.algorand.android.koin

import com.algorand.common.account.core.domain.usecase.AddAlgo25Account
import com.algorand.common.account.core.domain.usecase.AddLedgerBleAccount
import com.algorand.common.account.core.domain.usecase.AddNoAuthAccount
import com.algorand.common.account.core.domain.usecase.DeleteAccount
import com.algorand.common.account.core.domain.usecase.GetAccountDetailFlow
import com.algorand.common.account.custom.domain.usecase.GetAccountAsbBackUpStatus
import com.algorand.common.account.custom.domain.usecase.GetAccountCustomInfoOrNull
import com.algorand.common.account.custom.domain.usecase.GetAllAccountOrderIndexes
import com.algorand.common.account.custom.domain.usecase.GetBackedUpAccounts
import com.algorand.common.account.custom.domain.usecase.GetNotBackedUpAccounts
import com.algorand.common.account.custom.domain.usecase.SetAccountCustomName
import com.algorand.common.account.detail.domain.usecase.GetAccountDetail
import com.algorand.common.account.detail.domain.usecase.GetAccountsDetails
import com.algorand.common.account.info.domain.usecase.GetAccountInformation
import com.algorand.common.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.common.account.info.domain.usecase.IsThereAnyCachedErrorAccount
import com.algorand.common.account.info.domain.usecase.IsThereAnyCachedSuccessAccount
import com.algorand.common.account.local.domain.usecase.GetLocalAccounts
import com.algorand.common.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.common.account.local.domain.usecase.IsThereAnyNoAuthAccountWithAddress
import com.algorand.common.account.local.domain.usecase.UpdateNoAuthAccountToAlgo25
import com.algorand.common.account.local.domain.usecase.UpdateNoAuthAccountToLedgerBle
import com.algorand.common.asset.domain.usecase.GetAsset
import com.algorand.common.asset.domain.usecase.GetAssetDetail
import com.algorand.common.asset.domain.usecase.GetCollectibleDetail
import com.algorand.common.cache.domain.usecase.GetAppCacheStatusFlow
import com.algorand.common.cache.domain.usecase.InitializeAppCache
import com.algorand.common.deeplink.parser.CreateDeepLink
import com.algorand.common.nameservice.domain.usecase.GetAccountNameService
import com.algorand.common.remoteconfig.domain.usecase.InitializeOperationalToggles
import com.algorand.common.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.java.KoinJavaComponent.getKoin

@Module
@InstallIn(SingletonComponent::class)
object KoinHiltModule {

    @Provides
    fun provideInitializeOperationalToggles(): InitializeOperationalToggles = getKoin().get()

    @Provides
    fun provideIsFeatureToggleEnabled(): IsFeatureToggleEnabled = getKoin().get()

    @Provides
    fun provideCreateDeepLink(): CreateDeepLink = getKoin().get()

    @Provides
    fun provideAddAlgo25Account(): AddAlgo25Account = getKoin().get()

    @Provides
    fun provideAddLedgerBleAccount(): AddLedgerBleAccount = getKoin().get()

    @Provides
    fun provideAddNoAuthAccount(): AddNoAuthAccount = getKoin().get()

    @Provides
    fun provideUpdateNoAuthAccountToAlgo25(): UpdateNoAuthAccountToAlgo25 = getKoin().get()

    @Provides
    fun provideUpdateNoAuthAccountToLedgerBle(): UpdateNoAuthAccountToLedgerBle = getKoin().get()

    @Provides
    fun provideSetAccountCustomName(): SetAccountCustomName = getKoin().get()

    @Provides
    fun provideIsThereAnyAccountWithAddress(): IsThereAnyAccountWithAddress = getKoin().get()

    @Provides
    fun provideIsThereAnyNoAuthAccountWithAddress(): IsThereAnyNoAuthAccountWithAddress = getKoin().get()

    @Provides
    fun provideInitializeAppCache(): InitializeAppCache = getKoin().get()

    @Provides
    fun provideIsThereAnyCachedErrorAccount(): IsThereAnyCachedErrorAccount = getKoin().get()

    @Provides
    fun provideIsThereAnyCachedSuccessAccount(): IsThereAnyCachedSuccessAccount = getKoin().get()

    @Provides
    fun provideGetAccountAsbBackUpStatus(): GetAccountAsbBackUpStatus = getKoin().get()

    @Provides
    fun provideGetNotBackedUpAccounts(): GetNotBackedUpAccounts = getKoin().get()

    @Provides
    fun provideGetBackedUpAccounts(): GetBackedUpAccounts = getKoin().get()

    @Provides
    fun provideGetAccountDetail(): GetAccountDetail = getKoin().get()

    @Provides
    fun provideGetAccountsDetails(): GetAccountsDetails = getKoin().get()

    @Provides
    fun provideGetAccountInformation(): GetAccountInformation = getKoin().get()

    @Provides
    fun provideGetAccountCustomInfoOrNull(): GetAccountCustomInfoOrNull = getKoin().get()

    @Provides
    fun provideGetAllAccountOrderIndexes(): GetAllAccountOrderIndexes = getKoin().get()

    @Provides
    fun provideGetAppCacheStatusFlow(): GetAppCacheStatusFlow = getKoin().get()

    @Provides
    fun provideGetAsset(): GetAsset = getKoin().get()

    @Provides
    fun provideGetLocalAccounts(): GetLocalAccounts = getKoin().get()

    @Provides
    fun provideGetAccountNameService(): GetAccountNameService = getKoin().get()

    @Provides
    fun provideDeleteAccount(): DeleteAccount = getKoin().get()

    @Provides
    fun provideGetAccountDetailFlow(): GetAccountDetailFlow = getKoin().get()

    @Provides
    fun provideGetAssetDetail(): GetAssetDetail = getKoin().get()

    @Provides
    fun provideGetAccountInformationFlow(): GetAccountInformationFlow = getKoin().get()

    @Provides
    fun provideGetCollectibleDetail(): GetCollectibleDetail = getKoin().get()
}
