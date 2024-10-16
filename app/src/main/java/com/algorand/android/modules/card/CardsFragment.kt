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

package com.algorand.android.modules.card

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.algorand.android.BuildConfig.CARDS_URL
import com.algorand.android.R
import com.algorand.android.databinding.FragmentCardsBinding
import com.algorand.android.discover.common.ui.model.PeraWebChromeClient
import com.algorand.android.discover.common.ui.model.PeraWebViewClient
import com.algorand.android.discover.common.ui.model.WebViewError
import com.algorand.android.discover.common.ui.model.WebViewError.HTTP_ERROR
import com.algorand.android.discover.common.ui.model.WebViewError.NO_CONNECTION
import com.algorand.android.discover.home.domain.PeraMobileWebInterface
import com.algorand.android.discover.home.domain.PeraMobileWebInterface.Companion.WEB_INTERFACE_NAME
import com.algorand.android.discover.utils.JAVASCRIPT_NAVIGATION
import com.algorand.android.discover.utils.JAVASCRIPT_PERACONNECT
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
import javax.inject.Inject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

@AndroidEntryPoint
class CardsFragment : BasePeraWebViewFragment(R.layout.fragment_cards), PeraMobileWebInterface.WebInterfaceListener {

    private val cardsViewModel: CardsViewModel by viewModels()

    override lateinit var binding: FragmentCardsBinding

    override val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    @Inject
    lateinit var webViewThemeHelper: WebViewThemeHelper

    override fun bindWebView(view: View?) {
        view?.let { binding = FragmentCardsBinding.bind(it) }
    }

    override val basePeraWebViewViewModel: BasePeraWebViewViewModel
        get() = cardsViewModel

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        initWebViewTheme()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        loadCardsUrl()
    }

    private fun initUi() {
        initWebView()
        with(binding) {
            screenStateView.setOnNeutralButtonClickListener {
                screenStateView.hide()
                webView.show()
                webView.loadUrl(CARDS_URL)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        with(binding) {
            val peraWebInterface = PeraMobileWebInterface.create(this@CardsFragment)
            webView.addJavascriptInterface(peraWebInterface, WEB_INTERFACE_NAME)
            webView.webViewClient = PeraWebViewClient(peraWebViewClientListener)
            webView.webChromeClient = PeraWebChromeClient(peraWebViewClientListener)
            webView.evaluateJavascript(JAVASCRIPT_NAVIGATION, null)
            webView.evaluateJavascript(JAVASCRIPT_PERACONNECT, null)
        }
    }

    private fun loadCardsUrl() {
        with(binding.webView) {
            if (url == null) loadUrl(CARDS_URL)
        }
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            cardsViewModel.cardsPreviewFlow.mapNotNull { it?.sendMessageEvent }.distinctUntilChanged(),
            sendMessageEventCollector
        )
        collectLatestOnLifecycle(
            cardsViewModel.cardsPreviewFlow.mapNotNull { it?.errorEvent }.distinctUntilChanged(),
            errorEventCollector
        )
    }

    override fun getAuthorizedAddresses() {
        cardsViewModel.getAuthorizedAddresses()
    }

    override fun getDeviceId() {
        cardsViewModel.getDeviceId()
    }

    override fun closePeraCards() {
        binding.root.post {
            findNavController().navigateUp()
        }
    }

    override fun openSystemBrowser(jsonEncodedPayload: String) {
        cardsViewModel.getOpenSystemBrowserUrl(jsonEncodedPayload)?.let { url ->
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
