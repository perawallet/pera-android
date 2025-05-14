package com.algorand.android.credentials.ui

import android.content.Intent
import android.content.pm.SigningInfo
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo.Builder

import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.provider.CallingAppInfo
import androidx.credentials.provider.PendingIntentHandler
import androidx.credentials.provider.ProviderCreateCredentialRequest
import androidx.fragment.app.FragmentActivity
import cash.z.ecc.android.bip39.Mnemonics

import com.algorand.android.R
import com.algorand.android.credentials.BiometricErrorUtils
import com.algorand.android.credentials.KeyManager
import com.algorand.android.credentials.ProviderService
import com.algorand.android.credentials.encoding.appInfoToOrigin
import com.algorand.android.credentials.webauthn.AssetLinkVerifier
import com.algorand.android.credentials.webauthn.AuthenticatorAttestationResponse
import com.algorand.android.credentials.webauthn.AuthenticatorFlags
import com.algorand.android.credentials.webauthn.Cbor
import com.algorand.android.credentials.webauthn.FidoPublicKeyCredential
import com.algorand.android.credentials.webauthn.PublicKeyCredentialCreationOptions

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

import kotlinx.coroutines.runBlocking

import org.json.JSONObject

import java.math.BigInteger
import java.net.URL
import java.security.KeyPair
import java.security.SecureRandom
import java.security.interfaces.ECPublicKey
import java.time.Instant
import kotlin.collections.plus
import kotlin.collections.set
import kotlin.io.readText
import kotlin.text.isNotEmpty
import androidx.core.content.edit

