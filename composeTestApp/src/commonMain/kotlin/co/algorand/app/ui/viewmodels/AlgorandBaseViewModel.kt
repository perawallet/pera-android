package co.algorand.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

open class AlgorandBaseViewModel() : ViewModel() {
    open val TAG: String = "AlgorandBaseViewModel"

    var _snackBarMessage = MutableStateFlow("")
    var snackBarStateFlow = _snackBarMessage.asStateFlow()

    fun setSnackBarMessage(resource: StringResource) {
        viewModelScope.launch {
            _snackBarMessage.value = getString(resource = resource)
        }
    }

    fun setSnackBarMessage(str: String) {
        viewModelScope.launch {
            _snackBarMessage.value = str
        }
    }
}

sealed class ScreenState {
    data object Loading : ScreenState()

    data class Error(
        val errorMessage: String,
    ) : ScreenState()

    data class Success(
        val responseData: Any,
    ) : ScreenState()
}