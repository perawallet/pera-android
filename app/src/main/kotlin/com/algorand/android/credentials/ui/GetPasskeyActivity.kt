package com.algorand.android.credentials.ui

import android.content.Intent
import android.content.pm.SigningInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.biometric.BiometricPrompt.PromptInfo.Builder
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.provider.BiometricPromptResult
import androidx.credentials.provider.CallingAppInfo
import androidx.credentials.provider.PendingIntentHandler
import androidx.credentials.provider.ProviderGetCredentialRequest
import androidx.fragment.app.FragmentActivity
import com.algorand.android.R
import com.algorand.android.credentials.BiometricErrorUtils
import com.algorand.android.credentials.encoding.appInfoToOrigin
import com.algorand.android.credentials.encoding.b64Decode
import com.algorand.android.credentials.webauthn.AssetLinkVerifier
import com.algorand.android.credentials.webauthn.AuthenticatorAssertionResponse
import com.algorand.android.credentials.webauthn.AuthenticatorFlags
import com.algorand.android.credentials.webauthn.FidoPublicKeyCredential
import com.algorand.android.credentials.webauthn.PublicKeyCredentialRequestOptions
import com.algorand.wallet.account.webauthn.domain.PasskeyManager
import com.algorand.wallet.account.webauthn.domain.repository.PasskeyRepository
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.net.URL
import java.security.Security

