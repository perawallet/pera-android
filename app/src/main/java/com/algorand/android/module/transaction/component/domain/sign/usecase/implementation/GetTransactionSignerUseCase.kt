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

package com.algorand.android.module.transaction.component.domain.sign.usecase.implementation

import com.algorand.android.module.account.local.domain.usecase.GetLedgerBleAccount
import com.algorand.android.module.account.local.domain.usecase.GetSecretKey
import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.account.core.component.detail.domain.model.AccountDetail
import com.algorand.android.module.account.core.component.detail.domain.model.AccountRegistrationType
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Algo25
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.LedgerBle
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.NoAuth
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.Rekeyed
import com.algorand.android.module.account.core.component.detail.domain.model.AccountType.RekeyedAuth
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.transaction.component.domain.sign.model.TransactionSigner
import com.algorand.android.module.transaction.component.domain.sign.model.TransactionSigner.SignerNotFound
import com.algorand.android.module.transaction.component.domain.sign.model.TransactionSigner.SignerNotFound.AccountNotFound
import com.algorand.android.module.transaction.component.domain.sign.model.TransactionSigner.SignerNotFound.AuthAddressNotFound
import com.algorand.android.module.transaction.component.domain.sign.usecase.GetTransactionSigner
import javax.inject.Inject

internal class GetTransactionSignerUseCase @Inject constructor(
    private val getAccountDetail: GetAccountDetail,
    private val getLedgerBleAccount: GetLedgerBleAccount,
    private val getSecretKey: GetSecretKey,
    private val getAccountInformation: GetAccountInformation
) : GetTransactionSigner {

    override suspend fun invoke(address: String): TransactionSigner {
        val accountDetail = getAccountDetail(address)
        return when (accountDetail.accountType) {
            Algo25 -> getAlgo25Signer(address)
            LedgerBle -> getLedgerSigner(address)
            NoAuth -> SignerNotFound.NoAuth(address)
            Rekeyed -> SignerNotFound.NoAuth(address)
            RekeyedAuth -> getRekeyedAuthSigner(accountDetail, address)
            null -> AccountNotFound(address)
        }
    }

    private suspend fun getAlgo25Signer(address: String): TransactionSigner {
        val secretKey = getSecretKey(address) ?: return AccountNotFound(address)
        return TransactionSigner.Algo25(address, secretKey)
    }

    private suspend fun getRekeyedAuthSigner(rekeyedAccountDetail: AccountDetail, address: String): TransactionSigner {
        val rekeyedAccountInformation = getAccountInformation(rekeyedAccountDetail.address)
            ?: return AccountNotFound(address)
        val authAddress = rekeyedAccountInformation.rekeyAdminAddress ?: return AuthAddressNotFound(address)
        val authAccountDetail = getAccountDetail(authAddress)
        return when (authAccountDetail.accountRegistrationType) {
            AccountRegistrationType.Algo25 -> getAlgo25Signer(authAddress)
            AccountRegistrationType.LedgerBle -> getLedgerSigner(authAddress)
            AccountRegistrationType.NoAuth -> SignerNotFound.AuthAccountIsNoAuth(address)
            null -> AccountNotFound(authAddress)
        }
    }

    private suspend fun getLedgerSigner(address: String): TransactionSigner {
        return getLedgerBleAccount(address)?.run {
            TransactionSigner.LedgerBle(address, deviceMacAddress, indexInLedger)
        } ?: AccountNotFound(address)
    }
}