const val DEFAULT_BYTE_LENGTH = 32

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
class CreatePasskeyActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // TODO: Remove fake keys
        val entropy = ByteArray(DEFAULT_BYTE_LENGTH)
        SecureRandom().nextBytes(entropy)
        KeyManager.setRootKey(Mnemonics.MnemonicCode(entropy))

        val request = PendingIntentHandler.retrieveProviderCreateCredentialRequest(intent)

        if (request == null) {
            // If the request is null, send an unknown exception to client and finish the flow
            setUpFailureResponseAndFinish("Unable to extract request from intent")
            return
        }

        handleCreatePublicKeyCredentialRequest(request)
    }

    private fun setUpFailureResponseAndFinish(message: String) {
        val result = Intent()
        PendingIntentHandler.setGetCredentialException(
            result,
            GetCredentialUnknownException(message),
        )
        setResult(RESULT_OK, result)
        finish()
    }

    private fun handleCreatePublicKeyCredentialRequest(request: ProviderCreateCredentialRequest) {
        val accountId = intent.getStringExtra(KEY_ACCOUNT_ID)
        // Retrieve the BiometricPromptResult from the request.
        val biometricPromptResult = request.biometricPromptResult

        // Validate the error message in biometric result
        val biometricErrorMessage =
            BiometricErrorUtils.getBiometricErrorMessage(this, biometricPromptResult)

        // If there's valid biometric error, set up the failure response and finish.
        if (biometricErrorMessage.isNotEmpty()) {
            setUpFailureResponseAndFinish(biometricErrorMessage)
            return
        }

        // access the associated intent and pass it into the PendingIntentHandler class to
        // get the ProviderCreateCredentialRequest.
        if (request.callingRequest is CreatePublicKeyCredentialRequest) {
            val publicKeyRequest: CreatePublicKeyCredentialRequest =
                request.callingRequest as CreatePublicKeyCredentialRequest

            // Check if the biometric prompt result contains a successful authentication result.
            if (biometricPromptResult?.authenticationResult != null) {
                // If biometric authentication was successful, use the biometric flow to create the passkey.
                createPasskeyWithBiometricFlow(
                    publicKeyRequest.requestJson,
                    request.callingAppInfo,
                    publicKeyRequest.clientDataHash,
                    accountId,
                )
                return
            }

            // If biometric authentication was not used or was not successful, use the default flow.
            createPasskeyWithDefaultFlow(
                publicKeyRequest.requestJson,
                request.callingAppInfo,
                publicKeyRequest.clientDataHash,
                accountId,
            )
        } else {
            setUpFailureResponseAndFinish(getString(R.string.unexpected_create_request_found_in_intent))
            return
        }
    }

    private fun createPasskeyWithDefaultFlow(
        requestJson: String,
        callingAppInfo: CallingAppInfo?,
        clientDataHash: ByteArray?,
        accountId: String?,
    ) {
        if (callingAppInfo == null) {
            finish()
            return
        }

        val request = PublicKeyCredentialCreationOptions(requestJson)

        if (!hasRequestContainsOrigin(callingAppInfo)) {
            // Native call. Check for asset links
            validateAssetLinks(request.rp.id, callingAppInfo)
        }

        // Surface an authentication prompt. The example below uses the Android Biometric API.
        val biometricPrompt = BiometricPrompt(
            this,
            mainExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e(TAG, getString(R.string.authentication_error, errString))
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.e(TAG, getString(R.string.authentication_failed))
                    finish()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult,
                ) {
                    super.onAuthenticationSucceeded(result)

                    createPasskeyWithBiometricFlow(
                        requestJson,
                        callingAppInfo,
                        clientDataHash,
                        accountId,
                    )
                }
            },
        )
        authenticate(biometricPrompt)
    }

    private fun createPasskeyWithBiometricFlow(
        requestJson: String,
        callingAppInfo: CallingAppInfo?,
        clientDataHash: ByteArray?,
        accountId: String?,
    ) {
        if (callingAppInfo == null) {
            finish()
            return
        }

        val request = PublicKeyCredentialCreationOptions(requestJson)

        var callingAppInfoOrigin: String? = null
        if (hasRequestContainsOrigin(callingAppInfo)) {
            callingAppInfoOrigin = validatePrivilegedCallingApp(
                callingAppInfo,
            ) ?: return
        } else {
            // Native call. Check for asset links
            validateAssetLinks(request.rp.id, callingAppInfo)
        }

        // Generate CredentialID
        val credentialId = ByteArray(DEFAULT_BYTE_LENGTH)
        SecureRandom().nextBytes(credentialId)

        var callingOrigin = appInfoToOrigin(callingAppInfo)
        val obj = JSONObject(requestJson)
        val user = obj.getJSONObject("user")

        val userHandle = if (user.has("name")) user.getString("name") else user.getString("displayName")

        // Generate key
        val keyPair = generateKeyPair(callingOrigin, userHandle)

        // Save the private key in your local database against callingAppInfo.packageName.
        savePasskeyInCredentialsDataStore(request, credentialId, keyPair)

        updateMetaInSharedPreferences(accountId)

        if (callingAppInfoOrigin != null) {
            callingOrigin = callingAppInfoOrigin
        }
        val response = constructWebAuthnResponse(
            keyPair,
            request,
            credentialId,
            callingOrigin,
            callingAppInfo,
            clientDataHash,
        )

        setIntentForCredentialCredentialResponse(credentialId, response)
    }

    private fun constructWebAuthnResponse(
        keyPair: KeyPair,
        request: PublicKeyCredentialCreationOptions,
        credId: ByteArray,
        callingOrigin: String,
        callingAppInfo: CallingAppInfo,
        clientDataHash: ByteArray?,
    ): AuthenticatorAttestationResponse {
        val coseKey = publicKeyToCose(keyPair.public as ECPublicKey)
        val spki = coseKeyToSPKI(coseKey)

        // Construct a Web Authentication API JSON response that consists of the public key and the credentialId.
        val response = AuthenticatorAttestationResponse(
            requestOptions = request,
            credentialId = credId,
            credentialPublicKey = Cbor().encode(coseKey),
            origin = callingOrigin,
            authFlags = AuthenticatorFlags(
                up = true,
                uv = true,
                be = true,
                bs = true,
            ),
            packageName = callingAppInfo.packageName,
            clientDataHash = clientDataHash,
            spki,
        )
        return response
    }

    private fun generateKeyPair(origin: String, userHandle: String): KeyPair {
        return KeyManager.generatePasskey(origin, userHandle)
    }

    private fun validateAssetLinks(rpId: String, callingAppInfo: CallingAppInfo) {
        val isRpValid: Boolean = runBlocking {
            val isRpValidDeferred: Deferred<Boolean> = async(Dispatchers.IO) {
                if (!isValidRpId(
                        rpId,
                        callingAppInfo.signingInfo,
                        callingAppInfo.packageName,
                    )
                ) {
                    return@async false
                }
                return@async true
            }
            return@runBlocking isRpValidDeferred.await()
        }

        if (!isRpValid) {
            setUpFailureResponseAndFinish(getString(R.string.failed_to_validate_rp))
            return
        }
    }

    private fun isValidRpId(
        rpId: String,
        signingInfo: SigningInfo,
        callingPackage: String,
    ): Boolean {
        val websiteUrl = "https://$rpId"
        val assetLinkVerifier = AssetLinkVerifier(websiteUrl)
        try {
            // log the info returned.
            return assetLinkVerifier.verify(callingPackage, signingInfo)
        } catch (e: Exception) {
            // Log exception
        }
        return false
    }

    private fun validatePrivilegedCallingApp(callingAppInfo: CallingAppInfo): String? {
        val privilegedAppsAllowlist = getGPMPrivilegedAppAllowlist()
        if (privilegedAppsAllowlist != null) {
            return try {
                callingAppInfo.getOrigin(
                    privilegedAppsAllowlist,
                )
            } catch (e: IllegalStateException) {
                val message = getString(R.string.incoming_call_is_not_privileged_to_get_the_origin)
                setUpFailureResponseAndFinish(message)
                null
            } catch (e: IllegalArgumentException) {
                val message = getString(R.string.privileged_allowlist_is_not_formatted_properly)
                setUpFailureResponseAndFinish(message)
                null
            }
        }
        val message = "Could not retrieve GPM allowlist"
        setUpFailureResponseAndFinish(message)
        return null
    }

    private fun getGPMPrivilegedAppAllowlist(): String? {
        val gpmAllowlist: String? = runBlocking {
            val allowlist: Deferred<String?> = async(Dispatchers.IO) {
                try {
                    val url = URL(GPM_ALLOWLIST_URL)
                    return@async url.readText()
                } catch (e: Exception) {
                    return@async null
                }
            }
            return@runBlocking allowlist.await()
        }

        return gpmAllowlist
    }

    private fun hasRequestContainsOrigin(callingAppInfo: CallingAppInfo): Boolean {
        try {
            callingAppInfo.getOrigin(INVALID_ALLOWLIST)
        } catch (e: IllegalStateException) {
            return true
        }
        return false
    }

    @Suppress("MagicNumber")
    private fun coseKeyToSPKI(coseKey: MutableMap<Int, Any>): ByteArray? {
        try {
            val spkiPrefix: ByteArray = Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE", 0)
            val x = coseKey[-2] as ByteArray
            val y = coseKey[-3] as ByteArray
            return spkiPrefix + x + y
        } catch (e: Exception) {
            // Log exceptions
        }
        return null
    }

    private fun setIntentForCredentialCredentialResponse(
        credentialId: ByteArray,
        response: AuthenticatorAttestationResponse,
    ) {
        val credential = FidoPublicKeyCredential(
            rawId = credentialId,
            response = response,
            authenticatorAttachment = getString(R.string.platform),
        )
        val intent = Intent()
        // Construct a CreatePublicKeyCredentialResponse with the JSON generated above.
        val publicKeyResponse = CreatePublicKeyCredentialResponse(credential.json())

        // Set CreatePublicKeyCredentialResponse as an extra on an Intent
        // through PendingIntentHandler.setCreateCredentialResponse(),
        // and set that intent to the result of the Activity.
        PendingIntentHandler.setCreateCredentialResponse(intent, publicKeyResponse)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun authenticate(
        biometricPrompt: BiometricPrompt,
    ) {
        val promptInfo = Builder()
            .setTitle(getString(R.string.use_your_screen_lock))
            .setSubtitle(getString(R.string.use_fingerprint))
            .setAllowedAuthenticators(Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    @Suppress("MagicNumber")
    private fun publicKeyToCose(key: ECPublicKey): MutableMap<Int, Any> {
        val x = bigIntToFixedArray(key.w.affineX)
        val y = bigIntToFixedArray(key.w.affineY)
        val coseKey = mutableMapOf<Int, Any>()
        coseKey[1] = 2 // EC Key type
        coseKey[3] = -7 // ES256
        coseKey[-1] = 1 // P-265 Curve
        coseKey[-2] = x // x
        coseKey[-3] = y // y
        return coseKey
    }

    @Suppress("MagicNumber")
    private fun bigIntToFixedArray(n: BigInteger): ByteArray {
        assert(n.signum() >= 0)

        val bytes = n.toByteArray()
        // `toByteArray` will left-pad with a leading zero if the
        // most-significant bit of the first byte would otherwise be one.
        var offset = 0
        if (bytes[0] == 0x00.toByte()) {
            offset++
        }
        val bytesLen = bytes.size - offset
        assert(bytesLen <= 32)

        val output = ByteArray(32)
        System.arraycopy(bytes, offset, output, 32 - bytesLen, bytesLen)
        return output
    }

    private fun updateMetaInSharedPreferences(accountId: String?) {
        if (accountId == null || (accountId != ProviderService.APPLICATION_ACCOUNT)) {
            // AccountId was not set
        } else {
            applicationContext.getSharedPreferences(
                applicationContext.packageName,
                MODE_PRIVATE,
            ).edit {
                putLong(
                    KEY_ACCOUNT_LAST_USED_MS,
                    Instant.now().toEpochMilli(),
                )
            }
        }
    }

    /**
     * Saves the passkey in the credentials data store.
     *
     * @param request The public key credential creation options.
     * @param credId The credential ID.
     * @param keyPair The key pair.
     */
    private fun savePasskeyInCredentialsDataStore(
        request: PublicKeyCredentialCreationOptions,
        credId: ByteArray,
        keyPair: KeyPair,
    ) {
        // TODO: Save credential
//        runBlocking {
//            credentialsRepository.addNewPasskey(
//                PasskeyMetadata(
//                    uid = b64Encode(request.user.id),
//                    rpid = request.rp.id,
//                    username = request.user.name,
//                    displayName = request.user.displayName,
//                    credId = b64Encode(credId),
//                    credPublicKey = b64Encode((keyPair.public as ECPublicKey).encoded),
//                    credPrivateKey = b64Encode((keyPair.private as ECPrivateKey).s.toByteArray()),
//                ),
//            )
//        }
    }
    companion object {
        private const val INVALID_ALLOWLIST = "{\"apps\": [\n" +
                "   {\n" +
                "      \"type\": \"android\", \n" +
                "      \"info\": {\n" +
                "         \"package_name\": \"androidx.credentials.test\",\n" +
                "         \"signatures\" : [\n" +
                "         {\"build\": \"release\",\n" +
                "             \"cert_fingerprint_sha256\": \"HELLO\"\n" +
                "         },\n" +
                "         {\"build\": \"ud\",\n" +
                "         \"cert_fingerprint_sha256\": \"YELLOW\"\n" +
                "         }]\n" +
                "      }\n" +
                "    }\n" +
                "]}\n" +
                "\n"

        private const val GPM_ALLOWLIST_URL =
            "https://www.gstatic.com/gpm-passkeys-privileged-apps/apps.json"

        private const val TAG = "ProviderService"
        const val KEY_ACCOUNT_LAST_USED_MS = "key_account_last_used_ms"
        const val KEY_ACCOUNT_ID = "key_account_id"
    }
}
