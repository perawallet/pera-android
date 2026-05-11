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

package com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.baseintroduction.ui.BaseIntroductionViewModel
import com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.model.RekeyToJointAccountIntroductionPreview
import com.algorand.android.modules.rekey.rekeytojointaccount.instruction.ui.usecase.RekeyToJointAccountInstructionPreviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RekeyToJointAccountIntroductionViewModel @Inject constructor(
    private val rekeyToJointAccountInstructionPreviewUseCase: RekeyToJointAccountInstructionPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseIntroductionViewModel() {

    private val navArgs = RekeyToJointAccountIntroductionFragmentArgs.fromSavedStateHandle(savedStateHandle)
    val accountAddress: String = navArgs.accountAddress

    private val _previewFlow = MutableStateFlow<RekeyToJointAccountIntroductionPreview?>(null)
    override val introductionPreviewFlow: StateFlow<RekeyToJointAccountIntroductionPreview?>
        get() = _previewFlow

    init {
        initPreview()
    }

    private fun initPreview() {
        viewModelScope.launch {
            _previewFlow.value = rekeyToJointAccountInstructionPreviewUseCase
                .getInitialRekeyToJointAccountInstructionPreview(accountAddress)
        }
    }
}
