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

package com.algorand.backup.account.domain.model

import com.google.gson.annotations.SerializedName

internal data class HdSeedAddressData(
    @SerializedName("first_derived_address")
    val firstDerivedAddress: String
)

internal data class HdKeyAddressData(
    @SerializedName("seed_first_derived_address")
    val seedFirstDerivedAddress: String,
    @SerializedName("algo_address")
    val algoAddress: String,
    @SerializedName("public_key")
    val publicKey: String,
    @SerializedName("account")
    val account: Int,
    @SerializedName("change")
    val change: Int,
    @SerializedName("key_index")
    val keyIndex: Int,
    @SerializedName("derivation_type")
    val derivationType: Int,
    @SerializedName("custom_name")
    val customName: String?
)

internal data class Algo25AddressData(
    @SerializedName("algo_address")
    val algoAddress: String,
    @SerializedName("custom_name")
    val customName: String?
)

internal data class LedgerBleAddressData(
    @SerializedName("algo_address")
    val algoAddress: String,
    @SerializedName("device_mac_address")
    val deviceMacAddress: String,
    @SerializedName("bluetooth_name")
    val bluetoothName: String?,
    @SerializedName("index_in_ledger")
    val indexInLedger: Int,
    @SerializedName("custom_name")
    val customName: String?
)

internal data class NoAuthAddressData(
    @SerializedName("algo_address")
    val algoAddress: String,
    @SerializedName("custom_name")
    val customName: String?
)

internal data class JointAddressData(
    @SerializedName("algo_address")
    val algoAddress: String,
    @SerializedName("participant_addresses")
    val participantAddresses: List<String>,
    @SerializedName("threshold")
    val threshold: Int,
    @SerializedName("version")
    val version: Int,
    @SerializedName("custom_name")
    val customName: String?
)
