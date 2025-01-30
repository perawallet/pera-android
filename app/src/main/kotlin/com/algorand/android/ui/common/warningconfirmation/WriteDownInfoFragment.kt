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

package com.algorand.android.ui.common.warningconfirmation

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.common.warningconfirmation.WriteDownInfoFragmentDirections.Companion.actionWriteDownInfoFragmentToBackupAccountSelectionFragment
import com.algorand.android.ui.common.warningconfirmation.WriteDownInfoFragmentDirections.Companion.actionWriteDownInfoFragmentToBackupPassphraseAccountNameNavigation
import com.algorand.android.ui.common.warningconfirmation.WriteDownInfoFragmentDirections.Companion.actionWriteDownInfoFragmentToBackupPassphrasesNavigation
import com.algorand.android.ui.compose.widget.PeraDescriptionText
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.PeraTitleText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriteDownInfoFragment : BaseInfoFragment() {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration
    )

    private val writeDownInfoViewModel: WriteDownInfoViewModel by viewModels()

    private val args: WriteDownInfoFragmentArgs by navArgs()

    @Composable
    override fun Icon(modifier: Modifier) =
        Image(
            painter = painterResource(id = R.drawable.ic_pen),
            colorFilter = ColorFilter.tint(color = colorResource(R.color.info_image_color)),
            contentDescription = "pen",
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraTitleText(
            modifier = modifier,
            text = stringResource(id = R.string.prepare_to_write)
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraDescriptionText(
            text = stringResource(
                id = R.string.the_only_way_to
            ),
            modifier = modifier
        )

    @Composable
    override fun Warning(modifier: Modifier) =
        Text(
            text = stringResource(id = R.string.do_not_share),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = modifier
        )

    @Composable
    override fun PrimaryButton(modifier: Modifier) =
        PeraPrimaryButton(
            onClick = { onFirstButtonClicked() },
            modifier = modifier,
            text = stringResource(id = R.string.im_ready_to_begin)
        )

    @Composable
    override fun SecondaryButton(modifier: Modifier) {
        if (args.publicKeysOfAccountsToBackup.isEmpty()) {
            PeraSecondaryButton(
                onClick = { onSecondButtonClicked() },
                modifier = modifier,
                text = stringResource(id = R.string.skip_for_now)
            )
        }
    }

    private fun onFirstButtonClicked() {
        if (args.publicKeysOfAccountsToBackup.size > 1) {
            navToBackupAccountSelectionFragment()
        } else {
            navToBackupPassphraseFragment()
        }
    }

    private fun onSecondButtonClicked() {
        navToBackupPassphraseAccountNameNavigation()
    }

    private fun navToBackupAccountSelectionFragment() {
        nav(actionWriteDownInfoFragmentToBackupAccountSelectionFragment(args.publicKeysOfAccountsToBackup))
    }

    private fun navToBackupPassphraseFragment() {
        nav(
            actionWriteDownInfoFragmentToBackupPassphrasesNavigation(
                args.publicKeysOfAccountsToBackup.firstOrNull().orEmpty(),
                accountCreation = args.accountCreation
            )
        )
    }

    private fun navToBackupPassphraseAccountNameNavigation() {
        args.accountCreation?.let { accountCreation ->
            nav(actionWriteDownInfoFragmentToBackupPassphraseAccountNameNavigation(accountCreation))
        }
    }
}
