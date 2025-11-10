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

package com.algorand.android.credentials.passkeys.ui

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.OutcomeReceiver
import androidx.annotation.RequiresApi
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.provider.BeginCreateCredentialRequest
import androidx.credentials.provider.BeginCreateCredentialResponse
import androidx.credentials.provider.BeginCreatePublicKeyCredentialRequest
import androidx.credentials.provider.BeginGetCredentialRequest
import androidx.credentials.provider.BeginGetCredentialResponse
import androidx.credentials.provider.CreateEntry
import androidx.credentials.provider.CredentialProviderService
import androidx.credentials.provider.ProviderClearCredentialStateRequest
import androidx.credentials.provider.PublicKeyCredentialEntry
import com.algorand.android.credentials.R
import com.algorand.android.credentials.passkeys.ui.biometric.BiometricPromptDataBuilder
import com.algorand.android.credentials.passkeys.ui.builder.PasskeyCreateCredentialEntryBuilder
import com.algorand.android.credentials.passkeys.ui.builder.PasskeyGetCredentialsEntryBuilder
import com.algorand.android.credentials.passkeys.ui.model.CreatePasskeyCredentialCreateEntry
import com.algorand.android.credentials.passkeys.ui.model.GetPasskeyCredentialEntry
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@AndroidEntryPoint
class PasskeyProviderService : CredentialProviderService() {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val requestCode: AtomicInteger = AtomicInteger()

    @Inject
    lateinit var createCredentialEntryBuilder: PasskeyCreateCredentialEntryBuilder

    @Inject
    lateinit var getCredentialsEntryBuilder: PasskeyGetCredentialsEntryBuilder

    @Inject
    lateinit var isFeatureToggleEnabled: IsFeatureToggleEnabled

    override fun onBeginCreateCredentialRequest(
        request: BeginCreateCredentialRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<BeginCreateCredentialResponse, CreateCredentialException>
    ) {
        when (request) {
            is BeginCreatePublicKeyCredentialRequest -> {
                scope.launch {
                    createCredentialEntryBuilder.buildEntries(request).use(
                        onSuccess = { entries ->
                            val response = buildCreateCredentialResponse(entries)
                            callback.onResult(response)
                        },
                        onFailed = { exception, _ ->
                            val error = (exception as? CreateCredentialException) ?: CreateCredentialUnknownException()
                            callback.onError(error)
                        }
                    )
                }
            }

            else -> callback.onError(CreateCredentialUnknownException())
        }
    }

    private fun buildCreateCredentialResponse(
        entries: List<CreatePasskeyCredentialCreateEntry>
    ): BeginCreateCredentialResponse {
        val builder = BeginCreateCredentialResponse.Builder()
        entries.forEach { entry ->
            val extras = Bundle().apply { putString(BIP44ADDRESS, entry.bip44Address) }
            val intent = createNewPendingIntent(CREATE_PASSKEY_INTENT, extras)
            val createEntry = getCreateEntry(entry.accountName, entry.passkeyCount, intent)
            builder.addCreateEntry(createEntry)
        }
        return builder.build()
    }

    override fun onBeginGetCredentialRequest(
        request: BeginGetCredentialRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<BeginGetCredentialResponse, GetCredentialException>
    ) {
        val callingPackage = request.callingAppInfo?.packageName
        if (callingPackage == null) {
            callback.onError(NoCredentialException())
            return
        }

        scope.launch {
            getCredentialsEntryBuilder.buildEntries(request).use(
                onSuccess = { entries ->
                    val responseBuilder = BeginGetCredentialResponse.Builder()
                    entries.forEach {
                        val credEntry = createPublicKeyCredentialEntry(it)
                        responseBuilder.addCredentialEntry(credEntry)
                    }
                    callback.onResult(responseBuilder.build())
                },
                onFailed = { exception, _ ->
                    val error = (exception as? GetCredentialException) ?: GetCredentialUnknownException()
                    callback.onError(error)
                }
            )
        }
    }

    private fun createPublicKeyCredentialEntry(entry: GetPasskeyCredentialEntry): PublicKeyCredentialEntry {
        val extras = Bundle().apply { putString(CRED_ID_KEY, entry.credentialId) }
        val intent = createNewPendingIntent(GET_PASSKEY_INTENT, extras)
        var entry = PublicKeyCredentialEntry.Builder(
            applicationContext,
            entry.username.orEmpty(),
            intent,
            entry.option
        ).setDisplayName(entry.userDisplayName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            entry = entry.setBiometricPromptData(BiometricPromptDataBuilder.getDefaultPromptData())
        }

        return entry.build()
    }

    override fun onClearCredentialStateRequest(
        request: ProviderClearCredentialStateRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<Void?, ClearCredentialException>
    ) {
        // Nothing to do
    }

    private fun getCreateEntry(accountName: String, passkeyCount: Int, intent: PendingIntent): CreateEntry {
        val description = resources.getString(R.string.your_credential_will_be_saved)
        var entry = CreateEntry.Builder(accountName, intent)
            .setLastUsedTime(Instant.ofEpochMilli(0L))
            .setPublicKeyCredentialCount(passkeyCount)
            .setTotalCredentialCount(passkeyCount)
            .setDescription(description)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            entry = entry.setBiometricPromptData(BiometricPromptDataBuilder.getDefaultPromptData())
        }

        return entry.build()
    }

    private fun createNewPendingIntent(action: String, extra: Bundle? = null): PendingIntent {
        val intent = Intent(action).setPackage(applicationContext.packageName)
        if (extra != null) {
            Intent.EXTRA_INTENT
            intent.putExtra(EXTRA_INTENT_DATA_KEY, extra)
        }
        val flags = PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getActivity(applicationContext, requestCode.incrementAndGet(), intent, flags)
    }

    internal companion object {
        const val CREATE_PASSKEY_INTENT = "com.algorand.android.credentials.CREATE_PASSKEY"
        const val GET_PASSKEY_INTENT = "com.algorand.android.credentials.GET_PASSKEY"
        const val BIP44ADDRESS = "bip44Address"
        const val EXTRA_INTENT_DATA_KEY = "extraIntentData"
        const val CRED_ID_KEY = "credId"
    }
}
