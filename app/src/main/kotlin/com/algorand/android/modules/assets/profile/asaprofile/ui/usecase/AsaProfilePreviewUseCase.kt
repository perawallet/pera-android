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

package com.algorand.android.modules.assets.profile.asaprofile.ui.usecase

import com.algorand.android.R
import com.algorand.android.decider.AssetDrawableProviderDecider
import com.algorand.android.mapper.AssetActionMapper
import com.algorand.android.models.AssetAction
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.modules.assets.core.ui.domain.model.AssetName
import com.algorand.android.modules.assets.core.ui.domain.usecase.GetAssetName
import com.algorand.android.modules.assets.profile.about.domain.usecase.GetSelectedAssetExchangeValueUseCase
import com.algorand.android.modules.assets.profile.asaprofile.ui.mapper.AsaProfilePreviewMapper
import com.algorand.android.modules.assets.profile.asaprofile.ui.mapper.AsaStatusPreviewMapper
import com.algorand.android.modules.assets.profile.asaprofile.ui.model.AsaProfilePreview
import com.algorand.android.modules.assets.profile.asaprofile.ui.model.AsaStatusPreview
import com.algorand.android.modules.assets.profile.asaprofile.ui.model.PeraButtonState
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.usecase.AccountAddressUseCase
import com.algorand.android.utils.ALGO_SHORT_NAME
import com.algorand.android.utils.isGreaterThan
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.asset.domain.usecase.GetAsset
import com.algorand.wallet.asset.domain.usecase.GetSingleAssetDetailFlow
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@SuppressWarnings("LongParameterList")
class AsaProfilePreviewUseCase @Inject constructor(
    private val getSelectedAssetExchangeValueUseCase: GetSelectedAssetExchangeValueUseCase,
    private val asaProfilePreviewMapper: AsaProfilePreviewMapper,
    private val verificationTierConfigurationDecider: VerificationTierConfigurationDecider,
    private val assetDrawableProviderDecider: AssetDrawableProviderDecider,
    private val accountAddressUseCase: AccountAddressUseCase,
    private val asaStatusPreviewMapper: AsaStatusPreviewMapper,
    private val assetActionMapper: AssetActionMapper,
    private val getAssetName: GetAssetName,
    private val getSingleAssetDetailFlow: GetSingleAssetDetailFlow,
    private val getAsset: GetAsset,
    private val getAccountInformationFlow: GetAccountInformationFlow,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
) {

    fun createAssetAction(assetId: Long, accountAddress: String?, assetName: AssetName?): AssetAction {
        return assetActionMapper.mapTo(
            assetId = assetId,
            assetName = assetName,
            accountAddress = accountAddress
        )
    }

    fun getAsaProfilePreview(accountAddress: String?, assetId: Long): Flow<AsaProfilePreview?> {
        return when {
            accountAddress.isNullOrBlank() -> createAsaProfilePreviewWithoutAccountInformation()
            assetId == ALGO_ID -> createAlgoProfilePreviewWithAccountInformation(accountAddress)
            else -> createAsaProfilePreviewWithAccountInformation(accountAddress, assetId)
        }
    }

    private fun createAlgoProfilePreviewWithAccountInformation(accountAddress: String) = flow {
        val algoDetail = getAsset(ALGO_ID) ?: return@flow
        val asaStatusPreview = createAsaStatusPreview(
            isAlgo = true,
            isUserOptedInAsset = true,
            accountAddress = accountAddress,
            hasUserAmount = true,
            assetShortName = getAssetName(ALGO_SHORT_NAME)
        )
        val preview = createAsaProfilePreviewFromAssetDetail(algoDetail, asaStatusPreview)
        emit(preview)
    }.distinctUntilChanged()

    private fun createAsaProfilePreviewWithAccountInformation(
        accountAddress: String,
        assetId: Long
    ): Flow<AsaProfilePreview?> {
        return combine(
            getSingleAssetDetailFlow(),
            getAccountInformationFlow(accountAddress)
        ) { assetDetail, accountInfo ->
            val ownedAssetData = getAccountBaseOwnedAssetData(accountAddress, assetId)
            val hasUserAmount = ownedAssetData?.amount isGreaterThan BigInteger.ZERO
            val isUserOptedInAsset = accountInfo?.hasAsset(assetId) == true
            val asaStatusPreview = createAsaStatusPreview(
                isAlgo = false,
                isUserOptedInAsset = isUserOptedInAsset,
                accountAddress = accountAddress,
                hasUserAmount = hasUserAmount,
                formattedAccountBalance = ownedAssetData?.formattedAmount,
                assetShortName = getAssetName(ownedAssetData?.shortName)
            )
            createAsaProfilePreviewFromAssetDetail(assetDetail = assetDetail, asaStatusPreview = asaStatusPreview)
        }
    }

    private fun createAsaProfilePreviewWithoutAccountInformation(): Flow<AsaProfilePreview> {
        return getSingleAssetDetailFlow().map { assetDetail ->
            val asaStatusPreview = createAsaStatusPreview(
                isAlgo = false,
                isUserOptedInAsset = false,
                accountAddress = null,
                hasUserAmount = false,
                assetShortName = null
            )
            createAsaProfilePreviewFromAssetDetail(assetDetail = assetDetail, asaStatusPreview = asaStatusPreview)
        }
    }

    private fun createAsaProfilePreviewFromAssetDetail(
        assetDetail: Asset,
        asaStatusPreview: AsaStatusPreview?
    ): AsaProfilePreview {
        return with(assetDetail) {
            val minValueToDisplayExactAmount = BigDecimal.valueOf(MINIMUM_CURRENCY_VALUE_TO_DISPLAY_EXACT_AMOUNT)
            val formattedAssetPrice = getSelectedAssetExchangeValueUseCase
                .getSelectedAssetExchangeValue(assetDetail = this)
                ?.getFormattedValue(isCompact = true, minValueToDisplayExactAmount = minValueToDisplayExactAmount)
            val verificationTierConfiguration = verificationTierConfigurationDecider
                .decideVerificationTierConfiguration(verificationTier)
            val assetDrawableProvider = assetDrawableProviderDecider.getAssetDrawableProvider(assetDetail)
            val isAvailableOnDiscoverMobile = assetInfo?.isAvailableOnDiscoverMobile ?: false
            val isMarketInformationVisible = isAvailableOnDiscoverMobile &&
                verificationTier != VerificationTier.SUSPICIOUS &&
                hasUsdValue()
            asaProfilePreviewMapper.mapToAsaProfilePreview(
                isAlgo = assetDetail.id == ALGO_ID,
                assetFullName = getAssetName(fullName),
                assetShortName = getAssetName(shortName),
                assetId = id,
                formattedAssetPrice = formattedAssetPrice,
                verificationTierConfiguration = verificationTierConfiguration,
                baseAssetDrawableProvider = assetDrawableProvider,
                assetPrismUrl = logoUri,
                asaStatusPreview = asaStatusPreview,
                isMarketInformationVisible = isMarketInformationVisible,
                last24HoursChange = assetInfo?.fiat?.last24HoursAlgoPriceChangePercentage
            )
        }
    }

    private suspend fun createAsaStatusPreview(
        isAlgo: Boolean,
        isUserOptedInAsset: Boolean,
        accountAddress: String?,
        hasUserAmount: Boolean,
        formattedAccountBalance: String? = null,
        assetShortName: AssetName?
    ): AsaStatusPreview? {
        return when {
            isAlgo -> null
            accountAddress.isNullOrBlank() -> {
                asaStatusPreviewMapper.mapToAsaAccountSelectionStatusPreview(
                    statusLabelTextResId = R.string.you_can_opt_in_to_this,
                    peraButtonState = PeraButtonState.ADDITION,
                    actionButtonTextResId = R.string.opt_dash_in
                )
            }
            !isUserOptedInAsset -> {
                asaStatusPreviewMapper.mapToAsaAdditionStatusPreview(
                    accountAddress = accountAddressUseCase.getAccountAddress(accountAddress),
                    statusLabelTextResId = R.string.you_can_add_this_asset,
                    peraButtonState = PeraButtonState.ADDITION,
                    actionButtonTextResId = R.string.opt_dash_in
                )
            }
            isUserOptedInAsset && hasUserAmount -> {
                asaStatusPreviewMapper.mapToAsaTransferStatusPreview(
                    statusLabelTextResId = R.string.balance,
                    peraButtonState = PeraButtonState.REMOVAL,
                    actionButtonTextResId = R.string.remove,
                    formattedAccountBalance = formattedAccountBalance.orEmpty(),
                    assetShortName = assetShortName
                )
            }
            isUserOptedInAsset -> {
                asaStatusPreviewMapper.mapToAsaRemovalStatusPreview(
                    statusLabelTextResId = R.string.balance,
                    peraButtonState = PeraButtonState.REMOVAL,
                    actionButtonTextResId = R.string.remove,
                    formattedAccountBalance = formattedAccountBalance.orEmpty(),
                    assetShortName = assetShortName
                )
            }
            else -> null
        }
    }

    companion object {
        const val MINIMUM_CURRENCY_VALUE_TO_DISPLAY_EXACT_AMOUNT = 0.000001
    }
}
