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

package com.algorand.android.ui.register.createaccount.passphraseverified

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.PeraDescriptionText
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraTitleText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PassphraseVerifiedInfoFragment : BaseInfoFragment() {
    override val fragmentConfiguration = FragmentConfiguration()

    private val args: PassphraseVerifiedInfoFragmentArgs by navArgs()

    @Composable
    override fun Icon(modifier: Modifier) =
        Image(
            painter = painterResource(id = R.drawable.ic_shield_check_large),
            colorFilter = ColorFilter.tint(color = colorResource(R.color.info_image_color)),
            contentDescription = "check large",
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraTitleText(
            modifier = modifier,
            text = stringResource(id = R.string.passphrase_verified)
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraDescriptionText(
            text = stringResource(id = R.string.keep_this_recovery),
            modifier = modifier
        )

    @Composable
    override fun PrimaryButton(modifier: Modifier) =
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
                        accountCreation.copy(
                            tempAccount = accountCreation.tempAccount.copy(isBackedUp = true)
                        )
                    )
            )
        } ?: navToHomeNavigation()
    }

    private fun navToHomeNavigation() {
        nav(PassphraseVerifiedInfoFragmentDirections.actionPassphraseVerifiedInfoFragmentToHomeNavigation())
    }
}
