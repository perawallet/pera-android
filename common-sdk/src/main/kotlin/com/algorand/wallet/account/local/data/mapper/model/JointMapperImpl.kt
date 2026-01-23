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

package com.algorand.wallet.account.local.data.mapper.model

import com.algorand.wallet.account.local.data.database.model.JointWithParticipants
import com.algorand.wallet.account.local.domain.model.LocalAccount
import javax.inject.Inject

internal class JointMapperImpl @Inject constructor() : JointMapper {

    override fun invoke(jointWithParticipants: JointWithParticipants): LocalAccount.Joint {
        val sortedParticipants = jointWithParticipants.participants
            .sortedBy { it.participantIndex }
            .map { it.participantAddress }

        return LocalAccount.Joint(
            algoAddress = jointWithParticipants.joint.algoAddress,
            participantAddresses = sortedParticipants,
            threshold = jointWithParticipants.joint.threshold,
            version = jointWithParticipants.joint.version
        )
    }
}
