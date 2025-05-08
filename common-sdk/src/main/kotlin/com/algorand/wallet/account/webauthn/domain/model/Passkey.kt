package com.algorand.wallet.account.webauthn.domain.model

/**
 * Represents a passkey used for authentication or cryptographic purposes.
 *
 * The `Passkey` class encapsulates metadata associated with a passkey, including
 * user identification, site-specific details, and credential information. This
 * data is typically used for authentication workflows, secure storage, and
 * managing credentials associated with a site or service.
 *
 * Primary fields include:
 * - `uid`: A unique identifier for the user associated with the passkey.
 * - `rpid`: The relying party ID (relying party identifier), which corresponds to
 *   the domain or site the passkey is linked with.
 * - `username`: The username associated with the passkey, identifying the user at
 *   the site or service.
 * - `displayName`: A human-readable display name for the user, which may be shown
 *   during user interactions.
 * - `credId`: A unique credential ID used to identify the passkey credential.
 */
data class Passkey(
    val seedId: String,
    val uid: String,
    val rpid: String,
    val username: String,
    val displayName: String,
    val credId: String
)
