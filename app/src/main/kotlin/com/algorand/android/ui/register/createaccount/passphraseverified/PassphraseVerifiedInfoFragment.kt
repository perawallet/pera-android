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

package com.algorand.android.ui.register.createaccount.passphraseverified

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.text.PeraBodyText
import com.algorand.android.ui.compose.widget.text.PeraHeadlineText
import com.algorand.android.ui.compose.widget.icon.PeraIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PassphraseVerifiedInfoFragment : BaseInfoFragment() {
    override val fragmentConfiguration = FragmentConfiguration()

    private val args: PassphraseVerifiedInfoFragmentArgs by navArgs()

    @Composable
    override fun Icon(modifier: Modifier) =
        PeraIcon(
            painter = painterResource(id = R.drawable.ic_shield_check_large),
            contentDescription = stringResource(id = R.string.check),
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraHeadlineText(
            modifier = modifier,
            text = stringResource(id = R.string.passphrase_verified)
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraBodyText(
            text = stringResource(id = R.string.keep_this_recovery),
            modifier = modifier
        )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun PrimaryButton(modifier: Modifier, sheetState: SheetState) =
        PeraPrimaryButton(
            onClick = { handleNextNavigation() },
            modifier = modifier,
            text = stringResource(
                id = if (args.accountCreation != null) {
                    R.string.next
                } else {
                    R.string.done
                }
            )
        )

    private fun handleNextNavigation() {
        args.accountCreation?.let { accountCreation ->
            nav(
                PassphraseVerifiedInfoFragmentDirections
                    .actionPassphraseVerifiedInfoFragmentToBackupPassphraseAccountNameNavigation(
                        accountCreation.copy(isBackedUp = true)
                    )
            )
        } ?: navToHomeNavigation()
    }

    private fun navToHomeNavigation() {
        nav(PassphraseVerifiedInfoFragmentDirections.actionPassphraseVerifiedInfoFragmentToHomeNavigation())
    }
}
