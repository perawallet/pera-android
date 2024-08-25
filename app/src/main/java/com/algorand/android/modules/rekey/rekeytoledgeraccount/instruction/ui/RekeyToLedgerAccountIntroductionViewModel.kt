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

package com.algorand.android.modules.rekey.rekeytoledgeraccount.instruction.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.baseintroduction.ui.BaseIntroductionViewModel
import com.algorand.android.modules.rekey.rekeytoledgeraccount.instruction.ui.model.RekeyToLedgerAccountPreview
import com.algorand.android.modules.rekey.rekeytoledgeraccount.instruction.ui.usecase.RekeyToLedgerAccountPreviewUseCase
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class RekeyToLedgerAccountIntroductionViewModel @Inject constructor(
    private val rekeyToLedgerAccountPreviewUseCase: RekeyToLedgerAccountPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseIntroductionViewModel() {

    private val navArgs = RekeyToLedgerAccountIntroductionFragmentArgs.fromSavedStateHandle(savedStateHandle)
    val accountAddress = navArgs.accountAddress

    private val rekeyToLedgerAccountPreviewFlow = MutableStateFlow<RekeyToLedgerAccountPreview?>(null)
    override val introductionPreviewFlow: StateFlow<RekeyToLedgerAccountPreview?>
        get() = rekeyToLedgerAccountPreviewFlow

    init {
        initPreview()
    }

    private fun initPreview() {
        viewModelScope.launchIO {
            rekeyToLedgerAccountPreviewFlow.value = rekeyToLedgerAccountPreviewUseCase
                .getInitialRekeyToLedgerAccountPreview(accountAddress)
        }
    }
}
