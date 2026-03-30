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
import com.algorand.android.modules.addaccount.joint.creation.usecase.CreateExternalAddressAsContact
import com.algorand.android.modules.addaccount.joint.creation.usecase.CreateExternalAddressAsContactUseCase
import com.algorand.android.modules.addaccount.joint.creation.usecase.GetNextJointAccountNumber
import com.algorand.android.modules.addaccount.joint.creation.usecase.GetNextJointAccountNumberUseCase
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.DeclineJointAccountSignRequest
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.DeclineJointAccountSignRequestUseCase
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.FormatAlgoAsDisplayCurrency
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.FormatAlgoAsDisplayCurrencyUseCase
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.GetJointAccountSignerItems
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.GetJointAccountSignerItemsUseCase
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.GetJointAccountTransactionViewState
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.GetJointAccountTransactionViewStateDependencies
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.GetJointAccountTransactionViewStateUseCase
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.SignAndSubmitJointAccountSignature
import com.algorand.android.modules.addaccount.joint.transaction.domain.usecase.SignAndSubmitJointAccountSignatureUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object JointAccountUseCaseModule {

    @Provides
    fun provideSignAndSubmitJointAccountSignature(
        useCase: SignAndSubmitJointAccountSignatureUseCase
    ): SignAndSubmitJointAccountSignature = useCase

    @Provides
    fun provideGetNextJointAccountNumber(
        useCase: GetNextJointAccountNumberUseCase
    ): GetNextJointAccountNumber = useCase

    @Provides
    fun provideCreateExternalAddressAsContact(
        useCase: CreateExternalAddressAsContactUseCase
    ): CreateExternalAddressAsContact = useCase

    @Provides
    fun provideDeclineJointAccountSignRequest(
        useCase: DeclineJointAccountSignRequestUseCase
    ): DeclineJointAccountSignRequest = useCase

    @Provides
    fun provideGetJointAccountTransactionViewState(
        dependencies: GetJointAccountTransactionViewStateDependencies,
        @ApplicationContext context: Context
    ): GetJointAccountTransactionViewState = GetJointAccountTransactionViewStateUseCase(
        dependencies = dependencies,
        resources = context.resources
    )

    @Provides
    fun provideFormatAlgoAsDisplayCurrency(
        useCase: FormatAlgoAsDisplayCurrencyUseCase
    ): FormatAlgoAsDisplayCurrency = useCase

    @Provides
    fun provideGetJointAccountSignerItems(
        useCase: GetJointAccountSignerItemsUseCase
    ): GetJointAccountSignerItems = useCase
}