/**
 * GetPasskeyActivity is responsible for managing the workflow related to retrieving passkeys
 * for authentication in a secure and efficient manner. This includes handling the intent to retrieve
 * a passkey, configuring assertion processes, and validating the calling application's credentials.
 *
 * @property passkeyRepository The repository interface for accessing passkey-related data and operations.
 * @property passkeyManager The manager facilitating passkey operations and handling the assertion process.
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@AndroidEntryPoint
class GetPasskeyActivity : FragmentActivity() {
    @Inject
    lateinit var passkeyRepository: PasskeyRepository
    @Inject
    lateinit var passkeyManager: PasskeyManager

    /**
     * Called when the activity is first created.
     *
     * This method initializes the activity by setting up the necessary providers
     * and enabling edge-to-edge UI.
     *
     * Additionally, it invokes intent handling logic specific to retrieving
     * and processing passkey-related information.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down, this Bundle contains the data it most recently supplied.
     * Otherwise, it is null.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 0)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleGetPasskeyIntent()
    }

    /**
     * Handles the intent to retrieve a passkey.
     *
     * This method is invoked to extract and process the relevant data from the incoming intent
     * for handling passkey functionality. The flow includes checking whether the intent contains
     * the required information, validating biometric authentication results, and configuring the
     * passkey assertion process.
     *
     * Key Steps:
     * - Extracts the intent data, including supplementary request details and the passkey request.
     * - Validates the extracted data and checks for any errors.
     * - Handles biometric authentication results and ensures there are no errors during the process.
     * - Configures the assertion flow for the passkey based on the retrieved information.
     *
     * Failure Handling:
     * If the required data or request is missing, or if biometric errors are encountered, a failure
     * response is configured, and the activity is terminated.
     */
    private fun handleGetPasskeyIntent() {
        // Retrieve the request information from the intent.
        val requestInfo = intent.getBundleExtra(getString(R.string.extra_intent_data))

        // Retrieve the boolean indicating if the passkey is auto selected.
        intent.getBooleanExtra(getString(R.string.is_auto_selected), false)

        /*
         * retrieveProviderGetCredentialRequest extracts the [ProviderGetCredentialRequest] from the provider's
         * [PendingIntent] invoked by the Android system, when the user selects a
         * [CredentialEntry].
         */
        val request = PendingIntentHandler.retrieveProviderGetCredentialRequest(intent)

        // Check if the request information or the request itself is null.
        if (requestInfo == null || request == null) {
            // If either is null, set up a failure response and finish the activity.
            setUpFailureResponseAndFinish(getString(R.string.unable_to_extract_data_from_intent))
            return
        }

        // Retrieve the BiometricPromptResult from the request.
        val biometricPromptResult = request.biometricPromptResult

        // Validate the error message in a biometric result
        val biometricErrorMessage =
            BiometricErrorUtils.getBiometricErrorMessage(this, biometricPromptResult)

        // If there's a valid biometric error, set up the failure response and finish.
        if (biometricErrorMessage.isNotEmpty()) {
            setUpFailureResponseAndFinish(biometricErrorMessage)
            return
        }

        // Configure the passkey assertion.
        configurePasskeyAssertion(
            biometricPromptResult,
            requestInfo,
            request,
        )
    }

    /**
     * Configures the passkey assertion process.
     *
     * <p>This method orchestrates the passkey assertion process by retrieving
     * necessary data from the request, validating the calling application, and
     * determining whether to use the biometric or default flow for assertion.
     *
     * @param biometricPromptResult The result of the biometric authentication prompt.
     * @param requestInfo           The bundle containing additional request information.
     * @param request               The {@link ProviderGetCredentialRequest} containing the
     *                              credential request details.
     */
    @Suppress("MaxLineLength")
    private fun configurePasskeyAssertion(
        biometricPromptResult: BiometricPromptResult?,
        requestInfo: Bundle,
        request: ProviderGetCredentialRequest,
    ) {
        // Retrieve the encoded credential ID from the request information.
        val credentialIdEncoded = requestInfo.getString(getString(R.string.cred_id))!!

        // Retrieve the PasskeyItem from the data source using the encoded credential ID.
        val passkey = runBlocking { passkeyRepository.getPasskey(credentialIdEncoded)!! }

        // Extract the GetPublicKeyCredentialOption from the request.
        val publicKeyCredentialOption = request.credentialOptions[0]
        val publicKeyRequest = publicKeyCredentialOption as GetPublicKeyCredentialOption

        // Create a PublicKeyCredentialRequestOptions object from the extracted option.
        val publicKeyRequestOptions = PublicKeyCredentialRequestOptions(
            publicKeyRequest.requestJson,
        )

        // Decode the credential ID, private key, and UID from their base64 encoded forms.
        val credentialID = b64Decode(credentialIdEncoded)

        val uid = b64Decode(passkey.userId)
        val userHandle = passkey.userHandle.lowercase()

        // Determine the calling application's origin and validate it.
        var callingAppOriginInfo: String? = null
        if (hasRequestContainsOrigin(request.callingAppInfo)) {
            callingAppOriginInfo = validatePrivilegedCallingApp(
                request.callingAppInfo,
            )
        } else {
            // Native call. Check for asset links to verify app's identity
            validateAssetLinks(
                publicKeyRequestOptions.rpId,
                request.callingAppInfo,
            )
        }

        // Get the origin and package name from the calling app info.
        val origin = appInfoToOrigin(request.callingAppInfo)
        val packageName = request.callingAppInfo.packageName

        // Extract the client data hash if the calling application's origin is available.
        var clientDataHash: ByteArray? = null
        if (callingAppOriginInfo != null) {
            clientDataHash = publicKeyRequest.clientDataHash
        }

        // Check if the biometric prompt result indicates successful authentication.
        if (biometricPromptResult?.authenticationResult != null) {
            // If biometric authentication was successful, use the biometric flow.
            assertPasskeyWithBiometricFlow(
                credId = credentialID,
                origin = origin,
                request = publicKeyRequestOptions,
                uid = uid,
                userHandle = userHandle,
                packageName = packageName,
                callingAppInfo = callingAppOriginInfo,
                clientDataHash = clientDataHash,

            )
        } else {
            // If biometric authentication was not used or was not successful, use the default flow.
            assertPasskeyWithDefaultFlow(
                credId = credentialID,
                origin = origin,
                request = publicKeyRequestOptions,
                uid = uid,
                userHandle = userHandle,
                callingAppInfo = callingAppOriginInfo,
                clientDataHash = clientDataHash,
            )
        }
    }

    /**
     * This method helps check the asset linking to verify client app idenity
     * @param rpId : Relying party identifier
     * @param callingAppInfo : Information pertaining to the calling application.
     */
    private fun validateAssetLinks(rpId: String, callingAppInfo: CallingAppInfo) {
        val isRpValid: Boolean = runBlocking {
            val isRpValidDeferred: Deferred<Boolean> = async(Dispatchers.IO) {
                return@async isValidRpId(
                    rpId,
                    callingAppInfo.signingInfo,
                    callingAppInfo.packageName,
                )
            }
            return@runBlocking isRpValidDeferred.await()
        }

        if (!isRpValid) {
            setUpFailureResponseAndFinish("Failed to validate rp")
            return
        }
    }

    /**
     * Validates the Relying Party (RP) identifier using asset linking.
     *
     * @param rpId The identifier of the RP.
     * @param signingInfo The signing information of the calling app.
     * @param callingPackage The package name of the calling app.
     * @return True if the RP is valid, false otherwise.
     */
    private fun isValidRpId(
        rpId: String,
        signingInfo: SigningInfo,
        callingPackage: String,
    ): Boolean {
        val websiteUrl = "https://$rpId"
        val assetLinkVerifier = AssetLinkVerifier(websiteUrl)
        try {
            return assetLinkVerifier.verify(callingPackage, signingInfo)
        } catch (e: Exception) {
            // Log exception
        }
        return false
    }

    /**
     * Validates if the app is privileged to get the origin, i.e., allowlisted in GPM privileged apps.
     *
     * @param callingAppInfo Information pertaining to the calling application.
     * @return The origin if the app is privileged, or null otherwise.
     */
    private fun validatePrivilegedCallingApp(callingAppInfo: CallingAppInfo): String? {
        val allowlistJson = getGPMPrivilegedAppAllowlist()
        if (allowlistJson != null) {
            return try {
                callingAppInfo.getOrigin(
                    allowlistJson,
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
        val message = getString(R.string.could_not_retrieve_gpm_allowlist)
        setUpFailureResponseAndFinish(message)
        return null
    }

    /**
     * Method to retrieve the list of privileged apps allowlisted by GPM
     *
     * @return The allowlist as a JSON string, or null if there is an error.
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
     * Checks if the client request contains an origin for the calling app.
     *
     * @param callingAppInfo Information pertaining to the calling application.
     * @return True if the request contains an origin, false otherwise.
     */
    private fun hasRequestContainsOrigin(callingAppInfo: CallingAppInfo): Boolean {
        try {
            callingAppInfo.getOrigin(INVALID_ALLOWLIST)
        } catch (e: IllegalStateException) {
            return true
        } catch (e: IllegalArgumentException) {
            return false
        }
        return false
    }

    /**
     * Sets up a failure response and finishes the activity.
     *
     * @param message The error message to include in the response.
     */
    private fun setUpFailureResponseAndFinish(message: String) {
        val result = Intent()
        PendingIntentHandler.setGetCredentialException(
            result,
            GetCredentialUnknownException(message),
        )
        setResult(RESULT_OK, result)
        finish()
    }

    /**
     * Asserts the passkey using the default authentication flow.
     *
     * This method configures a BiometricPrompt with the provided parameters and initiates the
     * authentication process to assert the passkey.
     *
     * @param origin The origin of the calling application.
     * @param callingAppInfo Information about the calling application, optionally null.
     * @param request The PublicKeyCredentialRequestOptions containing the credential request details.
     * @param uid The unique identifier associated with the passkey.
     * @param userHandle The user handle associated with the credential request.
     * @param clientDataHash The client data hash, optionally null.
     * @param credId The credential ID for the associated passkey.
     */
    private fun assertPasskeyWithDefaultFlow(
        credId: ByteArray,
        origin: String,
        request: PublicKeyCredentialRequestOptions,
        uid: ByteArray,
        userHandle: String,
        callingAppInfo: String?,
        clientDataHash: ByteArray?,
    ) {
        // Configure the BiometricPrompt with the provided parameters.
        val biometricPrompt = configureBioMetricPrompt(
            credId = credId,
            origin = origin,
            callingAppInfoOrigin = callingAppInfo,
            request = request,
            uid = uid,
            userHandle = userHandle,
            packageName = packageName,
            clientDataHash = clientDataHash,
        )
        // Initiate the authentication process using the configured BiometricPrompt.
        authenticate(biometricPrompt)
    }

    /**
     * Configures and returns a BiometricPrompt for user authentication.
     *
     * This method sets up a BiometricPrompt with a callback to handle authentication
     * events like success, failure, or errors. Once the user is authenticated,
     * it invokes the passkey assertion process.
     *
     * @param origin The origin of the calling application.
     * @param callingAppInfoOrigin The origin information of the calling application, if available.
     * @param request The PublicKeyCredentialRequestOptions containing the credential request details.
     * @param uid The unique identifier associated with the passkey.
     * @param userHandle The user handle associated with the credential request.
     * @param packageName The package name of the calling application.
     * @param clientDataHash The client data hash, optionally null.
     * @param credId The credential ID for the associated passkey.
     * @return A configured BiometricPrompt instance ready to be displayed to the user.
     */
    private fun configureBioMetricPrompt(
        credId: ByteArray,
        origin: String,
        request: PublicKeyCredentialRequestOptions,
        uid: ByteArray,
        userHandle: String,
        packageName: String,
        callingAppInfoOrigin: String?,
        clientDataHash: ByteArray?,
    ): BiometricPrompt {
        val biometricPrompt = BiometricPrompt(
            this,
            mainExecutor,
            object : AuthenticationCallback() {
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
                    result: AuthenticationResult,
                ) {
                    super.onAuthenticationSucceeded(result)

                    assertPasskeyWithBiometricFlow(
                        credId = credId,
                        origin = origin,
                        request = request,
                        uid = uid,
                        userHandle = userHandle,
                        packageName = packageName,
                        callingAppInfo = callingAppInfoOrigin,
                        clientDataHash = clientDataHash,
                    )
                }
            },
        )
        return biometricPrompt
    }

    /**
     * Asserts the passkey using the biometric authentication flow.
     * This method determines the appropriate origin (calling app origin if available, otherwise the provided origin)
     * and configures the credential response accordingly.
     *
     * @param credId The credential ID for the associated passkey.
     * @param origin The origin of the calling application.
     * @param callingAppInfoOrigin The origin information of the calling application if available.
     * @param request The PublicKeyCredentialRequestOptions containing the credential request details.
     * @param uid The unique identifier associated with the passkey.
     * @param userHandle The user handle associated with the credential request.
     * @param packageName The package name of the calling application.
     * @param clientDataHash The client data hash, if available.
     */
    private fun assertPasskeyWithBiometricFlow(
        credId: ByteArray,
        origin: String,
        request: PublicKeyCredentialRequestOptions,
        uid: ByteArray,
        userHandle: String,
        packageName: String,
        callingAppInfo: String?,
        clientDataHash: ByteArray?,
    ) {

        // Determine the calling origin. If callingAppInfoOrigin is available, use it;
        // otherwise, use the provided origin.
        var callingOrigin = origin
        if (callingAppInfo != null) {
            callingOrigin = callingAppInfo
        }

        // Configure the credential response with the determined origin and other parameters.
        configureGetCredentialResponse(
            credId = credId,
            request = request,
            origin = callingOrigin,
            uid = uid,
            userHandle = userHandle,
            packageName = packageName,
            clientDataHash = clientDataHash,
        )
    }

    /**
     * To validate the user, surface a Biometric prompt (or other assertion method).
     *
     * @param biometricPrompt The BiometricPrompt object to use for authentication.
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
     * Configures the response for a Get Credential operation, constructing an
     * AuthenticatorAssertionResponse and packaging it into a PublicKeyCredential.
     *
     * @param credId The credential identifier in the form of a byte array.
     * @param request The options for the public key credential request.
     * @param origin The origin of the request.
     * @param uid A byte array representing the user identifier.
     * @param userHandle The string representation of the user handle.
     * @param packageName The package name of the requesting application.
     * @param clientDataHash Optional byte array representing the hash of the client data, if provided.
     */
    private fun configureGetCredentialResponse(
        credId: ByteArray,
        request: PublicKeyCredentialRequestOptions,
        origin: String,
        uid: ByteArray,
        userHandle: String,
        packageName: String,
        clientDataHash: ByteArray?,
    ) {
        val response = AuthenticatorAssertionResponse(
            requestOptions = request,
            origin = origin,
            authFlags = AuthenticatorFlags(
                up = true,
                uv = true,
                be = true,
                bs = true,
            ),
            userHandle = uid,
            packageName = packageName,
            clientDataHash,
        )

         response.signature = passkeyManager.signPasskey(1, origin, userHandle, response.dataToSign())

        val credential = FidoPublicKeyCredential(
            rawId = credId,
            response = response,
            authenticatorAttachment = getString(R.string.platform),
        )
        val intent = Intent()
        val cred = PublicKeyCredential(credential.json())
        PendingIntentHandler.setGetCredentialResponse(
            intent,
            GetCredentialResponse(cred),
        )
        setResult(RESULT_OK, intent)
        finish()
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

        private const val TAG = "GetPasskeyActivity"
    }
}
