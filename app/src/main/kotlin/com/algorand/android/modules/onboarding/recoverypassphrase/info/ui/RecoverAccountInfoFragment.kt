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

package com.algorand.android.modules.onboarding.recoverypassphrase.info.ui

import android.os.Bundle
import android.view.View
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.customviews.toolbar.buttoncontainer.model.IconButton
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraHeadlineText
import com.algorand.android.utils.browser.RECOVER_OR_IMPORT_ACCOUNT_SUPPORT_URL
import com.algorand.android.utils.browser.openUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecoverAccountInfoFragment : BaseInfoFragment() {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    private val args: RecoverAccountInfoFragmentArgs by navArgs()

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar()
    }

    private fun configureToolbar() {
        getAppToolbar()?.setEndButton(
            button = IconButton(
                R.drawable.ic_info,
                onClick = ::onInfoClick
            )
        )
    }

    private fun onInfoClick() {
        context?.openUrl(RECOVER_OR_IMPORT_ACCOUNT_SUPPORT_URL)
    }

    @Composable
    override fun Icon(modifier: Modifier) =
        PeraIcon(
            painter = painterResource(id = R.drawable.ic_key),
            contentDescription = stringResource(id = R.string.key),
            modifier = modifier,
            tintColor = PeraTheme.colors.link.icon
        )

    @Composable
    override fun Title(modifier: Modifier) {
        PeraHeadlineText(
            modifier = modifier,
            text = stringResource(id = R.string.recover_an_algorand_wallet)
        )
    }

    @Composable
    override fun Description(modifier: Modifier) {
        PeraBodyText(
            modifier = modifier,
            text = stringResource(id = R.string.in_the_following_wallet)
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun PrimaryButton(modifier: Modifier, sheetState: SheetState) {
        val coroutineScope = rememberCoroutineScope()
        val showBottomSheet = rememberSaveable { mutableStateOf(false) }
        PeraPrimaryButton(
            modifier = modifier,
            onClick = {
                coroutineScope.launch {
                    navigateToRecoverWithPassphraseNavigation(
                        args.onboardingAccountType
                    )
                }
            },
            text = stringResource(id = R.string.recover_an_algorand_wallet)
        )

        if (showBottomSheet.value) {
            androidx.compose.material3.ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet.value = false
                },
                sheetState = sheetState,
                containerColor = PeraTheme.colors.background.primary,
                contentColor = PeraTheme.colors.text.grayLighter,
            ) {
                BottomSheetContent(
                    sheetState = sheetState,
                    onDismiss = { showBottomSheet.value = false }
                )
            }
        }
    }

    private fun navigateToRecoverWithPassphraseNavigation(onboardingAccountType: OnboardingAccountType) {
        nav(
            RecoverAccountInfoFragmentDirections
                .actionRecoverAccountInfoFragmentToRecoverWithPassphraseNavigation(
                    onboardingAccountType = onboardingAccountType
                )
        )
    }
}
