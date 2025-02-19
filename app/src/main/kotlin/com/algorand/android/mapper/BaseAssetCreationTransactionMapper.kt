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

import com.algorand.android.models.BaseAssetConfigurationTransaction.BaseAssetCreationTransaction
import com.algorand.android.models.BaseAssetConfigurationTransaction.BaseAssetCreationTransaction.Companion.isTransactionWithCloseTo
import com.algorand.android.models.BaseAssetConfigurationTransaction.BaseAssetCreationTransaction.Companion.isTransactionWithCloseToAndRekeyed
import com.algorand.android.models.BaseAssetConfigurationTransaction.BaseAssetCreationTransaction.Companion.isTransactionWithRekeyed
import com.algorand.android.models.WCAlgoTransactionRequest
import com.algorand.android.models.WalletConnectPeerMeta
import com.algorand.android.models.WalletConnectTransactionRequest
import com.algorand.android.models.WalletConnectTransactionSigner
import com.algorand.android.modules.walletconnect.domain.WalletConnectErrorProvider
import com.algorand.android.modules.walletconnect.domain.usecase.CreateWalletConnectAccount
import com.algorand.android.modules.walletconnect.domain.usecase.GetWalletConnectTransactionSigner
import com.algorand.android.utils.extensions.mapNotBlank
import com.algorand.android.utils.walletconnect.encodeBase64EncodedHexString
import com.algorand.wallet.account.local.domain.usecase.IsThereAnyAccountWithAddress
import java.math.BigInteger
import javax.inject.Inject

