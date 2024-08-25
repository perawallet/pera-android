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

package com.algorand.android.modules.walletconnect.domain.usecase

import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.models.WalletConnectTransactionSigner
import javax.inject.Inject

internal class GetWalletConnectTransactionAuthAddressUseCase @Inject constructor(
    private val getAccountInformation: GetAccountInformation
) : GetWalletConnectTransactionAuthAddress {

    override suspend fun invoke(senderAddress: String?, signer: WalletConnectTransactionSigner): String? {
        return when (signer) {
            is WalletConnectTransactionSigner.Rekeyed -> signer.address.decodedAddress
            is WalletConnectTransactionSigner.Sender -> {
                if (senderAddress == null) return null
                getAccountInformation(senderAddress)?.run {
                    if (isRekeyed()) rekeyAdminAddress else address
                }
            }
            else -> null
        }
    }
}
