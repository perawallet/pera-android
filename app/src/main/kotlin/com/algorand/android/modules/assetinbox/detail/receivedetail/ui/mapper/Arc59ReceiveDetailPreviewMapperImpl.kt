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

package com.algorand.android.modules.assetinbox.detail.receivedetail.ui.mapper

import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.modules.accounticon.ui.usecase.CreateAccountIconDrawableUseCase
import com.algorand.android.modules.accounts.domain.model.AccountValue
import com.algorand.android.modules.accounts.domain.usecase.AccountDisplayNameUseCase
import com.algorand.android.modules.accounts.domain.usecase.GetAccountValueUseCase
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailNavArgs
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailPreview
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.Arc59ReceiveDetailPreview.AssetPreviewDetail
import com.algorand.android.modules.assetinbox.detail.receivedetail.ui.model.ReceiverAccountDetailPreview
import com.algorand.android.modules.assetinbox.detail.transactiondetail.model.Arc59TransactionDetailArgs
import com.algorand.android.modules.currency.domain.model.Currency
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.usecase.AccountDetailUseCase
import com.algorand.android.utils.AssetName
import com.algorand.android.utils.formatAmount
import com.algorand.android.utils.formatAsCurrency
import com.algorand.android.utils.toShortenedAddress
import java.math.BigDecimal.ZERO
import javax.inject.Inject

class Arc59ReceiveDetailPreviewMapperImpl @Inject constructor(
    private val accountDisplayNameUseCase: AccountDisplayNameUseCase,
    private val accountValueUseCase: GetAccountValueUseCase,
    private val accountDetailUseCase: AccountDetailUseCase,
    private val accountIconDrawableUseCase: CreateAccountIconDrawableUseCase,
    private val verificationTierConfigDecider: VerificationTierConfigurationDecider,
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider
) : Arc59ReceiveDetailPreviewMapper {

    override fun getInitialPreview(args: Arc59ReceiveDetailNavArgs): Arc59ReceiveDetailPreview {
        return Arc59ReceiveDetailPreview(
            receiverAccountDetailPreview = getReceiverAccountDetail(args),
            assetPreviewDetail = getAssetPreviewDetail(args),
            arc59TransactionDetailArgs = getArc59TransactionDetailNavArgs(args),
            claimTransaction = null,
            rejectTransaction = null,
            showError = null,
            isLoading = false,
            onTransactionSendSuccessfully = null
        )
    }

    private fun getReceiverAccountDetail(args: Arc59ReceiveDetailNavArgs): ReceiverAccountDetailPreview {
        val accountDetail = accountDetailUseCase.getCachedAccountDetail(args.receiverAddress)?.data
        val accountValue = if (accountDetail != null) {
            accountValueUseCase.getAccountValue(accountDetail)
        } else {
            AccountValue(ZERO, ZERO, 0)
        }
        return ReceiverAccountDetailPreview(
            displayName = accountDisplayNameUseCase(args.receiverAddress),
            formattedPrimaryValue = accountValue.primaryAccountValue.formatAsCurrency(Currency.ALGO.symbol),
            formattedSecondaryValue = getFormattedFiatValue(accountValue),
            accountIconDrawable = accountIconDrawableUseCase(args.receiverAddress)
        )
    }

    private fun getFormattedFiatValue(accountValue: AccountValue): String {
        return "≈ ${accountValue.secondaryAccountValue.formatAsCurrency(Currency.USD.symbol, isFiat = true)}"
    }

    private fun getAssetPreviewDetail(args: Arc59ReceiveDetailNavArgs): AssetPreviewDetail {
        return with(args.assetDetail) {
            val firstSender = args.senderDetails.firstOrNull()
            AssetPreviewDetail(
                name = name,
                shortName = shortName,
                iconUrl = (args.assetDetail as? Arc59ReceiveDetailNavArgs.BaseAssetDetail.CollectibleDetail)?.imageUrl,
                verificationTier = verificationTierConfigDecider.decideVerificationTierConfiguration(verificationTier),
                drawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(id, name, imageUrl),
                firstSender = firstSender?.name?.ifBlank { firstSender.address.toShortenedAddress() },
                otherSendersCount = args.senderDetails.size - 1
            )
        }
    }

    private fun getArc59TransactionDetailNavArgs(args: Arc59ReceiveDetailNavArgs): Arc59TransactionDetailArgs {
        return with(args.assetDetail) {
            val assetDetail = when (this) {
                is Arc59ReceiveDetailNavArgs.BaseAssetDetail.AssetDetail -> {
                    Arc59TransactionDetailArgs.BaseAssetDetail.AssetDetail(
                        id = id,
                        amount = amount,
                        shortName = AssetName.createShortName(shortName),
                        decimal = decimals,
                        usdValue = usdValue,
                        name = name,
                    )
                }

                is Arc59ReceiveDetailNavArgs.BaseAssetDetail.CollectibleDetail -> {
                    Arc59TransactionDetailArgs.BaseAssetDetail.CollectibleDetail(
                        id = id,
                        name = name,
                        shortName = AssetName.createShortName(shortName),
                        imageUrl = imageUrl
                    )
                }
            }

            Arc59TransactionDetailArgs(
                assetDetail = assetDetail,
                optInExpense = args.gainOnReject,
                receiverAccountDetailPreview = getReceiverAccountDetail(args),
                senders = args.senderDetails.map {
                    Arc59TransactionDetailArgs.Sender(
                        address = it.name.ifBlank { it.address.toShortenedAddress() },
                        amount = "+${it.amount.formatAmount(decimals, isCompact = true)}"
                    )
                }
            )
        }
    }
}
