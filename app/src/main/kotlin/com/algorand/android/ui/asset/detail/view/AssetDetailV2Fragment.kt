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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.core.BaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.asset.detail.viewmodel.AssetDetailV2ViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetLineChartViewModel
import com.algorand.android.ui.asset.detail.viewmodel.AssetPriceLineChartViewModel
import com.algorand.android.ui.compose.extensions.createComposeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssetDetailV2Fragment : BaseFragment(0), AssetDetailScreenListener {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private val assetDetailV2ViewModel: AssetDetailV2ViewModel by viewModels()
    private val assetBalanceChartViewModel: AssetLineChartViewModel by viewModels()
    private val priceChartViewModel: AssetPriceLineChartViewModel by viewModels()

    private val arg: AssetDetailV2FragmentArgs by navArgs()

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
    }

    override fun onSwapClick() {
        TODO("Not yet implemented")
    }

    override fun onBuyAlgoClick() {
        TODO("Not yet implemented")
    }

    override fun onReceiveClick() {
        TODO("Not yet implemented")
    }

    override fun onSendClick() {
        TODO("Not yet implemented")
    }

    override fun onNavBackClick() {
        TODO("Not yet implemented")
    }

    override fun onUrlClick(url: String) {
        TODO("Not yet implemented")
    }

    override fun onReportClick(assetId: Long, assetShortName: String) {
        TODO("Not yet implemented")
    }

    override fun onCreatorAddressClick(address: String) {
        TODO("Not yet implemented")
    }
}
