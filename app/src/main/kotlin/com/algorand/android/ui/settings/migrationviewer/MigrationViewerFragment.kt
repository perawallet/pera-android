
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

package com.algorand.android.ui.settings.migrationviewer

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.Account
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.ErrorContentWidget
import com.algorand.android.ui.compose.widget.PeraBodyText
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.settings.migrationviewer.MigrationViewerViewModel.ViewEvent
import com.algorand.android.ui.settings.migrationviewer.MigrationViewerViewModel.ViewState
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.detail.domain.model.AccountDetail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MigrationViewerFragment : DaggerBaseFragment(0) {

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.migration_viewer,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val migrationViewerViewModel: MigrationViewerViewModel by viewModels()

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                PeraTheme {
                    MigrationViewerContent()
                }
            }
        }
    }

    @Composable
    fun MigrationViewerContent() {
        val viewState = migrationViewerViewModel.state.collectAsState().value

        LaunchedEffect(Unit) {
            migrationViewerViewModel.viewEvent.collect { event ->
                when (event) {
                    is ViewEvent.NavigateBack -> {
                        navBack()
                    }
                }
            }
        }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            when (viewState) {
                is ViewState.Loading -> LoadingStateContent()
                is ViewState.Error -> ErrorStateContent(viewState.error)
                is ViewState.Content -> ContentStateContent(viewState)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    @Composable
    fun LoadingStateContent() {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    }

    @Composable
    fun ErrorStateContent(error: String) {
        ErrorContentWidget(
            message = stringResource(R.string.error_state_message_default),
            showNavigateBackButton = true,
            onClick = {
                migrationViewerViewModel.triggerEvent(ViewEvent.NavigateBack)
            }
        )
        showGlobalError(errorMessage = error, tag = baseActivityTag)
    }

    @Composable
    fun ContentStateContent(viewState: ViewState.Content) {
        val oldAccounts = viewState.oldAccounts
        val newAccounts = viewState.newAccounts

        PeraBodyText(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            text = "Pre v6 Accounts",
            textAlign = TextAlign.Center
        )

        AccountList(accounts = oldAccounts)

        Spacer(modifier = Modifier.height(40.dp))

        PeraBodyText(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            text = "Post v6 Accounts",
            textAlign = TextAlign.Center
        )

        AccountDetailList(accounts = newAccounts)

        Spacer(modifier = Modifier.height(40.dp))

        PeraPrimaryButton(
            modifier = Modifier.width(300.dp),
            text = stringResource(id = R.string.migrate),
            onClick = {
                migrationViewerViewModel.migrate()
            }
        )
    }

    @Composable
    fun AccountList(accounts: List<Account>) {
        LazyColumn {
            if (accounts.isEmpty()) {
                item {
                    PeraBodyText(
                        text = stringResource(id = R.string.no_accounts),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PeraBodyText(
                            modifier = Modifier.weight(1f),
                            text = "Address",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PeraBodyText(
                            modifier = Modifier.weight(1f),
                            text = "Type",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PeraBodyText(
                            modifier = Modifier.weight(1f),
                            text = "Index",
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(accounts) { account ->
                    AccountListItem(
                        account.address,
                        account.type.toString(),
                        account.index.toString()
                    )
                }
            }
        }
    }

    @Composable
    fun AccountDetailList(accounts: List<AccountDetail>) {
        LazyColumn {
            if (accounts.isEmpty()) {
                item {
                    PeraBodyText(
                        text = stringResource(id = R.string.no_accounts),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PeraBodyText(
                            modifier = Modifier.weight(1f),
                            text = "Address",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PeraBodyText(
                            modifier = Modifier.weight(1f),
                            text = "Type",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PeraBodyText(
                            modifier = Modifier.weight(1f),
                            text = "Index",
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(accounts) { account ->
                    AccountListItem(
                        account.address,
                        account.accountType?.toString()
                            ?: account.accountRegistrationType?.toString()
                            ?: "Unknown",
                        account.customAccountInfo?.orderIndex.toString()
                    )
                }
            }
        }
    }

    @Composable
    fun AccountListItem(address: String, type: String, index: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            PeraBodyText(
                modifier = Modifier.weight(1f),
                text = address.toShortenedAddress(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))
            PeraBodyText(
                modifier = Modifier.weight(1f),
                text = type,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))
            PeraBodyText(
                modifier = Modifier.weight(1f),
                text = index,
                textAlign = TextAlign.Center
            )
        }
    }
}
