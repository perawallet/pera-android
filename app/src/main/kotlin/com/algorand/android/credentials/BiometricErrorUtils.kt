package com.algorand.android.credentials

import android.annotation.SuppressLint
import android.content.Context
import androidx.credentials.provider.BiometricPromptResult
import com.algorand.android.R
import kotlin.toString

object BiometricErrorUtils {
    @SuppressLint("StringFormatMatches")
    fun getBiometricErrorMessage(
        context: Context,
        biometricPromptResult: BiometricPromptResult?,
    ): String {
        // If the biometricPromptResult is null, there was no error.
        if (biometricPromptResult == null) return context.getString(R.string.empty)

        // If the biometricPromptResult indicates success, there was no error.
        if (biometricPromptResult.isSuccessful) return context.getString(R.string.empty)

        // Initialize default values for the error code and message.
        var biometricAuthErrorCode = -1
        var biometricAuthErrorMsg = context.getString(R.string.unknown_failure)

        // Check if there is an authentication error in the biometricPromptResult.
        if (biometricPromptResult.authenticationError != null) {
            // Extract the error code and message from the authentication error.
            biometricAuthErrorCode = biometricPromptResult.authenticationError!!.errorCode
            biometricAuthErrorMsg = biometricPromptResult.authenticationError!!.errorMsg.toString()
        }

        // Build the error message to be sent to the client.
        val errorMessage = buildString {
            append(
                context.getString(
                    R.string.biometric_error_code_with_message,
                    biometricAuthErrorCode,
                ),
            )
            append(biometricAuthErrorMsg)
            append(context.getString(R.string.other_providers_error_message))
        }

        // Indicate that there was an error during the biometric flow.
        return errorMessage
    }
}
