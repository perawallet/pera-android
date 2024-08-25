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

package com.algorand.android.transactionui.core.model

import android.content.Context
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.designsystem.R
import com.algorand.android.designsystem.getXmlStyledString
import com.algorand.android.transaction.domain.model.SignedTransaction

sealed interface SignTransactionUiResult {

    data class TransactionSigned(val signedTransaction: SignedTransaction) : SignTransactionUiResult

    sealed interface Error : SignTransactionUiResult {

        val titleResId: Int

        sealed interface GlobalWarningError : Error {

            fun getMessage(context: Context): Pair<String, CharSequence> {
                val title = context.getString(titleResId)
                return when (this) {
                    is Defined -> Pair(title, context.getXmlStyledString(description))
                    is Api -> Pair(title, errorMessage)
                }
            }

            data class Defined(
                val description: AnnotatedString,
                override val titleResId: Int = R.string.error_default_title
            ) : GlobalWarningError

            data class Api(
                val errorMessage: String,
                override val titleResId: Int = R.string.error_default_title
            ) : GlobalWarningError
        }

        sealed interface SnackbarError : Error {

            val descriptionResId: Int?
            val buttonTextResId: Int?

            data class Retry(
                override val titleResId: Int,
                override val descriptionResId: Int?,
                override val buttonTextResId: Int = R.string.retry
            ) : SnackbarError
        }
    }

    data object Loading : SignTransactionUiResult

    data class LedgerWaitingForApproval(val ledgerName: String?) : SignTransactionUiResult

    data object LedgerScanFailed : SignTransactionUiResult

    data object BluetoothNotEnabled : SignTransactionUiResult

    data object BluetoothPermissionsAreNotGranted : SignTransactionUiResult

    data object LocationNotEnabled : SignTransactionUiResult

    data object LedgerDisconnected : SignTransactionUiResult

    data object LedgerOperationCancelled : SignTransactionUiResult
}
