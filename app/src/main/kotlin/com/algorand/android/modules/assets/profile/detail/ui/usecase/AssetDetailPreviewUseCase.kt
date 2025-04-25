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

package com.algorand.android.modules.assets.profile.detail.ui.usecase

import androidx.navigation.NavDirections
import com.algorand.android.R
import com.algorand.android.assetsearch.domain.model.VerificationTier
import com.algorand.android.discover.home.domain.model.TokenDetailInfo
import com.algorand.android.models.AssetTransaction
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountdetail.quickaction.genericaccount.AccountQuickActionsBottomSheetDirections
import com.algorand.android.modules.accounts.domain.usecase.AccountDetailSummaryUseCase
import com.algorand.android.modules.assets.profile.about.domain.usecase.GetSelectedAssetExchangeValueUseCase
import com.algorand.android.modules.assets.profile.detail.ui.AssetDetailFragmentDirections
import com.algorand.android.modules.assets.profile.detail.ui.mapper.AssetDetailPreviewMapper
import com.algorand.android.modules.assets.profile.detail.ui.model.AssetDetailPreview
import com.algorand.android.modules.swap.common.domain.usecase.GetSwapNavigationDestination
import com.algorand.android.modules.swap.model.SwapNavigationDestination
import com.algorand.android.modules.swap.reddot.domain.usecase.GetSwapFeatureRedDotVisibilityUseCase
import com.algorand.android.utils.ALGO_SHORT_NAME
import com.algorand.android.utils.Event
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.info.domain.usecase.GetAccountInformationFlow
import com.algorand.wallet.asset.domain.usecase.GetAssetDetail
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@SuppressWarnings("LongParameterList")
class AssetDetailPreviewUseCase @Inject constructor(
    private val assetDetailPreviewMapper: AssetDetailPreviewMapper,
    private val getSwapFeatureRedDotVisibilityUseCase: GetSwapFeatureRedDotVisibilityUseCase,
    private val getAssetDetail: GetAssetDetail,
    private val getSelectedAssetExchangeValueUseCase: GetSelectedAssetExchangeValueUseCase,
    private val accountDetailSummaryUseCase: AccountDetailSummaryUseCase,
    private val getAccountInformationFlow: GetAccountInformationFlow,
    private val getAccountType: GetAccountType,
    private val getSwapNavigationDestination: GetSwapNavigationDestination,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val getAccountDisplayName: GetAccountDisplayName
) {

    fun updatePreviewForDiscoverMarketEvent(currentPreview: AssetDetailPreview): AssetDetailPreview {
        val safeTokenId = if (currentPreview.assetId == ALGO_ID) ALGO_SHORT_NAME else currentPreview.assetId.toString()
        return currentPreview.copy(
            navigateToDiscoverMarket = Event(
                TokenDetailInfo(tokenId = safeTokenId, poolId = null)
            )
        )
    }

    suspend fun updatePreviewWithSwapNavigation(
        assetId: Long,
        preview: AssetDetailPreview?,
        accountAddress: String
    ): AssetDetailPreview? {
        val canSignTransaction = getAccountType(accountAddress)?.canSignTransaction() == true
        return if (canSignTransaction) {
            val navDestination = getSwapNavigationDestination(accountAddress)
            val swapNavDirection: NavDirections? = when (navDestination) {
                SwapNavigationDestination.AccountSelection -> null
                SwapNavigationDestination.Introduction -> AssetDetailFragmentDirections
                    .actionAssetDetailFragmentToSwapIntroductionNavigation(accountAddress)
                is SwapNavigationDestination.Swap -> AssetDetailFragmentDirections
                    .actionAssetDetailFragmentToSwapNavigation(accountAddress, assetId)
            }
            val safeDirection = swapNavDirection ?: return preview
            preview?.copy(onNavigationEvent = Event(safeDirection))
        } else {
            preview?.copy(onShowGlobalErrorEvent = Event(R.string.this_action_is_not_available))
        }
    }

    suspend fun updatePreviewWithAssetAdditionNavigation(
        preview: AssetDetailPreview?,
        accountAddress: String
    ): AssetDetailPreview? {
        val canSignTransaction = getAccountType(accountAddress)?.canSignTransaction() == true
        return if (canSignTransaction) {
            preview?.copy(
                onNavigationEvent = Event(
                    AccountQuickActionsBottomSheetDirections
                        .actionAccountQuickActionsBottomSheetToAssetAdditionNavigation(accountAddress)
                )
            )
        } else {
            preview?.copy(onShowGlobalErrorEvent = Event(R.string.this_action_is_not_available))
        }
    }

    suspend fun updatePreviewWithOfframpNavigation(
        preview: AssetDetailPreview?,
        accountAddress: String
    ): AssetDetailPreview? {
        val canSignTransaction = getAccountType(accountAddress)?.canSignTransaction() == true
        return if (canSignTransaction) {
            preview?.copy(
                onNavigationEvent = Event(
                    AssetDetailFragmentDirections.actionAssetDetailFragmentToMeldNavigation(accountAddress)
                )
            )
        } else {
            preview?.copy(onShowGlobalErrorEvent = Event(R.string.this_action_is_not_available))
        }
    }

    suspend fun updatePreviewWithSendNavigation(
        preview: AssetDetailPreview?,
        accountAddress: String,
        assetId: Long
    ): AssetDetailPreview? {
        val canSignTransaction = getAccountType(accountAddress)?.canSignTransaction() == true
        return if (canSignTransaction) {
            val assetTransaction = AssetTransaction(senderAddress = accountAddress, assetId = assetId)
            preview?.copy(
                onNavigationEvent = Event(
                    AssetDetailFragmentDirections.actionAssetDetailFragmentToSendAlgoNavigation(assetTransaction)
                )
            )
        } else {
            preview?.copy(onShowGlobalErrorEvent = Event(R.string.this_action_is_not_available))
        }
    }

    suspend fun initAssetDetailPreview(
        accountAddress: String,
        assetId: Long,
        isQuickActionButtonsVisible: Boolean
    ): Flow<AssetDetailPreview?> {
        return getAccountInformationFlow(accountAddress).filterNotNull().map { accountInfo ->
            val baseOwnedAssetDetail = getAccountBaseOwnedAssetData(
                assetId = assetId,
                address = accountAddress
            ) ?: return@map null
            val isSwapButtonSelected = getRedDotVisibility(baseOwnedAssetDetail.isAlgo)
            val isUserOptedInToAsa = accountInfo.hasAsset(assetId)
            val assetDetail = getAssetDetail(assetId)
            val isAvailableOnDiscoverMobile = assetDetail?.assetInfo?.isAvailableOnDiscoverMobile ?: false
            val formattedAssetPrice = getSelectedAssetExchangeValueUseCase.getSelectedAssetExchangeValue(assetDetail)
                ?.getFormattedValue(isCompact = true)
            val isMarketInformationVisible = isAvailableOnDiscoverMobile &&
                baseOwnedAssetDetail.verificationTier != VerificationTier.SUSPICIOUS &&
                assetDetail?.hasUsdValue() == true
            val isWatchAccount = getAccountType(accountAddress) == AccountType.NoAuth
            val safeIsQuickActionButtonsVisible = isQuickActionButtonsVisible && !isWatchAccount
            assetDetailPreviewMapper.mapToAssetDetailPreview(
                baseOwnedAssetDetail = baseOwnedAssetDetail,
                accountDisplayName = getAccountDisplayName(accountAddress),
                isQuickActionButtonsVisible = safeIsQuickActionButtonsVisible,
                isSwapButtonSelected = isSwapButtonSelected,
                isSwapButtonVisible = isUserOptedInToAsa && safeIsQuickActionButtonsVisible,
                isMarketInformationVisible = isMarketInformationVisible,
                last24HoursChange = assetDetail?.assetInfo?.fiat?.last24HoursAlgoPriceChangePercentage,
                formattedAssetPrice = formattedAssetPrice,
                accountDetailSummary = accountDetailSummaryUseCase.getAccountDetailSummary(accountAddress)
            )
        }.distinctUntilChanged()
    }

    private suspend fun getRedDotVisibility(isAlgo: Boolean): Boolean {
        return getSwapFeatureRedDotVisibilityUseCase.getSwapFeatureRedDotVisibility() && isAlgo
    }
}
