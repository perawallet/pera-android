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

package com.algorand.wallet.swap.data.repository

import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.cache.InMemoryCachedObject
import com.algorand.wallet.foundation.cache.PersistentCache
import com.algorand.wallet.swap.data.mapper.AvailableSwapAssetMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteProviderMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteRequestBodyMapper
import com.algorand.wallet.swap.data.mapper.SwapQuoteTransactionMapper
import com.algorand.wallet.swap.data.mapper.SwapUpdateStatusRequestBodyMapper
import com.algorand.wallet.swap.data.mapper.TopSwapPairsMapper
import com.algorand.wallet.swap.data.model.CreateSwapQuoteTransactionsRequestBody
import com.algorand.wallet.swap.data.model.SwapPeraFeeRequestBody
import com.algorand.wallet.swap.data.service.SwapApiService
import com.algorand.wallet.swap.domain.model.AvailableSwapAsset
import com.algorand.wallet.swap.domain.model.SwapPeraFee
import com.algorand.wallet.swap.domain.model.SwapQuoteProvider
import com.algorand.wallet.swap.domain.model.SwapQuoteRequestPayload
import com.algorand.wallet.swap.domain.model.SwapQuoteTransaction
import com.algorand.wallet.swap.domain.model.SwapQuoteV2
import com.algorand.wallet.swap.domain.model.SwapStatusFailureReason
import com.algorand.wallet.swap.domain.model.TopSwapPairs
import com.algorand.wallet.swap.domain.repository.SwapRepository
import com.algorand.wallet.transaction.domain.model.TransactionId
import java.math.BigInteger
import javax.inject.Inject

internal class DefaultSwapRepository @Inject constructor(
    private val swapApiService: SwapApiService,
    private val lastUsedAddressCache: PersistentCache<String>,
    private val quoteTransactionMapper: SwapQuoteTransactionMapper,
    private val quoteRequestMapper: SwapQuoteRequestBodyMapper,
    private val quoteMapper: SwapQuoteMapper,
    private val swapQuoteProviderMapper: SwapQuoteProviderMapper,
    private val availableSwapAssetMapper: AvailableSwapAssetMapper,
    private val providersCache: InMemoryCachedObject<List<SwapQuoteProvider>>,
    private val topSwapPairsMapper: TopSwapPairsMapper,
    private val swapUpdateStatusRequestBodyMapper: SwapUpdateStatusRequestBodyMapper,
    private val useLocalCurrencyCache: PersistentCache<Boolean>,
    private val slippageTolerancePersistentCache: PersistentCache<Float>
) : SwapRepository {

    override suspend fun getSwapQuotes(payload: SwapQuoteRequestPayload): PeraResult<List<SwapQuoteV2>> {
        return try {
            val providers = getSwapQuoteProviders().getDataOrNull() ?: return PeraResult.Error(Exception())
            val response = swapApiService.getSwapQuote(quoteRequestMapper(payload))
            val quotes = response.swapQuoteResponseList.mapNotNull { quoteMapper(it, providers) }
            if (quotes.isEmpty()) PeraResult.Error(Exception()) else PeraResult.Success(quotes)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun getPeraFee(assetInId: Long, amount: BigInteger): PeraResult<SwapPeraFee> {
        return try {
            val response = swapApiService.getPeraFee(SwapPeraFeeRequestBody(assetInId, amount))
            PeraResult.Success(SwapPeraFee(response.peraFeeAmount))
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun createQuoteTransactions(quoteId: Long): PeraResult<List<SwapQuoteTransaction>> {
        return try {
            val response = swapApiService.getQuoteTransactions(CreateSwapQuoteTransactionsRequestBody(quoteId))
            val transactions = response.transactionGroups?.mapNotNull { quoteTransactionMapper(it) }
            if (transactions.isNullOrEmpty()) PeraResult.Error(Exception()) else PeraResult.Success(transactions)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun setSwapStatusInProgress(swapId: Long, txnIds: List<TransactionId>) {
        try {
            val body = swapUpdateStatusRequestBodyMapper.mapToInProgress(txnIds)
            swapApiService.updateSwapStatus(swapId, body)
        } catch (e: Exception) {
            // Fire and forget request, no need to handle the exception
        }
    }

    override suspend fun setSwapStatusFailed(quoteId: Long, reason: SwapStatusFailureReason) {
        try {
            val body = swapUpdateStatusRequestBodyMapper.mapToFailed(reason)
            swapApiService.updateSwapStatus(quoteId, body)
        } catch (e: Exception) {
            // Fire and forget request, no need to handle the exception
        }
    }

    override suspend fun getAvailableAssetsToSwap(
        assetInId: Long,
        query: String?,
    ): PeraResult<List<AvailableSwapAsset>> {
        return try {
            val providers = getSwapQuoteProviders().getDataOrNull() ?: return PeraResult.Error(Exception())
            val providersCsv = providers.joinToString(separator = ",") { it.name }
            val response = swapApiService.getAvailableSwapAssetList(assetInId, providersCsv, query)
            val availableAssets = response.results?.mapNotNull { availableSwapAssetMapper(it) }.orEmpty()
            if (availableAssets.isEmpty()) PeraResult.Error(Exception()) else PeraResult.Success(availableAssets)
        } catch (exception: Exception) {
            PeraResult.Error(exception)
        }
    }

    override suspend fun setLastUsedSwapAddress(address: String) = lastUsedAddressCache.put(address)

    override suspend fun getLastUsedSwapAddress(): String? = lastUsedAddressCache.get()

    private suspend fun getSwapQuoteProviders(): PeraResult<List<SwapQuoteProvider>> {
        return try {
            val cachedProviders = providersCache.get()
            if (cachedProviders != null) {
                PeraResult.Success(cachedProviders)
            } else {
                val response = swapApiService.getSwapQuoteProviders()
                val providers = response.results.mapNotNull { swapQuoteProviderMapper(it) }
                providersCache.put(providers)
                PeraResult.Success(providers)
            }
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override suspend fun getTopSwapPairs(): PeraResult<TopSwapPairs> {
        return try {
            val response = swapApiService.getTopSwapPairs()
            PeraResult.Success(topSwapPairsMapper(response))
        } catch (e: Exception) {
            PeraResult.Error(e)
        }
    }

    override suspend fun getUseLocalCurrencyPreference(): Boolean {
        return useLocalCurrencyCache.get() ?: false
    }

    override suspend fun setUseLocalCurrencyPreference(useLocalCurrency: Boolean) {
        useLocalCurrencyCache.put(useLocalCurrency)
    }

    override suspend fun getSlippageTolerancePercentage(): Float? {
        return slippageTolerancePersistentCache.get()
    }

    override suspend fun setSlippageTolerancePercentage(percentage: Float?) {
        if (percentage == null) {
            slippageTolerancePersistentCache.clear()
        } else {
            slippageTolerancePersistentCache.put(percentage)
        }
    }
}
