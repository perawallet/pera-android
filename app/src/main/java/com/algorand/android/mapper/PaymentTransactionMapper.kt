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

package com.algorand.android.mapper

import com.algorand.android.account.localaccount.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.android.assetdetail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.models.BasePaymentTransaction
import com.algorand.android.models.BaseWalletConnectTransaction
import com.algorand.android.models.WCAlgoTransactionRequest
import com.algorand.android.models.WalletConnectAssetInformation
import com.algorand.android.models.WalletConnectPeerMeta
import com.algorand.android.models.WalletConnectTransactionRequest
import com.algorand.android.models.WalletConnectTransactionSigner
import com.algorand.android.modules.walletconnect.domain.WalletConnectErrorProvider
import com.algorand.android.modules.walletconnect.domain.usecase.CreateWalletConnectAccount
import com.algorand.android.modules.walletconnect.domain.usecase.GetWalletConnectTransactionAuthAddress
import com.algorand.android.utils.extensions.mapNotBlank
import com.algorand.android.utils.multiplyOrZero
import java.math.BigInteger
import javax.inject.Inject

// TODO: 19.01.2022 Mappers shouldn't inject use case
@SuppressWarnings("ReturnCount")
class PaymentTransactionMapper @Inject constructor(
    private val errorProvider: WalletConnectErrorProvider,
    private val walletConnectAssetInformationMapper: WalletConnectAssetInformationMapper,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val createWalletConnectAccount: CreateWalletConnectAccount,
    private val getWalletConnectTransactionAuthAddress: GetWalletConnectTransactionAuthAddress,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress
) : BaseWalletConnectTransactionMapper() {

    override suspend fun createTransaction(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseWalletConnectTransaction? {
        return with(transactionRequest) {
            when {
                !rekeyAddress.isNullOrBlank() && !closeToAddress.isNullOrBlank() -> {
                    createPaymentTransactionWithCloseToAndRekey(peerMeta, transactionRequest, rawTxn)
                }
                !rekeyAddress.isNullOrBlank() -> {
                    createPaymentTransactionWithRekey(peerMeta, transactionRequest, rawTxn)
                }
                !closeToAddress.isNullOrBlank() -> {
                    createPaymentTransactionWithClose(peerMeta, transactionRequest, rawTxn)
                }
                else -> createPaymentTransaction(peerMeta, transactionRequest, rawTxn)
            }
        }
    }

    private suspend fun createPaymentTransactionWithCloseToAndRekey(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTransaction: WCAlgoTransactionRequest
    ): BasePaymentTransaction.PaymentTransactionWithRekeyAndClose? {
        return with(transactionRequest) {
            val senderWCAddress = createWalletConnectAddress(senderAddress) ?: return null
            val amount = amount ?: BigInteger.ZERO
            val ownedAsset = senderWCAddress.decodedAddress?.mapNotBlank { safeAddress ->
                getAccountBaseOwnedAssetData(safeAddress, ALGO_ASSET_ID)
            }
            val walletConnectAssetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            val signer = WalletConnectTransactionSigner.create(rawTransaction, senderWCAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BasePaymentTransaction.PaymentTransactionWithRekeyAndClose(
                rawTransactionPayload = rawTransaction,
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                note = decodedNote,
                amount = amount,
                senderAddress = senderWCAddress,
                receiverAddress = createWalletConnectAddress(receiverAddress) ?: return null,
                peerMeta = peerMeta,
                closeToAddress = createWalletConnectAddress(closeToAddress) ?: return null,
                rekeyToAddress = createWalletConnectAddress(rekeyAddress) ?: return null,
                signer = signer,
                authAddress = getWalletConnectTransactionAuthAddress(senderWCAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWCAddress.decodedAddress),
                assetInformation = walletConnectAssetInformation,
                groupId = groupId,
                warningCount = 2.takeIf { isLocalAccountSigner }
            )
        }
    }

    private suspend fun createPaymentTransactionWithRekey(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTransaction: WCAlgoTransactionRequest
    ): BasePaymentTransaction.PaymentTransactionWithRekey? {
        return with(transactionRequest) {
            val senderWCAddress = createWalletConnectAddress(senderAddress) ?: return null
            val amount = amount ?: BigInteger.ZERO
            val ownedAsset = senderWCAddress.decodedAddress?.mapNotBlank { safeAddress ->
                getAccountBaseOwnedAssetData(safeAddress, ALGO_ASSET_ID)
            }
            val walletConnectAssetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            val signer = WalletConnectTransactionSigner.create(rawTransaction, senderWCAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BasePaymentTransaction.PaymentTransactionWithRekey(
                rawTransactionPayload = rawTransaction,
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                note = decodedNote,
                amount = amount,
                senderAddress = senderWCAddress,
                receiverAddress = createWalletConnectAddress(receiverAddress) ?: return null,
                peerMeta = peerMeta,
                rekeyToAddress = createWalletConnectAddress(rekeyAddress) ?: return null,
                signer = signer,
                authAddress = getWalletConnectTransactionAuthAddress(senderWCAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWCAddress.decodedAddress),
                assetInformation = walletConnectAssetInformation,
                groupId = groupId,
                warningCount = 1.takeIf { isLocalAccountSigner }
            )
        }
    }

    private suspend fun createPaymentTransactionWithClose(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTransaction: WCAlgoTransactionRequest
    ): BasePaymentTransaction.PaymentTransactionWithClose? {
        return with(transactionRequest) {
            val senderWCAddress = createWalletConnectAddress(senderAddress) ?: return null
            val amount = amount ?: BigInteger.ZERO
            val ownedAsset = senderWCAddress.decodedAddress?.mapNotBlank { safeAddress ->
                getAccountBaseOwnedAssetData(safeAddress, ALGO_ASSET_ID)
            }
            val walletConnectAssetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            val signer = WalletConnectTransactionSigner.create(rawTransaction, senderWCAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BasePaymentTransaction.PaymentTransactionWithClose(
                rawTransactionPayload = rawTransaction,
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                note = decodedNote,
                amount = amount,
                senderAddress = senderWCAddress,
                receiverAddress = createWalletConnectAddress(receiverAddress) ?: return null,
                peerMeta = peerMeta,
                closeToAddress = createWalletConnectAddress(closeToAddress) ?: return null,
                signer = signer,
                authAddress = getWalletConnectTransactionAuthAddress(senderWCAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWCAddress.decodedAddress),
                assetInformation = walletConnectAssetInformation,
                groupId = groupId,
                warningCount = 1.takeIf { isLocalAccountSigner }
            )
        }
    }

    private suspend fun createPaymentTransaction(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTransaction: WCAlgoTransactionRequest
    ): BasePaymentTransaction.PaymentTransaction? {
        return with(transactionRequest) {
            val senderWCAddress = createWalletConnectAddress(senderAddress) ?: return null
            val receiverWCAddress = createWalletConnectAddress(receiverAddress)
            val amount = amount ?: BigInteger.ZERO
            val ownedAsset = senderWCAddress.decodedAddress?.mapNotBlank { safeAddress ->
                getAccountBaseOwnedAssetData(safeAddress, ALGO_ASSET_ID)
            }
            val walletConnectAssetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            val signer = WalletConnectTransactionSigner.create(rawTransaction, senderWCAddress, errorProvider)
            BasePaymentTransaction.PaymentTransaction(
                rawTransactionPayload = rawTransaction,
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                note = decodedNote,
                amount = amount,
                senderAddress = senderWCAddress,
                receiverAddress = createWalletConnectAddress(receiverAddress) ?: return null,
                peerMeta = peerMeta,
                signer = signer,
                authAddress = getWalletConnectTransactionAuthAddress(senderWCAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWCAddress.decodedAddress),
                toAccount = createWalletConnectAccount(receiverWCAddress?.decodedAddress),
                assetInformation = walletConnectAssetInformation,
                groupId = groupId
            )
        }
    }

    private fun createWalletConnectAssetInformation(
        ownedAsset: BaseAccountAssetData.BaseOwnedAssetData?,
        amount: BigInteger
    ): WalletConnectAssetInformation? {
        if (ownedAsset == null) return null
        val safeAmount = amount.toBigDecimal().movePointLeft(ownedAsset.decimals).multiplyOrZero(ownedAsset.usdValue)
        return walletConnectAssetInformationMapper.mapToWalletConnectAssetInformation(ownedAsset, safeAmount)
    }
}
