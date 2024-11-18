package com.algorand.android.modules.keyreg.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.basesingleaccountselection.ui.BaseSingleAccountSelectionViewModel
import com.algorand.android.modules.keyreg.domain.usecase.KeyRegAccountSelectionPreviewUseCase
import com.algorand.android.modules.keyreg.ui.model.KeyRegTransactionDetail
import com.algorand.android.modules.rekey.rekeytostandardaccount.accountselection.ui.model.RekeyToStandardAccountSelectionPreview
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class KeyRegAccountSelectionViewModel @Inject constructor(
    private val keyRegAccountSelectionPreviewUseCase: KeyRegAccountSelectionPreviewUseCase,
    savedStateHandle: SavedStateHandle
) : BaseSingleAccountSelectionViewModel() {

    var keyRegTransactionDetail = savedStateHandle.getOrThrow<KeyRegTransactionDetail>(
        KEY_REG_DETAIL
    )

    override val singleAccountSelectionFieldsFlow: StateFlow<RekeyToStandardAccountSelectionPreview>
        get() = keyRegToAccountSingleAccountSelectionPreview
    private val keyRegToAccountSingleAccountSelectionPreview = MutableStateFlow(getInitialPreview())

    init {
        initPreviewFlow()
    }

    private fun initPreviewFlow() {
        viewModelScope.launchIO {
            keyRegAccountSelectionPreviewUseCase.getKeyRegSingleAccountSelectionPreviewFlow().collectLatest { preview ->
                keyRegToAccountSingleAccountSelectionPreview.emit(preview)
            }
        }
    }

    private fun getInitialPreview(): RekeyToStandardAccountSelectionPreview {
        return keyRegAccountSelectionPreviewUseCase
            .getInitialKeyRegSingleAccountSelectionPreview()
    }

    companion object {
        const val KEY_REG_DETAIL = "keyRegTransactionDetail"
    }
}
