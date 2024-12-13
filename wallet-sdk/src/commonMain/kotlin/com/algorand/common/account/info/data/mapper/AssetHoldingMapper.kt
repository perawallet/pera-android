/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.account.info.data.mapper

import com.algorand.common.account.info.data.database.model.AssetHoldingEntity
import com.algorand.common.account.info.data.model.AssetHoldingResponse
import com.algorand.common.account.info.domain.model.AssetHolding

internal interface AssetHoldingMapper {
    operator fun invoke(response: AssetHoldingResponse): AssetHolding?
    operator fun invoke(entity: AssetHoldingEntity): AssetHolding
    operator fun invoke(entities: List<AssetHoldingEntity>): List<AssetHolding>
}
