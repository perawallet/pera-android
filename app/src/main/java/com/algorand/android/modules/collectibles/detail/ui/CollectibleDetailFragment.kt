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

package com.algorand.android.modules.collectibles.detail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.module.asset.detail.ui.nftdetail.model.CollectibleDetailPreview
import com.algorand.android.models.AssetTransaction
import com.algorand.android.modules.collectibles.detail.base.ui.BaseCollectibleDetailFragment
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.openTextShareBottomMenuChooser
import com.google.android.material.transition.platform.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectibleDetailFragment : BaseCollectibleDetailFragment() {

    override val baseCollectibleDetailViewModel: CollectibleDetailViewModel by viewModels()

    private val collectibleDetailPreviewCollector: suspend (value: CollectibleDetailPreview?) -> Unit = { preview ->
        if (preview != null) initCollectibleDetailPreview(preview)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sharedElementReturnTransition = MaterialContainerTransform().apply {
            duration = resources.getInteger(R.integer.shared_fragment_transition_delay_ms).toLong()
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        binding.nftMediaPager.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            baseCollectibleDetailViewModel.nftDetailPreviewFlow,
            collectibleDetailPreviewCollector
        )
    }

    private fun initCollectibleDetailPreview(nftDetailPreview: CollectibleDetailPreview) {
        with(nftDetailPreview) {
            setProgressBarVisibility(isLoadingVisible)
            setCollectibleMedias(mediaListOfNFT)
            setPrimaryWarningText(primaryWarningResId)
            setSecondaryWarningText(secondaryWarningResId)
            setCollectionName(collectionNameOfNFT)
            setNFTName(nftName)
            setSendButton(isSendButtonVisible)
            setOptOutButton(isOptOutButtonVisible)
            setNFTDescription(nftDescription)
            setNFTOwnerAccount(optedInAccountTypeDrawableResId, optedInAccountDisplayName, formattedNFTAmount)
            setNFTId(nftId)
            setCollectibleAssetIdClickListener(nftId, optedInAccountDisplayName.accountAddress)
            setNFTCreatorAccount(creatorAccountAddressOfNFT)
            setNFTTraits(traitListOfNFT)
            setShowOnPeraExplorer(peraExplorerUrl)
            setNFTTotalSupply(formattedTotalSupply)
            globalErrorEvent?.consume()?.run { if (this.isNotBlank()) showGlobalError(this) }
            collectibleSendEvent?.consume()?.run {
                navToSendAlgoNavigation(optedInAccountDisplayName.accountAddress, nftId, isPureNFT)
            }
        }
    }

    private fun setSendButton(isSendButtonVisible: Boolean) {
        with(binding.nftSendButton) {
            setOnClickListener { baseCollectibleDetailViewModel.onSendNFTClick() }
            isVisible = isSendButtonVisible
        }
    }

    private fun setOptOutButton(isOptOutButtonVisible: Boolean) {
        with(binding.nftOptOutButton) {
            setOnClickListener { onOptOutClick() }
            isVisible = isOptOutButtonVisible
        }
    }

    private fun onOptOutClick() {
        val assetAction = baseCollectibleDetailViewModel.getOptOutAssetAction()
        nav(
            CollectibleDetailFragmentDirections
                .actionCollectibleDetailFragmentToNftOptOutConfirmationNavigation(assetAction = assetAction)
        )
    }

    private fun navToSendAlgoNavigation(ownerAccountAddress: String, nftId: Long, isPureNFT: Boolean) {
        val assetTransaction = AssetTransaction(senderAddress = ownerAccountAddress, assetId = nftId)
        nav(
            HomeNavigationDirections
                .actionGlobalSendAlgoNavigation(assetTransaction, shouldPopulateAmountWithMax = isPureNFT)
        )
    }

    private fun setCollectibleAssetIdClickListener(collectibleAssetId: Long, address: String) {
        binding.assetIdTextView.setOnClickListener {
            nav(
                CollectibleDetailFragmentDirections.actionCollectibleDetailFragmentToAssetProfileNavigation(
                    assetId = collectibleAssetId,
                    accountAddress = address
                )
            )
        }
    }

    override fun navToImagePreviewFragment(
        imageUrl: String,
        view: View,
        cachedMediaUri: String
    ) {
        exitTransition = getImageDetailTransitionAnimation(isGrowing = false)
        reenterTransition = getImageDetailTransitionAnimation(isGrowing = true)
        val transitionName = view.transitionName
        nav(
            directions = CollectibleDetailFragmentDirections
                .actionCollectibleDetailFragmentToCollectibleImagePreviewNavigation(
                    transitionName = transitionName,
                    imageUri = imageUrl,
                    cachedMediaUri = cachedMediaUri
                ),
            extras = FragmentNavigatorExtras(view to transitionName)
        )
    }

    override fun navToVideoPlayerFragment(videoUrl: String) {
        nav(CollectibleDetailFragmentDirections.actionCollectibleDetailFragmentToVideoPlayerNavigation(videoUrl))
    }

    override fun navToAudioPlayerFragment(audioUrl: String) {
        nav(CollectibleDetailFragmentDirections.actionCollectibleDetailFragmentToAudioPlayerNavigation(audioUrl))
    }

    override fun copyOptedInAccountAddress() {
        onAccountAddressCopied(baseCollectibleDetailViewModel.accountAddress)
    }

    override fun navToCardViewerFragment(url: String) {
        nav(CollectibleDetailFragmentDirections.actionCollectibleDetailFragmentToNftCardViewerNavigation(url))
    }

    override fun onShareButtonClick() {
        context?.openTextShareBottomMenuChooser(
            title = baseCollectibleDetailViewModel.getAssetName()?.assetName.orEmpty(),
            text = baseCollectibleDetailViewModel.getExplorerUrl().orEmpty()
        )
    }

    override fun onNavBack() {
        navBack()
    }
}
