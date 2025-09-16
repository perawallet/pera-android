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

package com.algorand.wallet.swap.di

import androidx.paging.PagingSource
import com.algorand.wallet.foundation.cache.InMemoryCacheProvider
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import com.algorand.wallet.foundation.database.PeraDatabase
import com.algorand.wallet.swap.data.dao.SwapSelectedAssetDao
import com.algorand.wallet.swap.data.mapper.AvailableSwapAssetMapper
import com.algorand.wallet.swap.data.mapper.DefaultAvailableSwapAssetMapper
import com.algorand.wallet.swap.data.mapper.DefaultSwapAssetAmountMapper
import com.algorand.wallet.swap.data.mapper.DefaultSwapAssetDetailMapper
import com.algorand.wallet.swap.data.mapper.DefaultSwapHistoryMapper
import com.algorand.wallet.swap.data.mapper.DefaultSwapHistoryStatusMapper
import com.algorand.wallet.swap.data.mapper.DefaultSwapQuoteMapper
import com.algorand.wallet.swap.data.mapper.DefaultSwapQuoteProviderMapper
import com.algorand.wallet.swap.data.mapper.DefaultSwapQuoteRequestBodyMapper
import com.algorand.wallet.swap.data.mapper.DefaultSwapQuoteTransactionMapper
import com.algorand.wallet.swap.data.mapper.DefaultSwapSelectedAssetDetailMapper
import com.algorand.wallet.swap.data.mapper.DefaultSwapTransactionPurposeMapper
import com.algorand.wallet.swap.data.mapper.DefaultTopSwapPairsMapper
import com.algorand.wallet.swap.data.mapper.SwapAssetAmountMapper
import com.algorand.wallet.swap.data.mapper.SwapAssetDetailMapper
import com.algorand.wallet.swap.data.mapper.SwapHistoryMapper
import com.algorand.wallet.swap.data.mapper.SwapHistoryStatusMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteProviderMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteRequestBodyMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteTransactionMapper
import com.algorand.wallet.swap.data.mapper.SwapSelectedAssetDetailMapper
import com.algorand.wallet.swap.data.mapper.SwapTransactionPurposeMapper
import com.algorand.wallet.swap.data.mapper.TopSwapPairsMapper
import com.algorand.wallet.swap.data.repository.DefaultSwapHistoryPagingSource
import com.algorand.wallet.swap.data.repository.DefaultSwapHistoryRepository
import com.algorand.wallet.swap.data.repository.DefaultSwapRepository
import com.algorand.wallet.swap.data.repository.DefaultSwapSelectedAssetRepository
import com.algorand.wallet.swap.data.service.SwapApiService
import com.algorand.wallet.swap.domain.model.SwapHistory
import com.algorand.wallet.swap.domain.model.SwapHistoryPagingData
import com.algorand.wallet.swap.domain.repository.SwapHistoryRepository
import com.algorand.wallet.swap.domain.repository.SwapRepository
import com.algorand.wallet.swap.domain.repository.SwapSelectedAssetRepository
import com.algorand.wallet.swap.domain.usecase.GetAvailableSwapAssets
import com.algorand.wallet.swap.domain.usecase.GetPreselectedSwapAddress
import com.algorand.wallet.swap.domain.usecase.GetPreselectedSwapAddressUseCase
import com.algorand.wallet.swap.domain.usecase.GetSelectedSwapAssetDetail
import com.algorand.wallet.swap.domain.usecase.GetSelectedSwapAssetDetailUseCase
import com.algorand.wallet.swap.domain.usecase.GetSwapAmountByPercentage
import com.algorand.wallet.swap.domain.usecase.GetSwapAmountByPercentageUseCase
import com.algorand.wallet.swap.domain.usecase.GetSwapHistory
import com.algorand.wallet.swap.domain.usecase.GetSwapPeraFee
import com.algorand.wallet.swap.domain.usecase.GetSwapPeraFeeUseCase
import com.algorand.wallet.swap.domain.usecase.GetSwapQuoteDetails
import com.algorand.wallet.swap.domain.usecase.GetSwapQuoteDetailsUseCase
import com.algorand.wallet.swap.domain.usecase.GetSwapQuotes
import com.algorand.wallet.swap.domain.usecase.GetSwapQuotesUseCase
import com.algorand.wallet.swap.domain.usecase.GetTopSwapPairs
import com.algorand.wallet.utils.date.parser.ISO8601DateTimeParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object SwapModule {

    @Singleton
    @Provides
    fun provideSwapRepository(
        swapApiService: SwapApiService,
        persistentCacheProvider: PersistentCacheProvider,
        quoteTransactionMapper: SwapQuoteTransactionMapper,
        quoteRequestMapper: SwapQuoteRequestBodyMapper,
        quoteMapper: SwapQuoteMapper,
        availableSwapAssetMapper: AvailableSwapAssetMapper,
        swapQuoteProviderMapper: SwapQuoteProviderMapper,
        inMemoryCacheProvider: InMemoryCacheProvider,
        topSwapPairsMapper: TopSwapPairsMapper
    ): SwapRepository {
        return DefaultSwapRepository(
            swapApiService = swapApiService,
            lastUsedAddressCache = persistentCacheProvider.getPersistentCache<String>(
                type = String::class.java,
                key = "swap_last_used_address"
            ),
            quoteTransactionMapper = quoteTransactionMapper,
            quoteRequestMapper = quoteRequestMapper,
            quoteMapper = quoteMapper,
            swapQuoteProviderMapper = swapQuoteProviderMapper,
            availableSwapAssetMapper = availableSwapAssetMapper,
            providersCache = inMemoryCacheProvider.getInMemoryCache(),
            topSwapPairsMapper = topSwapPairsMapper
        )
    }

    @Provides
    fun provideGetSwapQuotes(useCase: GetSwapQuotesUseCase): GetSwapQuotes = useCase

    @Provides
    fun provideSwapSelectedAssetDao(database: PeraDatabase): SwapSelectedAssetDao {
        return database.swapSelectedAssetDao()
    }

    @Provides
    fun provideSwapSelectedAssetRepository(
        repository: DefaultSwapSelectedAssetRepository
    ): SwapSelectedAssetRepository = repository

    @Provides
    fun provideGetSelectedSwapAssetDetail(
        useCase: GetSelectedSwapAssetDetailUseCase
    ): GetSelectedSwapAssetDetail = useCase

    @Provides
    fun provideGetPreselectedSwapAddress(
        useCase: GetPreselectedSwapAddressUseCase
    ): GetPreselectedSwapAddress = useCase

    @Provides
    fun provideSwapTransactionPurposeMapper(
        mapper: DefaultSwapTransactionPurposeMapper
    ): SwapTransactionPurposeMapper = mapper

    @Provides
    fun provideSwapQuoteTransactionMapper(
        mapper: DefaultSwapQuoteTransactionMapper
    ): SwapQuoteTransactionMapper = mapper

    @Provides
    fun provideSwapQuoteMapper(mapper: DefaultSwapQuoteMapper): SwapQuoteMapper = mapper

    @Provides
    fun provideSwapAssetAmountMapper(mapper: DefaultSwapAssetAmountMapper): SwapAssetAmountMapper = mapper

    @Provides
    fun provideSwapAssetDetailMapper(mapper: DefaultSwapAssetDetailMapper): SwapAssetDetailMapper = mapper

    @Provides
    fun provideSwapQuoteRequestBodyMapper(
        mapper: DefaultSwapQuoteRequestBodyMapper
    ): SwapQuoteRequestBodyMapper = mapper

    @Provides
    fun provideSwapQuoteProviderMapper(mapper: DefaultSwapQuoteProviderMapper): SwapQuoteProviderMapper = mapper

    @Provides
    fun provideSwapSelectedAssetDetailMapper(
        mapper: DefaultSwapSelectedAssetDetailMapper
    ): SwapSelectedAssetDetailMapper = mapper

    @Provides
    @Singleton
    fun provideSwapApiService(@Named("mobileAlgorandRetrofitInterface") retrofit: Retrofit): SwapApiService {
        return retrofit.create(SwapApiService::class.java)
    }

    @Provides
    fun provideGetSwapQuoteDetails(useCase: GetSwapQuoteDetailsUseCase): GetSwapQuoteDetails = useCase

    @Provides
    fun provideAvailableSwapAssetMapper(mapper: DefaultAvailableSwapAssetMapper): AvailableSwapAssetMapper = mapper

    @Provides
    fun provideGetAvailableSwapAssets(repository: SwapRepository): GetAvailableSwapAssets {
        return GetAvailableSwapAssets(repository::getAvailableAssetsToSwap)
    }

    @Provides
    fun provideGetSwapPeraFee(useCase: GetSwapPeraFeeUseCase): GetSwapPeraFee = useCase

    @Provides
    fun provideGetSwapAmountByPercentage(useCase: GetSwapAmountByPercentageUseCase): GetSwapAmountByPercentage = useCase

    @Provides
    fun provideTopSwapPairsMapper(mapper: DefaultTopSwapPairsMapper): TopSwapPairsMapper = mapper

    @Provides
    fun provideGetTopSwapPairs(repository: SwapRepository): GetTopSwapPairs {
        return GetTopSwapPairs(repository::getTopSwapPairs)
    }

    @Provides
    fun provideSwapHistoryPagingSource(
        pagingSource: DefaultSwapHistoryPagingSource
    ): PagingSource<SwapHistoryPagingData, SwapHistory> = pagingSource

    @Provides
    fun provideSwapHistoryMapper(dateTimeParser: ISO8601DateTimeParser): SwapHistoryMapper {
        return DefaultSwapHistoryMapper(dateTimeParser)
    }

    @Provides
    fun provideSwapHistoryRepository(repository: DefaultSwapHistoryRepository): SwapHistoryRepository = repository

    @Provides
    fun provideGetSwapHistory(repository: SwapHistoryRepository): GetSwapHistory {
        return GetSwapHistory(repository::getSwapHistory)
    }

    @Provides
    fun provideSwapHistoryStatusMapper(mapper: DefaultSwapHistoryStatusMapper): SwapHistoryStatusMapper = mapper
}
