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

package com.algorand.android.assetdetailui.detail.nftprofile.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.CollectibleTrait
import com.algorand.android.assetdetailui.detail.nftprofile.model.CollectibleTraitItem
import javax.inject.Inject

internal class CollectibleTraitItemMapperImpl @Inject constructor() : CollectibleTraitItemMapper {

    override fun invoke(trait: CollectibleTrait): CollectibleTraitItem {
        return CollectibleTraitItem(
            title = trait.name.orEmpty(),
            description = trait.value.orEmpty()
        )
    }
}
