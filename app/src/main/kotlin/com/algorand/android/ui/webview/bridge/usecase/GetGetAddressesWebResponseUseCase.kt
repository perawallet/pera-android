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

package com.algorand.android.ui.webview.bridge.usecase

import com.algorand.android.ui.webview.bridge.model.AddressWebResponse
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountsDetails
import com.algorand.wallet.account.info.domain.usecase.GetAccountAlgoBalance
import java.math.BigInteger
import javax.inject.Inject

internal class GetGetAddressesWebResponseUseCase @Inject constructor(
    private val getAccountsDetails: GetAccountsDetails,
    private val getAccountAlgoBalance: GetAccountAlgoBalance
) : GetGetAddressesWebResponse {

    override suspend fun invoke(): List<AddressWebResponse> {
        return getAccountsDetails()
            .filter { it.accountType?.canSignTransaction() == true }
            .mapNotNull { mapWebResponse(it) }
            .sortedByDescending { (_, algoBalance) -> algoBalance }
            .map { (response, _) -> response }
    }

    private suspend fun mapWebResponse(accountDetail: AccountDetail): Pair<AddressWebResponse, BigInteger?>? {
        val type = accountDetail.accountType ?: return null
        val response = AddressWebResponse(
            name = accountDetail.customAccountInfo?.customName,
            address = accountDetail.address,
            type = getAccountTypeString(type)
        )
        val algoBalance = getAccountAlgoBalance(accountDetail.address)
        return response to algoBalance
    }

    private fun getAccountTypeString(type: AccountType): String {
        return when (type) {
            AccountType.Algo25 -> "Algo25"
            AccountType.HdKey -> "HdKey"
            AccountType.Joint -> "Joint"
            AccountType.LedgerBle -> "LedgerBle"
            AccountType.NoAuth -> "NoAuth"
            AccountType.Rekeyed -> "Rekeyed"
            AccountType.RekeyedAuth -> "RekeyedAuth"
        }
    }
}
