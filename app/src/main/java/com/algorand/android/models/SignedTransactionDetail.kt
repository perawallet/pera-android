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
import java.math.BigInteger
import kotlinx.parcelize.Parcelize

sealed class SignedTransactionDetail : Parcelable {

    abstract val signedTransactionData: ByteArray

    open var shouldWaitForConfirmation: Boolean = false

    @Parcelize
    data class Send(
        override val signedTransactionData: ByteArray,
        val amount: BigInteger,
        val senderAccountType: Account.Type?,
        val senderAccountName: String,
        val senderAccountAddress: String,
        val targetUser: TargetUser,
        val isMax: Boolean,
        var fee: Long,
        val assetInformation: AssetInformation,
        val note: String? = null,
        val xnote: String? = null
    ) : SignedTransactionDetail()

    sealed class AssetOperation : SignedTransactionDetail() {

        abstract val senderAccountAddress: String
        abstract val assetInformation: AssetInformation

        @Parcelize
        data class AssetAddition(
            override val signedTransactionData: ByteArray,
            override val senderAccountAddress: String,
            override val assetInformation: AssetInformation
        ) : AssetOperation()

        @Parcelize
        data class AssetRemoval(
            override val signedTransactionData: ByteArray,
            override val senderAccountAddress: String,
            override val assetInformation: AssetInformation
        ) : AssetOperation()
    }

    @Parcelize
    data class RekeyOperation(
        override val signedTransactionData: ByteArray,
        val accountDetail: Account.Detail?,
        val rekeyedAccountDetail: Account.Detail?,
        val accountAddress: String,
        val accountName: String,
        val rekeyAdminAddress: String,
        val ledgerDetail: Account.Detail.Ledger
    ) : SignedTransactionDetail()

    @Parcelize
    data class RekeyToStandardAccountOperation(
        override val signedTransactionData: ByteArray,
        val accountDetail: Account.Detail?,
        val rekeyedAccountDetail: Account.Detail?,
        val accountAddress: String,
        val accountName: String,
        val rekeyAdminAddress: String,
    ) : SignedTransactionDetail()

    @Parcelize
    data class Group(
        override val signedTransactionData: ByteArray,
        val transactions: List<SignedTransactionDetail>?
    ) : SignedTransactionDetail()

    @Parcelize
    data class ExternalTransaction(
        override val signedTransactionData: ByteArray
    ) : SignedTransactionDetail()

    @Parcelize
    data class Arc59Send(
        override val signedTransactionData: ByteArray
    ) : SignedTransactionDetail()

    @Parcelize
    data class Arc59OptIn(
        override val signedTransactionData: ByteArray,
    ) : SignedTransactionDetail() {
        override var shouldWaitForConfirmation: Boolean = true
    }

    @Parcelize
    data class Arc59ClaimOrReject(
        override val signedTransactionData: ByteArray
    ) : SignedTransactionDetail()
}
