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

package com.algorand.android.modules.onboarding.recoverypassphrase.info.ui

import MnemonicTypeCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.PeraBodyText
import com.algorand.android.ui.compose.widget.PeraIconBig
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraHeadelineText
import com.algorand.android.ui.compose.widget.PeraTitleText
import kotlinx.coroutines.launch

class RecoverAccountInfoFragment : BaseInfoFragment() {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    @Composable
    override fun Icon(modifier: Modifier) =
        PeraIconBig(
            painter = painterResource(id = R.drawable.ic_key),
            contentDescription = "key",
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraHeadelineText(
            modifier = modifier,
            text = stringResource(id = R.string.recover_an_algorand)
        )

    @Composable
    override fun Description(modifier: Modifier) =
        PeraBodyText(
            modifier = modifier,
            text = stringResource(id = R.string.in_the_following)
        )

    @ExperimentalMaterial3Api
    @Composable
    override fun PrimaryButton(modifier: Modifier, bottomSheetState: BottomSheetScaffoldState) {
        val coroutineScope = rememberCoroutineScope()

        PeraPrimaryButton(
            modifier = modifier,
            onClick = {
                coroutineScope.launch {
                    if (bottomSheetState.bottomSheetState.currentValue == SheetValue.Hidden) {
                        bottomSheetState.bottomSheetState.expand()
                    }
                }
            },
            text = stringResource(id = R.string.recover_an_algorand)
        )
    }

    @SuppressWarnings("LongMethod")
    @ExperimentalMaterial3Api
    @Composable
    override fun BottomSheetContent(bottomSheetState: BottomSheetScaffoldState) {
        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        end = 40.dp,
                        bottom = 24.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.bottomSheetState.hide()
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Close"
                    )
                }
                Spacer(Modifier.weight(HALF_SIZE))
                PeraTitleText(
                    text = "Select your Mnemonic type"
                )
                Spacer(Modifier.weight(HALF_SIZE))
            }

            MnemonicTypeCard(
                title = "BIP39",
                description = "New inter-operable format that enables important features like HD Wallet",
                footer = "24 Key mnemonic keys",
                highlighted = "Recommended",
                onClick = {
                    navigateToRecoverWithPassphraseFragment()
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.hide()
                    }
                }
            )

            MnemonicTypeCard(
                title = "ALGO25",
                description = "Legacy format that is specific to Algorand ecosystem",
                footer = "25 Key mnemonic keys",
                onClick = {
                    navigateToRecoverWithPassphraseFragment()
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.hide()
                    }
                }
            )
        }
    }

    private fun navigateToRecoverWithPassphraseFragment() {
        nav(RecoverAccountInfoFragmentDirections.actionRecoverAccountInfoFragmentToRecoverWithPassphraseNavigation())
    }
}
