package com.algorand.android.credentials.webauthn

data class AuthenticatorFlags(
    val up: Boolean,
    val uv: Boolean,
    val be: Boolean,
    val bs: Boolean,
)

data class PublicKeyCredentialRpEntity(
    val name: String,
    val id: String,
)

data class PublicKeyCredentialUserEntity(
    val name: String,
    val id: ByteArray,
    val displayName: String,
)

data class PublicKeyCredentialParameters(
    val type: String,
    val alg: Long,
)

data class PublicKeyCredentialDescriptor(
    val type: String,
    val id: ByteArray,
    val transports: List<String>,
)

data class AuthenticatorSelectionCriteria(
    val authenticatorAttachment: String,
    val residentKey: String,
    val requireResidentKey: Boolean = false,
    val userVerification: String = "preferred",
)
