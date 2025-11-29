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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentPublicWebviewBinding
import com.algorand.android.discover.common.ui.model.PeraWebChromeClient
import com.algorand.android.discover.common.ui.model.PeraWebViewClient
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.webview.bridge.PeraWebViewPublicJsBridge
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel
import com.algorand.android.ui.webview.publicfragment.viewmodel.PublicWebViewViewModel.ViewEvent
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PublicWebViewFragment : BaseFragment(R.layout.fragment_public_webview) {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private var _binding: FragmentPublicWebviewBinding? = null
    private val binding: FragmentPublicWebviewBinding
        get() = _binding!!

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPublicWebviewBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectLatestOnLifecycle(viewModel.viewEvent, viewEventCollector)
        initWebView()
        initBottomNavigation()
    }

    private fun initWebView() {
        binding.publicWebView.apply {
            webViewClient = PeraWebViewClient(webViewClientListener)
            webChromeClient = PeraWebChromeClient(webViewClientListener)
            addJsInterface(PeraWebViewPublicJsBridge(viewModel::processWebEvent))
            loadUrl(args.navArgs.url)
        }
    }

    private fun initBottomNavigation() {
        binding.webViewBottomNavigation.apply {
            homeNavButton.setOnClickListener { binding.publicWebView.loadUrl(args.navArgs.url) }
            previousNavButton.apply {
                isEnabled = false
                setOnClickListener { binding.publicWebView.goBack() }
            }
            nextNavButton.apply {
                isEnabled = false
                setOnClickListener { binding.publicWebView.goForward() }
            }
            val isFavorite = args.navArgs.isFavorite
            favoritesNavButton.apply {
                if (isFavorite != null) {
                    val icon = if (isFavorite) R.drawable.ic_star_full else R.drawable.ic_star_empty
                    setImageResource(icon)
                    show()
                } else {
                    hide()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.publicWebView.destroyWebView()
        _binding = null
    }

    private fun checkWebViewControls() {
        _binding?.webViewBottomNavigation?.previousNavButton?.isEnabled = _binding?.publicWebView?.canGoBack() == true
        _binding?.webViewBottomNavigation?.nextNavButton?.isEnabled = _binding?.publicWebView?.canGoForward() == true
    }
}
