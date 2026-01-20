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

import com.algorand.android.modules.addaccount.joint.core.data.repository.JointAccountRepositoryImpl
import com.algorand.android.modules.addaccount.joint.core.domain.repository.JointAccountRepository
import com.algorand.android.modules.addaccount.joint.creation.domain.usecase.CreateJointAccount
import com.algorand.android.modules.addaccount.joint.creation.domain.usecase.CreateJointAccountUseCase
import com.algorand.android.modules.addaccount.joint.creation.domain.usecase.DeleteInboxJointInvitationNotification
import com.algorand.android.modules.addaccount.joint.creation.domain.usecase.DeleteInboxJointInvitationNotificationUseCase
import com.algorand.android.modules.addaccount.joint.creation.usecase.CreateExternalAddressAsContact
import com.algorand.android.modules.addaccount.joint.creation.usecase.CreateExternalAddressAsContactUseCase
import com.algorand.android.modules.addaccount.joint.creation.usecase.GetDefaultJointAccountName
import com.algorand.android.modules.addaccount.joint.creation.usecase.GetDefaultJointAccountNameUseCase
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.CalculateConvertedAlgoAmount
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.CalculateConvertedAlgoAmountUseCase
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.CreateSignerAccounts
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.CreateSignerAccountsUseCase
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.DeclineJointAccountSignRequest
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.DeclineJointAccountSignRequestUseCase
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.GetJointAccountTransactionPreview
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.GetJointAccountTransactionPreviewUseCase
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.SignAndSubmitJointAccountSignature
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.SignAndSubmitJointAccountSignatureUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
internal object JointAccountUseCaseModule {

    @Provides
    @Named(JointAccountRepository.INJECTION_NAME)
    fun provideJointAccountRepository(
        repository: JointAccountRepositoryImpl
    ): JointAccountRepository = repository

    @Provides
    fun provideSignAndSubmitJointAccountSignature(
        useCase: SignAndSubmitJointAccountSignatureUseCase
    ): SignAndSubmitJointAccountSignature = useCase

    @Provides
    fun provideCreateJointAccount(
        useCase: CreateJointAccountUseCase
    ): CreateJointAccount = useCase

    @Provides
    fun provideDeleteInboxJointInvitationNotification(
        useCase: DeleteInboxJointInvitationNotificationUseCase
    ): DeleteInboxJointInvitationNotification = useCase

    @Provides
    fun provideGetDefaultJointAccountName(
        useCase: GetDefaultJointAccountNameUseCase
    ): GetDefaultJointAccountName = useCase

    @Provides
    fun provideCreateExternalAddressAsContact(
        useCase: CreateExternalAddressAsContactUseCase
    ): CreateExternalAddressAsContact = useCase

    @Provides
    fun provideCalculateConvertedAlgoAmount(
        useCase: CalculateConvertedAlgoAmountUseCase
    ): CalculateConvertedAlgoAmount = useCase

    @Provides
    fun provideCreateSignerAccounts(
        useCase: CreateSignerAccountsUseCase
    ): CreateSignerAccounts = useCase

    @Provides
    fun provideDeclineJointAccountSignRequest(
        useCase: DeclineJointAccountSignRequestUseCase
    ): DeclineJointAccountSignRequest = useCase

    @Provides
    fun provideGetJointAccountTransactionPreview(
        useCase: GetJointAccountTransactionPreviewUseCase
    ): GetJointAccountTransactionPreview = useCase
}
