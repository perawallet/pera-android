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

package com.algorand.android.ui.send.confirmation.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.algorand.android.R
import com.algorand.android.SendAlgoNavigationDirections
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentTransactionConfirmationBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.send.confirmation.ui.model.TransactionStatusPreview
import com.algorand.android.utils.browser.openTransactionInPeraExplorer
import com.algorand.android.utils.setFragmentNavigationResult
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TransactionConfirmationFragment : BaseFragment(R.layout.fragment_transaction_confirmation) {

    private val toolbarConfiguration: ToolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::onBackPressed
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val binding by viewBinding(FragmentTransactionConfirmationBinding::bind)

    private val transactionConfirmationViewModel by viewModels<TransactionConfirmationViewModel>()

    private val transactionStatusPreviewFlowCollector: suspend (TransactionStatusPreview) -> Unit = { preview ->
        updatePreview(preview)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLottieAnimatorListener()
        initObservers()
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun setLottieAnimatorListener() {
        binding.transactionStatusLottieView.addAnimatorUpdateListener { valueAnimator ->
            valueAnimator.doOnEnd { transactionConfirmationViewModel.onTransactionIsLoaded() }
        }
    }

    private fun updatePreview(preview: TransactionStatusPreview) {
        with(preview) {
            with(binding) {
                transactionStatusLottieView.apply {
                    transactionStatusAnimationResId?.run {
                        setAnimation(transactionStatusAnimationResId)
                    }
                    transactionStatusAnimationDrawableResId?.run {
                        setImageDrawable(ContextCompat.getDrawable(context, this))
                    }
                    transactionStatusAnimationDrawableTintResId?.run {
                        imageTintList = getColorStateList(this)
                    }
                    setBackgroundResource(transactionStatusAnimationBackgroundResId)
                    backgroundTintList = getColorStateList(transactionStatusAnimationBackgroundTintResId)
                }
                transactionTitleTextView.setText(transactionStatusTitleResId)
                transactionInfoTextView.setText(transactionStatusDescriptionResId)
                openPeraExplorerButton.apply {
                    isVisible = false
                    setOnClickListener { onOpenPeraExplorerClick() }
                }
                doneButton.apply {
                    isVisible = isDoneButtonVisible
                    setOnClickListener { onDoneButtonClick() }
                }
            }
//            onExitSendAlgoNavigationEvent?.consume()?.run { popSendAlgoNavigation() }
        }
    }

    private fun onDoneButtonClick() {
        popBackToHome()
    }

    private fun onOpenPeraExplorerClick() {
        context?.openTransactionInPeraExplorer(
            transactionConfirmationViewModel.geTransactionGroupId(),
            transactionConfirmationViewModel.getNetworkSlug()
        )
    }

    // TODO: we can create an extension function
    private fun getColorStateList(@ColorRes colorResId: Int): ColorStateList {
        return ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, colorResId))
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            transactionConfirmationViewModel.transactionStatusPreviewFlow.collectLatest(
                transactionStatusPreviewFlowCollector
            )
        }
    }

    private fun popSendAlgoNavigation() {
        // TODO: use new extension function to return fragment result
        setFragmentNavigationResult(TRANSACTION_CONFIRMATION_KEY, true)
        nav(SendAlgoNavigationDirections.actionSendAlgoNavigationPop())
    }

    private fun popBackToHome() {
        nav(TransactionConfirmationFragmentDirections.actionTransactionConfirmationFragmentToHomeNavigation())
    }

    private fun onBackPressed() {
        setFragmentNavigationResult(TRANSACTION_CONFIRMATION_KEY, false)
        navBack()
    }

    companion object {
        const val TRANSACTION_CONFIRMATION_KEY = "transaction_confirmation_key"
    }
}
