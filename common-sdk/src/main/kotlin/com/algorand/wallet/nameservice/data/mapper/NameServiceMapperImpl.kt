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

package com.algorand.wallet.nameservice.data.mapper

import com.algorand.wallet.nameservice.data.model.NameServicePayload
import com.algorand.wallet.nameservice.domain.model.NameService
import javax.inject.Inject

internal class NameServiceMapperImpl @Inject constructor(
    private val nameServiceSourceMapper: NameServiceSourceMapper
) : NameServiceMapper {

    override fun invoke(responses: List<NameServicePayload>): List<NameService> {
        return responses.mapNotNull {
            NameService(
                accountAddress = it.address ?: return@mapNotNull null,
                nameServiceName = it.nameResponse?.name,
                nameServiceSource = nameServiceSourceMapper(it.nameResponse?.source),
                nameServiceUri = it.nameResponse?.imageUri
            )
        }
    }
}
