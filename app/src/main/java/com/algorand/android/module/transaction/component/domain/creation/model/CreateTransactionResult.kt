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

package com.algorand.android.module.transaction.component.domain.creation.model

import com.algorand.android.module.algosdk.transaction.model.Transaction
import java.math.BigInteger

sealed interface CreateTransactionResult {

    data object Idle : CreateTransactionResult

    data object Loading : CreateTransactionResult

    data class MinBalanceViolated(val requiredBalance: BigInteger) : CreateTransactionResult

    data class TransactionCreated(val transaction: Transaction) : CreateTransactionResult

    data class AccountNotFound(val address: String) : CreateTransactionResult

    data class NetworkError(val message: String?) : CreateTransactionResult

    data class CannotOptOutDueToBalance(val assetName: String) : CreateTransactionResult

    data object CannotOptOutAssetNotFound : CreateTransactionResult

    data object AssetCreatorCannotOptOut : CreateTransactionResult

    data class AccountAlreadyOptedIn(val assetName: String) : CreateTransactionResult

    data object AssetAlreadyWaitingForRemoval : CreateTransactionResult

    data object CloseTransactionToSameAccount : CreateTransactionResult
}
