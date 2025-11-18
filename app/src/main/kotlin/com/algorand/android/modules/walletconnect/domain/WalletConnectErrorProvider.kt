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

package com.algorand.android.modules.walletconnect.domain

import android.content.Context
import com.algorand.android.R
import com.algorand.android.modules.walletconnect.domain.mapper.WalletConnectErrorMapper
import com.algorand.android.modules.walletconnect.domain.model.WalletConnectError
import com.algorand.android.modules.walletconnect.domain.model.WalletConnectErrorReason
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletConnectErrorProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val errorMapper: WalletConnectErrorMapper
) {

    fun getUserRejectionError(): WalletConnectError {
        val errorMessage = context.getString(R.string.transaction_request_rejected_user)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.UserRejected)
    }

    fun getFailedGroupingTransactionsError(): WalletConnectError {
        val errorMessage = context.getString(R.string.missing_transactions_this_transaction)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.FailedGroupingTransactions)
    }

    fun getPendingTransactionError(): WalletConnectError {
        val errorMessage = context.getString(R.string.transaction_request_pending_user)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.PendingTransaction)
    }

    fun getMismatchingNodesError(): WalletConnectError {
        val errorMessage = context.getString(R.string.network_mismatch_error_this)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.MismatchingNodes)
    }

    fun getUnknownTransactionType(): WalletConnectError {
        val errorMessage = context.getString(R.string.unsupported_transaction_type_this)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.UnknownTransactionType)
    }

    fun getMultisigTransactionError(): WalletConnectError {
        val errorMessage = context.getString(R.string.multisig_required_this_transaction)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.MultisigTransaction)
    }

    fun getMaxTransactionLimitError(maxTxnLimit: Int): WalletConnectError {
        val errorMessage = context.getString(R.string.transaction_limit_this_transaction, maxTxnLimit)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.MaxTransactionLimit)
    }

    fun getMaxArbitraryDataLimitError(maxArbitraryDataLimit: Int): WalletConnectError {
        val errorMessage = context.getString(R.string.arbitrary_data_limit_this, maxArbitraryDataLimit)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.MaxArbitraryDataLimit)
    }

    fun getUnableToParseTransactionError(): WalletConnectError {
        val errorMessage = context.getString(R.string.invalid_request_format_pera)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.UnableToParseTransaction)
    }

    fun getUnableToParseArbitraryDataError(): WalletConnectError {
        val errorMessage = context.getString(R.string.invalid_arbitrary_data_request)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.UnableToParseArbitraryData)
    }

    fun getInvalidPublicKeyError(): WalletConnectError {
        val errorMessage = context.getString(R.string.invalid_public_key_transaction)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.InvalidPublicKey)
    }

    fun getInvalidAssetError(): WalletConnectError {
        val errorMessage = context.getString(R.string.invalid_asset_id_transaction)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.InvalidAsset)
    }

    fun getUnableToSignError(): WalletConnectError {
        val errorMessage = context.getString(R.string.invalid_input_pera_wallet)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.UnableToSign)
    }

    fun getAtomicNoNeedToSignError(): WalletConnectError {
        val errorMessage = context.getString(R.string.invalid_input_group_transaction)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.AtomicNoNeedToSign)
    }

    fun getMissingSignerError(): WalletConnectError {
        val errorMessage = context.getString(R.string.missing_signer_account_pera)
        return createWalletConnectError(errorMessage, WalletConnectErrorReason.MissingSigner)
    }

    private fun createWalletConnectError(message: String, errorReason: WalletConnectErrorReason): WalletConnectError {
        return errorMapper.mapToError(message, context.getString(errorReason.category.titleResId), errorReason)
    }
}
