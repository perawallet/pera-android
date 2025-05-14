package com.algorand.android.credentials

import android.app.PendingIntent
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.OutcomeReceiver
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.provider.Action
import androidx.credentials.provider.BeginCreateCredentialRequest
import androidx.credentials.provider.BeginCreateCredentialResponse
import androidx.credentials.provider.BeginGetCredentialRequest
import androidx.credentials.provider.BeginGetCredentialResponse
import androidx.credentials.provider.CredentialProviderService
import androidx.credentials.provider.ProviderClearCredentialStateRequest
import androidx.annotation.RequiresApi
import androidx.credentials.provider.BeginCreatePublicKeyCredentialRequest
import androidx.credentials.provider.BeginGetCredentialResponse.Builder
import androidx.credentials.provider.BeginGetPasswordOption
import androidx.credentials.provider.BeginGetPublicKeyCredentialOption
import androidx.credentials.provider.BiometricPromptData
import androidx.credentials.provider.CreateEntry
import androidx.credentials.provider.PublicKeyCredentialEntry
import com.algorand.android.R
import com.algorand.android.credentials.webauthn.PublicKeyCredentialRequestOptions
import com.algorand.wallet.account.webauthn.data.database.model.PasskeyEntity
import java.io.IOException
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
class ProviderService : CredentialProviderService() {
    private val requestCode: AtomicInteger = AtomicInteger()
    private val allowedAuthenticator =
        BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

    private fun publicKeyCredentialEntry(
        passkey: PasskeyEntity,
        option: BeginGetPublicKeyCredentialOption,
        pendingIntent: PendingIntent
    ): PublicKeyCredentialEntry {
        return PublicKeyCredentialEntry.Builder(
            applicationContext,
            passkey.username,
            pendingIntent,
            option,
        )
            .setDisplayName(passkey.userHandle)
            .setBiometricPromptData(
                BiometricPromptData(
                    cryptoObject = null,
                    allowedAuthenticators = allowedAuthenticator,
                ),
            )
            .build()
    }
    private fun createEntry(passkeyCount: Int, intent: PendingIntent): CreateEntry {
        return CreateEntry.Builder(
            APPLICATION_ACCOUNT, intent
        )
            .setLastUsedTime(
                Instant.ofEpochMilli(0L),
            )
            .setPublicKeyCredentialCount(passkeyCount)
            .setTotalCredentialCount(passkeyCount).setDescription(
                CREDENTIAL_DESCRIPTION,
            )
            .setBiometricPromptData(
                BiometricPromptData(
                    cryptoObject = null,
                    allowedAuthenticators = allowedAuthenticator,
                ),
            ).build()
    }

    private fun processCreateCredentialsRequest(request: BeginCreateCredentialRequest): BeginCreateCredentialResponse? {
        var passkeyCount = 0

        // TODO: get count from database for origin
//        val requestJson =
//            request.candidateQueryData.getString("androidx.credentials.BUNDLE_KEY_REQUEST_JSON")
//        if (!requestJson.isNullOrEmpty()) {
//            val requestJsonObject = JSONObject(requestJson)
//            val rp: JSONObject = requestJsonObject.getJSONObject("rp")
//            val id: String = rp.getString("id")
//            val query = passkeyRepository.getSitePasskeys(id)
//            passkeyCount = query?.passkeys?.size ?: 0
//        }

        when (request) {
            is BeginCreatePublicKeyCredentialRequest -> {
                return BeginCreateCredentialResponse.Builder().addCreateEntry(
                    createEntry(
                        passkeyCount,
                        createNewPendingIntent(CREATE_PASSKEY_INTENT)
                    ),
                ).build()
            }
        }
        return null
    }

