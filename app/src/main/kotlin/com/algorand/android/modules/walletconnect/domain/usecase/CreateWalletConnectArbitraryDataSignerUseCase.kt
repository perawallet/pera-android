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

package com.algorand.android.modules.walletconnect.domain.usecase

import com.algorand.android.models.WalletConnectArbitraryDataSigner
import com.algorand.android.models.WalletConnectArbitraryDataSigner.DisplayOnly
import com.algorand.android.models.WalletConnectArbitraryDataSigner.Signer
import com.algorand.android.models.WalletConnectArbitraryDataSigner.Unsignable
import com.algorand.android.modules.walletconnect.domain.WalletConnectErrorProvider
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import javax.inject.Inject

internal class CreateWalletConnectArbitraryDataSignerUseCase @Inject constructor(
    private val getLocalAccount: GetLocalAccount,
    private val errorProvider: WalletConnectErrorProvider,
) : CreateWalletConnectArbitraryDataSigner {

    override suspend fun invoke(signerAddress: String): WalletConnectArbitraryDataSigner {
        if (signerAddress.isBlank()) return DisplayOnly

        return when (getLocalAccount(signerAddress)) {
            is LocalAccount.Algo25, is LocalAccount.HdKey ->
                Signer(address = signerAddress, isLedger = false)
            is LocalAccount.LedgerBle -> Unsignable(errorProvider.getUnableToSignError())
            is LocalAccount.Joint -> Unsignable(errorProvider.getMultisigTransactionError())
            is LocalAccount.NoAuth, null -> Unsignable(errorProvider.getMissingSignerError())
        }
    }
}
