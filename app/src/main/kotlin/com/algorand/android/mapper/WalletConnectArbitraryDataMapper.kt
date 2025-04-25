/*
 * Copyright 2025 Pera Wallet, LDA
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

import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.models.WCArbitraryData
import com.algorand.android.models.WalletConnectAccount
import com.algorand.android.models.WalletConnectArbitraryData
import com.algorand.android.models.WalletConnectArbitraryDataSigner
import com.algorand.android.models.WalletConnectAssetInformation
import com.algorand.android.models.WalletConnectPeerMeta
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountOwnedAssetData
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.walletconnect.domain.WalletConnectErrorProvider
import com.algorand.android.utils.multiplyOrZero
import com.algorand.wallet.account.custom.domain.usecase.GetAccountCustomName
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.info.domain.usecase.GetAccountAlgoBalance
import com.algorand.wallet.asset.domain.util.AssetConstants
import java.math.BigInteger
import javax.inject.Inject

@SuppressWarnings("ReturnCount")
class WalletConnectArbitraryDataMapper @Inject constructor(
    private val errorProvider: WalletConnectErrorProvider,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val walletConnectAssetInformationMapper: WalletConnectAssetInformationMapper,
    private val getAccountCustomName: GetAccountCustomName,
    private val getAccountAlgoBalance: GetAccountAlgoBalance,
    private val getAccountOwnedAssetData: GetAccountOwnedAssetData,
    private val getAccountType: GetAccountType
) {

    suspend fun createWalletConnectArbitraryData(
        peerMeta: WalletConnectPeerMeta,
        arbitraryData: WCArbitraryData,
    ): WalletConnectArbitraryData {
        return with(arbitraryData) {
            val signerAddress = signer.orEmpty()
            val wcAccount = WalletConnectAccount(
                address = signerAddress,
                name = getAccountCustomName(signerAddress).orEmpty(),
                accountIconDrawablePreview = getAccountIconDrawablePreview(signerAddress)
            )
            val amount = getAccountAlgoBalance(signerAddress) ?: BigInteger.ZERO
            val ownedAsset = getAccountOwnedAssetData(signerAddress, AssetConstants.ALGO_ID)

            val walletConnectAssetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            val wcSigner = signer?.let {
                WalletConnectArbitraryDataSigner.create(
                    signerAccountType = getAccountType(signerAddress),
                    signer,
                    errorProvider
                )
            }

            WalletConnectArbitraryData(
                chainId = arbitraryData.chainId,
                data = arbitraryData.data,
                message = arbitraryData.message,
                peerMeta = peerMeta,
                signerAccount = wcAccount,
                signer = wcSigner,
                signerAlgoBalance = walletConnectAssetInformation
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