    override fun onBeginCreateCredentialRequest(
        request: BeginCreateCredentialRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<BeginCreateCredentialResponse, CreateCredentialException>,
    ) {
        val response: BeginCreateCredentialResponse? = processCreateCredentialsRequest(request)

        if (response != null) {
            callback.onResult(response)
        } else {
            callback.onError(
                CreateCredentialUnknownException(),
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun populatePasskeyData(
        option: BeginGetPublicKeyCredentialOption,
        responseBuilder: Builder,
    ): Boolean {
        try {
            // Parse the request options into a PublicKeyCredentialRequestOptions object.
            val request = PublicKeyCredentialRequestOptions(option.requestJson)
            return false
            // Get the credentials for the site specified in the request.
//            val credentials = passkeyRepository.getSitePasskeys(request.rpId) ?: return false

//            val passkeys = credentials.passkeys
//            for (passkey in passkeys) {
//                val data = Bundle()
//                data.putString("requestJson", option.requestJson)
//                data.putString("credId", passkey.credentialId)
//
//                // Create a PendingIntent to launch the activity that will handle the passkey retrieval
//                val pendingIntent = createNewPendingIntent(
//                    GET_PASSKEY_INTENT,
//                    data,
//                )
//
//                val entry = publicKeyCredentialEntry(passkey, option, pendingIntent)
//                // Add the entry to the response builder.
//                responseBuilder.addCredentialEntry(entry)
//            }
        } catch (e: IOException) {
            return false
        }
        return true
    }
    fun processGetCredentialsRequest(
        request: BeginGetCredentialRequest,
        responseBuilder: Builder,
    ): Boolean {
        val callingPackage = request.callingAppInfo?.packageName ?: return false

        var hasFoundCredentials = false

        for (option in request.beginGetCredentialOptions) {
            when (option) {
                // If the chosen option is a Password credential
                is BeginGetPasswordOption -> {
                    hasFoundCredentials = false
                }

                // If the chosen option is a Passkey credential
                is BeginGetPublicKeyCredentialOption -> {
                    if (populatePasskeyData(option, responseBuilder)) {
                        hasFoundCredentials = true
                    }
                }
            }
        }
        return hasFoundCredentials
    }

    override fun onBeginGetCredentialRequest(
        request: BeginGetCredentialRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<BeginGetCredentialResponse, GetCredentialException>,
    ) {
        val callingPackage = request.callingAppInfo?.packageName
        if (callingPackage == null) {
            callback.onError(NoCredentialException())
        }

        val responseBuilder = Builder()

        val hasCredentialsFound =
            processGetCredentialsRequest(request, responseBuilder)
        val hasActionsPopulated =
            populateActions(responseBuilder, requestCode)

        if (hasCredentialsFound || hasActionsPopulated) {
            callback.onResult(
                responseBuilder.build(),
            )
            return
        }

        callback.onError(
            GetCredentialUnknownException(),
        )
    }

    override fun onClearCredentialStateRequest(
        request: ProviderClearCredentialStateRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<Void?, ClearCredentialException>,
    ) {
        callback.onResult(null)
    }

    private fun createPendingIntent(counter: AtomicInteger, intentType: String): PendingIntent {
        val intent = Intent(intentType).setPackage(applicationContext.packageName)
        return PendingIntent.getActivity(
            applicationContext,
            counter.incrementAndGet(),
            intent,
            (PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT),
        )
    }

    private fun createNewPendingIntent(
        action: String,
        extra: Bundle? = null,
    ): PendingIntent {
        val intent = Intent(action).setPackage(applicationContext.packageName)
        if (extra != null) {
            intent.putExtra("VAULT_DATA", extra)
        }
        return PendingIntent.getActivity(
            applicationContext,
            requestCode.incrementAndGet(),
            intent,
            (PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT),
        )
    }

    private fun populateActions(
        responseBuilder: Builder,
        counter: AtomicInteger,
    ): Boolean {
        try {
            responseBuilder.addAction(
                Action(
                    title = getString(
                        R.string.open,
                        applicationContext.getString(R.string.app_name),
                    ),
                    subtitle = getString(R.string.manage_credentials),
                    pendingIntent = createPendingIntent(counter, OPEN_APP_INTENT),
                ),
            )
        } catch (e: IOException) {
            return false
        }
        return true
    }

    companion object {
        const val APPLICATION_ACCOUNT = "com.algorand.android.credentials"
        private const val OPEN_APP_INTENT = "com.algorand.android.OPEN_APP"
        private const val CREATE_PASSKEY_INTENT =
            "com.algorand.android.credentials.CREATE_PASSKEY"
        private const val GET_PASSKEY_INTENT =
            "com.algorand.android.credentials.GET_PASSKEY"
        const val CREDENTIAL_DESCRIPTION =
            "Your credential will be saved securely to the chosen account."
    }
}
