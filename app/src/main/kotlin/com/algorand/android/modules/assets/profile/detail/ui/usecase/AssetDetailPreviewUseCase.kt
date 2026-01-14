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

package com.algorand.android.modules.assets.profile.detail.ui.usecase

import com.algorand.android.R
import com.algorand.android.discover.home.domain.model.TokenDetailInfo
import com.algorand.android.models.AssetTransaction
import com.algorand.android.modules.accountcore.domain.usecase.GetAccountBaseOwnedAssetData
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accounts.domain.usecase.AccountDetailSummaryUseCase
import com.algorand.android.modules.assets.profile.about.domain.usecase.GetSelectedAssetExchangeValueUseCase
import com.algorand.android.modules.assets.profile.detail.ui.AssetDetailFragmentDirections
import com.algorand.android.modules.assets.profile.detail.ui.mapper.AssetDetailPreviewMapper
import com.algorand.android.modules.assets.profile.detail.ui.model.AssetDetailPreview
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.BuyAlgoButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.ReceiveButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.SendButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.StakeButton
import com.algorand.android.ui.asset.detail.model.AssetDetailQuickActionItem.SwapButton
import com.algorand.android.utils.ALGO_SHORT_NAME
import com.algorand.android.utils.Event
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.usecase.GetAccountAssetHoldingsFlow
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.asset.domain.usecase.GetAssetDetail
import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_ID
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@SuppressWarnings("LongParameterList")
class AssetDetailPreviewUseCase @Inject constructor(
    private val assetDetailPreviewMapper: AssetDetailPreviewMapper,
    private val getAssetDetail: GetAssetDetail,
    private val getSelectedAssetExchangeValueUseCase: GetSelectedAssetExchangeValueUseCase,
    private val accountDetailSummaryUseCase: AccountDetailSummaryUseCase,
    private val getAccountType: GetAccountType,
    private val getAccountBaseOwnedAssetData: GetAccountBaseOwnedAssetData,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountAssetHoldingsFlow: GetAccountAssetHoldingsFlow,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled
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
            val destination = AssetDetailFragmentDirections
                .actionAssetDetailFragmentToSwapV2Navigation(accountAddress, assetId)
            preview?.copy(onNavigationEvent = Event(destination))
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
        return getAccountAssetHoldingsFlow(accountAddress).filterNotNull().map { assetHoldings ->
            val baseOwnedAssetDetail = getAccountBaseOwnedAssetData(
                assetId = assetId,
                address = accountAddress
            ) ?: return@map null
            val assetDetail = getAssetDetail(assetId)
            val isAvailableOnDiscoverMobile = assetDetail?.assetInfo?.isAvailableOnDiscoverMobile ?: false
            val formattedAssetPrice = getSelectedAssetExchangeValueUseCase.getSelectedAssetExchangeValue(assetDetail)
                ?.getFormattedValue(isCompact = true)
            val isMarketInformationVisible = isAvailableOnDiscoverMobile &&
                    baseOwnedAssetDetail.verificationTier != VerificationTier.SUSPICIOUS && assetDetail.hasUsdValue()
            assetDetailPreviewMapper.mapToAssetDetailPreview(
                baseOwnedAssetDetail = baseOwnedAssetDetail,
                accountDisplayName = getAccountDisplayName(accountAddress),
                isMarketInformationVisible = isMarketInformationVisible,
                last24HoursChange = assetDetail?.assetInfo?.fiat?.last24HoursAlgoPriceChangePercentage,
                formattedAssetPrice = formattedAssetPrice,
                accountDetailSummary = accountDetailSummaryUseCase.getAccountDetailSummary(accountAddress),
                quickActionItems = getQuickActionItems(
                    accountAddress,
                    assetHoldings,
                    assetId,
                    isQuickActionButtonsVisible
                )
            )
        }.distinctUntilChanged()
    }

    private suspend fun getQuickActionItems(
        address: String,
        assetHoldings: List<AssetHolding>,
        assetId: Long,
        isQuickActionButtonsVisible: Boolean
    ): List<AssetDetailQuickActionItem> {
        val isWatchAccount = getAccountType(address) == AccountType.NoAuth
        val safeIsQuickActionButtonsVisible = isQuickActionButtonsVisible && !isWatchAccount
        if (!safeIsQuickActionButtonsVisible) return emptyList()

        val quickActionItems = mutableListOf<AssetDetailQuickActionItem>()

        val isAlgo = assetId == ALGO_ID
        val isUserOptedInToAsa = assetHoldings.any { it.assetId == assetId }
        if (isUserOptedInToAsa) {
            quickActionItems.add(SwapButton)
        }
        if (isAlgo) {
            if (isXoSwapEnabled() && isStakingEnabled()) {
                quickActionItems.add(StakeButton)
            } else {
                quickActionItems.add(BuyAlgoButton)
            }
        }
        quickActionItems.add(SendButton)
        quickActionItems.add(ReceiveButton)
        return quickActionItems
    }

    private fun isStakingEnabled() = isFeatureToggleEnabled(FeatureToggle.STAKING.key)

    private fun isXoSwapEnabled() = isFeatureToggleEnabled(FeatureToggle.XO_SWAP.key)
}
