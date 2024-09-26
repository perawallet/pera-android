package com.algorand.android.ui.accountoptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.module.account.core.component.domain.usecase.DeleteAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class AccountErrorOptionsViewModel @Inject constructor(
    private val deleteAccount: DeleteAccount,
) : ViewModel() {

    fun removeAccount(address: String, onCompleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteAccount(address)
            onCompleted()
        }
    }
}
