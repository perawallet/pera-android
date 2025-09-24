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

package com.algorand.wallet.asset.data.mapper.entity

import com.algorand.wallet.asset.data.database.model.AssetCategoryEntity
import com.algorand.wallet.asset.data.utils.AssetCategoryIds.MYTH_FINANCE_DUAL_STAKE
import com.algorand.wallet.asset.data.utils.AssetCategoryIds.POOL_TOKEN
import com.algorand.wallet.asset.data.utils.AssetCategoryIds.RUG_NINJA
import javax.inject.Inject

internal class DefaultAssetCategoryEntityMapper @Inject constructor() : AssetCategoryEntityMapper {

    override fun invoke(category: Int?): AssetCategoryEntity? {
        return when (category) {
            RUG_NINJA -> AssetCategoryEntity.RUG_NINJA
            MYTH_FINANCE_DUAL_STAKE -> AssetCategoryEntity.MYTH_FINANCE_DUAL_STAKE
            POOL_TOKEN -> AssetCategoryEntity.POOL_TOKEN
            else -> null
        }
    }
}
