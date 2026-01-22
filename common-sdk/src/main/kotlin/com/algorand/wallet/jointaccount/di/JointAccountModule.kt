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

import com.algorand.wallet.inbox.jointaccount.data.service.InboxApiService
import com.algorand.wallet.jointaccount.creation.data.mapper.CreateJointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.mapper.CreateJointAccountDTOMapperImpl
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapper
import com.algorand.wallet.jointaccount.creation.data.mapper.JointAccountDTOMapperImpl
import com.algorand.wallet.jointaccount.data.repository.JointAccountRepositoryImpl
import com.algorand.wallet.jointaccount.data.service.JointAccountApiService
import com.algorand.wallet.jointaccount.domain.repository.JointAccountRepository
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountParticipantCount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountParticipantCountUseCase
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountProposerAddress
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountProposerAddressUseCase
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccountUseCase
import com.algorand.wallet.jointaccount.transaction.domain.model.CreateSignRequestInput
import com.algorand.wallet.jointaccount.transaction.domain.usecase.AddJointAccountSignature
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSignRequestWithSignatures
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
    @Singleton
    fun provideInboxApiService(
        @Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit
    ): InboxApiService {
        return retrofit.create(InboxApiService::class.java)
    }

    @Provides
    @Singleton
    @Named(JointAccountRepository.INJECTION_NAME)
    fun provideJointAccountRepository(
        repository: JointAccountRepositoryImpl
    ): JointAccountRepository = repository

    @Provides
    fun provideProposeJointSignRequest(
        @Named(JointAccountRepository.INJECTION_NAME) repository: JointAccountRepository
    ): ProposeJointSignRequest = ProposeJointSignRequest { jointAccountAddress, proposerAddress, type, rawTransactionLists, transactionSignatureLists ->
        repository.proposeSignRequest(
            CreateSignRequestInput(
                jointAccountAddress = jointAccountAddress,
                proposerAddress = proposerAddress,
                type = type,
                rawTransactionLists = rawTransactionLists,
                transactionSignatureLists = transactionSignatureLists
            )
        )
    }

    @Provides
    fun provideGetSignRequestWithSignatures(
        @Named(JointAccountRepository.INJECTION_NAME) repository: JointAccountRepository
    ): GetSignRequestWithSignatures = GetSignRequestWithSignatures { deviceId, signRequestId ->
        repository.getSignRequestWithSignatures(deviceId, signRequestId)
    }

    @Provides
    fun provideAddJointAccountSignature(
        @Named(JointAccountRepository.INJECTION_NAME) repository: JointAccountRepository
    ): AddJointAccountSignature = AddJointAccountSignature { signRequestId, addSignatureInput ->
        repository.addSignature(signRequestId, addSignatureInput)
    }

    @Provides
    fun provideGetJointAccountProposerAddress(
        useCase: GetJointAccountProposerAddressUseCase
    ): GetJointAccountProposerAddress = useCase

    @Provides
    fun provideCreateJointAccountDTOMapper(
        impl: CreateJointAccountDTOMapperImpl
    ): CreateJointAccountDTOMapper = impl

    @Provides
    fun provideJointAccountDTOMapper(
        impl: JointAccountDTOMapperImpl
    ): JointAccountDTOMapper = impl

    @Provides
    fun provideGetJointAccount(
        useCase: GetJointAccountUseCase
    ): GetJointAccount = useCase

    @Provides
    fun provideGetJointAccountParticipantCount(
        useCase: GetJointAccountParticipantCountUseCase
    ): GetJointAccountParticipantCount = useCase
}
