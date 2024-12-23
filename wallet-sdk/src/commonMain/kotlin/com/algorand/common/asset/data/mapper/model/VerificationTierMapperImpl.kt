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

package com.algorand.common.asset.data.mapper.model

import com.algorand.common.asset.data.database.model.VerificationTierEntity
import com.algorand.common.asset.data.model.VerificationTierResponse
import com.algorand.common.asset.domain.model.VerificationTier

internal class VerificationTierMapperImpl : VerificationTierMapper {

    override fun invoke(response: VerificationTierResponse?): VerificationTier {
        return when (response) {
            VerificationTierResponse.VERIFIED -> VerificationTier.VERIFIED
            VerificationTierResponse.UNVERIFIED -> VerificationTier.UNVERIFIED
            VerificationTierResponse.TRUSTED -> VerificationTier.TRUSTED
            VerificationTierResponse.SUSPICIOUS -> VerificationTier.SUSPICIOUS
            VerificationTierResponse.UNKNOWN, null -> VerificationTier.UNKNOWN
        }
    }

    override fun invoke(entity: VerificationTierEntity): VerificationTier {
        return when (entity) {
            VerificationTierEntity.VERIFIED -> VerificationTier.VERIFIED
            VerificationTierEntity.UNVERIFIED -> VerificationTier.UNVERIFIED
            VerificationTierEntity.TRUSTED -> VerificationTier.TRUSTED
            VerificationTierEntity.SUSPICIOUS -> VerificationTier.SUSPICIOUS
            VerificationTierEntity.UNKNOWN -> VerificationTier.UNKNOWN
        }
    }
}
