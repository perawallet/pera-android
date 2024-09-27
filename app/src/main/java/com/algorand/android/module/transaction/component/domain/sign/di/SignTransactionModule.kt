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

package com.algorand.android.module.transaction.component.domain.sign.di

import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.module.account.info.domain.usecase.FetchAccountInformation
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.transaction.component.domain.usecase.IsValidAlgorandAddress
import com.algorand.android.module.transaction.component.domain.usecase.ValidateReceiverAccount
import com.algorand.android.module.transaction.component.domain.usecase.ValidateTransactionAmount
import com.algorand.android.module.transaction.component.domain.usecase.implementation.ValidateReceiverAccountUseCase
import com.algorand.android.module.transaction.component.domain.usecase.implementation.ValidateTransactionAmountUseCase
import com.algorand.android.module.transaction.component.domain.validator.AccountAddressValidator
import com.algorand.android.module.transaction.component.domain.validator.CloseTransactionToSameAccountValidator
import com.algorand.android.module.transaction.component.domain.validator.NewAccountBalanceValidator
import com.algorand.android.module.transaction.component.domain.validator.ReceiverOptedInToAssetValidator
import com.algorand.android.module.transaction.component.domain.validator.SendAssetMinRequiredBalanceValidator
import com.algorand.android.module.transaction.component.domain.validator.SenderOptedInToAssetValidator
import com.algorand.android.module.transaction.component.domain.validator.SendingLessAmountThanRequiredValidator
import com.algorand.android.module.transaction.component.domain.validator.SendingMaxAmountToSameAccountValidator
import com.algorand.android.module.transaction.component.domain.validator.SufficientBalanceToPayFeeValidator
import com.algorand.android.module.transaction.component.domain.validator.SufficientBalanceValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SignTransactionModule {

    @Provides
    @Singleton
    fun provideValidateTransactionAmount(
        getAccountInformation: GetAccountInformation,
        getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
        getAccountMinBalance: GetAccountMinBalance
    ): ValidateTransactionAmount {
        return ValidateTransactionAmountUseCase(
            getAccountInformation,
            getAccountBaseOwnedAssetData,
            SufficientBalanceValidator(),
            SufficientBalanceToPayFeeValidator(),
            SendAssetMinRequiredBalanceValidator(),
            getAccountMinBalance
        )
    }

    @Provides
    @Singleton
    fun provideValidateReceiverAccount(
        getAccountInformation: GetAccountInformation,
        fetchAccountInformation: FetchAccountInformation,
        getAccountMinBalance: GetAccountMinBalance,
        isValidAlgorandAddress: IsValidAlgorandAddress
    ): ValidateReceiverAccount {
        return ValidateReceiverAccountUseCase(
            ReceiverOptedInToAssetValidator(),
            SendingLessAmountThanRequiredValidator(getAccountMinBalance),
            SendingMaxAmountToSameAccountValidator(getAccountMinBalance),
            CloseTransactionToSameAccountValidator(),
            NewAccountBalanceValidator(),
            SenderOptedInToAssetValidator(),
            AccountAddressValidator(isValidAlgorandAddress),
            getAccountInformation,
            fetchAccountInformation
        )
    }
}
