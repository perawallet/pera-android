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

package com.algorand.android.modules.swap.accountselection

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.algorand.android.MainActivity
import com.algorand.android.R
import com.algorand.android.models.AssetAction
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ScreenState
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.module.asset.utils.AssetAdditionPayload
import com.algorand.android.module.drawable.AnnotatedString
import com.algorand.android.module.drawable.getXmlStyledString
import com.algorand.android.module.foundation.Event
import com.algorand.android.module.swap.ui.accountselection.model.SwapAccountSelectionNavDirection
import com.algorand.android.module.swap.ui.accountselection.model.SwapAccountSelectionNavDirection.SwapNavigation
import com.algorand.android.module.swap.ui.accountselection.model.SwapAccountSelectionPreview
import com.algorand.android.modules.assets.action.addition.AddAssetActionBottomSheet
import com.algorand.android.ui.accountselection.BaseAccountSelectionFragment
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.useFragmentResultListenerValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class SwapAccountSelectionFragment : BaseAccountSelectionFragment() {

    override val toolbarConfiguration = ToolbarConfiguration(
        startIconClick = ::navBack,
        startIconResId = R.drawable.ic_left_arrow
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val swapAccountSelectionViewModel by viewModels<SwapAccountSelectionViewModel>()

    private val swapAccountSelectionPreviewCollector: suspend (SwapAccountSelectionPreview) -> Unit = { preview ->
        updateSwapAccountSelectionPreview(preview)
    }

    private val navToSwapNavigationEventCollector: suspend (Event<SwapAccountSelectionNavDirection>?) -> Unit = {
        it?.consume()?.run {
            handleNavigation(this)
        }
    }

    private val errorEventCollector: suspend (Event<AnnotatedString>?) -> Unit = { errorEvent ->
        errorEvent?.consume()?.run {
            val error = context?.getXmlStyledString(this)
            showGlobalError(error)
        }
    }

    private val optIntoAssetEventCollector: suspend (Event<AssetAdditionPayload>?) -> Unit = { assetActionEvent ->
        assetActionEvent?.consume()?.run { handleAssetAddition(this) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenStateView(ScreenState.CustomState(title = R.string.no_account_found))
    }

    override fun onResume() {
        super.onResume()
        initSavedStateListener()
    }

    override fun setTitleTextView(textView: TextView) {
        textView.apply {
            setText(R.string.select_account)
            show()
        }
    }

    override fun setDescriptionTextView(textView: TextView) {
        textView.apply {
            setText(R.string.select_an_account_to_make)
            show()
        }
    }

    override fun onAccountSelected(publicKey: String) {
        swapAccountSelectionViewModel.onAccountSelected(publicKey)
    }

    override fun initObservers() {
        with(swapAccountSelectionViewModel) {
            viewLifecycleOwner.collectLatestOnLifecycle(
                swapAccountSelectionPreviewFlow,
                swapAccountSelectionPreviewCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                swapAccountSelectionPreviewFlow.map { it.navToSwapNavigationEvent }.distinctUntilChanged(),
                navToSwapNavigationEventCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                swapAccountSelectionPreviewFlow.map { it.errorEvent }.distinctUntilChanged(),
                errorEventCollector
            )
            viewLifecycleOwner.collectLatestOnLifecycle(
                swapAccountSelectionPreviewFlow.map { it.optInToAssetEvent }.distinctUntilChanged(),
                optIntoAssetEventCollector
            )
        }
    }

    private fun initSavedStateListener() {
        useFragmentResultListenerValue<Boolean>(
            key = AddAssetActionBottomSheet.ADD_ASSET_ACTION_RESULT_KEY,
            result = { isConfirmed -> if (!isConfirmed) hideProgress() }
        )
    }

    private fun updateSwapAccountSelectionPreview(preview: SwapAccountSelectionPreview) {
        with(preview) {
            if (isLoading) showProgress() else hideProgress()
            accountAdapter.submitList(accountListItems)
            setScreenStateViewVisibility(isEmptyStateVisible)
        }
    }

    private fun handleNavigation(navigation: SwapAccountSelectionNavDirection) {
        when (navigation) {
            is SwapNavigation -> {
                val dest = SwapAccountSelectionFragmentDirections.actionSwapAccountSelectionFragmentToSwapNavigation(
                    accountAddress = navigation.accountAddress,
                    fromAssetId = navigation.fromAssetId,
                    toAssetId = navigation.toAssetId
                )
                nav(dest)
            }
        }
    }

    private fun handleAssetAddition(payload: AssetAdditionPayload) {
        val assetAction = AssetAction(assetId = payload.assetId, publicKey = payload.address)
        nav(
            SwapAccountSelectionFragmentDirections.actionSwapAccountSelectionFragmentToAssetAdditionActionNavigation(
                assetAction = assetAction,
                shouldWaitForConfirmation = true
            )
        )
        (activity as? MainActivity)?.mainViewModel?.assetOperationResultLiveData?.observe(viewLifecycleOwner) {
            it.peek().use(
                onSuccess = {
                    if (it.assetId == assetAction.assetId) {
                        assetAction.publicKey?.run {
                            swapAccountSelectionViewModel.onAssetAdded(accountAddress = this, assetAction.assetId)
                        }
                    }
                }
            )
        }
    }
}
