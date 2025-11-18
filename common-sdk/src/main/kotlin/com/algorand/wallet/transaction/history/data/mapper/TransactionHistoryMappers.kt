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

package com.algorand.wallet.transaction.history.data.mapper

import com.algorand.wallet.transaction.history.data.model.TransactionHistoryDetailResponse
import com.algorand.wallet.transaction.history.data.model.TransactionHistoryItemResponse
import com.algorand.wallet.transaction.history.data.model.TransactionHistorySwapGroupDetailResponse
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type
import com.algorand.wallet.transaction.history.domain.model.TransactionHistorySwapGroupDetail

internal interface TransactionHistoryMapper {
    operator fun invoke(address: String, response: TransactionHistoryItemResponse): TransactionHistory?
    operator fun invoke(address: String, response: TransactionHistoryDetailResponse): TransactionHistory?
}

internal interface TransactionHistorySwapGroupDetailMapper {
    operator fun invoke(
        address: String,
        response: TransactionHistorySwapGroupDetailResponse
    ): TransactionHistorySwapGroupDetail?
}

internal interface TransactionHistoryPaymentTypeMapper {
    operator fun invoke(address: String, response: TransactionHistoryItemResponse): Type.Payment?
    operator fun invoke(address: String, response: TransactionHistoryDetailResponse): Type.Payment?
}

internal interface TransactionHistoryAssetTransferTypeMapper {
    operator fun invoke(address: String, response: TransactionHistoryItemResponse): Type.AssetTransfer?
    operator fun invoke(address: String, response: TransactionHistoryDetailResponse): Type.AssetTransfer?
}
