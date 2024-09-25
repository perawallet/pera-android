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

package com.algorand.android.modules.assets.profile.detail.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.accountcore.ui.model.AssetName
import com.algorand.android.accountcore.ui.model.VerificationTierConfiguration
import com.algorand.android.accountcore.ui.summary.model.AccountDetailSummary
import com.algorand.android.module.asset.detail.ui.detail.model.AssetDetailPreview
import com.algorand.android.core.BaseFragment
import com.algorand.android.customviews.toolbar.buttoncontainer.model.BaseAccountIconButton
import com.algorand.android.databinding.FragmentAssetDetailBinding
import com.algorand.android.dateui.model.DateFilter
import com.algorand.android.discover.home.domain.model.TokenDetailInfo
import com.algorand.android.drawableui.asset.BaseAssetDrawableProvider
import com.algorand.android.foundation.Event
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.module.transaction.history.ui.model.BaseTransactionItem
import com.algorand.android.modules.assets.profile.about.ui.AssetAboutFragment
import com.algorand.android.modules.assets.profile.activity.ui.AssetActivityFragment
import com.algorand.android.modules.assets.profile.detail.ui.adapter.AssetDetailPagerAdapter
import com.algorand.android.modules.transaction.detail.ui.model.TransactionDetailEntryPoint
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.AccountSelection
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.Introduction
import com.algorand.android.module.swap.component.common.model.SwapNavigationDestination.Swap
import com.algorand.android.utils.PERA_VERIFICATION_MAIL_ADDRESS
import com.algorand.android.utils.copyToClipboard
import com.algorand.android.utils.emptyString
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.getCustomLongClickableSpan
import com.algorand.android.utils.setDrawable
import com.algorand.android.utils.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class AssetDetailFragment : BaseFragment(R.layout.fragment_asset_detail), AssetAboutFragment.AssetAboutTabListener,
    AssetActivityFragment.Listener {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconClick = ::navBack,
        startIconResId = R.drawable.ic_left_arrow,
        backgroundColor = R.color.hero_bg
    )

    override val fragmentConfiguration = FragmentConfiguration()

    private val binding by viewBinding(FragmentAssetDetailBinding::bind)

    private val assetDetailViewModel by viewModels<AssetDetailViewModel>()

    private lateinit var assetDetailPagerAdapter: AssetDetailPagerAdapter

    private val assetDetailPreviewCollector: suspend (AssetDetailPreview?) -> Unit = { preview ->
        if (preview != null) updatePreview(preview)
    }

    private val accountDisplayNameCollector: suspend (AccountDisplayName?) -> Unit = { accountDisplayName ->
        if (accountDisplayName != null) setToolbarTitle(accountDisplayName)
    }

    private val accountDetailSummaryCollector: suspend (AccountDetailSummary?) -> Unit = { accountDetailSummary ->
        if (accountDetailSummary != null) setToolbarEndButton(accountDetailSummary)
    }

    private val onGlobalErrorEventCollector: suspend (Event<Int>?) -> Unit = {
        it?.consume()?.run {
            showGlobalError(title = getString(this), errorMessage = emptyString())
        }
    }

    private val baseAssetDrawableProviderCollector: suspend (BaseAssetDrawableProvider?) -> Unit = { drawableProvider ->
        drawableProvider?.let(::setAssetDrawable)
    }

    private val navigateToDiscoverMarketEventCollector: suspend (Event<String>?) -> Unit = { event ->
        event?.consume()?.run { navToDiscoverTokenDetailPage(TokenDetailInfo(this, null)) }
    }

    private val swapNavigationDestinationEventCollector: suspend (Event<SwapNavigationDestination>?) -> Unit =
        { event ->
            event?.consume()?.run { handleSwapNavigationDestination(this) }
        }

    override fun onDateFilterClick(currentFilter: DateFilter) {
        nav(AssetDetailFragmentDirections.actionAssetDetailFragmentToDateFilterNavigation(currentFilter))
    }

    override fun onStandardTransactionItemClick(transaction: BaseTransactionItem.TransactionItem) {
        nav(
            AssetDetailFragmentDirections.actionAssetDetailFragmentToTransactionDetailNavigation(
                transactionId = transaction.id ?: return,
                accountAddress = assetDetailViewModel.accountAddress,
                entryPoint = TransactionDetailEntryPoint.STANDARD_TRANSACTION
            )
        )
    }

    override fun onApplicationCallTransactionItemClick(transaction: BaseTransactionItem.TransactionItem) {
        nav(
            AssetDetailFragmentDirections.actionAssetDetailFragmentToTransactionDetailNavigation(
                transactionId = transaction.id ?: return,
                accountAddress = assetDetailViewModel.accountAddress,
                entryPoint = TransactionDetailEntryPoint.APPLICATION_CALL_TRANSACTION
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    private fun initUi() {
        configureToolbar()
        initPagerAdapter()
        configureTabLayout()
    }

    private fun initObservers() {
        with(assetDetailViewModel.assetDetailPreviewFlow) {
            collectLatestOnLifecycle(
                flow = this,
                collection = assetDetailPreviewCollector
            )
            collectLatestOnLifecycle(
                flow = map { it?.baseAssetDrawableProvider },
                collection = baseAssetDrawableProviderCollector
            )
            collectLatestOnLifecycle(
                flow = map { it?.accountDisplayName },
                collection = accountDisplayNameCollector
            )
            collectLatestOnLifecycle(
                flow = map { it?.accountDetailSummary },
                collection = accountDetailSummaryCollector
            )
            collectLatestOnLifecycle(
                flow = map { it?.swapNavigationDestinationEvent },
                collection = swapNavigationDestinationEventCollector
            )
            collectLatestOnLifecycle(
                flow = map { it?.onShowGlobalErrorEvent },
                collection = onGlobalErrorEventCollector
            )
            collectLatestOnLifecycle(
                flow = map { it?.navigateToDiscoverMarket },
                collection = navigateToDiscoverMarketEventCollector
            )
        }
    }

    private fun initPagerAdapter() {
        assetDetailPagerAdapter = AssetDetailPagerAdapter(
            accountAddress = assetDetailViewModel.accountAddress,
            assetId = assetDetailViewModel.assetId,
            fragment = this
        )
        binding.assetDetailViewPager.apply {
            isUserInputEnabled = false
            adapter = assetDetailPagerAdapter
        }
    }

    private fun configureToolbar() {
        binding.toolbar.configure(toolbarConfiguration)
    }

    private fun configureTabLayout() {
        TabLayoutMediator(binding.algorandTabLayout, binding.assetDetailViewPager) { tab, position ->
            assetDetailPagerAdapter.getItem(position)?.titleResId?.let { tab.text = getString(it) }
        }.attach()
    }

    private fun updatePreview(preview: AssetDetailPreview) {
        with(preview) {
            setAssetInformation(
                assetFullName = assetFullName,
                assetId = assetId,
                isAlgo = isAlgo,
                verificationTierConfiguration = verificationTierConfiguration
            )
            setAssetValues(
                formattedPrimaryValue = formattedPrimaryValue,
                formattedSecondaryValue = formattedSecondaryValue
            )
            setQuickActionsButtons(
                isAlgo = isAlgo,
                isSwapButtonVisible = isSwapButtonVisible,
                isQuickActionButtonsVisible = isQuickActionButtonsVisible,
                isSwapButtonSelected = isSwapButtonSelected
            )
            setMarketInformation(
                isMarketInformationVisible = isMarketInformationVisible,
                formattedAssetPrice = formattedAssetPrice,
                isChangePercentageVisible = isChangePercentageVisible,
                changePercentage = changePercentage,
                changePercentageIcon = changePercentageIcon,
                changePercentageTextColor = changePercentageTextColor
            )
        }
    }

    private fun setMarketInformation(
        isMarketInformationVisible: Boolean,
        formattedAssetPrice: String,
        isChangePercentageVisible: Boolean,
        changePercentage: BigDecimal?,
        changePercentageIcon: Int?,
        changePercentageTextColor: Int?
    ) {
        with(binding.marketInformationLayout) {
            root.isVisible = isMarketInformationVisible
            root.setOnClickListener { assetDetailViewModel.onMarketClick() }
            assetPriceTextView.text = formattedAssetPrice
            assetChangePercentageTextView.apply {
                changePercentageIcon?.let { setDrawable(start = AppCompatResources.getDrawable(context, it)) }
                changePercentageTextColor?.let { setTextColor(ContextCompat.getColor(context, it)) }
                changePercentage?.let { text = getString(R.string.formatted_changed_percentage, it.abs()) }
                isVisible = isChangePercentageVisible
            }
        }
    }

    private fun setToolbarTitle(accountDisplayName: AccountDisplayName) {
        with(binding.toolbar) {
            changeTitle(accountDisplayName.primaryDisplayName)
            setOnTitleLongClickListener { onAccountAddressCopied(accountDisplayName.accountAddress) }
            accountDisplayName.secondaryDisplayName?.let { changeSubtitle(it) }
        }
    }

    private fun setToolbarEndButton(accountDetailSummary: AccountDetailSummary) {
        val endButton = if (accountDetailSummary.shouldDisplayAccountType) {
            BaseAccountIconButton.ExtendedAccountButton(
                accountIconDrawablePreview = accountDetailSummary.accountIconDrawable,
                accountTypeResId = accountDetailSummary.accountTypeResId,
                onClick = ::navToAccountStatusDetailNavigation
            )
        } else {
            BaseAccountIconButton.AccountButton(
                accountIconDrawablePreview = accountDetailSummary.accountIconDrawable,
                onClick = ::navToAccountStatusDetailNavigation
            )
        }
        binding.toolbar.setEndButton(button = endButton)
    }

    private fun setAssetDrawable(baseAssetDrawableProvider: BaseAssetDrawableProvider) {
        binding.assetLogoImageView.apply {
            baseAssetDrawableProvider.provideAssetDrawable(
                imageView = this,
                onResourceFailed = ::setImageDrawable
            )
        }
    }

    private fun setAssetInformation(
        assetFullName: AssetName,
        assetId: Long,
        isAlgo: Boolean,
        verificationTierConfiguration: VerificationTierConfiguration
    ) {
        with(binding) {
            assetNameAndBadgeTextView.apply {
                setTextColor(ContextCompat.getColor(root.context, verificationTierConfiguration.textColorResId))
                verificationTierConfiguration.drawableResId?.run {
                    setDrawable(end = AppCompatResources.getDrawable(context, this))
                }
                text = assetFullName.assetName
            }
            if (!isAlgo) {
                assetIdTextView.apply {
                    text = assetId.toString()
                    setOnLongClickListener { context.copyToClipboard(assetId.toString()); true }
                }
            }
            assetIdTextView.isVisible = !isAlgo
            interpunctTextView.isVisible = !isAlgo
        }
    }

    private fun setQuickActionsButtons(
        isAlgo: Boolean,
        isSwapButtonVisible: Boolean,
        isQuickActionButtonsVisible: Boolean,
        isSwapButtonSelected: Boolean
    ) {
        with(binding) {
            quickActionButtons.isVisible = isQuickActionButtonsVisible
            swapButton.apply {
                isVisible = isSwapButtonVisible && isQuickActionButtonsVisible
                isSelected = isSwapButtonSelected
                setOnClickListener { assetDetailViewModel.onSwapClick() }
            }
            buyAlgoButton.apply {
                isVisible = isAlgo && isQuickActionButtonsVisible
                setOnClickListener { onBuySellClick() }
            }
            sendButton.setOnClickListener { onSendClick() }
            receiveButton.setOnClickListener { navToShowQRBottomSheet() }
        }
    }

    private fun onBuySellClick() {
        with(assetDetailViewModel) {
            if (canAccountSignTransaction) {
                nav(AssetDetailFragmentDirections.actionAssetDetailFragmentToMeldNavigation(accountAddress))
            } else {
                showGlobalError(title = getString(R.string.this_action_is_not_available), errorMessage = emptyString())
            }
        }
    }

    private fun onSendClick() {
        with(assetDetailViewModel) {
            if (canAccountSignTransaction) {
                val assetTransaction = AssetTransaction(senderAddress = accountAddress, assetId = assetId)
                nav(AssetDetailFragmentDirections.actionAssetDetailFragmentToSendAlgoNavigation(assetTransaction))
            } else {
                showGlobalError(title = getString(R.string.this_action_is_not_available), errorMessage = emptyString())
            }
        }
    }

    private fun navToShowQRBottomSheet() {
        nav(
            AssetDetailFragmentDirections.actionAssetDetailFragmentToShowQrNavigation(
                title = getString(R.string.qr_code),
                qrText = assetDetailViewModel.accountAddress
            )
        )
    }

    private fun navToAccountStatusDetailNavigation() {
        nav(
            AssetDetailFragmentDirections.actionAssetDetailFragmentToAccountStatusDetailNavigation(
                assetDetailViewModel.accountAddress
            )
        )
    }

    private fun handleSwapNavigationDestination(swapNavigationDestination: SwapNavigationDestination) {
        with(assetDetailViewModel) {
            if (canAccountSignTransaction) {
                val destination = with(AssetDetailFragmentDirections) {
                    when (swapNavigationDestination) {
                        Introduction -> actionAssetDetailFragmentToSwapNavigation(accountAddress, assetId)
                        is Swap -> actionAssetDetailFragmentToSwapIntroductionNavigation(accountAddress)
                        AccountSelection -> null
                    }
                }
                destination?.run { nav(this) }
            } else {
                showGlobalError(title = getString(R.string.this_action_is_not_available), errorMessage = emptyString())
            }
        }
    }

    private fun setAssetValues(formattedPrimaryValue: String, formattedSecondaryValue: String) {
        with(binding) {
            assetPrimaryValueTextView.text = formattedPrimaryValue
            assetSecondaryValueTextView.text = formattedSecondaryValue
        }
    }

    override fun onReportActionFailed() {
        val longClickSpannable = getCustomLongClickableSpan(
            clickableColor = ContextCompat.getColor(binding.root.context, R.color.positive),
            onLongClick = { context?.copyToClipboard(PERA_VERIFICATION_MAIL_ADDRESS) }
        )
        val titleAnnotatedString = AnnotatedString(R.string.report_an_asa)
        val descriptionAnnotatedString = AnnotatedString(
            stringResId = R.string.you_can_send_us_an,
            customAnnotationList = listOf("verification_mail_click" to longClickSpannable),
            replacementList = listOf("verification_mail" to PERA_VERIFICATION_MAIL_ADDRESS)
        )
        nav(
            AssetDetailFragmentDirections.actionAssetDetailFragmentToSingleButtonBottomSheetNavigation(
                titleAnnotatedString = titleAnnotatedString,
                descriptionAnnotatedString = descriptionAnnotatedString,
                buttonStringResId = R.string.got_it,
                drawableResId = R.drawable.ic_flag,
                drawableTintResId = R.color.negative,
                shouldDescriptionHasLinkMovementMethod = true
            )
        )
    }

    override fun onTotalSupplyClick() {
        nav(AssetDetailFragmentDirections.actionAssetDetailFragmentToAssetTotalSupplyNavigation())
    }

    private fun navToDiscoverTokenDetailPage(tokenDetailInfo: TokenDetailInfo) {
        nav(AssetDetailFragmentDirections.actionAssetDetailFragmentToDiscoverDetailNavigation(tokenDetailInfo))
    }
}