@SuppressWarnings("ReturnCount")
class BaseAssetCreationTransactionMapper @Inject constructor(
    private val errorProvider: WalletConnectErrorProvider,
    private val getWalletConnectTransactionSigner: GetWalletConnectTransactionSigner,
    private val createWalletConnectAccount: CreateWalletConnectAccount,
    private val isThereAnyAccountWithAddress: IsThereAnyAccountWithAddress
) : BaseWalletConnectTransactionMapper() {

    override suspend fun createTransaction(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseAssetCreationTransaction? {
        return when {
            isTransactionWithCloseToAndRekeyed(transactionRequest) -> {
                createAssetCreationTransactionWithCloseToAndRekey(peerMeta, transactionRequest, rawTxn)
            }
            isTransactionWithCloseTo(transactionRequest) -> {
                createAssetCreationTransactionWithCloseTo(peerMeta, transactionRequest, rawTxn)
            }
            isTransactionWithRekeyed(transactionRequest) -> {
                createAssetCreationTransactionWithRekey(peerMeta, transactionRequest, rawTxn)
            }
            else -> {
                createAssetCreationTransaction(peerMeta, transactionRequest, rawTxn)
            }
        }
    }

    private suspend fun createAssetCreationTransaction(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseAssetCreationTransaction.AssetCreationTransaction? {
        return with(transactionRequest) {
            val senderWalletConnectAddress = createWalletConnectAddress(senderAddress) ?: return null
            val signer = WalletConnectTransactionSigner.create(rawTxn, senderWalletConnectAddress, errorProvider)
            BaseAssetCreationTransaction.AssetCreationTransaction(
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                senderAddress = senderWalletConnectAddress,
                note = decodedNote,
                peerMeta = peerMeta,
                rawTransactionPayload = rawTxn,
                signer = signer,
                fromAccount = createWalletConnectAccount(senderWalletConnectAddress),
                totalAmount = assetConfigParams?.totalSupply ?: BigInteger.ZERO,
                decimals = assetConfigParams?.decimal ?: 0,
                isFrozen = assetConfigParams?.isFrozen ?: false,
                assetName = assetConfigParams?.name,
                unitName = assetConfigParams?.unitName,
                url = assetConfigParams?.url,
                metadataHash = encodeBase64EncodedHexString(assetConfigParams?.metadataHash),
                managerAddress = createWalletConnectAddress(assetConfigParams?.managerAddress),
                reserveAddress = createWalletConnectAddress(assetConfigParams?.reserveAddress),
                frozenAddress = createWalletConnectAddress(assetConfigParams?.frozenAddress),
                clawbackAddress = createWalletConnectAddress(assetConfigParams?.clawbackAddress),
                groupId = groupId,
                transactionSigner = getWalletConnectTransactionSigner(signer)
            )
        }
    }

    private suspend fun createAssetCreationTransactionWithCloseTo(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseAssetCreationTransaction.AssetCreationTransactionWithCloseTo? {
        return with(transactionRequest) {
            val senderWalletConnectAddress = createWalletConnectAddress(senderAddress) ?: return null
            val signer = WalletConnectTransactionSigner.create(rawTxn, senderWalletConnectAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BaseAssetCreationTransaction.AssetCreationTransactionWithCloseTo(
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                senderAddress = senderWalletConnectAddress,
                note = decodedNote,
                peerMeta = peerMeta,
                rawTransactionPayload = rawTxn,
                signer = signer,
                fromAccount = createWalletConnectAccount(senderWalletConnectAddress),
                closeToAddress = createWalletConnectAddress(closeToAddress) ?: return null,
                totalAmount = assetConfigParams?.totalSupply ?: BigInteger.ZERO,
                decimals = assetConfigParams?.decimal ?: 0,
                isFrozen = assetConfigParams?.isFrozen ?: false,
                assetName = assetConfigParams?.name,
                unitName = assetConfigParams?.unitName,
                url = assetConfigParams?.url,
                metadataHash = encodeBase64EncodedHexString(assetConfigParams?.metadataHash),
                managerAddress = createWalletConnectAddress(assetConfigParams?.managerAddress),
                reserveAddress = createWalletConnectAddress(assetConfigParams?.reserveAddress),
                frozenAddress = createWalletConnectAddress(assetConfigParams?.frozenAddress),
                clawbackAddress = createWalletConnectAddress(assetConfigParams?.clawbackAddress),
                groupId = groupId,
                warningCount = 1.takeIf { isLocalAccountSigner },
                transactionSigner = getWalletConnectTransactionSigner(signer)
            )
        }
    }

    private suspend fun createAssetCreationTransactionWithCloseToAndRekey(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseAssetCreationTransaction.AssetCreationTransactionWithCloseToAndRekey? {
        return with(transactionRequest) {
            val senderWalletConnectAddress = createWalletConnectAddress(senderAddress) ?: return null
            val signer = WalletConnectTransactionSigner.create(rawTxn, senderWalletConnectAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BaseAssetCreationTransaction.AssetCreationTransactionWithCloseToAndRekey(
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                senderAddress = senderWalletConnectAddress,
                note = decodedNote,
                peerMeta = peerMeta,
                rawTransactionPayload = rawTxn,
                signer = signer,
                fromAccount = createWalletConnectAccount(senderWalletConnectAddress),
                closeToAddress = createWalletConnectAddress(closeToAddress) ?: return null,
                rekeyAddress = createWalletConnectAddress(rekeyAddress) ?: return null,
                totalAmount = assetConfigParams?.totalSupply ?: BigInteger.ZERO,
                decimals = assetConfigParams?.decimal ?: 0,
                isFrozen = assetConfigParams?.isFrozen ?: false,
                assetName = assetConfigParams?.name,
                unitName = assetConfigParams?.unitName,
                url = assetConfigParams?.url,
                metadataHash = encodeBase64EncodedHexString(assetConfigParams?.metadataHash),
                managerAddress = createWalletConnectAddress(assetConfigParams?.managerAddress),
                reserveAddress = createWalletConnectAddress(assetConfigParams?.reserveAddress),
                frozenAddress = createWalletConnectAddress(assetConfigParams?.frozenAddress),
                clawbackAddress = createWalletConnectAddress(assetConfigParams?.clawbackAddress),
                groupId = groupId,
                warningCount = 2.takeIf { isLocalAccountSigner },
                transactionSigner = getWalletConnectTransactionSigner(signer)
            )
        }
    }

    private suspend fun createAssetCreationTransactionWithRekey(
        peerMeta: WalletConnectPeerMeta,
        transactionRequest: WalletConnectTransactionRequest,
        rawTxn: WCAlgoTransactionRequest
    ): BaseAssetCreationTransaction.AssetCreationTransactionWithRekey? {
        return with(transactionRequest) {
            val senderWalletConnectAddress = createWalletConnectAddress(senderAddress) ?: return null
            val signer = WalletConnectTransactionSigner.create(rawTxn, senderWalletConnectAddress, errorProvider)
            val isLocalAccountSigner = signer.address?.decodedAddress?.mapNotBlank { safeAddress ->
                isThereAnyAccountWithAddress(safeAddress)
            } ?: false
            BaseAssetCreationTransaction.AssetCreationTransactionWithRekey(
                walletConnectTransactionParams = createTransactionParams(transactionRequest),
                senderAddress = senderWalletConnectAddress,
                note = decodedNote,
                peerMeta = peerMeta,
                rawTransactionPayload = rawTxn,
                signer = signer,
                fromAccount = createWalletConnectAccount(senderWalletConnectAddress),
                rekeyAddress = createWalletConnectAddress(rekeyAddress) ?: return null,
                totalAmount = assetConfigParams?.totalSupply ?: BigInteger.ZERO,
                decimals = assetConfigParams?.decimal ?: 0,
                isFrozen = assetConfigParams?.isFrozen ?: false,
                assetName = assetConfigParams?.name,
                unitName = assetConfigParams?.unitName,
                url = assetConfigParams?.url,
                metadataHash = encodeBase64EncodedHexString(assetConfigParams?.metadataHash),
                managerAddress = createWalletConnectAddress(assetConfigParams?.managerAddress),
                reserveAddress = createWalletConnectAddress(assetConfigParams?.reserveAddress),
                frozenAddress = createWalletConnectAddress(assetConfigParams?.frozenAddress),
                clawbackAddress = createWalletConnectAddress(assetConfigParams?.clawbackAddress),
                groupId = groupId,
                warningCount = 1.takeIf { isLocalAccountSigner },
                transactionSigner = getWalletConnectTransactionSigner(signer)
            )
        }
    }
}
