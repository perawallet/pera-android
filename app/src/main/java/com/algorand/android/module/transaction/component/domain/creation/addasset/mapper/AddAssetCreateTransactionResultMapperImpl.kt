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

package com.algorand.android.module.transaction.component.domain.creation.addasset.mapper

import com.algorand.android.module.transaction.component.domain.creation.addasset.model.CreateAddAssetTransactionResult
import com.algorand.android.module.transaction.component.domain.creation.addasset.model.CreateAddAssetTransactionResult.AccountNotFound
import com.algorand.android.module.transaction.component.domain.creation.addasset.model.CreateAddAssetTransactionResult.AlreadyOptedIn
import com.algorand.android.module.transaction.component.domain.creation.addasset.model.CreateAddAssetTransactionResult.Idle
import com.algorand.android.module.transaction.component.domain.creation.addasset.model.CreateAddAssetTransactionResult.MinBalanceViolated
import com.algorand.android.module.transaction.component.domain.creation.addasset.model.CreateAddAssetTransactionResult.NetworkError
import com.algorand.android.module.transaction.component.domain.creation.addasset.model.CreateAddAssetTransactionResult.TransactionCreated
import com.algorand.android.module.transaction.component.domain.creation.model.CreateTransactionResult
import javax.inject.Inject

internal class AddAssetCreateTransactionResultMapperImpl @Inject constructor() : AddAssetCreateTransactionResultMapper {

    override fun invoke(result: CreateAddAssetTransactionResult): CreateTransactionResult {
        return when (result) {
            Idle -> CreateTransactionResult.Idle
            is AccountNotFound -> CreateTransactionResult.AccountNotFound(result.address)
            is MinBalanceViolated -> CreateTransactionResult.MinBalanceViolated(result.requiredBalance)
            is NetworkError -> CreateTransactionResult.NetworkError(result.message)
            is TransactionCreated -> CreateTransactionResult.TransactionCreated(result.transaction)
            is AlreadyOptedIn -> CreateTransactionResult.AccountAlreadyOptedIn(result.assetName)
        }
    }
}
