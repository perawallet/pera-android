/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.register.recoveraccounttypeselection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.algorand.android.R
import com.algorand.android.models.OnboardingAccountType
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.theme.PeraTheme.typography
import com.algorand.android.ui.compose.widget.GroupChoiceWidget
import com.algorand.android.ui.compose.widget.PeraCard
import com.algorand.android.ui.compose.widget.text.PeraHighlightedGrayText
import com.algorand.android.ui.compose.widget.text.PeraHighlightedGreenText
import com.algorand.android.ui.compose.widget.text.PeraTitleText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountRecoveryTypeSelectionScreen(
    listener: AccountRecoveryTypeSelectionScreenListener
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TitleWidget()
        Spacer(modifier = Modifier.height(30.dp))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RecoverAnAccountWidget(
                sheetState = sheetState,
                onNavigateToRecoverAccountInfo = listener::onNavigateToRecoverAccountInfo
            )
            RecoverAnAccountWithQRWidget(onClick = listener::onRecoverWithQRClick)
            PairLedgerDeviceWidget(onClick = listener::onPairLedgerClick)
            ImportPeraWebWidget(onClick = listener::onImportFromWebClick)
            AlgorandSecureBackupWidget(onClick = listener::onAlgorandSecureBackupClick)
        }
    }
}

@Composable
private fun TitleWidget() {
    Text(
        modifier = Modifier.padding(horizontal = 24.dp),
        style = typography.title.regular.sansMedium,
        color = PeraTheme.colors.text.main,
        text = stringResource(R.string.import_a_wallet)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecoverAnAccountWidget(
    sheetState: SheetState,
    onNavigateToRecoverAccountInfo: (OnboardingAccountType) -> Unit
) {
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    GroupChoiceWidget(
        title = stringResource(id = R.string.recover_a_wallet),
        description = stringResource(id = R.string.i_want_to_recover_wallet),
        icon = ImageVector.vectorResource(R.drawable.ic_key),
        iconContentDescription = stringResource(id = R.string.key),
        onClick = {
            showBottomSheet = true
        }
    )
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            containerColor = PeraTheme.colors.background.primary,
            contentColor = PeraTheme.colors.text.grayLighter,
        ) {
            BottomSheetContent(
                sheetState = sheetState,
                onDismiss = { showBottomSheet = false },
                onNavigateToRecoverAccountInfo = onNavigateToRecoverAccountInfo
            )
        }
    }
}

@Composable
private fun RecoverAnAccountWithQRWidget(onClick: () -> Unit) {
    GroupChoiceWidget(
        title = stringResource(id = R.string.recover_an_account_with_qr),
        description = stringResource(id = R.string.i_want_to_recover_qr),
        icon = ImageVector.vectorResource(R.drawable.ic_qr),
        iconContentDescription = stringResource(id = R.string.qr_code),
        onClick = onClick
    )
}

@Composable
private fun PairLedgerDeviceWidget(onClick: () -> Unit) {
    GroupChoiceWidget(
        title = stringResource(id = R.string.pair_ledger_device),
        description = stringResource(id = R.string.i_want_to_recover_an),
        iconContentDescription = stringResource(id = R.string.ledger),
        icon = ImageVector.vectorResource(R.drawable.ic_ledger),
        onClick = onClick
    )
}

@Composable
private fun ImportPeraWebWidget(onClick: () -> Unit) {
    GroupChoiceWidget(
        title = stringResource(id = R.string.import_from_pera_web),
        description = stringResource(id = R.string.i_want_to_import_algorand),
        iconContentDescription = stringResource(id = R.string.import_from_pera_web),
        icon = ImageVector.vectorResource(R.drawable.ic_global),
        onClick = onClick
    )
}

@Composable
private fun AlgorandSecureBackupWidget(onClick: () -> Unit) {
    GroupChoiceWidget(
        title = stringResource(id = R.string.algorand_secure_backup),
        description = stringResource(id = R.string.i_want_to_restore_my),
        iconContentDescription = stringResource(id = R.string.i_want_to_restore_my),
        icon = ImageVector.vectorResource(R.drawable.ic_backup),
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onNavigateToRecoverAccountInfo: (OnboardingAccountType) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PeraTheme.colors.background.primary)
            .padding(16.dp)
    ) {
        BottomSheetHeader(sheetState, onDismiss)

        PeraCard(
            title = stringResource(R.string.mnemonic_type_universal_title),
            description = stringResource(R.string.mnemonic_type_universal_description),
            footer = stringResource(R.string.mnemonic_type_universal_footer),
            highlightContent = {
                PeraHighlightedGreenText(
                    text = stringResource(R.string.new_text)
                )
            },
            onClick = {
                onNavigateToRecoverAccountInfo(OnboardingAccountType.HdKey)
                coroutineScope.launch {
                    sheetState.hide()
                }
            }
        )

        PeraCard(
            title = stringResource(R.string.mnemonic_type_algo25_title),
            description = stringResource(R.string.mnemonic_type_algo25_description),
            footer = stringResource(R.string.mnemonic_type_algo25_footer),
            highlightContent = {
                PeraHighlightedGrayText(
                    text = stringResource(R.string.legacy_text)
                )
            },
            onClick = {
                onNavigateToRecoverAccountInfo(OnboardingAccountType.Algo25)
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
                tint = PeraTheme.colors.text.main,
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

interface AccountRecoveryTypeSelectionScreenListener {
    fun onNavigateToRecoverAccountInfo(onboardingAccountType: OnboardingAccountType)
    fun onRecoverWithQRClick()
    fun onPairLedgerClick()
    fun onImportFromWebClick()
    fun onAlgorandSecureBackupClick()
}
