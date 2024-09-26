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

package com.algorand.android.module.swap.component.domain.model.swapquotetxns

import com.algorand.android.module.algosdk.transaction.model.RawTransaction
import com.algorand.android.foundation.common.decodeBase64

data class UnsignedSwapSingleTransactionData(
    override val parentListIndex: Int,
    override val transactionListIndex: Int,
    val transactionMsgPack: String?,
    val accountAddress: String,
    val accountAuthAddress: String?,
    val decodedTransaction: RawTransaction?
) : BaseSwapSingleTransactionData {

    val isRekeyedToAnotherAccount: Boolean
        get() = !accountAuthAddress.isNullOrBlank() && accountAuthAddress != accountAddress

    val transactionByteArray: ByteArray?
        get() = transactionMsgPack?.decodeBase64()

    fun getSignerAddress(): String = accountAuthAddress ?: accountAddress
}
