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

package com.algorand.android.modules.addaccount.joint.di

import android.content.Context
import com.algorand.android.core.transaction.JointAccountTransactionSignHelper
import com.algorand.android.core.transaction.sync.JointAccountSyncSignDependencies
import com.algorand.android.core.transaction.sync.SignArbitraryDataForSyncRequest
import com.algorand.android.core.transaction.sync.SignArbitraryDataForSyncRequestUseCase
import com.algorand.android.core.transaction.sync.SyncSignPollingTriggerImpl
import com.algorand.android.core.transaction.sync.SyncSignRequestPollingManager
import com.algorand.android.core.transaction.sync.SyncSignResultHolder
import com.algorand.android.modules.firebase.token.usecase.SyncJointAccountsOnNetworkSwitch
import com.algorand.android.modules.firebase.token.usecase.SyncJointAccountsOnNetworkSwitchUseCase
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.inbox.domain.SyncSignPollingTrigger
import com.algorand.wallet.jointaccount.transaction.domain.MultisigTransactionAssembler
import com.algorand.wallet.jointaccount.transaction.domain.usecase.MarkSignRequestsConfirmed
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object JointAccountSyncSignModule {

    @Provides
    fun provideSignArbitraryDataForSyncRequest(
        useCase: SignArbitraryDataForSyncRequestUseCase
    ): SignArbitraryDataForSyncRequest = useCase

    @Provides
    fun provideJointAccountSyncSignDependencies(
        jointAccountTransactionSignHelper: JointAccountTransactionSignHelper,
        signArbitraryDataForSyncRequest: SignArbitraryDataForSyncRequest,
        syncSignRequestPollingManager: SyncSignRequestPollingManager,
        multisigTransactionAssembler: MultisigTransactionAssembler,
        getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
        @ApplicationContext applicationContext: Context,
        syncSignResultHolder: SyncSignResultHolder,
        markSignRequestsConfirmed: MarkSignRequestsConfirmed
    ): JointAccountSyncSignDependencies = JointAccountSyncSignDependencies(
        jointAccountTransactionSignHelper = jointAccountTransactionSignHelper,
        signArbitraryDataForSyncRequest = signArbitraryDataForSyncRequest,
        syncSignRequestPollingManager = syncSignRequestPollingManager,
        multisigTransactionAssembler = multisigTransactionAssembler,
        getSelectedNodeDeviceId = getSelectedNodeDeviceId,
        applicationContext = applicationContext,
        syncSignResultHolder = syncSignResultHolder,
        markSignRequestsConfirmed = markSignRequestsConfirmed
    )

    @Provides
    fun provideSyncSignPollingTrigger(
        impl: SyncSignPollingTriggerImpl
    ): SyncSignPollingTrigger = impl

    @Provides
    fun provideSyncJointAccountsOnNetworkSwitch(
        useCase: SyncJointAccountsOnNetworkSwitchUseCase
    ): SyncJointAccountsOnNetworkSwitch = useCase
}
