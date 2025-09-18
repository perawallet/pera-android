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

package com.algorand.wallet.asset.data.mapper.model

import com.algorand.wallet.asset.data.database.model.AssetCategoryEntity
import com.algorand.wallet.asset.data.utils.AssetCategoryIds
import com.algorand.wallet.asset.domain.model.AssetCategory
import javax.inject.Inject

internal class DefaultAssetCategoryMapper @Inject constructor() : AssetCategoryMapper {

    override fun invoke(category: Int?): AssetCategory? {
        return when (category) {
            AssetCategoryIds.RUG_NINJA -> AssetCategory.RUG_NINJA
            AssetCategoryIds.MYTH_FINANCE_DUAL_STAKE -> AssetCategory.MYTH_FINANCE_DUAL_STAKE
            AssetCategoryIds.POOL_TOKEN -> AssetCategory.POOL_TOKEN
            else -> null
        }
    }

    override fun invoke(category: AssetCategoryEntity?): AssetCategory? {
        return when (category) {
            AssetCategoryEntity.RUG_NINJA -> AssetCategory.RUG_NINJA
            AssetCategoryEntity.MYTH_FINANCE_DUAL_STAKE -> AssetCategory.MYTH_FINANCE_DUAL_STAKE
            AssetCategoryEntity.POOL_TOKEN -> AssetCategory.POOL_TOKEN
            null -> null
        }
    }
}
