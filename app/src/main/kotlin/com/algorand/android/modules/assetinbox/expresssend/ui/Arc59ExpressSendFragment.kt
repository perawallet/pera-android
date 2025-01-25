/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.expresssend.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentArc59ExpressSendBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.models.TransactionData
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Arc59ExpressSendFragment : BaseFragment(R.layout.fragment_arc59_express_send) {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        backgroundColor = R.color.layer_gray_lightest,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val binding by viewBinding(FragmentArc59ExpressSendBinding::bind)
    private val viewModel by viewModels<Arc59ExpressSendViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding.continueButton.setOnClickListener(::onContinueClick)
        binding.doNotShowAgainButton.setOnClickListener(::onDoNotShowAgainClick)
    }

    private fun onContinueClick(view: View?) {
        navToAssetTransferPreviewFragment(viewModel.transactionData)
    }

    private fun onDoNotShowAgainClick(view: View?) {
        viewModel.disableArc59ExpressSendWarning()
        navToAssetTransferPreviewFragment(viewModel.transactionData)
    }

    private fun navToAssetTransferPreviewFragment(transactionData: TransactionData.Send) {
        nav(
            Arc59ExpressSendFragmentDirections
                .actionArc59ExpressSendFragmentToAssetTransferPreviewFragment(transactionData)
        )
    }
}
