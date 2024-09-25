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

package com.algorand.android.module.transaction.component.domain.creation.removeasset.mapper

import com.algorand.android.module.transaction.component.domain.creation.model.CreateTransactionResult
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.AccountHasAsset
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.AccountNotFound
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.AccountNotOptedIn
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.AssetAlreadyWaitingForRemoval
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.AssetCreatorCantOptOut
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.Idle
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.MinBalanceViolated
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.NetworkError
import com.algorand.android.module.transaction.component.domain.creation.removeasset.model.CreateRemoveAssetTransactionResult.TransactionCreated
import javax.inject.Inject

internal class CreateRemoveAssetTransactionResultMapperImpl @Inject constructor() :
    CreateRemoveAssetTransactionResultMapper {

    override fun invoke(result: CreateRemoveAssetTransactionResult): CreateTransactionResult {
        return when (result) {
            Idle -> CreateTransactionResult.Idle
            is AccountHasAsset -> CreateTransactionResult.CannotOptOutDueToBalance(result.assetName)
            is AccountNotFound -> CreateTransactionResult.AccountNotFound(result.address)
            AccountNotOptedIn -> CreateTransactionResult.CannotOptOutAssetNotFound
            AssetCreatorCantOptOut -> CreateTransactionResult.AssetCreatorCannotOptOut
            is MinBalanceViolated -> CreateTransactionResult.MinBalanceViolated(result.requiredBalance)
            is NetworkError -> CreateTransactionResult.NetworkError(result.message)
            is TransactionCreated -> CreateTransactionResult.TransactionCreated(result.transaction)
            is AssetAlreadyWaitingForRemoval -> CreateTransactionResult.AssetAlreadyWaitingForRemoval
        }
    }
}
