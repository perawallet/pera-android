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

package com.algorand.android.modules.dapp.bidali.ui.browser.usecase

import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAssetDetail
import com.algorand.android.modules.dapp.bidali.domain.model.BidaliPaymentRequestDTO
import com.algorand.android.modules.dapp.bidali.domain.model.MainnetBidaliSupportedCurrency
import com.algorand.android.modules.dapp.bidali.domain.model.TestnetBidaliSupportedCurrency
import com.algorand.android.node.domain.usecase.IsSelectedNodeMainnet
import com.algorand.android.transactionui.sendasset.model.AssetTransferTargetUser
import com.algorand.android.transactionui.sendasset.model.SendTransactionPayload
import com.algorand.android.utils.formatAmountAsBigInteger
import com.algorand.android.utils.toBigDecimalOrZero
import java.math.BigInteger
import javax.inject.Inject

internal class GetBidaliPaymentSendTransactionPayloadUseCase @Inject constructor(
    private val isSelectedNodeMainnet: IsSelectedNodeMainnet,
    private val getAssetDetail: GetAssetDetail
) : GetBidaliPaymentSendTransactionPayload {

    override suspend fun invoke(request: BidaliPaymentRequestDTO, accountAddress: String): SendTransactionPayload? {
        val assetId = getAssetIdFromBidaliIdentifier(request.protocol) ?: return null
        return SendTransactionPayload(
            assetId = assetId,
            senderAddress = accountAddress,
            targetUser = AssetTransferTargetUser.Address(request.address),
            amount = getAmountAsBigInteger(request, assetId) ?: return null,
            note = SendTransactionPayload.Note(note = null, xnote = request.extraId)
        )
    }

    private fun getAssetIdFromBidaliIdentifier(bidaliId: String): Long? {
        return if (isSelectedNodeMainnet()) {
            MainnetBidaliSupportedCurrency.entries.firstOrNull { it.key == bidaliId }?.assetId
        } else {
            TestnetBidaliSupportedCurrency.entries.firstOrNull { it.key == bidaliId }?.assetId
        }
    }

    private suspend fun getAmountAsBigInteger(request: BidaliPaymentRequestDTO, assetId: Long): BigInteger? {
        val amount = request.amount.toBigDecimalOrZero()
        val assetDetail = getAssetDetail(assetId) ?: return null
        return amount.formatAmountAsBigInteger(assetDetail.getDecimalsOrZero())
    }
}
