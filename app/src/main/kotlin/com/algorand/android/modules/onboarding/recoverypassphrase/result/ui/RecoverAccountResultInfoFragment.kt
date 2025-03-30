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

package com.algorand.android.modules.onboarding.recoverypassphrase.result.ui

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
import com.algorand.android.modules.onboarding.recoverypassphrase.result.ui.RecoverAccountResultInfoViewModel.ViewState
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.PeraBodyText
import com.algorand.android.ui.compose.widget.PeraHeadlineText
import com.algorand.android.ui.compose.widget.PeraIcon
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraSecondaryButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoverAccountResultInfoFragment : BaseInfoFragment() {
    override val fragmentConfiguration = FragmentConfiguration()

    private val recoverAccountResultInfoViewModel: RecoverAccountResultInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recoverAccountResultInfoViewModel.setDefaultState()
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
        val state = recoverAccountResultInfoViewModel.state.collectAsStateWithLifecycle().value
        val titleText = when (state) {
            is ViewState.DefaultState -> stringResource(id = state.titleTextRes)
            else -> ""
        }

        PeraHeadlineText(
            modifier = modifier,
            text = titleText
        )
    }

    @Composable
    override fun Description(modifier: Modifier) {
        val state = recoverAccountResultInfoViewModel.state.collectAsStateWithLifecycle().value
        val descriptionText = when (state) {
            is ViewState.DefaultState -> stringResource(id = state.descriptionTextRes)
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
        val state = recoverAccountResultInfoViewModel.state.collectAsStateWithLifecycle().value
        val firstButtonText = when (state) {
            is ViewState.DefaultState -> stringResource(id = state.firstButtonTextRes)
            else -> ""
        }

        PeraPrimaryButton(
            onClick = { navToMeldNavigation() },
            modifier = modifier,
            text = firstButtonText
        )
    }

    @Composable
    override fun SecondaryButton(modifier: Modifier) {
        val state = recoverAccountResultInfoViewModel.state.collectAsStateWithLifecycle().value
        val secondButtonText = when (state) {
            is ViewState.DefaultState -> stringResource(id = state.secondButtonTextRes)
            else -> ""
        }

        PeraSecondaryButton(
            onClick = { onStartUsingPeraClick() },
            modifier = modifier,
            text = secondButtonText
        )
    }

    private fun onStartUsingPeraClick() {
        if (recoverAccountResultInfoViewModel.shouldForceLockNavigation()) {
            navToForceLockNavigation()
        } else {
            navToHomeNavigation()
        }
    }

    private fun navToHomeNavigation() {
        nav(RecoverAccountResultInfoFragmentDirections.actionRecoverAccountResultInfoFragmentToHomeNavigation())
    }

    private fun navToForceLockNavigation() {
        nav(
            RecoverAccountResultInfoFragmentDirections.actionRecoverAccountResultInfoFragmentToLockPreferenceNavigation(
                shouldNavigateHome = true
            )
        )
    }

    private fun navToMeldNavigation() {
        nav(RecoverAccountResultInfoFragmentDirections.actionRecoverAccountResultInfoFragmentToMeldNavigation())
    }
}
