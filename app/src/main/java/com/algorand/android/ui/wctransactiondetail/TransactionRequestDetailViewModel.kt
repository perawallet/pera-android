/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.ui.wctransactiondetail

import androidx.lifecycle.ViewModel
import com.algorand.android.models.BaseWalletConnectTransaction
import com.algorand.android.models.TransactionRequestAmountInfo
import com.algorand.android.models.TransactionRequestExtrasInfo
import com.algorand.android.models.TransactionRequestNoteInfo
import com.algorand.android.models.TransactionRequestOfflineKeyRegInfo
import com.algorand.android.models.TransactionRequestOnlineKeyRegInfo
import com.algorand.android.models.TransactionRequestSenderInfo
import com.algorand.android.models.TransactionRequestTransactionInfo
import com.algorand.android.models.decider.WalletConnectTransactionDetailUiDecider
import com.algorand.android.module.node.domain.usecase.GetActiveNodeNetworkSlug
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionRequestDetailViewModel @Inject constructor(
    private val walletConnectTransactionDetailUiDecider: WalletConnectTransactionDetailUiDecider,
    private val getActiveNodeNetworkSlug: GetActiveNodeNetworkSlug
) : ViewModel() {

    fun getNetworkSlug(): String {
        return getActiveNodeNetworkSlug()
    }

    fun buildTransactionRequestTransactionInfo(txn: BaseWalletConnectTransaction): TransactionRequestTransactionInfo? {
        return walletConnectTransactionDetailUiDecider.buildTransactionRequestTransactionInfo(txn)
    }

    fun buildTransactionRequestSenderInfo(txn: BaseWalletConnectTransaction): TransactionRequestSenderInfo? {
        return walletConnectTransactionDetailUiDecider.buildTransactionRequestSenderInfo(txn)
    }

    fun buildTransactionRequestNoteInfo(txn: BaseWalletConnectTransaction): TransactionRequestNoteInfo? {
        return walletConnectTransactionDetailUiDecider.buildTransactionRequestNoteInfo(txn)
    }

    fun buildTransactionRequestExtrasInfo(txn: BaseWalletConnectTransaction): TransactionRequestExtrasInfo {
        return walletConnectTransactionDetailUiDecider.buildTransactionRequestExtrasInfo(txn)
    }

    fun buildTransactionRequestAmountInfo(txn: BaseWalletConnectTransaction): TransactionRequestAmountInfo {
        return walletConnectTransactionDetailUiDecider.buildTransactionRequestAmountInfo(txn)
    }

    fun buildTransactionRequestOnlineKeyRegInfo(
        txn: BaseWalletConnectTransaction
    ): TransactionRequestOnlineKeyRegInfo? {
        return walletConnectTransactionDetailUiDecider.buildTransactionRequestOnlineKeyRegInfo(txn)
    }

    fun buildTransactionRequestOfflineKeyRegInfo(
        txn: BaseWalletConnectTransaction
    ): TransactionRequestOfflineKeyRegInfo? {
        return walletConnectTransactionDetailUiDecider.buildTransactionRequestOfflineKeyRegInfo(txn)
    }
}
