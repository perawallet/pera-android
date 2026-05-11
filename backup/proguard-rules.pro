# Backup module proguard rules (local minification)
# Consumer-facing rules are in consumer-rules.pro

-keepattributes Signature, *Annotation*

# Gson serialization
-keep class com.algorand.backup.data.api.model.** { *; }
-keep class com.algorand.backup.data.model.** { *; }
-keep class com.algorand.backup.account.domain.model.** { *; }
-keep class com.algorand.backup.contact.data.model.** { *; }

# Enums deserialized by Gson
-keep enum com.algorand.backup.domain.model.** { *; }

# Retrofit
-keep,allowobfuscation interface com.algorand.backup.data.service.BackupApiService
-keep,allowobfuscation interface com.algorand.backup.data.service.BackupRegistrationApiService

# BouncyCastle crypto
-keep class org.bouncycastle.crypto.generators.Argon2BytesGenerator { *; }
-keep class org.bouncycastle.crypto.params.Argon2Parameters { *; }
-keep class org.bouncycastle.crypto.params.Argon2Parameters$Builder { *; }
-keep class org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters { *; }
-keep class org.bouncycastle.crypto.params.Ed25519PublicKeyParameters { *; }
-keep class org.bouncycastle.crypto.generators.HKDFBytesGenerator { *; }
-keep class org.bouncycastle.crypto.params.HKDFParameters { *; }
-keep class org.bouncycastle.crypto.digests.SHA256Digest { *; }
-keep class org.bouncycastle.crypto.signers.Ed25519Signer { *; }

# Security / Keystore
-keep class com.algorand.backup.domain.security.** { *; }
