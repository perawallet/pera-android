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

package com.algorand.android.modules.perawebview

import com.algorand.android.modules.peraserializer.PeraSerializer
import com.algorand.android.usecase.GetLocalAccountsUseCase
import com.google.crypto.tink.subtle.Base64
import javax.inject.Inject

class GetAuthorizedAddressesWebMessagesUseCase @Inject constructor(
    private val localAccountsUseCase: GetLocalAccountsUseCase,
    private val peraSerializer: PeraSerializer,
    private val peraWebMessageBuilder: PeraWebMessageBuilder
) : GetAuthorizedAddressesWebMessage {

    override suspend fun invoke(): String {
        val addressNameMap = getAddressNameMap()
        val messagePayload = getMessagePayload(addressNameMap)
        return peraWebMessageBuilder.buildMessage(PeraWebMessageAction.GET_AUTHORIZED_ADDRESSES, messagePayload)
    }

    private fun getAddressNameMap(): List<Map<String, String>> {
        val localAccounts = localAccountsUseCase.getLocalAccountsThatCanSignTransaction()
        return localAccounts.map {
            mapOf(it.address to it.name)
        }
    }

    private fun getMessagePayload(addressNameMap: List<Map<String, String>>): String {
        return Base64.encode(peraSerializer.toJson(addressNameMap).toByteArray())
    }
}
