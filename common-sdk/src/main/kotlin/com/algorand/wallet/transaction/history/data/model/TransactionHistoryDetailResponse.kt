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

package com.algorand.wallet.transaction.history.data.model

import com.google.gson.annotations.SerializedName

internal data class TransactionHistoryDetailResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("tx_type")
    val txType: TransactionTypeResponse?,
    @SerializedName("sender")
    val sender: String?,
    @SerializedName("receiver")
    val receiver: String?,
    @SerializedName("confirmed_round")
    val confirmedRound: Long?,
    @SerializedName("round_time")
    val roundTime: Long?,
    @SerializedName("swap_group_detail")
    val swapGroupDetail: TransactionHistorySwapGroupDetailResponse?,
    @SerializedName("interpreted_meaning")
    val interpretedMeaning: TransactionHistoryInterpretedMeaning?,
    @SerializedName("fee")
    val fee: String?,
    @SerializedName("first_valid")
    val firstValid: Long?,
    @SerializedName("last_valid")
    val lastValid: Long?,
    @SerializedName("group")
    val groupId: String?,
    @SerializedName("note")
    val note: String?,
    @SerializedName("note_text")
    val noteText: String?,
    @SerializedName("inner_txns")
    val innerTxns: List<TransactionHistoryDetailResponse>?,
    @SerializedName("logs")
    val logs: List<String>?,
    @SerializedName("payment_transaction")
    val paymentTransaction: PaymentTransactionResponse?,
    @SerializedName("asset_transfer_transaction")
    val assetTransferTransaction: AssetTransactionResponse?,
    @SerializedName("application_transaction")
    val applicationTransaction: ApplicationTransactionResponse?,
    @SerializedName("asset_config_transaction")
    val assetConfigTransaction: AssetConfigTransactionResponse?,
    @SerializedName("asset_freeze_transaction")
    val assetFreezeTransaction: AssetFreezeTransactionResponse?,
    @SerializedName("keyreg_transaction")
    val keyRegTransaction: KeyRegTransactionResponse?
) {

    internal data class PaymentTransactionResponse(
        @SerializedName("receiver")
        val receiver: String?,
        @SerializedName("amount")
        val amount: String?,
        @SerializedName("close_remainder_to")
        val closeRemainderTo: String?
    )

    internal data class AssetTransactionResponse(
        @SerializedName("receiver")
        val receiver: String?,
        @SerializedName("amount")
        val amount: String?,
        @SerializedName("asset_id")
        val assetId: Long?,
        @SerializedName("close_to")
        val closeTo: String?,
        @SerializedName("clawback_address")
        val clawbackAddress: String?
    )

    internal data class ApplicationTransactionResponse(
        @SerializedName("application_id")
        val applicationId: Long?,
        @SerializedName("on_completion")
        val onCompletion: String?,
        @SerializedName("application_args")
        val applicationArgs: List<String?>?,
        @SerializedName("accounts")
        val accounts: List<String?>?,
        @SerializedName("foreign_apps")
        val foreignApps: List<Long?>?,
        @SerializedName("foreign_assets")
        val foreignAssets: List<Long?>?
    )

    internal data class AssetConfigTransactionResponse(
        @SerializedName("asset_id")
        val assetId: Long?
    )

    internal data class KeyRegTransactionResponse(
        @SerializedName("vote_participation_key")
        val voteParticipationKey: String?,
        @SerializedName("selection_participation_key")
        val selectionParticipationKey: String?,
        @SerializedName("state_proof_key")
        val stateProofKey: String?,
        @SerializedName("vote_first_valid")
        val voteFirstValid: Long?,
        @SerializedName("vote_last_valid")
        val voteLastValid: Long?,
        @SerializedName("vote_key_dilution")
        val voteKeyDilution: String?,
        @SerializedName("non_participation")
        val nonParticipation: Boolean?
    )

    internal data class AssetFreezeTransactionResponse(
        @SerializedName("address")
        val address: String?,
        @SerializedName("asset_id")
        val assetId: Long?,
        @SerializedName("new_freeze_status")
        val newFreezeStatus: Boolean?
    )
}
