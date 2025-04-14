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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.ui.common.BaseInfoFragment
import com.algorand.android.ui.compose.widget.PeraBodyText
import com.algorand.android.ui.compose.widget.PeraCard
import com.algorand.android.ui.compose.widget.PeraHeadlineText
import com.algorand.android.ui.compose.widget.PeraIcon
import com.algorand.android.ui.compose.widget.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.PeraTitleText
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
        PeraIcon(
            painter = painterResource(id = R.drawable.ic_key),
            contentDescription = stringResource(id = R.string.key),
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun PrimaryButton(modifier: Modifier, sheetState: SheetState) {
        val coroutineScope = rememberCoroutineScope()
        val showBottomSheet = rememberSaveable { mutableStateOf(false) }

        PeraPrimaryButton(
            modifier = modifier,
            onClick = {
                coroutineScope.launch {
                    if (recoveryAccountInfoViewModel.isHdWalletToggleEnabled()) {
                        showBottomSheet.value = true
                    } else {
                        navigateToRecoverWithPassphraseFragment(
                            OnboardingAccountType.Algo25
                        )
                    }
                }
            },
            text = stringResource(id = R.string.recover_an_algorand)
        )

        if (showBottomSheet.value) {
            androidx.compose.material3.ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet.value = false
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                BottomSheetContent(
                    sheetState = sheetState,
                    onDismiss = { showBottomSheet.value = false }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun BottomSheetContent(
        sheetState: SheetState,
        onDismiss: () -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            BottomSheetHeader(sheetState, onDismiss)

            PeraCard(
                title = stringResource(R.string.mnemonic_type_bip39_title),
                highlighted = stringResource(R.string.new_text),
                description = stringResource(R.string.mnemonic_type_bip39_description),
                footer = stringResource(R.string.mnemonic_type_bip39_footer),
                onClick = {
                    navigateToRecoverWithPassphraseFragment(
                        OnboardingAccountType.HdKey
                    )
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                }
            )

            PeraCard(
                title = stringResource(R.string.mnemonic_type_algo25_title),
                highlighted = stringResource(R.string.legacy_text),
                description = stringResource(R.string.mnemonic_type_algo25_description),
                footer = stringResource(R.string.mnemonic_type_algo25_footer),
                onClick = {
                    navigateToRecoverWithPassphraseFragment(
                        OnboardingAccountType.Algo25
                    )
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Suppress("MagicNumber")
    @Composable
    fun BottomSheetHeader(
        sheetState: SheetState,
        onDismiss: () -> Unit
    ) {
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
                        sheetState.hide()
                        onDismiss()
                    }
                }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = stringResource(id = R.string.close)
                )
            }
            Spacer(Modifier.weight(0.1f))

            PeraTitleText(
                text = stringResource(id = R.string.bottom_sheet_mnemonic_type_title)
            )
            Spacer(Modifier.weight(1f))
        }
    }

    private fun navigateToRecoverWithPassphraseFragment(onboardingAccountType: OnboardingAccountType) {
        recoveryAccountInfoViewModel.logRecoverAccountTypeClickEvent(onboardingAccountType)
        nav(RecoverAccountInfoFragmentDirections
            .actionRecoverAccountInfoFragmentToRecoverWithPassphraseNavigation(
                mnemonic = null,
                onboardingAccountType = onboardingAccountType)
        )
    }
}
