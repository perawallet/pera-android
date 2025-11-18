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

import com.algorand.wallet.transaction.history.data.model.TransactionHistoryAssetSummaryResponse
import com.algorand.wallet.transaction.history.data.model.TransactionHistoryDetailResponse
import com.algorand.wallet.transaction.history.data.model.TransactionHistoryItemResponse
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer.AssetTransferType
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer.AssetTransferType.OptOut
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer.AssetTransferType.ReceiveOptOut
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type.AssetTransfer.AssetTransferType.SendOptOut
import com.algorand.wallet.utils.formatToBigDecimal
import com.algorand.wallet.utils.isZero
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import javax.inject.Inject

internal class DefaultTransactionHistoryAssetTransferTypeMapper @Inject constructor() :
    TransactionHistoryAssetTransferTypeMapper {

    override fun invoke(address: String, response: TransactionHistoryItemResponse): Type.AssetTransfer? {
        return Type.AssetTransfer(
            assetId = response.asset?.id ?: return null,
            assetUnitName = response.asset.unitName.orEmpty(),
            type = mapAssetTransferType(address, response) ?: return null
        )
    }

    override fun invoke(address: String, response: TransactionHistoryDetailResponse): Type.AssetTransfer? {
        val assetTransferType = mapAssetTransferType(address, response) ?: return null
        return Type.AssetTransfer(
            assetId = response.assetTransferTransaction?.assetId ?: return null,
            assetUnitName = "", // TODO
            type = assetTransferType
        )
    }

    private fun mapAssetTransferType(address: String, response: TransactionHistoryDetailResponse): AssetTransferType? {
        return response.assetTransferTransaction?.run {
            mapAssetTransferType(address, receiver, closeTo, response.sender, amount, null) // TODO
        }
    }

    private fun mapAssetTransferType(address: String, response: TransactionHistoryItemResponse): AssetTransferType? {
        return with(response) {
            mapAssetTransferType(address, receiver, closeToAddress, response.sender, amount, asset)
        }
    }

    private fun mapAssetTransferType(
        address: String,
        receiver: String?,
        closeToAddress: String?,
        sender: String?,
        amount: String?,
        asset: TransactionHistoryAssetSummaryResponse?
    ): AssetTransferType? {
        val assetAmount = amount.formatToBigDecimal(asset?.decimals) ?: ZERO
        return when {
            isReceiveOptOut(address, closeToAddress) -> ReceiveOptOut(assetAmount)
            isSendOptOut(closeToAddress, assetAmount) -> SendOptOut(assetAmount)
            !closeToAddress.isNullOrBlank() -> OptOut
            isSelfTransaction(address, sender, receiver) && assetAmount.isZero() -> AssetTransferType.OptIn
            isSelfTransaction(address, sender, receiver) -> AssetTransferType.Self(assetAmount)
            isReceiveTransaction(address, receiver, closeToAddress) -> AssetTransferType.Receive(assetAmount)
            else -> AssetTransferType.Send(receiver ?: return null, assetAmount)
        }
    }

    private fun isReceiveOptOut(address: String, closeToAddress: String?): Boolean {
        return !closeToAddress.isNullOrBlank() && closeToAddress == address
    }

    private fun isSendOptOut(closeToAddress: String?, amount: BigDecimal?): Boolean {
        return !closeToAddress.isNullOrBlank() && amount != null && amount > ZERO
    }

    private fun isSelfTransaction(address: String, sender: String?, receiver: String?): Boolean {
        return address == sender && address == receiver
    }

    private fun isReceiveTransaction(address: String, receiver: String?, closeToAddress: String?): Boolean {
        return address == receiver || address == closeToAddress
    }
}
