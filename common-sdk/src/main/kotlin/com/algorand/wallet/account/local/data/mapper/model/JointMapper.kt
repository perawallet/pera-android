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

import com.algorand.wallet.account.local.data.database.model.JointEntity
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

internal interface JointMapper {
    operator fun invoke(entity: JointEntity): LocalAccount.Joint
}

internal class JointMapperImpl @Inject constructor(
    private val gson: Gson
) : JointMapper {

    override fun invoke(entity: JointEntity): LocalAccount.Joint {
        val type = object : TypeToken<List<String>>() {}.type
        val participantAddresses: List<String> = runCatching {
            gson.fromJson<List<String>>(entity.participantAddresses, type)
        }.getOrNull() ?: emptyList()
        return LocalAccount.Joint(
            algoAddress = entity.algoAddress,
            participantAddresses = participantAddresses,
            threshold = entity.threshold,
            version = entity.version
        )
    }
}
