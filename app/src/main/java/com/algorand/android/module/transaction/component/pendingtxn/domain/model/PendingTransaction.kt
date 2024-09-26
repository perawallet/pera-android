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

package com.algorand.android.module.transaction.component.pendingtxn.domain.model

import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import java.math.BigInteger

data class PendingTransaction(
    val signatureKey: String?,
    val detail: PendingTransactionDetail?
) {

    fun isAlgorand() = detail?.assetId == null

    fun getAssetId(): Long? = if (isAlgorand()) ALGO_ASSET_ID else detail?.assetId

    fun getAmount(): BigInteger = detail?.amount ?: detail?.assetAmount ?: BigInteger.ZERO

    fun getReceiverAddress(): String {
        return if (isAlgorand()) detail?.receiverAddress.orEmpty() else detail?.assetReceiverAddress.orEmpty()
    }

    fun getSenderAddress(): String = detail?.senderAddress.orEmpty()

    fun isSendTransaction(accountPublicKey: String): Boolean {
        with(this) {
            val receiverAddress = getReceiverAddress()
            return when {
                detail?.senderAddress == accountPublicKey && receiverAddress == accountPublicKey -> null
                receiverAddress == accountPublicKey -> false
                else -> true
            } == true
        }
    }

    fun isReceiveTransaction(accountPublicKey: String): Boolean {
        return getReceiverAddress() == accountPublicKey
    }

    fun isSenderAndReceiverSame(): Boolean {
        return getSenderAddress() == getReceiverAddress()
    }

    fun isSelfTransaction(address: String): Boolean {
        return getSenderAddress() == address && getReceiverAddress() == address
    }

    fun getTransactionSign(address: String): String? {
        return when {
            isSendTransaction(address) -> if (isSenderAndReceiverSame()) null else "-"
            isReceiveTransaction(address) -> if (isSenderAndReceiverSame()) null else "+"
            else -> null
        }
    }
}