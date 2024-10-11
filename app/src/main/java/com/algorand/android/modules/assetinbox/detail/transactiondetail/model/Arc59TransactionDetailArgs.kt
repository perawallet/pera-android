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

package com.algorand.android.modules.assetinbox.detail.transactiondetail.model

import android.os.Parcelable
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.ReceiverAccountDetailPreview
import com.algorand.android.utils.AssetName
import java.math.BigInteger
import kotlinx.parcelize.Parcelize

@Parcelize
data class Arc59TransactionDetailArgs(
    val receiverAccountDetailPreview: ReceiverAccountDetailPreview,
    val assetDetail: BaseAssetDetail,
    val optInExpense: BigInteger,
    val senders: List<Sender>
) : Parcelable {

    sealed interface BaseAssetDetail : Parcelable {

        val id: Long
        val name: AssetName
        val shortName: AssetName

        @Parcelize
        data class AssetDetail(
            override val id: Long,
            override val name: AssetName,
            override val shortName: AssetName,
            val amount: BigInteger,
            val decimal: Int,
            val usdValue: String
        ) : BaseAssetDetail

        @Parcelize
        data class CollectibleDetail(
            override val id: Long,
            override val name: AssetName,
            override val shortName: AssetName,
            val imageUrl: String
        ) : BaseAssetDetail
    }

    @Parcelize
    data class Sender(
        val address: String,
        val amount: String
    ) : Parcelable
}
