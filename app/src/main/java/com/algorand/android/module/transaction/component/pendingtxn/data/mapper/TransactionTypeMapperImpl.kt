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

package com.algorand.android.module.transaction.component.pendingtxn.data.mapper

import com.algorand.android.module.transaction.component.pendingtxn.data.model.TransactionTypeResponse
import com.algorand.android.module.transaction.component.pendingtxn.domain.model.TransactionType
import javax.inject.Inject

internal class TransactionTypeMapperImpl @Inject constructor() : TransactionTypeMapper {

    override fun invoke(response: TransactionTypeResponse): TransactionType {
        return when (response) {
            TransactionTypeResponse.PAY_TRANSACTION -> TransactionType.PAY_TRANSACTION
            TransactionTypeResponse.ASSET_TRANSACTION -> TransactionType.ASSET_TRANSACTION
            TransactionTypeResponse.ASSET_CONFIGURATION -> TransactionType.ASSET_CONFIGURATION
            TransactionTypeResponse.APP_TRANSACTION -> TransactionType.APP_TRANSACTION
            TransactionTypeResponse.UNDEFINED -> TransactionType.UNDEFINED
            TransactionTypeResponse.KEYREG -> TransactionType.KEYREG_TRANSACTION
        }
    }
}
