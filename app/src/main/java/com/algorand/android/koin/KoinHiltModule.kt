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
import com.algorand.common.account.custom.domain.usecase.SetAccountCustomName
import com.algorand.common.account.info.domain.usecase.IsThereAnyCachedErrorAccount
import com.algorand.common.account.info.domain.usecase.IsThereAnyCachedSuccessAccount
import com.algorand.common.account.local.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.common.account.local.domain.usecase.IsThereAnyNoAuthAccountWithAddress
import com.algorand.common.account.local.domain.usecase.UpdateNoAuthAccountToAlgo25
import com.algorand.common.account.local.domain.usecase.UpdateNoAuthAccountToLedgerBle
import com.algorand.common.cache.domain.usecase.InitializeAppCache
import com.algorand.common.deeplink.parser.CreateDeepLink
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
}
