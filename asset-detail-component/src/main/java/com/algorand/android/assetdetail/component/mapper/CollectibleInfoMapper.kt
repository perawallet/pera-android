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

package com.algorand.android.assetdetail.component.mapper

import com.algorand.android.assetdetail.component.asset.data.model.collectible.CollectibleResponse
import com.algorand.android.assetdetail.component.asset.domain.model.detail.CollectibleDetail
import com.algorand.android.shared_db.assetdetail.model.CollectibleEntity
import com.algorand.android.shared_db.assetdetail.model.CollectibleTraitEntity

internal interface CollectibleInfoMapper {
    operator fun invoke(response: CollectibleResponse, explorerUrl: String?): CollectibleDetail.CollectibleInfo
    operator fun invoke(
        entity: CollectibleEntity,
        traitEntities: List<CollectibleTraitEntity>?,
        explorerUrl: String?
    ): CollectibleDetail.CollectibleInfo
}