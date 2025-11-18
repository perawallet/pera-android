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

package com.algorand.android.ui.menu.view

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.button.PeraButtonIcon
import com.algorand.android.ui.compose.widget.button.PeraPrimaryButton
import com.algorand.android.ui.compose.widget.button.PeraSecondaryButton
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.CardCreated
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.Error
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.Idle
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.Loading
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.NewUser
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.Waitlisted

@Composable
internal fun MenuListCardItem(viewModel: MenuCardsViewModel, listener: MenuListCardItemListener) {
    val currentState = viewModel.state.collectAsStateWithLifecycle().value
    when (currentState) {
        Idle, Loading -> LoadingState()
        Error -> ErrorState(viewModel::initCardState)
        CardCreated -> GoToCardsState(listener::onGoToCardsClick)
        NewUser -> CreateCardState(listener::onCreateCardClick)
        Waitlisted -> WaitlistedState()
    }
    LaunchedEffect(Unit) {
        viewModel.initCardState()
    }
}

@Composable
private fun LoadingState() {
    CardItemContainer(
        description = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PeraCircularProgressIndicator()
            }
        }
    )
}

@Composable
private fun ErrorState(onRetryClick: () -> Unit) {
    CardItemContainer(
        description = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CardDescriptionText(R.string.an_error_occurred)
                PeraSecondaryButton(
                    onClick = onRetryClick,
                    text = stringResource(R.string.retry)
                )
            }
        }
    )
}

@Composable
private fun CreateCardState(onClick: () -> Unit) {
    CardItemContainer(
        description = {
            CardDescriptionText(R.string.get_the_worlds_first)
        },
        button = {
            PeraPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick,
                text = stringResource(R.string.create_pera_card),
                leftIcon = { PeraButtonIcon(R.drawable.ic_add) }
            )
        }
    )
}

@Composable
private fun GoToCardsState(onClick: () -> Unit) {
    CardItemContainer(
        description = {
            CardDescriptionText(R.string.get_the_worlds_first)
        },
        button = {
            PeraPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick,
                text = stringResource(R.string.go_to_cards),
                rightIcon = { PeraButtonIcon(R.drawable.ic_right_arrow) }
            )
        }
    )
}

@Composable
private fun WaitlistedState() {
    CardItemContainer(
        description = {
            Column {
                CardDescriptionTitleText(R.string.you_are_all_set)
                Spacer(modifier = Modifier.height(8.dp))
                CardDescriptionText(R.string.we_will_inform_this)
            }
        }
    )
}

@Composable
private fun CardDescriptionText(@StringRes resId: Int) {
    Text(
        text = stringResource(resId),
        style = PeraTheme.typography.body.regular.sans,
        color = PeraTheme.colors.text.gray
    )
}

@Composable
private fun CardDescriptionTitleText(@StringRes resId: Int) {
    Text(
        text = stringResource(resId),
        style = PeraTheme.typography.body.regular.sansMedium,
        color = PeraTheme.colors.text.main
    )
}

@Composable
private fun CardItemContainer(
    description: @Composable () -> Unit,
    button: @Composable (() -> Unit)? = null
) {
    MenuListItemContainer(modifier = Modifier.clipToBounds()) {
        Column {
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                ) {
                    Row {
                        MenuListItemIcon(R.drawable.ic_cards)
                        MenuListItemTitleText(text = stringResource(R.string.cards))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    description()
                }
                Image(
                    modifier = Modifier
                        .size(width = 116.dp, height = 112.dp)
                        .offset(x = 24.dp),
                    painter = painterResource(R.drawable.ic_cards_coloured),
                    contentDescription = null
                )
            }
            if (button != null) {
                Spacer(modifier = Modifier.height(16.dp))
                button()
            }
        }
    }
}

interface MenuListCardItemListener {
    fun onGoToCardsClick()
    fun onCreateCardClick()
}
