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

package com.algorand.android.modules.dapp.bidali.ui.browser.usecase

import com.algorand.android.models.TargetUser
import com.algorand.android.models.TransactionSignData
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountOwnedAssetsData
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import com.algorand.android.modules.dapp.bidali.domain.mapper.BidaliAssetMapper
import com.algorand.android.modules.dapp.bidali.domain.model.BidaliPaymentRequestDTO
import com.algorand.android.modules.dapp.bidali.domain.model.MainnetBidaliSupportedCurrency
import com.algorand.android.modules.dapp.bidali.domain.model.TestnetBidaliSupportedCurrency
import com.algorand.android.modules.dapp.bidali.getCompiledBidaliJavascript
import com.algorand.android.usecase.IsOnMainnetUseCase
import com.algorand.android.utils.formatAmountAsBigInteger
import com.algorand.android.utils.toBigDecimalOrZero
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.info.domain.usecase.IsAssetOptedInByAccount
import com.algorand.wallet.asset.domain.usecase.GetAsset
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

@Suppress("LongParameterList")
class BidaliBrowserUseCase @Inject constructor(
    private val getAccountOwnedAssetsData: GetAccountOwnedAssetsData,
    private val bidaliAssetMapper: BidaliAssetMapper,
    private val isOnMainnetUseCase: IsOnMainnetUseCase,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getTransactionSigner: GetTransactionSigner,
    private val getAsset: GetAsset,
    private val getAccountLite: GetAccountLite,
    private val isAssetOptedInByAccount: IsAssetOptedInByAccount
) {

    suspend fun generateBidaliJavascript(accountAddress: String): String {
        return getCompiledBidaliJavascript(
            currencies = bidaliAssetMapper.mapFromOwnedAssetData(
                ownedAssetDataList = getAccountOwnedAssetsData(accountAddress, true),
                isMainnet = isOnMainnetUseCase.invoke()
            ),
            isMainnet = isOnMainnetUseCase.invoke()
        )
    }

    @Suppress("ReturnCount")
    suspend fun getTransactionDataFromPaymentRequest(
        paymentRequest: BidaliPaymentRequestDTO,
        accountAddress: String
    ): TransactionSignData.Send? {
        // TODO handle cases when we can't find address or assets
        val selectedAccountLite = getAccountLite(accountAddress)
        if (selectedAccountLite?.cachedInfo == null) return null

        val selectedAssetId = getAssetIdFromBidaliIdentifier(
            bidaliId = paymentRequest.protocol,
            isMainnet = isOnMainnetUseCase.invoke()
        ) ?: return null
        val amountAsBigInteger = getAmountAsBigInteger(
            paymentRequest.amount.toBigDecimalOrZero(),
            selectedAssetId
        ) ?: return null

        return TransactionSignData.Send(
            senderAccountAddress = selectedAccountLite.address,
            senderAuthAddress = selectedAccountLite.cachedInfo.rekeyAuthAddress,
            senderAccountName = selectedAccountLite.customName,
            senderAlgoAmount = selectedAccountLite.cachedInfo.algoAmountValue.amount,
            minimumBalance = selectedAccountLite.cachedInfo.minRequiredBalance.toLong(),
            amount = amountAsBigInteger,
            assetId = selectedAssetId,
            xnote = paymentRequest.extraId,
            targetUser = TargetUser(
                publicKey = paymentRequest.address,
                accountIconDrawablePreview = getAccountIconDrawablePreview(accountAddress)
            ),
            isArc59Transaction = !isAssetOptedInByAccount(paymentRequest.address, selectedAssetId),
            signer = getTransactionSigner(selectedAccountLite.address)
        )
    }

    private suspend fun getAmountAsBigInteger(amount: BigDecimal, assetId: Long): BigInteger? {
        val assetDecimals = getAsset(assetId)?.assetInfo?.decimals ?: return null
        return amount.formatAmountAsBigInteger(assetDecimals)
    }

    private fun getAssetIdFromBidaliIdentifier(bidaliId: String, isMainnet: Boolean): Long? {
        return if (isMainnet) {
            MainnetBidaliSupportedCurrency.entries.firstOrNull {
                it.key == bidaliId
            }?.assetId
        } else {
            TestnetBidaliSupportedCurrency.entries.firstOrNull {
                it.key == bidaliId
            }?.assetId
        }
    }
}
