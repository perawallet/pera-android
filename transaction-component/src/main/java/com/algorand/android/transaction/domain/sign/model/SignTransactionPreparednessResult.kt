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

package com.algorand.android.transaction.domain.sign.model

sealed interface SignTransactionPreparednessResult {

    data object ReadyToSign : SignTransactionPreparednessResult

    sealed interface NotReadyToSign : SignTransactionPreparednessResult {

        data class SignerNotFound(val signerNotFound: TransactionSigner.SignerNotFound) : NotReadyToSign

        data object BluetoothPermissionsNotGranted : NotReadyToSign

        data object BluetoothNotEnabled : NotReadyToSign

        data object LocationNotEnabled : NotReadyToSign
    }
}
