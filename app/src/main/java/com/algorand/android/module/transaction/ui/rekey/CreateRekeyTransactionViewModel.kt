package com.algorand.android.module.transaction.ui.rekey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.coroutine.CoroutineExtensions.launchIfInactive
import com.algorand.android.transaction.domain.creation.CreateRekeyTransaction
import com.algorand.android.transaction.domain.creation.model.CreateTransactionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class CreateRekeyTransactionViewModel @Inject constructor(
    private val createRekeyTransaction: CreateRekeyTransaction
) : ViewModel() {

    private val _createTransactionFlow = MutableStateFlow<Event<CreateTransactionResult>?>(null)
    val createTransactionFlow: StateFlow<Event<CreateTransactionResult>?> = _createTransactionFlow.asStateFlow()

    private var createTransactionJob: Job? = null

    fun create(address: String, rekeyAuthAddress: String) {
        createTransactionJob = viewModelScope.launchIfInactive(createTransactionJob) {
            val result = createRekeyTransaction(address, rekeyAuthAddress)
            _createTransactionFlow.value = Event(result)
        }
    }
}
