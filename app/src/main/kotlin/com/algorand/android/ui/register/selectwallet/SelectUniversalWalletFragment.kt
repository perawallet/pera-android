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

package com.algorand.android.ui.register.selectwallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.StatusBarConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.ui.register.selectwallet.SelectUniversalWalletViewModel.ViewEvent
import com.algorand.android.ui.register.selectwallet.SelectUniversalWalletViewModel.ViewState
import com.algorand.android.ui.register.selectwallet.SelectUniversalWalletViewModel.WalletItemPreview
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectUniversalWalletFragment : DaggerBaseFragment(0) {

    private val viewModel: SelectUniversalWalletViewModel by viewModels()

    private val statusBarConfiguration =
        StatusBarConfiguration(backgroundColor = R.color.tertiary_background)

    private val toolbarConfiguration =
        ToolbarConfiguration(backgroundColor = R.color.primary_background)

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration,
        statusBarConfiguration = statusBarConfiguration
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PeraTheme {
                    SelectUniversalWalletScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar()
        observeEvents()
        viewModel.loadLocalWallets()
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewEvent.collectLatest { event ->
                    when (event) {
                        is ViewEvent.NavigateToCreateWalletNameRegistrationNavigation ->
                            navToCreateWalletNameNavigation(event.accountCreation)

                        is ViewEvent.NavigateToCreateAccountNameRegistrationNavigation ->
                            navToCreateAccountNameRegistrationNavigation(event.accountCreation)

                        is ViewEvent.NavigateBack -> navBack()
                    }
                }
            }
        }
    }

    @Composable
    fun SelectUniversalWalletScreen() {
        val viewState by viewModel.state.collectAsState()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            when (val currentState = viewState) {
                is ViewState.Idle -> Unit
                is ViewState.Loading -> Unit
                is ViewState.Content -> ContentState(currentState.walletItemPreviews)
                is ViewState.Error -> Unit
            }
        }
    }

    @Suppress("LongMethod")
    @Composable
    private fun ContentState(walletItemPreviews: List<WalletItemPreview>) {
        Box(Modifier.padding(horizontal = 24.dp)) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    style = PeraTheme.typography.title.regular.sansMedium,
                    text = stringResource(R.string.select_universal_wallet),
                    color = PeraTheme.colors.text.main
                )
                Text(
                    style = PeraTheme.typography.body.regular.sans,
                    text = stringResource(R.string.create_your_new_account),
                    color = PeraTheme.colors.text.gray,
                    modifier = Modifier.padding(top = 12.dp)
                )
                LazyColumn(modifier = Modifier.padding(top = 24.dp, bottom = 50.dp)) {
                    items(walletItemPreviews) { walletItemPreview ->
                        with(walletItemPreview) {
                            WalletItem(
                                modifier = Modifier,
                                walletName = name,
                                numberOfAccounts = numberOfAccounts,
                                primaryValue = primaryValue,
                                secondaryValue = secondaryValue,
                                icon = ImageVector.vectorResource(id = R.drawable.ic_wallet),
                                iconContentDescription = stringResource(
                                    id = R.string.create_a_new_algorand_account_with
                                ),
                                onClick = { onWalletSelected(seedId, maxAccountIndex) }
                            )
                        }
                    }
                }
            }
            PeraSecondaryButton(
                onClick = ::onCreateNewWalletClick,
                text = stringResource(id = R.string.create_a_new_wallet),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                leftIcon = {
                    PeraIcon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = stringResource(id = R.string.plus),
                        modifier = Modifier
                    )
                }
            )
        }
    }

    @Composable
    fun WalletItem(
        modifier: Modifier = Modifier,
        walletName: String,
        numberOfAccounts: String,
        primaryValue: String,
        secondaryValue: String,
        icon: ImageVector,
        iconContentDescription: String,
        onClick: () -> Unit
    ) {
        Row(
            modifier = modifier
                .clickable { onClick() }
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PeraTheme.colors.layer.grayLighter)
                    .padding(8.dp),
                imageVector = icon,
                contentDescription = iconContentDescription,
                tint = PeraTheme.colors.text.main
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    style = PeraTheme.typography.body.regular.sans,
                    color = PeraTheme.colors.text.main,
                    text = walletName
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.grayLighter,
                    text = numberOfAccounts
                )
            }
            Column {
                Text(
                    style = PeraTheme.typography.body.regular.sansMedium,
                    color = PeraTheme.colors.text.main,
                    text = primaryValue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    style = PeraTheme.typography.footnote.sans,
                    color = PeraTheme.colors.text.gray,
                    text = secondaryValue
                )
            }
        }
    }

    private fun onCreateNewWalletClick() {
        viewModel.createNewHdWallet()
    }

    private fun onWalletSelected(seedId: Int, maxAccountIndex: Int) {
        viewModel.createNewHdAccount(seedId, maxAccountIndex)
    }

    private fun navToCreateWalletNameNavigation(accountCreation: AccountCreation?) {
        nav(
            SelectUniversalWalletFragmentDirections
                .actionSelectUniversalWalletFragmentToCreateWalletNameRegistrationNavigation(accountCreation)
        )
    }

    private fun navToCreateAccountNameRegistrationNavigation(accountCreation: AccountCreation?) {
        nav(
            SelectUniversalWalletFragmentDirections
                .actionSelectUniversalWalletFragmentToCreateAccountNameRegistrationNavigation(accountCreation)
        )
    }

    private fun configureToolbar() {
        getAppToolbar()?.configureStartButton(R.drawable.ic_left_arrow, ::navBack)
    }
}
