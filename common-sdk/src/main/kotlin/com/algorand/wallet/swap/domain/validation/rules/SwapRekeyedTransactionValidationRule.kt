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

package com.algorand.wallet.swap.domain.validation.rules

import com.algorand.wallet.algosdk.transaction.model.RawTransaction
import com.algorand.wallet.swap.domain.usecase.GetParsedSwapTransactions
import com.algorand.wallet.swap.domain.validation.SwapTransactionValidationRule
import com.algorand.wallet.swap.domain.validation.model.SwapTransactionValidationData
import javax.inject.Inject

internal class SwapRekeyedTransactionValidationRule @Inject constructor(
    private val getParsedSwapTransactions: GetParsedSwapTransactions
) : SwapTransactionValidationRule {

    override fun invoke(data: SwapTransactionValidationData): Boolean {
        val transactions = getParsedSwapTransactions(data.signedTransactions, data.unsignedTransactions)
        return transactions.none { it.isRekeyingLocalAddress(data.localAddresses) }
    }

    private fun RawTransaction.isRekeyingLocalAddress(localAddresses: List<String>): Boolean {
        val rekeyedAddress = this.rekeyAddress?.decodedAddress ?: return false
        return localAddresses.any { it == rekeyedAddress }
    }
}
