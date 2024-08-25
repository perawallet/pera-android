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

package com.algorand.android.transaction.domain.creation.di

import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.algosdk.component.transaction.AlgoSdkTransaction
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAsset
import com.algorand.android.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.core.component.domain.usecase.GetAccountMinBalance
import com.algorand.android.transaction.domain.creation.CreateAddAssetTransaction
import com.algorand.android.transaction.domain.creation.CreatePaymentTransaction
import com.algorand.android.transaction.domain.creation.CreateRekeyTransaction
import com.algorand.android.transaction.domain.creation.CreateRemoveAssetTransaction
import com.algorand.android.transaction.domain.creation.CreateSendAssetTransaction
import com.algorand.android.transaction.domain.creation.CreateSendTransaction
import com.algorand.android.transaction.domain.creation.addasset.mapper.AddAssetCreateTransactionResultMapper
import com.algorand.android.transaction.domain.creation.addasset.mapper.AddAssetCreateTransactionResultMapperImpl
import com.algorand.android.transaction.domain.creation.addasset.usecase.CreateAddAssetTransactionUseCase
import com.algorand.android.transaction.domain.creation.rekey.mapper.CreateRekeyTransactionResultMapper
import com.algorand.android.transaction.domain.creation.rekey.mapper.CreateRekeyTransactionResultMapperImpl
import com.algorand.android.transaction.domain.creation.rekey.usecase.CreateRekeyTransactionUseCase
import com.algorand.android.transaction.domain.creation.removeasset.mapper.CreateRemoveAssetTransactionResultMapper
import com.algorand.android.transaction.domain.creation.removeasset.mapper.CreateRemoveAssetTransactionResultMapperImpl
import com.algorand.android.transaction.domain.creation.removeasset.usecase.CreateRemoveAssetTransactionUseCase
import com.algorand.android.transaction.domain.creation.send.asset.mapper.CreateSendAssetTransactionPayloadMapper
import com.algorand.android.transaction.domain.creation.send.asset.mapper.CreateSendAssetTransactionPayloadMapperImpl
import com.algorand.android.transaction.domain.creation.send.asset.usecase.CreateSendAssetTransactionUseCase
import com.algorand.android.transaction.domain.creation.send.payment.mapper.CreatePaymentTransactionPayloadMapper
import com.algorand.android.transaction.domain.creation.send.payment.mapper.CreatePaymentTransactionPayloadMapperImpl
import com.algorand.android.transaction.domain.creation.send.payment.usecase.CreatePaymentTransactionUseCase
import com.algorand.android.transaction.domain.creation.send.usecase.CreateSendTransactionUseCase
import com.algorand.android.transaction.domain.mapper.SuggestedTransactionParamsMapper
import com.algorand.android.transaction.domain.usecase.CalculateTransactionFee
import com.algorand.android.transaction.domain.usecase.GetTransactionParams
import com.algorand.android.transaction.domain.validator.AddAssetAssetStatusValidator
import com.algorand.android.transaction.domain.validator.AddAssetMinRequiredBalanceValidator
import com.algorand.android.transaction.domain.validator.OptOutAssetCreatorValidator
import com.algorand.android.transaction.domain.validator.OptOutZeroBalanceValidator
import com.algorand.android.transaction.domain.validator.RemoveAssetAssetStatusValidator
import com.algorand.android.transaction.domain.validator.RemoveAssetMinRequiredBalanceValidator
import com.algorand.android.transaction.domain.validator.SendAssetMinRequiredBalanceValidator
import com.algorand.android.transaction.domain.validator.SufficientBalanceToPayFeeValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object TransactionCreationModule {

    @Provides
    @Singleton
    fun provideAddAssetCreateTransactionResultMapper(
        mapper: AddAssetCreateTransactionResultMapperImpl
    ): AddAssetCreateTransactionResultMapper = mapper

    @Provides
    @Singleton
    fun provideCreateAddAssetTransaction(
        getTransactionParams: GetTransactionParams,
        getAccountInformation: GetAccountInformation,
        algoSdkTransaction: AlgoSdkTransaction,
        suggestedTransactionParamsMapper: SuggestedTransactionParamsMapper,
        addAssetCreateTransactionResultMapper: AddAssetCreateTransactionResultMapper,
        getAccountMinBalance: GetAccountMinBalance,
        calculateTransactionFee: CalculateTransactionFee,
        getAsset: GetAsset
    ): CreateAddAssetTransaction {
        return CreateAddAssetTransactionUseCase(
            getTransactionParams,
            getAccountInformation,
            AddAssetMinRequiredBalanceValidator(getAccountMinBalance, calculateTransactionFee),
            AddAssetAssetStatusValidator(),
            algoSdkTransaction,
            suggestedTransactionParamsMapper,
            addAssetCreateTransactionResultMapper,
            getAsset
        )
    }

    @Provides
    @Singleton
    fun provideCreateRemoveAssetTransactionResultMapper(
        mapper: CreateRemoveAssetTransactionResultMapperImpl
    ): CreateRemoveAssetTransactionResultMapper = mapper

    @Provides
    @Singleton
    fun provideCreateRemoveAssetTransaction(
        getTransactionParams: GetTransactionParams,
        getAccountInformation: GetAccountInformation,
        minRequiredBalanceValidator: RemoveAssetMinRequiredBalanceValidator,
        optOutAssetCreatorValidator: OptOutAssetCreatorValidator,
        optOutZeroBalanceValidator: OptOutZeroBalanceValidator,
        algoSdkTransaction: AlgoSdkTransaction,
        getAsset: GetAsset,
        suggestedTransactionParamsMapper: SuggestedTransactionParamsMapper,
        createRemoveAssetTransactionResultMapper: CreateRemoveAssetTransactionResultMapper
    ): CreateRemoveAssetTransaction {
        return CreateRemoveAssetTransactionUseCase(
            getTransactionParams,
            getAccountInformation,
            minRequiredBalanceValidator,
            optOutAssetCreatorValidator,
            optOutZeroBalanceValidator,
            RemoveAssetAssetStatusValidator(),
            algoSdkTransaction,
            getAsset,
            suggestedTransactionParamsMapper,
            createRemoveAssetTransactionResultMapper
        )
    }

    @Provides
    @Singleton
    fun provideCreateRekeyTransactionResultMapper(
        impl: CreateRekeyTransactionResultMapperImpl
    ): CreateRekeyTransactionResultMapper = impl

    @Provides
    @Singleton
    fun provideCreateRekeyTransaction(
        getTransactionParams: GetTransactionParams,
        suggestedTransactionParamsMapper: SuggestedTransactionParamsMapper,
        algoSdkTransaction: AlgoSdkTransaction,
        getAccountInformation: GetAccountInformation,
        getAccountMinBalance: GetAccountMinBalance,
        createRekeyTransactionResultMapper: CreateRekeyTransactionResultMapper
    ): CreateRekeyTransaction {
        return CreateRekeyTransactionUseCase(
            getTransactionParams,
            SufficientBalanceToPayFeeValidator(),
            suggestedTransactionParamsMapper,
            algoSdkTransaction,
            getAccountInformation,
            getAccountMinBalance,
            createRekeyTransactionResultMapper
        )
    }

    @Provides
    @Singleton
    fun provideCreatePaymentTransaction(
        getAccountInformation: GetAccountInformation,
        calculateTransactionFee: CalculateTransactionFee,
        getAccountMinBalance: GetAccountMinBalance,
        algoSdkTransaction: AlgoSdkTransaction,
        suggestedTransactionParamsMapper: SuggestedTransactionParamsMapper,
        getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    ): CreatePaymentTransaction {
        return CreatePaymentTransactionUseCase(
            getAccountInformation,
            calculateTransactionFee,
            getAccountMinBalance,
            algoSdkTransaction,
            suggestedTransactionParamsMapper,
            SendAssetMinRequiredBalanceValidator(),
            getAccountBaseOwnedAssetData
        )
    }

    @Provides
    @Singleton
    fun provideCreateSendAssetTransaction(
        useCase: CreateSendAssetTransactionUseCase
    ): CreateSendAssetTransaction = useCase

    @Provides
    @Singleton
    fun provideCreatePaymentTransactionPayloadMapper(
        impl: CreatePaymentTransactionPayloadMapperImpl
    ): CreatePaymentTransactionPayloadMapper = impl

    @Provides
    @Singleton
    fun provideCreateSendAssetTransactionPayloadMapper(
        impl: CreateSendAssetTransactionPayloadMapperImpl
    ): CreateSendAssetTransactionPayloadMapper = impl

    @Provides
    @Singleton
    fun provideCreateSendTransaction(useCase: CreateSendTransactionUseCase): CreateSendTransaction = useCase
}
