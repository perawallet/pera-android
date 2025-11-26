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

package com.algorand.android.ui.webview.publicfragment.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentPublicWebviewBinding
import com.algorand.android.discover.common.ui.model.PeraWebChromeClient
import com.algorand.android.discover.common.ui.model.PeraWebViewClient
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewEvent
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewState.Content
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewState.Content.FavoriteState.Favorite
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewState.Content.FavoriteState.Hidden
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewState.Content.FavoriteState.Unfavorite
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewState.Idle
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PublicWebViewFragment : BaseFragment(R.layout.fragment_public_webview) {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private val binding: FragmentPublicWebviewBinding by viewBinding(FragmentPublicWebviewBinding::bind)

    private val viewModel: PublicWebViewViewModel by viewModels()

    private val args: PublicWebViewFragmentArgs by navArgs()

    private val webViewClientListener = object : PeraWebViewClient.PeraWebViewClientListener {
        override fun onWalletConnectUrlDetected(url: String) {
            handleWalletConnectUrl(url)
        }

        override fun onPageUrlChanged() {
            checkWebViewControls()
        }
    }

    private val viewEventCollector: suspend (ViewEvent) -> Unit = { event ->
        when (event) {
            is ViewEvent.SendWebMessage -> binding.publicWebView.sendJsMessage(event.message)
        }
    }

    private val viewStateCollector: suspend (PublicWebViewViewModel.ViewState) -> Unit = { state ->
        when (state) {
            Idle -> Unit
            is Content -> initContentState(state)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initIdleState()
        collectLatestOnLifecycle(viewModel.state, viewStateCollector)
        collectLatestOnLifecycle(viewModel.viewEvent, viewEventCollector)
        viewModel.initViewState(args.navArgs)
    }

    private fun initIdleState() {
        binding.bottomDappNavigation.apply {
            previousNavButton.isEnabled = false
            nextNavButton.isEnabled = false
            homeNavButton.isEnabled = false
            favoritesNavButton.hide()
        }
    }

    private fun initContentState(state: Content) {
        binding.publicWebView.apply {
            webViewClient = PeraWebViewClient(webViewClientListener)
            webChromeClient = PeraWebChromeClient(webViewClientListener)
            addJsInterface(state.jsInterface)
            loadUrl(state.url)
        }
        binding.bottomDappNavigation.apply {
            homeNavButton.apply {
                setOnClickListener { binding.publicWebView.loadUrl(state.url) }
                isEnabled = true
            }
            nextNavButton.setOnClickListener { binding.publicWebView.goForward() }
            previousNavButton.setOnClickListener { binding.publicWebView.goBack() }
            favoritesNavButton.apply {
                when (state.favoriteState) {
                    Hidden -> hide()
                    Favorite -> {
                        setImageResource(R.drawable.ic_star_full)
                        show()
                    }
                    Unfavorite -> {
                        setImageResource(R.drawable.ic_star_empty)
                        show()
                    }
                }
            }
        }
    }

    private fun checkWebViewControls() {
        with(binding) {
            bottomDappNavigation.previousNavButton.isEnabled = publicWebView.canGoBack()
            bottomDappNavigation.nextNavButton.isEnabled = publicWebView.canGoForward()
        }
    }
}
