/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.inbox.asset.di

import com.algorand.wallet.inbox.asset.data.repository.AssetInboxRepositoryImpl
import com.algorand.wallet.inbox.asset.domain.repository.AssetInboxRepository
import com.algorand.wallet.inbox.asset.domain.usecase.GetAssetInboxRequest
import com.algorand.wallet.inbox.asset.domain.usecase.GetAssetInboxRequestCountFlow
import com.algorand.wallet.inbox.domain.repository.InboxRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AssetInboxModule {

    @Provides
    @Singleton
    fun provideAssetInboxRepository(inboxRepository: InboxRepository): AssetInboxRepository {
        return AssetInboxRepositoryImpl(inboxRepository)
    }

    @Provides
    fun provideGetAssetInboxRequestCountFlow(repository: AssetInboxRepository): GetAssetInboxRequestCountFlow {
        return GetAssetInboxRequestCountFlow(repository::getRequestCountFlow)
    }

    @Provides
    fun provideGetAssetInboxRequest(repository: AssetInboxRepository): GetAssetInboxRequest {
        return GetAssetInboxRequest(repository::getRequest)
    }
}
