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

import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_DECIMALS
import com.algorand.wallet.transaction.history.data.model.TransactionHistoryDetailResponse
import com.algorand.wallet.transaction.history.data.model.TransactionHistoryItemResponse
import com.algorand.wallet.transaction.history.domain.model.TransactionHistory.Type
import com.algorand.wallet.utils.formatToBigDecimal
import javax.inject.Inject

internal class DefaultTransactionHistoryPaymentTypeMapper @Inject constructor() : TransactionHistoryPaymentTypeMapper {

    override fun invoke(address: String, response: TransactionHistoryItemResponse): Type.Payment? {
        return with(response) {
            mapPayment(address, sender, receiver, amount)
        }
    }

    override fun invoke(address: String, response: TransactionHistoryDetailResponse): Type.Payment? {
        return with(response) {
            mapPayment(address, sender, paymentTransaction?.receiver, paymentTransaction?.amount)
        }
    }

    private fun mapPayment(address: String, sender: String?, receiver: String?, amount: String?): Type.Payment? {
        val paymentType = when {
            address == receiver && address == sender -> Type.Payment.PaymentType.Self
            address == receiver -> Type.Payment.PaymentType.Receive
            address == sender -> Type.Payment.PaymentType.Send(receiver ?: return null)
            else -> null
        } ?: return null
        return Type.Payment(amount = amount?.formatToBigDecimal(ALGO_DECIMALS) ?: return null, paymentType)
    }
}
