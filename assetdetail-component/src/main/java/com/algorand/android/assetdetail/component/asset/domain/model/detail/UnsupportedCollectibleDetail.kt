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

package com.algorand.android.assetdetail.component.asset.domain.model.detail

import com.algorand.android.assetdetail.component.asset.domain.model.VerificationTier
import com.algorand.android.assetdetail.component.collectible.domain.model.BaseCollectibleMedia

data class UnsupportedCollectibleDetail(
    override val id: Long,
    override val collectibleInfo: CollectibleDetail.CollectibleInfo,
    override val collectibleMedias: List<BaseCollectibleMedia>,
    override val assetInfo: Asset.AssetInfo?,
    override val verificationTier: VerificationTier
) : CollectibleDetail
