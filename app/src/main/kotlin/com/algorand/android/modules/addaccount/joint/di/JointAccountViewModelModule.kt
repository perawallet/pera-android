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

import com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel.NameJointAccountInboxCleanup
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.DefaultJointAccountTransactionProcessor
import com.algorand.android.modules.addaccount.joint.transaction.viewmodel.JointAccountTransactionProcessor
import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.wallet.inbox.domain.usecase.DeleteInboxJointInvitationNotification
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal object JointAccountViewModelModule {

    @Provides
    fun provideJointAccountTransactionProcessor(
        processor: DefaultJointAccountTransactionProcessor
    ): JointAccountTransactionProcessor = processor

    @Provides
    fun provideNameJointAccountInboxCleanup(
        getDeviceConfig: GetDeviceConfig,
        deleteInboxJointInvitationNotification: DeleteInboxJointInvitationNotification
    ): NameJointAccountInboxCleanup = NameJointAccountInboxCleanup(
        getDeviceConfig = getDeviceConfig,
        deleteInboxJointInvitationNotification = deleteInboxJointInvitationNotification
    )
}
