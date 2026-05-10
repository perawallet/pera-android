# ---------------- Backup Module Consumer Rules -------------------

# Gson serialization models — keep class members for reflection-based deserialization
-keep class com.algorand.backup.data.api.model.** { *; }
-keep class com.algorand.backup.data.model.** { *; }
-keep class com.algorand.backup.account.domain.model.** { *; }
-keep class com.algorand.backup.contact.data.model.** { *; }

# Enums deserialized by Gson — keep values from being stripped
-keep enum com.algorand.backup.domain.model.** { *; }

# Retrofit service interfaces — R8 full mode can strip proxy-based interfaces
-keep,allowobfuscation interface com.algorand.backup.data.service.BackupApiService
-keep,allowobfuscation interface com.algorand.backup.data.service.BackupRegistrationApiService

# BouncyCastle crypto — accessed via reflection and JCA provider lookup
-keep class org.bouncycastle.crypto.generators.Argon2BytesGenerator { *; }
-keep class org.bouncycastle.crypto.params.Argon2Parameters { *; }
-keep class org.bouncycastle.crypto.params.Argon2Parameters$Builder { *; }
-keep class org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters { *; }
-keep class org.bouncycastle.crypto.params.Ed25519PublicKeyParameters { *; }
-keep class org.bouncycastle.crypto.generators.HKDFBytesGenerator { *; }
-keep class org.bouncycastle.crypto.params.HKDFParameters { *; }
-keep class org.bouncycastle.crypto.digests.SHA256Digest { *; }
-keep class org.bouncycastle.crypto.signers.Ed25519Signer { *; }

# Security classes that interact with Android Keystore via reflection
-keep class com.algorand.backup.domain.security.** { *; }
