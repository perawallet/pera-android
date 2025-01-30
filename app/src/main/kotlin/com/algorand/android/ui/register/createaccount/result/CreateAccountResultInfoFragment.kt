/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.register.createaccount.result

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.PeraDescriptionText
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.PeraTitleText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountResultInfoFragment : BaseInfoFragment() {
    override val fragmentConfiguration = FragmentConfiguration()

    private val createAccountResultInfoViewModel: CreateAccountResultInfoViewModel by viewModels()

    @Composable
    override fun Icon(modifier: Modifier) =
        Image(
            painter = painterResource(id = R.drawable.ic_check),
            colorFilter = ColorFilter.tint(color = colorResource(R.color.info_image_color)),
            contentDescription = "check",
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraTitleText(
            modifier = modifier,
            text = stringResource(id = createAccountResultInfoViewModel.getPreviewTitle())
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraDescriptionText(
            text = stringResource(id = createAccountResultInfoViewModel.getPreviewDescription()),
            modifier = modifier
        )

    @Composable
    override fun PrimaryButton(modifier: Modifier) =
        PeraPrimaryButton(
            onClick = { navToMeldNavigation() },
            modifier = modifier,
            text = stringResource(id = createAccountResultInfoViewModel.getPreviewFirstButtonText())
        )

    @Composable
    override fun SecondaryButton(modifier: Modifier) =
        PeraSecondaryButton(
            onClick = { onStartUsingPeraClick() },
            modifier = modifier,
            text = stringResource(id = createAccountResultInfoViewModel.getPreviewSecondButtonText())
        )

    private fun onStartUsingPeraClick() {
        createAccountResultInfoViewModel.logOnboardingStartUsingPeraClickEvent()
        if (createAccountResultInfoViewModel.shouldForceLockNavigation()) {
            navToForceLockNavigation()
        } else {
            navToHomeNavigation()
        }
    }

    private fun navToHomeNavigation() {
        nav(CreateAccountResultInfoFragmentDirections.actionCreateAccountResultInfoFragmentToHomeNavigation())
    }

    private fun navToForceLockNavigation() {
        nav(
            CreateAccountResultInfoFragmentDirections.actionCreateAccountResultInfoFragmentToLockPreferenceNavigation(
                shouldNavigateHome = true
            )
        )
    }

    private fun navToMeldNavigation() {
        createAccountResultInfoViewModel.logOnboardingBuyAlgoClickEvent()
        nav(CreateAccountResultInfoFragmentDirections.actionCreateAccountResultInfoFragmentToMeldNavigation())
    }
}
