/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.pushtoken.di

import com.algorand.android.caching.SingleInMemoryLocalCache
import com.algorand.android.pushtoken.FirebaseMessagingTokenProvider
import com.algorand.android.pushtoken.MessagingTokenProvider
import com.algorand.android.pushtoken.data.repository.PushTokenRepositoryImpl
import com.algorand.android.pushtoken.domain.repository.PushTokenRepository
import com.algorand.android.pushtoken.domain.usecase.GetPushTokenCacheFlow
import com.algorand.android.pushtoken.domain.usecase.SetPushToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object PushTokenModule {

    @Provides
    @Singleton
    fun providePushTokenRepository(): PushTokenRepository {
        return PushTokenRepositoryImpl(SingleInMemoryLocalCache())
    }

    @Provides
    @Singleton
    fun provideGetPushTokenCacheFlow(repository: PushTokenRepository): GetPushTokenCacheFlow {
        return GetPushTokenCacheFlow(repository::getPushTokenCacheFlow)
    }

    @Provides
    @Singleton
    fun provideSetPushToken(repository: PushTokenRepository): SetPushToken = SetPushToken(repository::setPushToken)

    @Provides
    @Singleton
    fun provideMessagingTokenProvider(
        firebaseMessagingTokenProvider: FirebaseMessagingTokenProvider
    ): MessagingTokenProvider = firebaseMessagingTokenProvider
}
