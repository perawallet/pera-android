package com.algorand.android.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AccountsQrScannerViewModel : ViewModel() {

    private val _isQrCodeInProgressFlow = MutableSharedFlow<Boolean>()
    val isQrCodeInProgressFlow: SharedFlow<Boolean> = _isQrCodeInProgressFlow

    fun setQrCodeInProgress(isInProgress: Boolean) {
        viewModelScope.launch {
            _isQrCodeInProgressFlow.emit(isInProgress)
        }
    }
}
