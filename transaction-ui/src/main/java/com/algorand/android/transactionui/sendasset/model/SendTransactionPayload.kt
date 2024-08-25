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

package com.algorand.android.transactionui.sendasset.model

import android.os.Parcelable
import java.math.BigInteger
import kotlinx.parcelize.Parcelize

@Parcelize
data class SendTransactionPayload(
    val assetId: Long,
    val senderAddress: String,
    val targetUser: AssetTransferTargetUser,
    val amount: BigInteger,
    val note: Note?
) : Parcelable {

    @Parcelize
    data class Note(
        val note: String?,
        val xnote: String?
    ) : Parcelable
}
