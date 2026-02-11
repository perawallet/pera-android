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

package com.algorand.android.modules.inbox.allaccounts.di

import com.algorand.android.modules.inbox.allaccounts.ui.InboxViewModelDependencies
import com.algorand.android.modules.inbox.allaccounts.ui.mapper.InboxViewStateMapper
import com.algorand.wallet.inbox.domain.usecase.GetInboxLastOpenedTime
import com.algorand.wallet.inbox.domain.usecase.GetInboxMessagesFlow
import com.algorand.wallet.inbox.domain.usecase.GetInboxValidAddresses
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import com.algorand.wallet.inbox.domain.usecase.SetInboxLastOpenedTime
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object InboxUiModule {

    @Provides
    fun provideInboxViewModelDependencies(
        inboxViewStateMapper: InboxViewStateMapper,
        getInboxValidAddresses: GetInboxValidAddresses,
        getInboxMessagesFlow: GetInboxMessagesFlow,
        refreshInboxCache: RefreshInboxCache,
        setInboxLastOpenedTime: SetInboxLastOpenedTime,
        getInboxLastOpenedTime: GetInboxLastOpenedTime,
        isFeatureToggleEnabled: IsFeatureToggleEnabled
    ): InboxViewModelDependencies = InboxViewModelDependencies(
        inboxViewStateMapper = inboxViewStateMapper,
        getInboxValidAddresses = getInboxValidAddresses,
        getInboxMessagesFlow = getInboxMessagesFlow,
        refreshInboxCache = refreshInboxCache,
        setInboxLastOpenedTime = setInboxLastOpenedTime,
        getInboxLastOpenedTime = getInboxLastOpenedTime,
        isFeatureToggleEnabled = isFeatureToggleEnabled
    )
}
