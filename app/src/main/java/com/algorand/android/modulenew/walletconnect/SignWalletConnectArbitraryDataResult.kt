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

package com.algorand.android.modulenew.walletconnect

import android.content.Context
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.R
import com.algorand.android.module.drawable.getXmlStyledString
import com.algorand.android.modules.walletconnect.ui.model.WalletConnectSessionIdentifier

interface SignWalletConnectArbitraryDataResult {

    data class TransactionsSigned(
        val transactions: List<ByteArray?>,
        val sessionIdentifier: WalletConnectSessionIdentifier,
        val requestId: Long
    ) : SignWalletConnectArbitraryDataResult

    sealed interface Error : SignWalletConnectArbitraryDataResult {

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
}
