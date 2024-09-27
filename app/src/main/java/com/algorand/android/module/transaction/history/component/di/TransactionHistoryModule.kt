@file:Suppress("TooManyFunctions")
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

package com.algorand.android.module.transaction.history.component.di

import com.algorand.android.module.transaction.history.component.data.mapper.ApplicationCallHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.AssetConfigurationHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.AssetFreezeHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.AssetTransferHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.KeyRegTransactionHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.OnCompletionMapper
import com.algorand.android.module.transaction.history.component.data.mapper.PaginatedTransactionMapper
import com.algorand.android.module.transaction.history.component.data.mapper.PaymentHistoryMapper
import com.algorand.android.module.transaction.history.component.data.mapper.SignatureMapper
import com.algorand.android.module.transaction.history.component.data.mapper.TransactionTypeMapper
import com.algorand.android.module.transaction.history.component.data.mapper.implementation.ApplicationCallHistoryMapperImpl
import com.algorand.android.module.transaction.history.component.data.mapper.implementation.AssetConfigurationHistoryMapperImpl
import com.algorand.android.module.transaction.history.component.data.mapper.implementation.AssetFreezeHistoryMapperImpl
import com.algorand.android.module.transaction.history.component.data.mapper.implementation.AssetTransferHistoryMapperImpl
import com.algorand.android.module.transaction.history.component.data.mapper.implementation.KeyRegTransactionHistoryMapperImpl
import com.algorand.android.module.transaction.history.component.data.mapper.implementation.OnCompletionMapperImpl
import com.algorand.android.module.transaction.history.component.data.mapper.implementation.PaginatedTransactionMapperImpl
import com.algorand.android.module.transaction.history.component.data.mapper.implementation.PaymentHistoryMapperImpl
import com.algorand.android.module.transaction.history.component.data.mapper.implementation.SignatureMapperImpl
import com.algorand.android.module.transaction.history.component.data.mapper.implementation.TransactionTypeMapperImpl
import com.algorand.android.module.transaction.history.component.data.repository.TransactionHistoryRepositoryImpl
import com.algorand.android.module.transaction.history.component.data.service.TransactionHistoryApiService
import com.algorand.android.module.transaction.history.component.domain.mapper.AssetTransactionMapper
import com.algorand.android.module.transaction.history.component.domain.mapper.AssetTransactionMapperImpl
import com.algorand.android.module.transaction.history.component.domain.mapper.BaseTransactionHistoryMapper
import com.algorand.android.module.transaction.history.component.domain.mapper.BaseTransactionHistoryMapperImpl
import com.algorand.android.module.transaction.history.component.domain.mapper.KeyRegTransactionMapper
import com.algorand.android.module.transaction.history.component.domain.mapper.KeyRegTransactionMapperImpl
import com.algorand.android.module.transaction.history.component.domain.mapper.PayTransactionMapper
import com.algorand.android.module.transaction.history.component.domain.mapper.PayTransactionMapperImpl
import com.algorand.android.module.transaction.history.component.domain.repository.TransactionHistoryRepository
import com.algorand.android.module.transaction.history.component.domain.usecase.GetTransactionPaginationFlow
import com.algorand.android.module.transaction.history.component.domain.usecase.implementation.GetTransactionPaginationFlowUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object TransactionHistoryModule {

    @Provides
    fun provideGetTransactionPaginationFlow(
        useCase: GetTransactionPaginationFlowUseCase
    ): GetTransactionPaginationFlow = useCase

    @Provides
    fun provideTransactionHistoryRepository(
        repository: TransactionHistoryRepositoryImpl
    ): TransactionHistoryRepository = repository

    @Provides
    fun provideApplicationCallHistoryMapper(
        mapper: ApplicationCallHistoryMapperImpl
    ): ApplicationCallHistoryMapper = mapper

    @Provides
    fun provideAssetConfigurationHistoryMapper(
        mapper: AssetConfigurationHistoryMapperImpl
    ): AssetConfigurationHistoryMapper = mapper

    @Provides
    fun provideAssetFreezeHistoryMapper(
        mapper: AssetFreezeHistoryMapperImpl
    ): AssetFreezeHistoryMapper = mapper

    @Provides
    fun provideAssetTransferHistoryMapper(
        mapper: AssetTransferHistoryMapperImpl
    ): AssetTransferHistoryMapper = mapper

    @Provides
    fun provideKeyRegistrationHistoryMapper(
        mapper: KeyRegTransactionHistoryMapperImpl
    ): KeyRegTransactionHistoryMapper = mapper

    @Provides
    fun provideOnCompletionMapper(
        mapper: OnCompletionMapperImpl
    ): OnCompletionMapper = mapper

    @Provides
    fun providePaginatedTransactionMapper(
        mapper: PaginatedTransactionMapperImpl
    ): PaginatedTransactionMapper = mapper

    @Provides
    fun providePaymentHistoryMapper(
        mapper: PaymentHistoryMapperImpl
    ): PaymentHistoryMapper = mapper

    @Provides
    fun provideSignatureMapper(
        mapper: SignatureMapperImpl
    ): SignatureMapper = mapper

    @Provides
    fun provideTransactionTypeMapper(
        mapper: TransactionTypeMapperImpl
    ): TransactionTypeMapper = mapper

    @Provides
    fun provideAssetTransactionMapper(
        mapper: AssetTransactionMapperImpl
    ): AssetTransactionMapper = mapper

    @Provides
    fun provideBaseTransactionHistoryMapper(
        mapper: BaseTransactionHistoryMapperImpl
    ): BaseTransactionHistoryMapper = mapper

    @Provides
    fun provideKeyRegTransactionMapper(
        mapper: KeyRegTransactionMapperImpl
    ): KeyRegTransactionMapper = mapper

    @Provides
    fun providePayTransactionMapper(
        mapper: PayTransactionMapperImpl
    ): PayTransactionMapper = mapper

    @Provides
    fun provideTransactionHistoryApiService(
        @Named("indexerRetrofitInterface") retrofit: Retrofit
    ): TransactionHistoryApiService {
        return retrofit.create(TransactionHistoryApiService::class.java)
    }
}
