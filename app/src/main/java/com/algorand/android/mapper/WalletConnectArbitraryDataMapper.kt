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

import com.algorand.android.module.account.info.domain.usecase.GetAccountInformation
import com.algorand.android.module.asset.detail.component.AssetConstants.ALGO_ASSET_ID
import com.algorand.android.module.account.core.component.detail.domain.usecase.GetAccountDetail
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData
import com.algorand.android.module.account.core.component.assetdata.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.models.WCArbitraryData
import com.algorand.android.models.WalletConnectArbitraryData
import com.algorand.android.models.WalletConnectArbitraryDataSigner
import com.algorand.android.models.WalletConnectAssetInformation
import com.algorand.android.models.WalletConnectPeerMeta
import com.algorand.android.modules.walletconnect.domain.WalletConnectErrorProvider
import com.algorand.android.modules.walletconnect.domain.usecase.CreateWalletConnectAccount
import com.algorand.android.utils.extensions.mapNotBlank
import com.algorand.android.utils.multiplyOrZero
import java.math.BigInteger
import javax.inject.Inject

@SuppressWarnings("ReturnCount")
class WalletConnectArbitraryDataMapper @Inject constructor(
    private val errorProvider: WalletConnectErrorProvider,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val walletConnectAssetInformationMapper: WalletConnectAssetInformationMapper,
    private val createWalletConnectAccount: CreateWalletConnectAccount,
    private val getAccountInformation: GetAccountInformation,
    private val getAccountDetail: GetAccountDetail
) {

    suspend fun createWalletConnectArbitraryData(
        peerMeta: WalletConnectPeerMeta,
        arbitraryData: WCArbitraryData,
    ): WalletConnectArbitraryData {
        return with(arbitraryData) {
            val signerAddress = arbitraryData.signer.orEmpty()
            val wcAccount = createWalletConnectAccount(signerAddress)

            val amount = signerAddress.mapNotBlank { getAccountInformation(it)?.amount } ?: BigInteger.ZERO
            val ownedAsset = signerAddress.mapNotBlank {
                getAccountBaseOwnedAssetData(it, ALGO_ASSET_ID)
            }
            val walletConnectAssetInformation = createWalletConnectAssetInformation(ownedAsset, amount)
            val wcSigner = signer?.let {
                WalletConnectArbitraryDataSigner.create(
                    signerAccountType = signerAddress.mapNotBlank { getAccountDetail(it).accountType },
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
