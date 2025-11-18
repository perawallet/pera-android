@file:Suppress("EmptyFunctionBlock")
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

package com.algorand.android.ui.menu.view.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.algorand.android.ui.menu.view.MenuListCardItem
import com.algorand.android.ui.menu.view.MenuListCardItemListener
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.CardCreated
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.Error
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.Idle
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.Loading
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.NewUser
import com.algorand.android.ui.menu.viewmodel.MenuCardsViewModel.ViewState.Waitlisted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Preview
@Composable
fun MenuListCardItemPreview(
    @PreviewParameter(MenuListCardItemPreviewProvider::class) menuCardsViewModel: MenuCardsViewModel,
) {
    val listener = object : MenuListCardItemListener {
        override fun onGoToCardsClick() {}
        override fun onCreateCardClick() {}
    }
    MenuListCardItem(menuCardsViewModel, listener)
}

private class MenuListCardItemPreviewProvider : PreviewParameterProvider<MenuCardsViewModel> {

    override val values: Sequence<MenuCardsViewModel> = listOf(
        createViewModel(Idle),
        createViewModel(Loading),
        createViewModel(Waitlisted),
        createViewModel(NewUser),
        createViewModel(CardCreated),
        createViewModel(Error)
    ).asSequence()

    private fun createViewModel(state: MenuCardsViewModel.ViewState): MenuCardsViewModel {
        return object : MenuCardsViewModel {
            override fun initCardState() {}
            override val state: StateFlow<MenuCardsViewModel.ViewState>
                get() = MutableStateFlow(state)
        }
    }
}
