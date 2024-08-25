package com.algorand.android.transaction_history_component.di

import com.algorand.android.transaction_history_component.data.mapper.*
import com.algorand.android.transaction_history_component.data.mapper.implementation.*
import com.algorand.android.transaction_history_component.data.repository.TransactionHistoryRepositoryImpl
import com.algorand.android.transaction_history_component.data.service.TransactionHistoryApiService
import com.algorand.android.transaction_history_component.data.util.TransactionHistoryPaginationHelper
import com.algorand.android.transaction_history_component.domain.mapper.*
import com.algorand.android.transaction_history_component.domain.repository.TransactionHistoryRepository
import com.algorand.android.transaction_history_component.domain.usecase.*
import com.algorand.android.transaction_history_component.domain.usecase.implementation.GetTransactionPaginationFlowUseCase
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named

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
