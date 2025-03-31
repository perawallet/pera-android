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
import com.algorand.wallet.account.info.domain.usecase.GetAccountAlgoBalance
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountsDetails
import com.google.crypto.tink.subtle.Base64
import javax.inject.Inject

class GetAuthorizedAddressesWebMessagesUseCase @Inject constructor(
    private val getAccountsDetails: GetAccountsDetails,
    private val peraSerializer: PeraSerializer,
    private val peraWebMessageBuilder: PeraWebMessageBuilder,
    private val getAccountAlgoBalance: GetAccountAlgoBalance
) : GetAuthorizedAddressesWebMessage {

    override suspend fun invoke(): String {
        val addressNameMap = getAddressNameMap()
        val messagePayload = getMessagePayload(addressNameMap)
        return peraWebMessageBuilder.buildMessage(PeraWebMessageAction.GET_AUTHORIZED_ADDRESSES, messagePayload)
    }

    private suspend fun getAddressNameMap(): List<Map<String, String>> {
        val localAccounts = getAccountsDetails().filter { it.accountType?.canSignTransaction() == true }
        val sortedAddressAlgoBalanceMap = localAccounts.map {
            it to getAccountAlgoBalance(it.address)
        }.sortedByDescending { (_, algoBalance) -> algoBalance }
        return sortedAddressAlgoBalanceMap.map { (account, _) ->
            mapOf(account.address to account.customAccountInfo?.customName.orEmpty())
        }
    }

    private fun getMessagePayload(addressNameMap: List<Map<String, String>>): String {
        return Base64.encode(peraSerializer.toJson(addressNameMap).toByteArray())
    }
}
