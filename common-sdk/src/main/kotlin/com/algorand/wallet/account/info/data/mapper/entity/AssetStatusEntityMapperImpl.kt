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

package com.algorand.wallet.account.info.data.mapper.entity

import com.algorand.wallet.account.info.data.database.model.AssetStatusEntity
import com.algorand.wallet.account.info.domain.model.AssetStatus
import javax.inject.Inject

internal class AssetStatusEntityMapperImpl @Inject constructor() : AssetStatusEntityMapper {

    override fun invoke(status: AssetStatus): AssetStatusEntity {
        return when (status) {
            AssetStatus.PENDING_FOR_REMOVAL -> AssetStatusEntity.PENDING_FOR_REMOVAL
            AssetStatus.PENDING_FOR_ADDITION -> AssetStatusEntity.PENDING_FOR_ADDITION
            AssetStatus.OWNED_BY_ACCOUNT -> AssetStatusEntity.OWNED_BY_ACCOUNT
        }
    }
}
