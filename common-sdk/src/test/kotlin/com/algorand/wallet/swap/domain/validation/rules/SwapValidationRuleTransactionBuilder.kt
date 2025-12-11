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
import com.algorand.wallet.algosdk.transaction.model.RawTransactionType

internal object SwapValidationRuleTransactionBuilder {

    fun buildEmptyTransaction(): RawTransaction {
        return RawTransaction(
            amount = null,
            fee = null,
            firstValidRound = null,
            genesisId = null,
            genesisHash = null,
            lastValidRound = null,
            note = null,
            receiverAddress = null,
            senderAddress = null,
            transactionType = RawTransactionType.UNDEFINED,
            closeToAddress = null,
            rekeyAddress = null,
            assetCloseToAddress = null,
            assetReceiverAddress = null,
            assetAmount = null,
            assetId = null,
            appArgs = null,
            appOnComplete = null,
            appId = null,
            appGlobalSchema = null,
            appLocalSchema = null,
            appExtraPages = null,
            approvalHash = null,
            stateHash = null,
            assetIdBeingConfigured = null,
            assetConfigParameters = null,
            groupId = null,
            innerTransactions = null
        )
    }
}
