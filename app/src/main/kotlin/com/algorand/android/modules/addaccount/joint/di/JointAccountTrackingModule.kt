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

import com.algorand.android.modules.addaccount.joint.tracking.DefaultJointAccountCreationEventTracker
import com.algorand.android.modules.addaccount.joint.tracking.DefaultJointAccountDetailEventTracker
import com.algorand.android.modules.addaccount.joint.tracking.DefaultJointAccountInboxEventTracker
import com.algorand.android.modules.addaccount.joint.tracking.DefaultJointAccountOptionsEventTracker
import com.algorand.android.modules.addaccount.joint.tracking.DefaultJointAccountTransactionEventTracker
import com.algorand.android.modules.addaccount.joint.tracking.JointAccountCreationEventTracker
import com.algorand.android.modules.addaccount.joint.tracking.JointAccountDetailEventTracker
import com.algorand.android.modules.addaccount.joint.tracking.JointAccountInboxEventTracker
import com.algorand.android.modules.addaccount.joint.tracking.JointAccountOptionsEventTracker
import com.algorand.android.modules.addaccount.joint.tracking.JointAccountTransactionEventTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object JointAccountTrackingModule {

    @Provides
    fun provideJointAccountInboxEventTracker(
        tracker: DefaultJointAccountInboxEventTracker
    ): JointAccountInboxEventTracker = tracker

    @Provides
    fun provideJointAccountDetailEventTracker(
        tracker: DefaultJointAccountDetailEventTracker
    ): JointAccountDetailEventTracker = tracker

    @Provides
    fun provideJointAccountCreationEventTracker(
        tracker: DefaultJointAccountCreationEventTracker
    ): JointAccountCreationEventTracker = tracker

    @Provides
    fun provideJointAccountTransactionEventTracker(
        tracker: DefaultJointAccountTransactionEventTracker
    ): JointAccountTransactionEventTracker = tracker

    @Provides
    fun provideJointAccountOptionsEventTracker(
        tracker: DefaultJointAccountOptionsEventTracker
    ): JointAccountOptionsEventTracker = tracker
}
