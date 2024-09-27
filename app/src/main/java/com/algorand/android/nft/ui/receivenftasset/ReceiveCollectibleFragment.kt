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

package com.algorand.android.nft.ui.receivenftasset

import android.os.Bundle
import android.view.View
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.customviews.AccountCopyQrView
import com.algorand.android.customviews.ScreenStateView
import com.algorand.android.databinding.FragmentReceiveCollectibleBinding
import com.algorand.android.models.AssetAction
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.assets.addition.base.ui.BaseAddAssetFragment
import com.algorand.android.modules.assets.addition.base.ui.BaseAddAssetFragment.BaseAddAssetFragmentListener
import com.algorand.android.modules.assets.addition.base.ui.BaseAddAssetViewModel
import com.algorand.android.modules.assets.addition.ui.model.AssetAdditionType
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceiveCollectibleFragment : BaseAddAssetFragment(R.layout.fragment_receive_collectible) {

    private val args: ReceiveCollectibleFragmentArgs by navArgs()

    private val binding by viewBinding(FragmentReceiveCollectibleBinding::bind)

    private val receiveCollectibleViewModel by viewModels<ReceiveCollectibleViewModel>()

    override val fragmentResId: Int
        get() = R.id.receiveCollectibleFragment

    override val accountPublicKey: String
        get() = args.accountAddress

    override val loadingProgressBar: ContentLoadingProgressBar
        get() = binding.loadingProgressBar

    override val screenStateView: ScreenStateView
        get() = binding.screenStateView

    override val assetsRecyclerView: RecyclerView
        get() = binding.collectiblesRecyclerView

    override val baseAddAssetViewModel: BaseAddAssetViewModel
        get() = receiveCollectibleViewModel

    override val assetAdditionType: AssetAdditionType
        get() = AssetAdditionType.COLLECTIBLE

    override val baseAddAssetFragmentListener = BaseAddAssetFragmentListener {
        receiveCollectibleViewModel.updateQuery(it)
        receiveCollectibleViewModel.updateBaseAddAssetPreviewWithHandleQueryChangeForScrollEvent()
    }

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.opt_in_to_nft,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val receiverAccountDetailObserver: suspend (Pair<String, AccountIconDrawablePreview>?) -> Unit = {
        it?.run {
            binding.accountCopyQrView.setAccountName(first)
            binding.accountCopyQrView.setAccountIcon(second)
        }
    }

    private val accountCopyQrViewListener = object : AccountCopyQrView.Listener {
        override fun onCopyClick() {
            onAccountAddressCopied(accountPublicKey)
        }

        override fun onQrClick() {
            nav(
                HomeNavigationDirections.actionGlobalShowQrNavigation(
                    title = getString(R.string.qr_code),
                    qrText = accountPublicKey
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.collectLatestOnLifecycle(
            receiveCollectibleViewModel.receiverAccountDetailsFlow,
            receiverAccountDetailObserver
        )
    }

    override fun initUi() {
        with(binding) {
            collectiblesRecyclerView.adapter = assetSearchAdapter
            receiveCollectibleViewModel.initReceiverAccountDetails(accountPublicKey)
            accountCopyQrView.setListener(accountCopyQrViewListener)
            screenStateView.setOnNeutralButtonClickListener {
                receiveCollectibleViewModel.refreshReceiveCollectiblePreview()
            }
        }
    }

    override fun navigateToAssetAdditionBottomSheet(assetAdditionAssetAction: AssetAction) {
        nav(
            ReceiveCollectibleFragmentDirections
                .actionReceiveCollectibleFragmentToAssetAdditionActionNavigation(assetAdditionAssetAction)
        )
    }

    override fun onNavigateCollectibleDetail(collectibleId: Long) {
        nav(
            ReceiveCollectibleFragmentDirections.actionReceiveCollectibleFragmentToCollectibleProfileNavigation(
                accountAddress = accountPublicKey,
                collectibleId = collectibleId
            )
        )
    }
}
