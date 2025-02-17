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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.PeraBodyText
import com.algorand.android.ui.compose.widget.PeraHeadlineText
import com.algorand.android.ui.compose.widget.PeraIconBig
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraTitleText
import com.algorand.android.utils.PassphraseKeywordUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecoverAccountInfoFragment : BaseInfoFragment() {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val recoveryAccountInfoViewModel by viewModels<RecoveryAccountInfoViewModel>()

    @Composable
    override fun Icon(modifier: Modifier) =
        PeraIconBig(
            painter = painterResource(id = R.drawable.ic_key),
            contentDescription = "key",
            modifier = modifier
        )

    @Composable
    override fun Title(modifier: Modifier) =
        PeraHeadlineText(
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
                    if (recoveryAccountInfoViewModel.isHdWalletToggleEnabled()) {
                        if (bottomSheetState.bottomSheetState.currentValue == SheetValue.Hidden) {
                            bottomSheetState.bottomSheetState.expand()
                        }
                    } else {
                        navigateToRecoverWithPassphraseFragment(
                            PassphraseKeywordUtils.ALGO25_WALLET_PASSPHRASES_WORD_COUNT
                        )
                    }
                }
            },
            text = stringResource(id = R.string.recover_an_algorand)
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Suppress("MagicNumber")
    @Composable
    fun BottomSheetHeader(bottomSheetState: BottomSheetScaffoldState) {
        val coroutineScope = rememberCoroutineScope()
        Row(
            modifier = Modifier
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
            Spacer(Modifier.weight(0.1f))

            PeraTitleText(
                text = "Select your Mnemonic type"
            )
            Spacer(Modifier.weight(1f))
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun BottomSheetContent(bottomSheetState: BottomSheetScaffoldState) {
        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            BottomSheetHeader(bottomSheetState)

            MnemonicTypeCard(
                title = "Bip39",
                description = "New inter-operable format that enables important features like HD Wallet",
                footer = "24 Key mnemonic keys",
                onClick = {
                    navigateToRecoverWithPassphraseFragment(
                        PassphraseKeywordUtils.HD_WALLET_PASSPHRASES_WORD_COUNT
                    )
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.hide() // Use sheetState directly
                    }
                }
            )

            MnemonicTypeCard(
                title = "Algo25",
                description = "Legacy format that is specific to Algorand ecosystem",
                footer = "25 Key mnemonic keys",
                onClick = {
                    navigateToRecoverWithPassphraseFragment(
                        PassphraseKeywordUtils.ALGO25_WALLET_PASSPHRASES_WORD_COUNT
                    )
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.hide() // Use sheetState directly
                    }
                }
            )
        }
    }

    private fun navigateToRecoverWithPassphraseFragment(wordCount: Int) {
        nav(RecoverAccountInfoFragmentDirections
            .actionRecoverAccountInfoFragmentToRecoverWithPassphraseNavigation(
                mnemonic = null,
                wordCount = wordCount)
        )
    }
}
