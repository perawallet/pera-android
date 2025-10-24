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

package com.algorand.android.ui.asset.detail.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.AssetTransaction
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.assets.profile.asaprofile.ui.AsaProfileFragmentDirections
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent.DisplayError
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent.NavigateToMeld
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent.NavigateToSendNavigation
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent.NavigateToShowQr
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent.NavigateToSwapIntroduction
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent.NavigateToSwapV1
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel.ViewEvent.NavigateToSwapV2
import com.algorand.android.ui.asset.detail.viewmodel.AssetLineChartViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetPriceLineChartViewModel
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.utils.PERA_VERIFICATION_MAIL_ADDRESS
import com.algorand.android.utils.browser.openAccountAddressInPeraExplorer
import com.algorand.android.utils.browser.openUrl
import com.algorand.android.utils.composeReportAssetEmail
import com.algorand.android.utils.copyToClipboard
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.getCustomLongClickableSpan
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssetDetailV2Fragment : BaseFragment(0), AssetDetailScreenListener {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private val assetDetailV2ViewModel: AssetDetailV2ViewModel by viewModels()
    private val assetBalanceChartViewModel: AssetLineChartViewModel by viewModels()
    private val priceChartViewModel: AssetPriceLineChartViewModel by viewModels()

    private val arg: AssetDetailV2FragmentArgs by navArgs()

    private val viewEventCollector: suspend (AssetDetailV2ViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            is NavigateToSwapIntroduction -> navToSwapIntroduction(event.address)
            is DisplayError -> showGlobalError(getString(event.errorResId))
            is NavigateToSendNavigation -> navToSendNavigation(event.assetTransaction)
            is NavigateToSwapV1 -> navToSwapV1(event.address)
            is NavigateToSwapV2 -> navToSwapV2(event.address, event.assetOutId)
            is NavigateToMeld -> navToMeldNavigation(event.address)
            is NavigateToShowQr -> navToShowQRBottomSheet(event.address)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createComposeView {
            AssetDetailScreen(assetDetailV2ViewModel, assetBalanceChartViewModel, priceChartViewModel, this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assetDetailV2ViewModel.initViewState(arg.accountAddress, arg.assetId)
        assetBalanceChartViewModel.init(arg.accountAddress, arg.assetId)
        priceChartViewModel.init(arg.assetId)

        collectLatestOnLifecycle(assetDetailV2ViewModel.viewEvent, viewEventCollector)
    }

    override fun onNavBackClick() {
        navBack()
    }

    override fun onUrlClick(url: String) {
        context?.openUrl(url)
    }

    override fun onReportClick(assetId: Long, assetShortName: String) {
        context?.composeReportAssetEmail(
            assetId = assetId,
            assetShortName = assetShortName,
            onActivityNotFound = { navigateToInfoBottomSheet() }
        )
    }

    private fun navigateToInfoBottomSheet() {
        context?.let { ctx ->
            val longClickSpannable = getCustomLongClickableSpan(
                clickableColor = ContextCompat.getColor(ctx, R.color.positive),
                onLongClick = { ctx.copyToClipboard(PERA_VERIFICATION_MAIL_ADDRESS) }
            )
            val titleAnnotatedString = AnnotatedString(R.string.report_an_asa)
            val descriptionAnnotatedString = AnnotatedString(
                stringResId = R.string.you_can_send_us_an,
                customAnnotationList = listOf("verification_mail_click" to longClickSpannable),
                replacementList = listOf("verification_mail" to PERA_VERIFICATION_MAIL_ADDRESS)
            )
            nav(
                AsaProfileFragmentDirections.actionAsaProfileFragmentToSingleButtonBottomSheetNavigation(
                    titleAnnotatedString = titleAnnotatedString,
                    descriptionAnnotatedString = descriptionAnnotatedString,
                    buttonStringResId = R.string.got_it,
                    drawableResId = R.drawable.ic_flag,
                    drawableTintResId = R.color.negative,
                    shouldDescriptionHasLinkMovementMethod = true
                )
            )
        }
    }

    private fun navToShowQRBottomSheet(address: String) {
        nav(
            AssetDetailV2FragmentDirections.actionAssetDetailV2FragmentToShowQrNavigation(
                title = getString(R.string.qr_code),
                qrText = address
            )
        )
    }

    private fun navToMeldNavigation(address: String) {
        nav(AssetDetailV2FragmentDirections.actionAssetDetailV2FragmentToMeldNavigation(address))
    }

    private fun navToSwapIntroduction(address: String) {
        nav(AssetDetailV2FragmentDirections.actionAssetDetailV2FragmentToSwapIntroductionNavigation(address))
    }

    private fun navToSendNavigation(assetTransaction: AssetTransaction) {
        nav(AssetDetailV2FragmentDirections.actionAssetDetailV2FragmentToSendAlgoNavigation(assetTransaction))
    }

    private fun navToSwapV1(address: String) {
        nav(AssetDetailV2FragmentDirections.actionAssetDetailV2FragmentToSwapNavigation(address))
    }

    private fun navToSwapV2(address: String?, assetOutId: Long) {
        nav(AssetDetailV2FragmentDirections.actionGlobalSwapV2Navigation(address, assetOutId = assetOutId))
    }

    override fun onCreatorAddressClick(address: String) {
        context?.openAccountAddressInPeraExplorer(
            accountAddress = address,
            networkSlug = assetDetailV2ViewModel.getActiveNodeSlug()
        )
    }

    override fun onFailedToUpdateFavoriteStatus() {
        showGlobalError(getString(R.string.unable_to_update_favorite_status))
    }

    override fun onFailedToUpdatePriceAlertStatus() {
        showGlobalError(getString(R.string.unable_to_change_price_alert))
    }
}
