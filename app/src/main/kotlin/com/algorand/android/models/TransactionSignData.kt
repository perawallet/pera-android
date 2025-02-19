/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.models

import android.os.Parcelable
import com.algorand.android.utils.MIN_FEE
import com.algorand.wallet.account.core.domain.model.TransactionSigner
import java.math.BigInteger
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class TransactionSignData : Parcelable {

    abstract val senderAccountAddress: String
    abstract val signer: TransactionSigner
    abstract val senderAuthAddress: String?

    open var calculatedFee: Long? = null
    open var transactionByteArray: ByteArray? = null
    open var amount: BigInteger = BigInteger.ZERO
    open val targetUser: TargetUser? = null
    open var isArc59Transaction: Boolean = false

    abstract fun getSignedTransactionDetail(signedTransactionData: ByteArray): SignedTransactionDetail

    fun isSenderRekeyed(): Boolean {
        return senderAuthAddress != null && senderAuthAddress != senderAccountAddress
    }

    data class Send(
        override val senderAccountAddress: String,
        override val senderAuthAddress: String?,
        override val signer: TransactionSigner,
        override var amount: BigInteger,
        override var targetUser: TargetUser,
        override var transactionByteArray: ByteArray? = null,
        override var isArc59Transaction: Boolean,
        val senderAlgoAmount: BigInteger,
        val minimumBalance: Long,
        val senderAccountName: String,
        val assetId: Long,
        val note: String? = null,
        val xnote: String? = null,
        var isMax: Boolean = false,
        var projectedFee: Long = MIN_FEE
    ) : TransactionSignData() {
        override fun getSignedTransactionDetail(signedTransactionData: ByteArray): SignedTransactionDetail {
            return SignedTransactionDetail.Send(
                signedTransactionData = signedTransactionData,
                amount = amount,
                targetUser = targetUser,
                isMax = isMax,
                fee = calculatedFee ?: 0,
                assetId = assetId,
                note = note,
                xnote = xnote,
                senderAccountAddress = senderAccountAddress,
                senderAccountName = senderAccountName
            )
        }
    }

    data class AddAsset(
        override val senderAccountAddress: String,
        override val senderAuthAddress: String?,
        override val signer: TransactionSigner,
        override var transactionByteArray: ByteArray? = null,
        override var isArc59Transaction: Boolean = false,
        val assetId: Long
    ) : TransactionSignData() {
        override fun getSignedTransactionDetail(signedTransactionData: ByteArray): SignedTransactionDetail {
            return SignedTransactionDetail.AssetOperation.AssetAddition(
                signedTransactionData = signedTransactionData,
                senderAccountAddress = senderAccountAddress,
                assetId = assetId
            )
        }
    }

    data class RemoveAsset(
        override val senderAccountAddress: String,
        override val senderAuthAddress: String?,
        override val signer: TransactionSigner,
        val assetId: Long,
        val creatorAddress: String
    ) : TransactionSignData() {
        override fun getSignedTransactionDetail(signedTransactionData: ByteArray): SignedTransactionDetail {
            return SignedTransactionDetail.AssetOperation.AssetRemoval(
                signedTransactionData = signedTransactionData,
                senderAccountAddress = senderAccountAddress,
                assetId = assetId
            )
        }
    }

    data class SendAndRemoveAsset(
        override val senderAccountAddress: String,
        override val senderAuthAddress: String?,
        override val signer: TransactionSigner,
        override var amount: BigInteger,
        val senderAccountName: String,
        val assetId: Long,
        val note: String? = null,
        override val targetUser: TargetUser,
    ) : TransactionSignData() {
        override fun getSignedTransactionDetail(signedTransactionData: ByteArray): SignedTransactionDetail {
            return SignedTransactionDetail.Send(
                signedTransactionData = signedTransactionData,
                amount = amount,
                targetUser = targetUser,
                isMax = false,
                fee = calculatedFee ?: 0,
                assetId = assetId,
                note = note,
                senderAccountAddress = senderAccountAddress,
                senderAccountName = senderAccountName
            )
        }
    }

    data class Rekey(
        override val senderAccountAddress: String,
        override val senderAuthAddress: String?,
        override val signer: TransactionSigner,
        val senderAccountName: String,
        val rekeyAdminAddress: String
    ) : TransactionSignData() {
        override fun getSignedTransactionDetail(signedTransactionData: ByteArray): SignedTransactionDetail {
            return SignedTransactionDetail.RekeyOperation(
                signedTransactionData = signedTransactionData,
                accountAddress = senderAccountAddress,
                rekeyAdminAddress = rekeyAdminAddress,
                accountName = senderAccountName
            )
        }
    }
}
