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

package com.algorand.android.module.swap.ui.confirmswap.model

import android.content.Context
import com.algorand.android.designsystem.AnnotatedString
import com.algorand.android.designsystem.R
import com.algorand.android.designsystem.getXmlStyledString
import com.algorand.android.module.swap.component.domain.model.swapquotetxns.SwapQuoteTransaction

sealed interface SignSwapTransactionResult {

    data class TransactionsSigned(val swapQuoteTransactions: List<SwapQuoteTransaction>) : SignSwapTransactionResult

    sealed interface Error : SignSwapTransactionResult {

        val titleResId: Int

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
        ) : Error

        data class Api(
            val errorMessage: String,
            override val titleResId: Int = R.string.error_default_title
        ) : Error
    }

    data class LedgerWaitingForApproval(val ledgerName: String?) : SignSwapTransactionResult

    data object LedgerScanFailed : SignSwapTransactionResult

    data object BluetoothNotEnabled : SignSwapTransactionResult

    data object BluetoothPermissionsAreNotGranted : SignSwapTransactionResult

    data object LocationNotEnabled : SignSwapTransactionResult

    data object LedgerDisconnected : SignSwapTransactionResult

    data object LedgerOperationCancelled : SignSwapTransactionResult
}
