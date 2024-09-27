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

package com.algorand.android.module.swap.component.data.repository

import com.algorand.android.module.asset.utils.getSafeAssetIdForRequest
import com.algorand.android.module.foundation.PeraResult
import com.algorand.android.module.foundation.common.toCsvString
import com.algorand.android.module.network.exceptions.RetrofitErrorHandler
import com.algorand.android.module.network.requestWithHipoErrorHandler
import com.algorand.android.module.swap.component.data.mapper.AvailableSwapAssetsMapper
import com.algorand.android.module.swap.component.data.mapper.PeraSwapFeeMapper
import com.algorand.android.module.swap.component.data.mapper.SwapQuoteMapper
import com.algorand.android.module.swap.component.data.mapper.SwapQuoteRequestBodyMapper
import com.algorand.android.module.swap.component.data.mapper.SwapQuoteTransactionMapper
import com.algorand.android.module.swap.component.data.model.CreateSwapQuoteTransactionsRequestBody
import com.algorand.android.module.swap.component.data.model.PeraFeeRequestBody
import com.algorand.android.module.swap.component.data.model.SwapQuoteExceptionRequestBody
import com.algorand.android.module.swap.component.data.model.SwapQuoteProviderResponse
import com.algorand.android.module.swap.component.data.service.AssetSwapApi
import com.algorand.android.module.swap.component.domain.model.AvailableSwapAsset
import com.algorand.android.module.swap.component.domain.model.GetSwapQuoteRequestPayload
import com.algorand.android.module.swap.component.domain.model.PeraFee
import com.algorand.android.module.swap.component.domain.model.SwapQuote
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransactionResult
import com.algorand.android.module.swap.component.domain.repository.AssetSwapRepository
import java.math.BigInteger
import javax.inject.Inject

internal class AssetSwapRepositoryImpl @Inject constructor(
    private val assetSwapApi: AssetSwapApi,
    private val hipoErrorHandler: RetrofitErrorHandler,
    private val peraSwapFeeMapper: PeraSwapFeeMapper,
    private val swapQuoteTransactionMapper: SwapQuoteTransactionMapper,
    private val swapQuoteRequestBodyMapper: SwapQuoteRequestBodyMapper,
    private val swapQuoteMapper: SwapQuoteMapper,
    private val availableSwapAssetsMapper: AvailableSwapAssetsMapper
) : AssetSwapRepository {

    override suspend fun getPeraFee(fromAssetId: Long, amount: BigInteger): PeraResult<PeraFee> {
        val assetId = getSafeAssetIdForRequest(fromAssetId)
        return requestWithHipoErrorHandler(hipoErrorHandler) {
            assetSwapApi.getPeraFee(PeraFeeRequestBody(assetId, amount))
        }.use(
            onSuccess = { response ->
                PeraResult.Success(peraSwapFeeMapper(response))
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    override suspend fun createQuoteTransactions(quoteId: Long): PeraResult<List<SwapQuoteTransactionResult>> {
        val requestBody = CreateSwapQuoteTransactionsRequestBody(quoteId)
        return requestWithHipoErrorHandler(hipoErrorHandler) {
            assetSwapApi.getQuoteTransactions(requestBody)
        }.use(
            onSuccess = { response ->
                val swapQuoteTransactions = response.transactionGroups?.map { swapQuoteTransactionMapper(it) }
                if (swapQuoteTransactions.isNullOrEmpty()) {
                    PeraResult.Error(IllegalStateException())
                } else {
                    PeraResult.Success(swapQuoteTransactions)
                }
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    override suspend fun getSwapQuote(payload: GetSwapQuoteRequestPayload): PeraResult<SwapQuote> {
        val requestBody = swapQuoteRequestBodyMapper(payload)
        return requestWithHipoErrorHandler(hipoErrorHandler) {
            assetSwapApi.getSwapQuote(requestBody)
        }.use(
            onSuccess = { response ->
                val swapQuoteResponse = response.swapQuoteResponseList.firstOrNull()
                val swapQuote = swapQuoteMapper(swapQuoteResponse)
                if (swapQuote == null) {
                    PeraResult.Error(IllegalArgumentException())
                } else {
                    PeraResult.Success(swapQuote)
                }
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }

    override suspend fun recordSwapQuoteException(quoteId: Long, exceptionText: String?) {
        requestWithHipoErrorHandler(hipoErrorHandler) {
            assetSwapApi.putSwapQuoteException(quoteId, SwapQuoteExceptionRequestBody(exceptionText))
        }
    }

    override suspend fun getAvailableTargetSwapAssets(
        assetId: Long,
        query: String?
    ): PeraResult<List<AvailableSwapAsset>> {
        val providersQuery = SwapQuoteProviderResponse.entries.mapNotNull { it.value }.toCsvString()
        val safeAssetId = getSafeAssetIdForRequest(assetId)
        return requestWithHipoErrorHandler(hipoErrorHandler) {
            assetSwapApi.getAvailableSwapAssetList(safeAssetId, providersQuery, query)
        }.use(
            onSuccess = { response ->
                val availableSwapAssets = availableSwapAssetsMapper(response)
                PeraResult.Success(availableSwapAssets)
            },
            onFailed = { exception, code ->
                PeraResult.Error(exception, code)
            }
        )
    }
}
