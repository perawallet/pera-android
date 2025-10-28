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
import com.algorand.android.R
import com.algorand.android.credentials.BiometricErrorUtils
import com.algorand.android.credentials.ProviderService.Companion.KEY_SEED_ID
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
import kotlin.collections.plus
import kotlin.collections.set
import kotlin.io.readText
import kotlin.text.isNotEmpty
import com.algorand.android.credentials.encoding.b64Encode
import com.algorand.wallet.account.webauthn.domain.PasskeyManager
import com.algorand.wallet.account.webauthn.domain.model.Passkey
import com.algorand.wallet.account.webauthn.domain.repository.PasskeyRepository
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import java.util.UUID

const val DEFAULT_BYTE_LENGTH = 32

/**
 * `CreatePasskeyActivity` is responsible for handling the lifecycle and operations required
 * to create a public key credential for authentication. It manages the overall workflow for
 * passkey generation, including biometric authentication, origin validation, and response
 * construction.
 *
 * This activity leverages `PasskeyRepository` and `PasskeyManager` to handle credential storage
 * and cryptographic operations. It also provides mechanisms to interact with the calling app
 * and handle any errors effectively.
 *
 * Key responsibilities include:
 * - Managing the lifecycle of passkey creation requests.
 * - Handling error conditions or exceptions during the credential creation process.
 * - Validating the origin and other metadata associated with the requesting app.
 * - Facilitating biometric and non-biometric flows for creating passkeys.
 * - Constructing and returning responses adhering to WebAuthn specifications.
 *
 * Fields:
 * - `passkeyRepository`: Provides operations for managing stored passkey and site data.
 * - `passkeyManager`: Handles cryptographic operations such as passkey signing and derivation.
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@AndroidEntryPoint
class CreatePasskeyActivity : FragmentActivity() {
    @Inject
    lateinit var passkeyRepository: PasskeyRepository
    @Inject
    lateinit var passkeyManager: PasskeyManager

    /**
     * Called when the activity is first created. This method sets up the activity by enabling
     * edge-to-edge display and managing initialization required for handling a provider request
     * for creating a credential. If the request cannot be extracted or is invalid, it handles
     * the error condition gracefully.
     *
     * @param savedInstanceState A `Bundle` containing the activity's previously saved state, or
     *                           `null` if none existed.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Retrieve the Provider Request from the ProviderService
        val request = PendingIntentHandler.retrieveProviderCreateCredentialRequest(intent)

        // Handle undefined requests
        if (request == null) {
            handleUnknownException(getString(R.string.unable_to_extract_data_from_intent))
            return
        }

        handleCreatePublicKeyCredentialRequest(request)
    }

    /**
     * Handles unknown exceptions occurring within the activity by preparing the result and finishing the activity.
     *
     * @param message A string representing the message or description of the unknown exception.
     */
    private fun handleUnknownException(message: String) {
        Log.e(TAG, message)
        val result = Intent()
        PendingIntentHandler.setGetCredentialException(
            result,
            GetCredentialUnknownException(message),
        )
        setResult(RESULT_OK, result)
        finish()
    }

    /**
     * Handles the creation of a public key credential request. This method processes the request
     * by validating the biometric prompt result and determines the appropriate flow to create a passkey.
     * If a biometrics authentication result is present, it utilizes the biometric flow; otherwise, it
     * defaults to a non-biometric method.
     *
     * @param request An instance of `ProviderCreateCredentialRequest` which contains request data
     *        and biometric prompt results.
     */
    private fun handleCreatePublicKeyCredentialRequest(request: ProviderCreateCredentialRequest) {
        // Receive the Seed
        // TODO: Decide UX for selector when it is AutoSelect.
        //  Possibly just use Two Tap UX which allows the seed id to be passed to the activity
        val seedId: Int = intent.getIntExtra(KEY_SEED_ID, 1)

        Log.d(TAG, "handleCreatePublicKeyCredentialRequest: $seedId")

        // Retrieve the BiometricPromptResult from the request.
        val biometricPromptResult = request.biometricPromptResult

        // Validate the error message in biometric result
        val biometricErrorMessage =
            BiometricErrorUtils.getBiometricErrorMessage(this, biometricPromptResult)

        // If there's valid biometric error, set up the failure response and finish.
        if (biometricErrorMessage.isNotEmpty()) {
            handleUnknownException(biometricErrorMessage)
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
                    seedId,
                )
                return
            }

            // If biometric authentication was not used or was not successful, use the default flow.
            createPasskeyWithDefaultFlow(
                publicKeyRequest.requestJson,
                request.callingAppInfo,
                publicKeyRequest.clientDataHash,
                seedId,
            )
        } else {
            handleUnknownException(getString(R.string.unexpected_create_request_found_in_intent))
            return
        }
    }

    /**
     * Initiates the default flow for creating a passkey by validating the request origin
     * and displaying a biometric prompt for user authentication.
     *
     * @param requestJson A JSON string representing the public key credential creation options.
     * @param callingAppInfo An object containing information about the calling application;
     *                       it includes details such as origin and signing info.
     * @param clientDataHash A hash of client-provided data used in the creation of the credential
     * @param seedId An integer identifier used for deriving the passkey.
     */
    private fun createPasskeyWithDefaultFlow(
        requestJson: String,
        callingAppInfo: CallingAppInfo?,
        clientDataHash: ByteArray?,
        seedId: Int,
    ) {
        if (callingAppInfo == null) {
            finish()
            return
        }

        val request = PublicKeyCredentialCreationOptions(requestJson)

        // Should always be a native call from GPM and no need to validate
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
                        seedId,
                    )
                }
            },
        )
        authenticate(biometricPrompt)
    }

    /**
     * Initiates the creation of a passkey using the biometric flow. This method processes the provided
     * request, validates the calling application, generates a key pair, and saves the passkey,
     * ensuring secure association with the calling application.
     *
     * @param requestJson A JSON string representing the public key credential creation options.
     * @param callingAppInfo An object containing details about the calling application, such as
     *                       origin and signing information. This parameter can be null.
     * @param clientDataHash A nullable byte array representing a hash of client-provided data used in
     *                       the creation of the credential.
     * @param seedId An integer identifier used for the derivation of the passkey.
     */
    private fun createPasskeyWithBiometricFlow(
        requestJson: String,
        callingAppInfo: CallingAppInfo?,
        clientDataHash: ByteArray?,
        seedId: Int,
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

        // Generate key, used
        val keyPair = passkeyManager.derivePasskey(
            seedId = seedId,
            origin = callingAppInfoOrigin ?: callingOrigin,
            userHandle = userHandle
        )

        // Save the private key in your local database against callingAppInfo.packageName.
        addNewPasskey(seedId, request, credentialId)

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

    /**
     * Constructs a `WebAuthn` response in the form of an `AuthenticatorAttestationResponse` object.
     * This response includes details like the credential ID, public key, attestation data, origin,
     * and other metadata required for the Web Authentication process.
     *
     * @param keyPair The key pair used for generating the public key credential.
     * @param request The public key credential creation options that define the credential request data.
     * @param credId A byte array representing the credential ID that uniquely identifies the created credential.
     * @param callingOrigin A string representing the origin of the calling application.
     * @param callingAppInfo An object containing metadata about the calling application.
     * @param clientDataHash An optional byte array containing the hash of client-provided data.
     * @return An instance of `AuthenticatorAttestationResponse` containing the constructed WebAuthn response data.
     */
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
            aaguid = UUID.randomUUID(),
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
            spki = spki,
        )
        return response
    }

    /**
     * Validates the asset links for a given relying party identifier (rpId) and calling application
     * information. This method ensures that the rpId is associated with the calling application by
     * verifying its signing info and package name. If the validation fails, it handles the error
     * condition accordingly.
     *
     * @param rpId The relying party identifier representing the domain or origin being validated.
     * @param callingAppInfo An object containing information about the calling application, including
     *                       package name and signing details.
     */
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
            handleUnknownException(getString(R.string.failed_to_validate_rp))
            return
        }
    }

    /**
     * Validates the given relying party identifier (rpId) by verifying its association with a caller's
     * package and signing information through the Asset Link Verification process.
     *
     * @param rpId The relying party identifier representing the domain or origin to be validated.
     * @param signingInfo The signing information of the calling application, used to verify its authenticity.
     * @param callingPackage The package name of the calling application.
     * @return `true` if the rpId is valid and the verification is successful; `false` otherwise.
     */
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

    /**
     * Validates the calling application's privilege level by checking if it is within the allowlist
     * of privileged applications. If the origin is validated, it is returned. Otherwise, null is returned
     * if the allowlist cannot be retrieved, or if there are formatting or state-related issues.
     *
     * @param callingAppInfo An instance of `CallingAppInfo` containing information about the calling application,
     *                       such as its origin and signing details.
     * @return A string representing the origin of the calling application if validation is successful,
     *         or null if the application is not privileged or errors occur during validation.
     */
    private fun validatePrivilegedCallingApp(callingAppInfo: CallingAppInfo): String? {
        val privilegedAppsAllowlist = getGPMPrivilegedAppAllowlist()
        if (privilegedAppsAllowlist != null) {
            return try {
                callingAppInfo.getOrigin(
                    privilegedAppsAllowlist,
                )
            } catch (e: IllegalStateException) {
                val message = getString(R.string.incoming_call_is_not_privileged_to_get_the_origin)
                handleUnknownException(message)
                null
            } catch (e: IllegalArgumentException) {
                val message = getString(R.string.privileged_allowlist_is_not_formatted_properly)
                handleUnknownException(message)
                null
            }
        }
        val message = "Could not retrieve GPM allowlist"
        handleUnknownException(message)
        return null
    }

    /**
     * Retrieves the allowlist of privileged applications for Google Play Management (GPM).
     * This method attempts to fetch the allowlist by making a network request to a predefined
     * URL and returning the response as a string. If an error occurs during the retrieval process,
     * such as a networking issue or invalid URL, it returns null.
     *
     * @return A string representing the allowlist of privileged applications if successfully retrieved,
     *         or null if the retrieval fails due to an exception.
     */
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

    /**
     * Determines whether the request contains an origin by attempting to retrieve the origin
     * from the provided `CallingAppInfo` instance. If an `IllegalStateException` occurs during
     * the process, it implies that the origin is not available in the request.
     *
     * @param callingAppInfo An instance of `CallingAppInfo` that holds information about the
     *                       calling application, including its origin and other metadata.
     * @return `true` if the request does not contain a valid origin; `false` otherwise.
     */
    private fun hasRequestContainsOrigin(callingAppInfo: CallingAppInfo): Boolean {
        try {
            // TODO: allow fetching of allow list from origin
            callingAppInfo.getOrigin(INVALID_ALLOWLIST)
        } catch (e: IllegalStateException) {
            return true
        }
        return false
    }

    /**
     * Converts a COSE key represented as a mutable map into an SPKI (Subject Public Key Info) formatted byte array.
     *
     * @param coseKey A mutable map containing key-value pairs representing a COSE-encoded key.
     *                Keys should include `-2` for the X-coordinate and `-3` for the Y-coordinate of the EC point.
     * @return A byte array representing the SPKI-formatted public key, or null if the conversion fails.
     * @todo: move to common module.
     */
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

    /**
     * Sets up an intent containing a response for the creation of a public key credential.
     * This method constructs a `FidoPublicKeyCredential` with the provided credential ID and
     * attestation response, wraps it as a `CreatePublicKeyCredentialResponse`, and attaches it
     * to the intent as an extra. The configured intent is then set as the result of the activity.
     *
     * @param credentialId A byte array representing the unique identifier for the created credential.
     * @param response The attestation response (`AuthenticatorAttestationResponse`) containing data
     *                 necessary for the credential creation process.
     */
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

    /**
     * Initiates authentication using the provided biometric prompt. This method builds
     * the biometric prompt information with specific configurations like title, subtitle,
     * and allowed authenticators, and then requests user authentication.
     *
     * @param biometricPrompt An instance of `BiometricPrompt` used to authenticate the user.
     */
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

    /**
     * Converts an EC public key into a COSE (CBOR Object Signing and Encryption) key format.
     *
     * @param key The EC public key to be converted.
     * @return A mutable map containing the COSE representation of the provided EC public key.
     * @todo: Move to common
     */
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

    /**
     * Converts a non-negative BigInteger into a fixed-size 32-byte array.
     * If the BigInteger is smaller than 32 bytes, the resulting array is left-padded with zeros.
     *
     * @param n The non-negative BigInteger to be converted. Assumes the input value is non-negative.
     * @return A 32-byte array representing the BigInteger.
     * @throws AssertionError If the input BigInteger is negative or exceeds 32 bytes when converted to a byte array.
     * @todo: Move to common
     */
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

    /**
     * Saves the passkey in the credentials data store.
     *
     * @param request The public key credential creation options.
     * @param credId The credential ID.
     */
    private fun addNewPasskey(
        seedId: Int,
        request: PublicKeyCredentialCreationOptions,
        credId: ByteArray,
    ) {
        runBlocking {
            passkeyRepository.addNewPasskey(
                Passkey(
                    siteId = null,
                    seedId = seedId,
                    uid = b64Encode(request.user.id),
                    origin = request.rp.id,
                    username = request.user.name,
                    userHandle = request.user.displayName,
                    displayName = request.user.displayName,
                    credId = b64Encode(credId),
                    count = 0,
                    lastUsed = 0,
                ),
            )
        }
    }
    companion object {
        /**
         * A JSON string representing an invalid allowlist configuration.
         *
         * This constant holds a predefined JSON object structure that contains
         * details about applications including their package names, build types,
         * and certificate fingerprints. This configuration is intentionally invalid
         * and may be used for testing purposes where an improperly defined allowlist
         * is required to simulate erroneous scenarios.
         * @deprecated
         */
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

        /**
         * @todo: Move to common sdk
         */
        private const val GPM_ALLOWLIST_URL =
            "https://www.gstatic.com/gpm-passkeys-privileged-apps/apps.json"
        private const val TAG = "CreatePasskeyActivity"
    }
}
