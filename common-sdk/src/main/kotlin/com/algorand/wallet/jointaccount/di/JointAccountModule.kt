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

package com.algorand.wallet.jointaccount.di

import com.algorand.wallet.account.local.domain.repository.JointAccountPersistence
import com.algorand.wallet.inbox.domain.usecase.DeleteInboxJointInvitationNotification
import com.algorand.wallet.inbox.domain.usecase.DeleteInboxJointInvitationNotificationUseCase
import com.algorand.wallet.inbox.domain.usecase.FetchInboxMessages
import com.algorand.wallet.inbox.domain.usecase.FetchInboxMessagesUseCase
import com.algorand.wallet.jointaccount.creation.data.mapper.CreateJointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.mapper.CreateJointAccountDTOMapperImpl
import com.algorand.wallet.jointaccount.creation.data.mapper.IsJointAccountMapper
import com.algorand.wallet.jointaccount.creation.data.mapper.IsJointAccountMapperImpl
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapperImpl
import com.algorand.wallet.jointaccount.creation.domain.usecase.CreateJointAccount
import com.algorand.wallet.jointaccount.creation.domain.usecase.CreateJointAccountUseCase
import com.algorand.wallet.jointaccount.data.repository.JointAccountRepositoryImpl
import com.algorand.wallet.jointaccount.data.service.JointAccountApiService
import com.algorand.wallet.jointaccount.domain.repository.JointAccountRepository
import com.algorand.wallet.jointaccount.domain.usecase.CheckIsJointAccount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountDetail
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountParticipantCount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountProposerAddress
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountProposerAddressUseCase
import com.algorand.wallet.jointaccount.transaction.domain.MultisigTransactionAssembler
import com.algorand.wallet.jointaccount.transaction.domain.usecase.AddJointAccountSignature
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSignRequestWithSignatures
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSyncSignRequestWithSignatures
import com.algorand.wallet.jointaccount.transaction.domain.usecase.MarkSignRequestsConfirmed
import com.algorand.wallet.jointaccount.transaction.domain.usecase.ProposeJointSignRequest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object JointAccountModule {

    @Provides
    @Singleton
    fun provideJointAccountApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): JointAccountApiService {
        return retrofit.create(JointAccountApiService::class.java)
    }

    @Provides
    fun provideJointAccountRepository(
        repository: JointAccountRepositoryImpl
    ): JointAccountRepository = repository

    @Provides
    fun provideProposeJointSignRequest(
        repository: JointAccountRepository
    ): ProposeJointSignRequest = ProposeJointSignRequest(repository::proposeSignRequest)

    @Provides
    fun provideGetSignRequestWithSignatures(
        repository: JointAccountRepository
    ): GetSignRequestWithSignatures = GetSignRequestWithSignatures(repository::getSignRequestWithSignatures)

    @Provides
    fun provideGetSyncSignRequestWithSignatures(
        repository: JointAccountRepository
    ): GetSyncSignRequestWithSignatures = GetSyncSignRequestWithSignatures(repository::getSignRequestWithFullSignatures)

    @Provides
    fun provideMultisigTransactionAssembler(): MultisigTransactionAssembler = MultisigTransactionAssembler()

    @Provides
    fun provideAddJointAccountSignature(
        repository: JointAccountRepository
    ): AddJointAccountSignature = AddJointAccountSignature(repository::addSignatures)

    @Provides
    fun provideMarkSignRequestsConfirmed(
        repository: JointAccountRepository
    ): MarkSignRequestsConfirmed = MarkSignRequestsConfirmed(repository::markSignRequestsConfirmed)

    @Provides
    fun provideGetJointAccountProposerAddress(
        useCase: GetJointAccountProposerAddressUseCase
    ): GetJointAccountProposerAddress = useCase

    @Provides
    fun provideCreateJointAccountDTOMapper(
        impl: CreateJointAccountDTOMapperImpl
    ): CreateJointAccountDTOMapper = impl

    @Provides
    fun provideIsJointAccountMapper(
        impl: IsJointAccountMapperImpl
    ): IsJointAccountMapper = impl

    @Provides
    fun provideCheckIsJointAccount(
        repository: JointAccountRepository
    ): CheckIsJointAccount = CheckIsJointAccount(repository::checkIsJointAccount)

    @Provides
    fun provideJointAccountDTOMapper(
        impl: JointAccountDTOMapperImpl
    ): JointAccountDTOMapper = impl

    @Provides
    fun provideGetJointAccount(
        persistence: JointAccountPersistence
    ): GetJointAccount = GetJointAccount(persistence::getAccount)

    @Provides
    fun provideGetJointAccountDetail(
        repository: JointAccountRepository
    ): GetJointAccountDetail = GetJointAccountDetail(repository::getJointAccountDetail)

    @Provides
    fun provideGetJointAccountParticipantCount(
        persistence: JointAccountPersistence
    ): GetJointAccountParticipantCount = GetJointAccountParticipantCount(persistence::getParticipantCount)

    @Provides
    fun provideCreateJointAccount(
        useCase: CreateJointAccountUseCase
    ): CreateJointAccount = useCase

    @Provides
    fun provideFetchInboxMessages(
        useCase: FetchInboxMessagesUseCase
    ): FetchInboxMessages = useCase

    @Provides
    fun provideDeleteInboxJointInvitationNotification(
        useCase: DeleteInboxJointInvitationNotificationUseCase
    ): DeleteInboxJointInvitationNotification = useCase
}
