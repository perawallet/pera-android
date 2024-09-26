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

package com.algorand.android.models

import com.algorand.android.module.asset.detail.component.asset.domain.model.VerificationTier
import com.algorand.android.utils.ALGO_DECIMALS
import java.math.BigInteger
import kotlinx.parcelize.Parcelize

sealed class BasePaymentTransaction : BaseWalletConnectTransaction() {

    protected abstract val amount: BigInteger
    abstract val receiverAddress: WalletConnectAddress

    override val assetDecimal: Int = ALGO_DECIMALS

    override val transactionAmount: BigInteger
        get() = amount

    open val verificationTier: VerificationTier
        get() = VerificationTier.TRUSTED

    @Parcelize
    data class PaymentTransaction(
        override val rawTransactionPayload: WCAlgoTransactionRequest,
        override val walletConnectTransactionParams: WalletConnectTransactionParams,
        override val note: String?,
        override val amount: BigInteger,
        override val senderAddress: WalletConnectAddress,
        override val receiverAddress: WalletConnectAddress,
        override val peerMeta: WalletConnectPeerMeta,
        override val signer: WalletConnectTransactionSigner,
        override val authAddress: String?,
        override val assetInformation: WalletConnectAssetInformation?,
        override val fromAccount: WalletConnectAccount?,
        override val toAccount: WalletConnectAccount?,
        override val groupId: String?
    ) : BasePaymentTransaction() {

        override fun getAllAddressPublicKeysTxnIncludes(): List<WalletConnectAddress> {
            return listOf(senderAddress, receiverAddress) + signerAddressList.orEmpty()
        }

        override val fee: Long
            get() = walletConnectTransactionParams.fee
    }

    @Parcelize
    data class PaymentTransactionWithClose(
        override val rawTransactionPayload: WCAlgoTransactionRequest,
        override val walletConnectTransactionParams: WalletConnectTransactionParams,
        override val note: String?,
        override val amount: BigInteger,
        override val senderAddress: WalletConnectAddress,
        override val receiverAddress: WalletConnectAddress,
        override val peerMeta: WalletConnectPeerMeta,
        override val signer: WalletConnectTransactionSigner,
        override val authAddress: String?,
        override val assetInformation: WalletConnectAssetInformation?,
        override val fromAccount: WalletConnectAccount?,
        override val groupId: String?,
        override val warningCount: Int?,
        val closeToAddress: WalletConnectAddress
    ) : BasePaymentTransaction() {

        override val fee: Long
            get() = walletConnectTransactionParams.fee

        override fun getCloseToAccountAddress(): WalletConnectAddress = closeToAddress

        override fun getAllAddressPublicKeysTxnIncludes(): List<WalletConnectAddress> {
            return listOf(senderAddress, receiverAddress, closeToAddress) + signerAddressList.orEmpty()
        }
    }

    @Parcelize
    data class PaymentTransactionWithRekey(
        override val rawTransactionPayload: WCAlgoTransactionRequest,
        override val walletConnectTransactionParams: WalletConnectTransactionParams,
        override val note: String?,
        override val amount: BigInteger,
        override val senderAddress: WalletConnectAddress,
        override val receiverAddress: WalletConnectAddress,
        override val peerMeta: WalletConnectPeerMeta,
        override val signer: WalletConnectTransactionSigner,
        override val authAddress: String?,
        override val assetInformation: WalletConnectAssetInformation?,
        override val fromAccount: WalletConnectAccount?,
        override val groupId: String?,
        override val warningCount: Int?,
        val rekeyToAddress: WalletConnectAddress
    ) : BasePaymentTransaction() {

        override val fee: Long
            get() = walletConnectTransactionParams.fee

        override fun getRekeyToAccountAddress(): WalletConnectAddress = rekeyToAddress

        override fun getAllAddressPublicKeysTxnIncludes(): List<WalletConnectAddress> {
            return listOf(senderAddress, receiverAddress, rekeyToAddress) + signerAddressList.orEmpty()
        }
    }

    @Parcelize
    data class PaymentTransactionWithRekeyAndClose(
        override val rawTransactionPayload: WCAlgoTransactionRequest,
        override val walletConnectTransactionParams: WalletConnectTransactionParams,
        override val note: String?,
        override val amount: BigInteger,
        override val senderAddress: WalletConnectAddress,
        override val receiverAddress: WalletConnectAddress,
        override val peerMeta: WalletConnectPeerMeta,
        override val signer: WalletConnectTransactionSigner,
        override val authAddress: String?,
        override val assetInformation: WalletConnectAssetInformation?,
        override val fromAccount: WalletConnectAccount?,
        override val groupId: String?,
        override val warningCount: Int?,
        val closeToAddress: WalletConnectAddress,
        val rekeyToAddress: WalletConnectAddress
    ) : BasePaymentTransaction() {

        override val fee: Long
            get() = walletConnectTransactionParams.fee

        override fun getRekeyToAccountAddress(): WalletConnectAddress = rekeyToAddress

        override fun getCloseToAccountAddress(): WalletConnectAddress = closeToAddress

        override fun getAllAddressPublicKeysTxnIncludes(): List<WalletConnectAddress> {
            return listOf(senderAddress, receiverAddress, closeToAddress, rekeyToAddress) + signerAddressList.orEmpty()
        }
    }
}
