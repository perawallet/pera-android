/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.algosdk.transaction.model

interface Transaction {
    val signerAddress: String
    val value: ByteArray

    data class AssetTransaction(
        override val signerAddress: String,
        override val value: ByteArray
    ) : Transaction

    data class AlgoTransaction(
        override val signerAddress: String,
        override val value: ByteArray
    ) : Transaction

    data class RekeyTransaction(
        override val signerAddress: String,
        override val value: ByteArray
    ) : Transaction

    data class AddAssetTransaction(
        override val signerAddress: String,
        override val value: ByteArray
    ) : Transaction

    data class RemoveAssetTransaction(
        override val signerAddress: String,
        override val value: ByteArray
    ) : Transaction

    data class SendAndRemoveAssetTransaction(
        override val signerAddress: String,
        override val value: ByteArray
    ) : Transaction
}
