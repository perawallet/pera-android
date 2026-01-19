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

package com.algorand.android.ui.xoswap.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.BuildConfig
import com.algorand.android.CoreMainActivity
import com.algorand.android.R
import com.algorand.android.core.BaseFragment
import com.algorand.android.databinding.FragmentXoSwapBinding
import com.algorand.android.discover.common.ui.model.PeraWebViewClient
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.webview.bridge.PeraWebViewInternalBridge
import com.algorand.android.ui.webview.bridge.mapper.PeraInternalWebInterfaceEventMapper
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.PushPublicWebView
import com.algorand.android.ui.webview.publicfragment.model.PublicWebViewFragmentNavArgs
import com.algorand.android.ui.webview.viewmodel.PeraWebViewFragmentDelegate
import com.algorand.android.ui.webview.viewmodel.PeraWebViewViewModel
import com.algorand.android.utils.delegation.bottomnavbarvisibility.BottomNavBarVisibilityDelegation
import com.algorand.android.utils.delegation.bottomnavbarvisibility.BottomNavBarVisibilityDelegationImpl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class XoSwapFragment : BaseFragment(R.layout.fragment_xo_swap),
    BottomNavBarVisibilityDelegation by BottomNavBarVisibilityDelegationImpl(),
    PeraWebViewFragmentDelegate.Listener {

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration(isBottomBarNeeded = true)

    @Inject
    lateinit var webInterfaceEventMapper: PeraInternalWebInterfaceEventMapper

    @Inject
    lateinit var isFeatureToggleEnabled: IsFeatureToggleEnabled

    private val peraWebViewViewModel: PeraWebViewViewModel by viewModels()

    private val args: XoSwapFragmentArgs by navArgs()

    private var _binding: FragmentXoSwapBinding? = null
    private val binding: FragmentXoSwapBinding
        get() = _binding!!

    private var webViewFragmentDelegate: PeraWebViewFragmentDelegate? = null

    private val webViewClientListener = object : PeraWebViewClient.PeraWebViewClientListener {
        override fun onWalletConnectUrlDetected(url: String) {
            handleWalletConnectUrl(url)
        }

        override fun onPageStarted() {
            _binding?.progressLayout?.root?.show()
        }

        override fun onPageFinished(title: String?, url: String?) {
            _binding?.progressLayout?.root?.hide()
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            peraWebViewViewModel.processBackPress()
        }
    }

    private val webViewViewModelEventCollector: suspend (PeraWebViewViewModel.ViewEvent) -> Unit = {
        webViewFragmentDelegate?.collectViewEvent(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentXoSwapBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
        collectLatestOnLifecycle(peraWebViewViewModel.viewEvent, webViewViewModelEventCollector)
        initWebViewFragmentDelegate()
    }

    private fun initWebViewFragmentDelegate() {
        webViewFragmentDelegate = PeraWebViewFragmentDelegate(
            fragment = this,
            webView = binding.xoSwapWebView,
            listener = this,
            webViewClientListener = webViewClientListener
        )
        val bridge = PeraWebViewInternalBridge(webInterfaceEventMapper, peraWebViewViewModel::processWebEvents)
        webViewFragmentDelegate?.initWebView(getInitialUrl(), bridge)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webViewFragmentDelegate?.destroyWebView()
        webViewFragmentDelegate = null
        _binding = null
    }

    override fun onNavPublicWebView(params: PushPublicWebView) {
        val navArgs = with(params) { PublicWebViewFragmentNavArgs(url, title, isFavorite) }
        nav(XoSwapFragmentDirections.actionXoSwapFragmentToPublicWebViewFragment(navArgs))
    }

    override fun onNavigateBack() {
        (activity as? CoreMainActivity)?.setBottomNavigationBarSelectedItem(ACCOUNTS_FRAGMENT_NAVIGATION_ID)
    }

    private fun getInitialUrl(): String {
        val isTestPageEnabled = isFeatureToggleEnabled.invoke(FeatureToggle.XO_SWAP_TEST_PAGE.key)
        return StringBuilder(BuildConfig.ONRAMP_URL).apply {
            if (isTestPageEnabled) append("/test")
            if (args.path.isNotBlank()) {
                if (!args.path.startsWith("/")) append("/")
                append(args.path)
            }
        }.toString()
    }

    private companion object {
        val ACCOUNTS_FRAGMENT_NAVIGATION_ID = R.id.accountsFragment
    }
}
