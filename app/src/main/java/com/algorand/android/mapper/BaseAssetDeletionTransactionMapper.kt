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

package com.algorand.android.mapper

import com.algorand.android.account.localaccount.domain.usecase.IsThereAnyAccountWithAddress
import com.algorand.android.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.models.BaseAssetConfigurationTransaction.BaseAssetDeletionTransaction
import com.algorand.android.models.BaseAssetConfigurationTransaction.BaseAssetDeletionTransaction.Companion.isTransactionWithCloseTo
import com.algorand.android.models.BaseAssetConfigurationTransaction.BaseAssetDeletionTransaction.Companion.isTransactionWithCloseToAndRekeyed
import com.algorand.android.models.BaseAssetConfigurationTransaction.BaseAssetDeletionTransaction.Companion.isTransactionWithRekeyed
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

@SuppressWarnings("ReturnCount")
class BaseAssetDeletionTransactionMapper @Inject constructor(
    private val errorProvider: WalletConnectErrorProvider,
    private val walletConnectAssetInformationMapper: WalletConnectAssetInformationMapper,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val getWalletConnectTransactionAuthAddress: GetWalletConnectTransactionAuthAddress,
    private val createWalletConnectAccount: CreateWalletConnectAccount,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress
) : BaseWalletConnectTransactionMapper() {

    override suspend fun createTransaction(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseAssetDeletionTransaction? {
        return when {
            isTransactionWithCloseToAndRekeyed(transactionRequest) -> {
                createAssetDeletionTransactionWithCloseToAndRekey(peerMeta, transactionRequest, rawTxn)
            }
            isTransactionWithCloseTo(transactionRequest) -> {
                createAssetDeletionTransactionWithCloseTo(peerMeta, transactionRequest, rawTxn)
            }
            isTransactionWithRekeyed(transactionRequest) -> {
                createAssetDeletionTransactionWithRekey(peerMeta, transactionRequest, rawTxn)
            }
            else -> {
                createAssetDeletionTransaction(peerMeta, transactionRequest, rawTxn)
            }
        }
    }

    private suspend fun createAssetDeletionTransaction(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseAssetDeletionTransaction.AssetDeletionTransaction? {
        return with(transactionRequest) {
            val senderWalletConnectAddress = createWalletConnectAddress(senderAddress) ?: return null
            val safeAmount = amount ?: BigInteger.ZERO
            if (assetIdBeingConfigured == null) return null
            val ownedAsset = senderWalletConnectAddress.decodedAddress?.mapNotBlank { safeAddress ->
                getAccountBaseOwnedAssetData(safeAddress, assetIdBeingConfigured)
            }
            val signer = WalletConnectTransactionSigner.create(rawTxn, senderWalletConnectAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BaseAssetDeletionTransaction.AssetDeletionTransaction(
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                senderAddress = senderWalletConnectAddress,
                note = decodedNote,
                peerMeta = peerMeta,
                rawTransactionPayload = rawTxn,
                signer = signer,
                authAddress = getWalletConnectTransactionAuthAddress(senderWalletConnectAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWalletConnectAddress.decodedAddress),
                assetInformation = createWalletConnectAssetInformation(ownedAsset, safeAmount),
                assetId = assetIdBeingConfigured,
                url = assetConfigParams?.url,
                groupId = groupId,
                warningCount = 1.takeIf { isLocalAccountSigner }
            )
        }
    }

    private suspend fun createAssetDeletionTransactionWithCloseTo(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseAssetDeletionTransaction.AssetDeletionTransactionWithCloseTo? {
        return with(transactionRequest) {
            val senderWalletConnectAddress = createWalletConnectAddress(senderAddress) ?: return null
            val safeAmount = amount ?: BigInteger.ZERO
            if (assetIdBeingConfigured == null) return null
            val ownedAsset = senderWalletConnectAddress.decodedAddress?.mapNotBlank { safeAddress ->
                getAccountBaseOwnedAssetData(safeAddress, assetIdBeingConfigured)
            }
            val assetInformation = createWalletConnectAssetInformation(ownedAsset, safeAmount)
            val signer = WalletConnectTransactionSigner.create(rawTxn, senderWalletConnectAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BaseAssetDeletionTransaction.AssetDeletionTransactionWithCloseTo(
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                senderAddress = senderWalletConnectAddress,
                note = decodedNote,
                peerMeta = peerMeta,
                rawTransactionPayload = rawTxn,
                signer = signer,
                authAddress = getWalletConnectTransactionAuthAddress(senderWalletConnectAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWalletConnectAddress.decodedAddress),
                assetInformation = assetInformation,
                closeToAddress = createWalletConnectAddress(closeToAddress) ?: return null,
                assetId = assetIdBeingConfigured,
                url = assetConfigParams?.url,
                groupId = groupId,
                warningCount = 2.takeIf { isLocalAccountSigner }
            )
        }
    }

    private suspend fun createAssetDeletionTransactionWithRekey(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseAssetDeletionTransaction.AssetDeletionTransactionWithRekey? {
        return with(transactionRequest) {
            val senderWalletConnectAddress = createWalletConnectAddress(senderAddress) ?: return null
            val safeAmount = amount ?: BigInteger.ZERO
            if (assetIdBeingConfigured == null) return null
            val ownedAsset = senderWalletConnectAddress.decodedAddress?.mapNotBlank { safeAddress ->
                getAccountBaseOwnedAssetData(safeAddress, assetIdBeingConfigured)
            }
            val assetInformation = createWalletConnectAssetInformation(ownedAsset, safeAmount)
            val signer = WalletConnectTransactionSigner.create(rawTxn, senderWalletConnectAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BaseAssetDeletionTransaction.AssetDeletionTransactionWithRekey(
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                senderAddress = senderWalletConnectAddress,
                note = decodedNote,
                peerMeta = peerMeta,
                rawTransactionPayload = rawTxn,
                signer = signer,
                authAddress = getWalletConnectTransactionAuthAddress(senderWalletConnectAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWalletConnectAddress.decodedAddress),
                assetInformation = assetInformation,
                rekeyAddress = createWalletConnectAddress(rekeyAddress) ?: return null,
                assetId = assetIdBeingConfigured,
                url = assetConfigParams?.url,
                groupId = groupId,
                warningCount = 2.takeIf { isLocalAccountSigner }
            )
        }
    }

    @SuppressWarnings("MagicNumber")
    private suspend fun createAssetDeletionTransactionWithCloseToAndRekey(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseAssetDeletionTransaction.AssetDeletionTransactionWithCloseToAndRekey? {
        return with(transactionRequest) {
            val senderWalletConnectAddress = createWalletConnectAddress(senderAddress) ?: return null
            val safeAmount = amount ?: BigInteger.ZERO
            if (assetIdBeingConfigured == null) return null
            val ownedAsset = senderWalletConnectAddress.decodedAddress?.mapNotBlank { safeAddress ->
                getAccountBaseOwnedAssetData(safeAddress, assetIdBeingConfigured)
            }
            val assetInformation = createWalletConnectAssetInformation(ownedAsset, safeAmount)
            val signer = WalletConnectTransactionSigner.create(rawTxn, senderWalletConnectAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BaseAssetDeletionTransaction.AssetDeletionTransactionWithCloseToAndRekey(
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                senderAddress = senderWalletConnectAddress,
                note = decodedNote,
                peerMeta = peerMeta,
                rawTransactionPayload = rawTxn,
                signer = signer,
                authAddress = getWalletConnectTransactionAuthAddress(senderWalletConnectAddress.decodedAddress, signer),
                fromAccount = createWalletConnectAccount(senderWalletConnectAddress.decodedAddress),
                assetInformation = assetInformation,
                closeToAddress = createWalletConnectAddress(closeToAddress) ?: return null,
                rekeyAddress = createWalletConnectAddress(rekeyAddress) ?: return null,
                assetId = assetIdBeingConfigured,
                url = assetConfigParams?.url,
                groupId = groupId,
                warningCount = 3.takeIf { isLocalAccountSigner }
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
