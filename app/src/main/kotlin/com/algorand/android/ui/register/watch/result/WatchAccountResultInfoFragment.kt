/*
 *  Copyright 2025 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.register.watch.result

import android.os.Bundle
import android.view.View
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraHeadlineText
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchAccountResultInfoFragment : BaseInfoFragment() {
    override val fragmentConfiguration = FragmentConfiguration()

    private val watchAccountResultInfoViewModel: WatchAccountResultInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        watchAccountResultInfoViewModel.setDefaultState()
    }

    @Composable
    override fun Icon(modifier: Modifier) =
        PeraIcon(
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = stringResource(id = R.string.check),
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) {
        val state = watchAccountResultInfoViewModel.state.collectAsStateWithLifecycle().value
        val titleText = when (state) {
            is WatchAccountResultInfoViewModel.ViewState.DefaultState -> stringResource(id = state.titleTextRes)
            else -> ""
        }

        PeraHeadlineText(
            modifier = modifier,
            text = titleText
        )
    }

    @Composable
    override fun Description(modifier: Modifier) {
        val state = watchAccountResultInfoViewModel.state.collectAsStateWithLifecycle().value
        val descriptionText = when (state) {
            is WatchAccountResultInfoViewModel.ViewState.DefaultState -> stringResource(id = state.descriptionTextRes)
            else -> ""
        }

        PeraBodyText(
            text = descriptionText,
            modifier = modifier
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun PrimaryButton(modifier: Modifier, sheetState: SheetState) {
        val state = watchAccountResultInfoViewModel.state.collectAsStateWithLifecycle().value
        val firstButtonText = when (state) {
            is WatchAccountResultInfoViewModel.ViewState.DefaultState -> stringResource(id = state.firstButtonTextRes)
            else -> ""
        }

        PeraPrimaryButton(
            onClick = { onContinueClick() },
            modifier = modifier,
            text = firstButtonText
        )
    }
    private fun onContinueClick() {
        if (watchAccountResultInfoViewModel.shouldForceLockNavigation()) {
            navToForceLockNavigation()
        } else {
            navToHomeNavigation()
        }
    }

    private fun navToHomeNavigation() {
        nav(WatchAccountResultInfoFragmentDirections.actionWatchAccountResultInfoFragmentToHomeNavigation())
    }

    private fun navToForceLockNavigation() {
        nav(
            WatchAccountResultInfoFragmentDirections.actionWatchAccountResultInfoFragmentToLockPreferenceNavigation(
                shouldNavigateHome = true
            )
        )
    }
}
