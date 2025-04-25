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

package com.algorand.android.ui.common.warningconfirmation

import android.os.Bundle
import android.view.View
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.customviews.toolbar.buttoncontainer.model.IconButton
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.tracking.core.PeraClickEvent
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.common.warningconfirmation.BackupInfoFragmentDirections.Companion.actionBackupInfoFragmentToBackupPassphraseAccountNameNavigation
import com.algorand.android.ui.common.warningconfirmation.BackupInfoFragmentDirections.Companion.actionBackupInfoFragmentToWriteDownInfoFragment
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraHeadlineText
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.utils.browser.openRecoveryPassphraseSupportUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupInfoFragment : BaseInfoFragment() {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val backupInfoViewModel: BackupInfoViewModel by viewModels()

    private val args: BackupInfoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbar()
    }

    @Composable
    override fun Icon(modifier: Modifier) =
        PeraIcon(
            painter = painterResource(id = R.drawable.ic_shield),
            contentDescription = stringResource(R.string.shield),
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraHeadlineText(
            modifier = modifier,
            text = stringResource(id = R.string.create_a_passphrase_backup)
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraBodyText(
            text = stringResource(
                id = R.string.creating_a_passphrase_backup
            ),
            modifier = modifier
        )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun PrimaryButton(modifier: Modifier, sheetState: SheetState) =
        PeraPrimaryButton(
            onClick = { navToWriteDownFragment() },
            modifier = modifier,
            text = stringResource(id = R.string.i_understand)
        )

    @Composable
    override fun SecondaryButton(modifier: Modifier) {
        if (args.accountsToBackup.isEmpty()) {
            PeraSecondaryButton(
                onClick = {
                    backupInfoViewModel.logEvent(PeraClickEvent.TAP_ONBOARDING_CREATE_PASSPHRASE_SKIP)
                    navToBackupPassphraseAccountNameNavigation()
                },
                modifier = modifier,
                text = stringResource(id = R.string.skip_for_now)
            )
        }
    }

    private fun navToWriteDownFragment() {
        val accountCreation = getAccountCreation()

        when {
            args.accountsToBackup.isNotEmpty() -> {
                backupInfoViewModel.logOnboardingIUnderstandClickEvent()
                nav(actionBackupInfoFragmentToWriteDownInfoFragment(
                    args.accountsToBackup,
                    null
                ))
            }
            accountCreation != null -> {
                backupInfoViewModel.logOnboardingIUnderstandClickEvent()
                nav(actionBackupInfoFragmentToWriteDownInfoFragment(
                    arrayOf(),
                    accountCreation
                ))
            }
            else -> navBack()
        }
    }

    private fun navToBackupPassphraseAccountNameNavigation() {
        val accountCreation = getAccountCreation()
        accountCreation?.let {
            nav(actionBackupInfoFragmentToBackupPassphraseAccountNameNavigation(it))
        } ?: run {
            navBack()
        }
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
        context?.openRecoveryPassphraseSupportUrl()
    }

    private fun getAccountCreation(): AccountCreation? {
        return try {
            when (backupInfoViewModel.onboardingAccountType) {
                is OnboardingAccountType.HdKey -> {
                    backupInfoViewModel.createHdKeyAccount()
                }
                is OnboardingAccountType.Algo25 -> {
                    backupInfoViewModel.createAlgo25Account()
                }
            }
        } catch (exception: Exception) {
            navBack()
            null
        }
    }

    companion object {
        private const val ACCOUNT_CREATION_KEY = "accountCreation"
    }
}
