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

package com.algorand.android.modules.staking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.algorand.android.R
import com.algorand.android.databinding.FragmentStakingBinding
import com.algorand.android.discover.common.ui.model.PeraWebChromeClient
import com.algorand.android.discover.common.ui.model.PeraWebViewClient
import com.algorand.android.discover.common.ui.model.WebViewError
import com.algorand.android.discover.common.ui.model.WebViewError.HTTP_ERROR
import com.algorand.android.discover.common.ui.model.WebViewError.NO_CONNECTION
import com.algorand.android.discover.home.domain.PeraMobileWebInterface
import com.algorand.android.discover.home.domain.PeraMobileWebInterface.Companion.WEB_INTERFACE_NAME
import com.algorand.android.discover.utils.JAVASCRIPT_PERACONNECT
import com.algorand.android.discover.utils.getCustomUrl
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ScreenState
import com.algorand.android.modules.perawebview.WebViewThemeHelper
import com.algorand.android.modules.perawebview.ui.BasePeraWebViewFragment
import com.algorand.android.modules.perawebview.ui.BasePeraWebViewViewModel
import com.algorand.android.utils.Event
import com.algorand.android.utils.browser.openExternalBrowserApp
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class StakingFragment : BasePeraWebViewFragment(R.layout.fragment_staking),
    PeraMobileWebInterface.WebInterfaceListener {

    private val stakingViewModel: StakingViewModel by viewModels()

    override lateinit var binding: FragmentStakingBinding

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    @Inject
    lateinit var webViewThemeHelper: WebViewThemeHelper

    override fun bindWebView(view: View?) {
        view?.let { binding = FragmentStakingBinding.bind(it) }
    }

    override val basePeraWebViewViewModel: BasePeraWebViewViewModel
        get() = stakingViewModel

    private val sendMessageEventCollector: suspend (Event<String>) -> Unit = {
        it.consume()?.let { message ->
            sendWebMessage(message)
        }
    }

    private val errorEventCollector: suspend (Event<WebViewError>) -> Unit = {
        it.consume()?.let { error ->
            handleWebViewError(error)
        }
    }

    private val pageFinishedCollector: suspend (Event<Unit>) -> Unit = {
        it.consume()?.let {
            binding.webView.evaluateJavascript(JAVASCRIPT_PERACONNECT, null)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        initWebViewTheme()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        loadStakingUrl()
    }

    private fun initUi() {
        initWebView()
        with(binding) {
            screenStateView.setOnNeutralButtonClickListener {
                screenStateView.hide()
                webView.show()
                webView.loadUrl(stakingViewModel.getStakingUrl())
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        with(binding) {
            val peraWebInterface = PeraMobileWebInterface.create(this@StakingFragment)
            webView.addJavascriptInterface(peraWebInterface, WEB_INTERFACE_NAME)
            webView.webViewClient = PeraWebViewClient(peraWebViewClientListener)
            webView.webChromeClient = PeraWebChromeClient(peraWebViewClientListener)
            webView.evaluateJavascript(JAVASCRIPT_PERACONNECT, null)
        }
    }

    private fun loadStakingUrl() {
        with(binding.webView) {
            if (url == null) {
                val webViewTheme = webViewThemeHelper.getWebViewThemeFromThemePreference(context)
                val locale = Locale.getDefault().language
                val webviewUrl = getCustomUrl(
                    stakingViewModel.getStakingUrl(),
                    webViewTheme,
                    stakingViewModel.getPrimaryCurrencyId(),
                    locale
                )
                loadUrl(webviewUrl)
            }
        }
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            stakingViewModel.stakingPreviewFlow.mapNotNull { it?.sendMessageEvent }.distinctUntilChanged(),
            sendMessageEventCollector
        )
        collectLatestOnLifecycle(
            stakingViewModel.stakingPreviewFlow.mapNotNull { it?.errorEvent }.distinctUntilChanged(),
            errorEventCollector
        )
        collectLatestOnLifecycle(
            stakingViewModel.stakingPreviewFlow.mapNotNull { it?.onPageFinished }.distinctUntilChanged(),
            pageFinishedCollector
        )
    }

    override fun getAuthorizedAddresses() {
        stakingViewModel.getAuthorizedAddresses()
    }

    override fun getDeviceId() {
        stakingViewModel.getDeviceId()
    }

    override fun closeWebView() {
        binding.root.post {
            findNavController().navigateUp()
        }
    }

    override fun openDappWebview(jsonEncodedPayload: String) {
        stakingViewModel.getOpenDappWebview(jsonEncodedPayload)?.let { dappInfo ->
            nav(
                StakingFragmentDirections.actionStakingFragmentToDiscoverDappNavigation(
                    dappUrl = dappInfo.url ?: stakingViewModel.getStakingUrl(),
                    dappTitle = dappInfo.name ?: "",
                    favorites = null, // always empty for now
                    showFavorites = false
                )
            )
        }
    }

    override fun openSystemBrowser(jsonEncodedPayload: String) {
        stakingViewModel.getOpenSystemBrowserUrl(jsonEncodedPayload)?.let { url ->
            context?.openExternalBrowserApp(url)
        }
    }

    private fun handleWebViewError(error: WebViewError) {
        val errorState = when (error) {
            NO_CONNECTION -> ScreenState.ConnectionError()
            HTTP_ERROR -> ScreenState.DefaultError()
        }
        with(binding) {
            screenStateView.setupUi(errorState)
            screenStateView.show()
            webView.hide()
        }
    }

    private fun sendWebMessage(message: String) {
        binding.webView.post {
            binding.webView.evaluateJavascript(message, null)
        }
    }

    private fun initWebViewTheme() {
        getWebView(binding.root)?.let { currentWebView ->
            webViewThemeHelper.initWebViewTheme(currentWebView)
        }
    }
}
