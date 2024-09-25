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

package com.algorand.android.module.transaction.component.domain.creation.removeasset.model

import com.algorand.android.algosdk.component.transaction.model.Transaction
import java.math.BigInteger

sealed interface CreateRemoveAssetTransactionResult {

    data object Idle : CreateRemoveAssetTransactionResult

    data class MinBalanceViolated(val requiredBalance: BigInteger) : CreateRemoveAssetTransactionResult

    data class TransactionCreated(val transaction: Transaction) : CreateRemoveAssetTransactionResult

    data class AccountNotFound(val address: String) : CreateRemoveAssetTransactionResult

    data class NetworkError(val message: String?) : CreateRemoveAssetTransactionResult

    data class AccountHasAsset(val assetName: String) : CreateRemoveAssetTransactionResult

    data object AccountNotOptedIn : CreateRemoveAssetTransactionResult

    data object AssetCreatorCantOptOut : CreateRemoveAssetTransactionResult

    data object AssetAlreadyWaitingForRemoval : CreateRemoveAssetTransactionResult
}
