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

package com.algorand.wallet.account.local.data.mapper.entity

import com.algorand.wallet.account.local.data.database.model.JointEntity
import com.algorand.wallet.account.local.data.database.model.JointParticipantEntity
import com.algorand.wallet.account.local.domain.model.LocalAccount
import javax.inject.Inject

internal class JointEntityMapperImpl @Inject constructor() : JointEntityMapper {

    override fun invoke(localAccount: LocalAccount.Joint): JointEntityMapperResult {
        val jointEntity = JointEntity(
            algoAddress = localAccount.algoAddress,
            threshold = localAccount.threshold,
            version = localAccount.version
        )

        val participantEntities = localAccount.participantAddresses.mapIndexed { index, address ->
            JointParticipantEntity(
                jointAddress = localAccount.algoAddress,
                participantIndex = index,
                participantAddress = address
            )
        }

        return JointEntityMapperResult(
            jointEntity = jointEntity,
            participantEntities = participantEntities
        )
    }
}
