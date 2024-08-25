package com.algorand.android.asb.component.restorebackup.domain.model

sealed interface RestoreCipherTextResult {
    data object UnableToParseFile : RestoreCipherTextResult
    data object MissingVersion : RestoreCipherTextResult
    data object MissingSuite : RestoreCipherTextResult
    data object MissingCipherText : RestoreCipherTextResult
    data object InvalidBackUpVersion : RestoreCipherTextResult
    data object InvalidSuite : RestoreCipherTextResult
    data class Success(val cipherText: String) : RestoreCipherTextResult
}
