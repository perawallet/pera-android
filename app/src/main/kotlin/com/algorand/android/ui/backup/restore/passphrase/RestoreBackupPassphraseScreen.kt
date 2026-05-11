@file:Suppress("LongMethod")
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

package com.algorand.android.ui.backup.restore.passphrase

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.PeraToolbarIcon
import com.algorand.android.ui.compose.widget.button.PeraButtonState
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.textfield.PeraSlimTextField

private const val COLUMN_COUNT = 2
private const val WORDS_PER_COLUMN = WORD_COUNT / COLUMN_COUNT

@Composable
fun RestoreBackupPassphraseScreen(
    viewModel: RestoreBackupPassphraseViewModel,
    onBackClick: () -> Unit,
    onProceedClick: (passphrase: String) -> Unit,
    onShowError: (errorMessageResId: Int) -> Unit
) {
    val viewState = viewModel.state.collectAsStateWithLifecycle().value
    val focusRequesters = remember { List(WORD_COUNT) { FocusRequester() } }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }

    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is RestoreBackupPassphraseViewModel.ViewEvent.ShowError -> onShowError(event.messageResId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PeraTheme.colors.background.primary)
    ) {
        PeraToolbar(
            text = stringResource(R.string.verify_passphrase),
            startContainer = {
                PeraToolbarIcon(
                    iconResId = R.drawable.ic_left_arrow,
                    modifier = Modifier.clickableNoRipple(onClick = onBackClick)
                )
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            repeat(WORDS_PER_COLUMN) { rowIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(rowIndex, rowIndex + WORDS_PER_COLUMN).forEach { index ->
                        val isLast = index == WORD_COUNT - 1
                        PassphraseWordField(
                            modifier = Modifier.weight(1f),
                            index = index,
                            word = viewState.words[index],
                            focusRequester = focusRequesters[index],
                            imeAction = if (isLast) ImeAction.Done else ImeAction.Next,
                            onWordChange = { viewModel.updateWord(index, it) },
                            onImeAction = {
                                if (isLast) {
                                    focusManager.clearFocus()
                                } else {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        )
                    }
                }
                if (rowIndex != WORDS_PER_COLUMN - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        PeraPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            onClick = { onProceedClick(viewModel.joinedPassphrase()) },
            text = stringResource(R.string.proceed),
            state = if (viewState.isProceedEnabled) PeraButtonState.ENABLED else PeraButtonState.DISABLED
        )
    }
}

@Composable
private fun PassphraseWordField(
    modifier: Modifier = Modifier,
    index: Int,
    word: String,
    focusRequester: FocusRequester,
    imeAction: ImeAction,
    onWordChange: (String) -> Unit,
    onImeAction: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val underlineColor = if (isFocused) PeraTheme.colors.text.main else PeraTheme.colors.layer.gray
    val underlineHeight = if (isFocused) 1.5.dp else 1.dp

    Row(
        modifier = modifier.height(44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(20.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = (index + 1).toString(),
                style = PeraTheme.typography.body.regular.sans,
                color = if (isFocused) PeraTheme.colors.text.main else PeraTheme.colors.text.gray,
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            PeraSlimTextField(
                modifier = Modifier.focusRequester(focusRequester),
                text = word,
                onTextChanged = onWordChange,
                singleLine = true,
                interactionSource = interactionSource,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onImeAction() },
                    onDone = { onImeAction() }
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(underlineHeight)
                    .background(underlineColor)
            )
        }
    }
}
