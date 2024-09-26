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
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.models.BaseAssetTransferTransaction
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
import java.math.BigInteger.ZERO
import javax.inject.Inject

// TODO: 19.01.2022 Mappers shouldn't inject use case
@SuppressWarnings("ReturnCount")
class AssetTransferTransactionMapper @Inject constructor(
    private val errorProvider: WalletConnectErrorProvider,
    private val walletConnectAssetInformationMapper: WalletConnectAssetInformationMapper,
    private val getWalletConnectTransactionAuthAddress: GetWalletConnectTransactionAuthAddress,
    private val createWalletConnectAccount: CreateWalletConnectAccount,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData
) : BaseWalletConnectTransactionMapper() {

    override suspend fun createTransaction(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseWalletConnectTransaction? {
        return with(transactionRequest) {
            when {
                !rekeyAddress.isNullOrBlank() && !assetCloseToAddress.isNullOrBlank() -> {
                    createAssetTransferTransactionWithRekeyAndClose(peerMeta, transactionRequest, rawTxn)
                }
                assetCloseToAddress != null -> {
                    createAssetTransferTransactionWithClose(peerMeta, transactionRequest, rawTxn)
                }
                (assetAmount == null || assetAmount == ZERO) && senderAddress == assetReceiverAddress -> {
                    createAssetOptInTransaction(peerMeta, transactionRequest, rawTxn)
                }
                rekeyAddress != null -> {
                    createAssetTransferTransactionWithRekey(peerMeta, transactionRequest, rawTxn)
                }
                else -> {
                    createAssetTransferTransaction(peerMeta, transactionRequest, rawTxn)
                }
            }
        }
    }

    private suspend fun createAssetTransferTransactionWithClose(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTransaction: WCAlgoTransactionRequest
    ): BaseAssetTransferTransaction.AssetTransferTransactionWithClose? {
        return with(transactionRequest) {
            val senderWCAddress = createWalletConnectAddress(senderAddress) ?: return null
            val assetId = assetId ?: return null
            val amount = assetAmount ?: ZERO
            val ownedAsset = senderWCAddress.decodedAddress?.mapNotBlank {
                getAccountBaseOwnedAssetData(it, assetId)
            }
            val assetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            val signer = WalletConnectTransactionSigner.create(rawTransaction, senderWCAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BaseAssetTransferTransaction.AssetTransferTransactionWithClose(
                rawTransactionPayload = rawTransaction,
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                note = decodedNote,
                assetReceiverAddress = createWalletConnectAddress(assetReceiverAddress) ?: return null,
                senderAddress = senderWCAddress,
                assetId = assetId,
                peerMeta = peerMeta,
                assetCloseToAddress = createWalletConnectAddress(assetCloseToAddress) ?: return null,
                signer = signer,
                assetAmount = amount,
                authAddress = getWalletConnectTransactionAuthAddress(senderWCAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWCAddress.decodedAddress),
                assetInformation = assetInformation,
                groupId = groupId,
                warningCount = 1.takeIf { isLocalAccountSigner }
            )
        }
    }

    private suspend fun createAssetTransferTransactionWithRekey(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTransaction: WCAlgoTransactionRequest
    ): BaseAssetTransferTransaction.AssetTransferTransactionWithRekey? {
        return with(transactionRequest) {
            val senderWCAddress = createWalletConnectAddress(senderAddress) ?: return null
            val assetId = assetId ?: return null
            val amount = assetAmount ?: ZERO
            val ownedAsset = senderWCAddress.decodedAddress?.mapNotBlank {
                getAccountBaseOwnedAssetData(it, assetId)
            }
            val assetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            val signer = WalletConnectTransactionSigner.create(rawTransaction, senderWCAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BaseAssetTransferTransaction.AssetTransferTransactionWithRekey(
                rawTransactionPayload = rawTransaction,
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                note = decodedNote,
                assetReceiverAddress = createWalletConnectAddress(assetReceiverAddress) ?: return null,
                senderAddress = senderWCAddress,
                assetId = assetId,
                peerMeta = peerMeta,
                rekeyAddress = createWalletConnectAddress(rekeyAddress) ?: return null,
                signer = signer,
                assetAmount = amount,
                authAddress = getWalletConnectTransactionAuthAddress(senderWCAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWCAddress.decodedAddress),
                assetInformation = assetInformation,
                groupId = groupId,
                warningCount = 1.takeIf { isLocalAccountSigner }
            )
        }
    }

    private suspend fun createAssetTransferTransactionWithRekeyAndClose(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTransaction: WCAlgoTransactionRequest
    ): BaseAssetTransferTransaction.AssetTransferTransactionWithRekeyAndClose? {
        return with(transactionRequest) {
            val senderWCAddress = createWalletConnectAddress(senderAddress) ?: return null
            val assetId = assetId ?: return null
            val amount = assetAmount ?: ZERO
            val ownedAsset = senderWCAddress.decodedAddress?.mapNotBlank {
                getAccountBaseOwnedAssetData(it, assetId)
            }
            val assetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            val signer = WalletConnectTransactionSigner.create(rawTransaction, senderWCAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BaseAssetTransferTransaction.AssetTransferTransactionWithRekeyAndClose(
                rawTransactionPayload = rawTransaction,
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                note = decodedNote,
                assetReceiverAddress = createWalletConnectAddress(assetReceiverAddress) ?: return null,
                senderAddress = senderWCAddress,
                assetId = assetId,
                peerMeta = peerMeta,
                rekeyAddress = createWalletConnectAddress(rekeyAddress) ?: return null,
                signer = signer,
                assetAmount = amount,
                authAddress = getWalletConnectTransactionAuthAddress(senderWCAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWCAddress.decodedAddress),
                assetInformation = assetInformation,
                closeAddress = createWalletConnectAddress(assetCloseToAddress) ?: return null,
                groupId = groupId,
                warningCount = 2.takeIf { isLocalAccountSigner }
            )
        }
    }

    private suspend fun createAssetTransferTransaction(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTransaction: WCAlgoTransactionRequest
    ): BaseAssetTransferTransaction.AssetTransferTransaction? {
        return with(transactionRequest) {
            val senderWCAddress = createWalletConnectAddress(senderAddress) ?: return null
            val receiverWCAddress = createWalletConnectAddress(assetReceiverAddress)
            val assetId = assetId ?: return null
            val amount = assetAmount ?: ZERO
            val ownedAsset = senderWCAddress.decodedAddress?.mapNotBlank {
                getAccountBaseOwnedAssetData(it, assetId)
            }
            val signer = WalletConnectTransactionSigner.create(rawTransaction, senderWCAddress, errorProvider)
            val assetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            BaseAssetTransferTransaction.AssetTransferTransaction(
                rawTransactionPayload = rawTransaction,
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                note = decodedNote,
                assetReceiverAddress = createWalletConnectAddress(assetReceiverAddress) ?: return null,
                senderAddress = senderWCAddress,
                assetId = assetId,
                peerMeta = peerMeta,
                assetAmount = amount,
                signer = signer,
                authAddress = getWalletConnectTransactionAuthAddress(senderWCAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWCAddress.decodedAddress),
                toAccount = createWalletConnectAccount(receiverWCAddress?.decodedAddress),
                assetInformation = assetInformation,
                groupId = groupId
            )
        }
    }

    private suspend fun createAssetOptInTransaction(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTransaction: WCAlgoTransactionRequest
    ): BaseAssetTransferTransaction.AssetOptInTransaction? {
        return with(transactionRequest) {
            val senderWCAddress = createWalletConnectAddress(senderAddress) ?: return null
            val receiverWCAddress = createWalletConnectAddress(assetReceiverAddress)

            val assetId = assetId ?: return null
            val amount = assetAmount ?: ZERO
            val ownedAsset = senderWCAddress.decodedAddress?.mapNotBlank {
                getAccountBaseOwnedAssetData(it, assetId)
            }
            val assetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            val signer = WalletConnectTransactionSigner.create(rawTransaction, senderWCAddress, errorProvider)
            BaseAssetTransferTransaction.AssetOptInTransaction(
                rawTransactionPayload = rawTransaction,
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                note = decodedNote,
                assetReceiverAddress = createWalletConnectAddress(assetReceiverAddress) ?: return null,
                senderAddress = senderWCAddress,
                assetId = assetId,
                peerMeta = peerMeta,
                signer = signer,
                authAddress = getWalletConnectTransactionAuthAddress(senderWCAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWCAddress.decodedAddress),
                toAccount = createWalletConnectAccount(receiverWCAddress?.decodedAddress),
                assetInformation = assetInformation,
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
