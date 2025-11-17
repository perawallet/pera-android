# ============================================================================
#  PERA WALLET PROGUARD CONFIGURATION
# ============================================================================
# Optimized for code shrinking, obfuscation, and APK size reduction

# ────────────────────────────────────────────────────────────────────────────
#  GLOBAL ATTRIBUTES
# ────────────────────────────────────────────────────────────────────────────
# Keep source file and line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable

# Keep generic signatures for reflection
-keepattributes Signature

# Keep annotations for reflection and runtime access
-keepattributes *Annotation*

# Keep inner classes and enclosing methods for proper reflection
-keepattributes InnerClasses,EnclosingMethod

# Keep runtime visible annotations (for Retrofit, etc.)
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations

# Keep annotation default values
-keepattributes AnnotationDefault

# ────────────────────────────────────────────────────────────────────────────
#  PERA SPECIFIC RULES
# ────────────────────────────────────────────────────────────────────────────
# Keep data models that may be used with reflection/serialization
-keep class com.algorand.android.**.model.** { *; }
-keep class com.algorand.wallet.**.model.** { *; }
-keep class com.algorand.wallet.**.entity.** { *; }

# Keep enums (used in model classes and potentially serialized)
-keep public enum com.algorand.android.** { *; }

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep WalletConnect transaction classes
-keep class com.algorand.android.ui.wctransactionrequest.WalletConnectTransactionListItem { *; }

# ────────────────────────────────────────────────────────────────────────────
#  GLIDE
# ────────────────────────────────────────────────────────────────────────────
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
    *** rewind();
}

# ────────────────────────────────────────────────────────────────────────────
#  RETROFIT
# ────────────────────────────────────────────────────────────────────────────
# Keep service method parameters
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep interfaces with Retrofit annotations
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# Keep Continuation for suspend functions
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep return types
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# Keep Response wrapper
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Ignore warnings
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# ────────────────────────────────────────────────────────────────────────────
#  GSON
# ────────────────────────────────────────────────────────────────────────────
# Keep classes with @SerializedName
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep TypeAdapter implementations
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep TypeToken
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Ignore warnings
-dontwarn sun.misc.**

# ────────────────────────────────────────────────────────────────────────────
#  FIREBASE & CRASHLYTICS
# ────────────────────────────────────────────────────────────────────────────
# Keep exception classes for proper crash reporting
-keep public class * extends java.lang.Exception

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.crashlytics.** { *; }

# Keep @Keep annotated classes
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Ignore warnings
-dontwarn com.crashlytics.**
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**
-dontwarn com.fasterxml.jackson.databind.**

# ────────────────────────────────────────────────────────────────────────────
#  DAGGER HILT
# ────────────────────────────────────────────────────────────────────────────
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.EarlyEntryPoint class *
-keep class javax.inject.* { *; }
-keep class dagger.hilt.** { *; }
-keep class **.Hilt_* { *; }
-keep @dagger.hilt.EntryPoint class *
-keep @dagger.hilt.android.EarlyEntryPoint class *
-keep,allowobfuscation,allowshrinking @dagger.hilt.EntryPoint class *
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.EarlyEntryPoint class *

-dontwarn com.google.errorprone.annotations.*

# ────────────────────────────────────────────────────────────────────────────
#  ALGORAND SDK
# ────────────────────────────────────────────────────────────────────────────
-keep class com.algorand.algosdk.** { *; }
-keep class org.msgpack.core.buffer.** { *; }

# ────────────────────────────────────────────────────────────────────────────
#  BOUNCYCASTLE
# ────────────────────────────────────────────────────────────────────────────
-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.** { *; }

-dontwarn javax.naming.**

# ────────────────────────────────────────────────────────────────────────────
#  WALLET CONNECT
# ────────────────────────────────────────────────────────────────────────────
-keep class app.perawallet.walletconnectv1.** { *; }
-keep interface app.perawallet.walletconnectv1.** { *; }
-keep class app.perawallet.walletconnectv2.** { *; }
-keep interface app.perawallet.walletconnectv2.** { *; }

# ────────────────────────────────────────────────────────────────────────────
#  ROOM DATABASE
# ────────────────────────────────────────────────────────────────────────────
# Keep Room annotated classes
-keep @androidx.room.Dao class *
-keep @androidx.room.Dao interface *
-keep @androidx.room.Entity class *
-keep class * extends androidx.room.RoomDatabase

# Keep specific databases
-keep class com.algorand.wallet.account.local.data.database.AddressDatabase { *; }
-keep class com.algorand.wallet.foundation.database.PeraDatabase { *; }

# Keep Room mappers and DAOs
-keepclassmembers class com.algorand.wallet.**.mapper.** { *; }
-keepclassmembers class com.algorand.wallet.**.dao.** { *; }
-keepclassmembers interface com.algorand.wallet.**.mapper.** { *; }
-keepclassmembers interface com.algorand.wallet.**.dao.** { *; }

# Keep classes with Room annotations
-keep class * {
    @androidx.room.Query <methods>;
    @androidx.room.AutoMigration <methods>;
}

# Keep annotated fields
-keepclassmembers class ** {
    @androidx.room.Embedded <fields>;
    @androidx.room.Relation <fields>;
    @androidx.room.ColumnInfo <fields>;
    @androidx.room.PrimaryKey <fields>;
    @androidx.room.ForeignKey <fields>;
    @androidx.room.Index <fields>;
}

# Keep RoomDatabase members
-keepclassmembers class * extends androidx.room.RoomDatabase {
    androidx.room.InvalidationTracker invalidationTracker;
    androidx.room.dao.* *;
}

# ────────────────────────────────────────────────────────────────────────────
#  JETPACK COMPOSE
# ────────────────────────────────────────────────────────────────────────────
# Keep @Composable functions (for reflection/tooling)
-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep @Preview functions (for Android Studio previews)
-keepclasseswithmembers class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}

# ────────────────────────────────────────────────────────────────────────────
#  COROUTINES
# ────────────────────────────────────────────────────────────────────────────
# Keep Continuation for proper coroutine functionality
-keep class kotlin.coroutines.Continuation { *; }
-keep class kotlin.coroutines.CoroutineContext { *; }

# ────────────────────────────────────────────────────────────────────────────
#  JNA (Java Native Access)
# ────────────────────────────────────────────────────────────────────────────
-keep class net.java.dev.jna.** { *; }
-keepclassmembers class net.java.dev.jna.** { *; }
-keep class **.jna.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep Structure fields
-keep class ** {
    @**.Structure$FieldOrder *;
}

-keepclassmembers class * {
    @net.java.dev.jna.annotation.* *;
}

# XHD Wallet API
-keep class app.perawallet.xhdwalletapi.** { *; }
-keep interface app.perawallet.xhdwalletapi.** { *; }

-dontwarn com.sun.jna.Native

# ────────────────────────────────────────────────────────────────────────────
#  AES ENCRYPTION
# ────────────────────────────────────────────────────────────────────────────
-keep interface com.algorand.wallet.encryption.domain.manager.AESPlatformManager { *; }
-keep class com.algorand.wallet.encryption.domain.manager.AESPlatformManagerImpl { *; }
-keepclassmembers class com.algorand.wallet.encryption.domain.manager.AESPlatformManagerImpl { *; }

-keep class javax.crypto.** { *; }
-keep class android.security.keystore.** { *; }
-keep class java.security.** { *; }

# ────────────────────────────────────────────────────────────────────────────
#  SQLCIPHER
# ────────────────────────────────────────────────────────────────────────────
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }

# ────────────────────────────────────────────────────────────────────────────
#  SUPPRESS WARNINGS
# ────────────────────────────────────────────────────────────────────────────
# Android/Google
-dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.ThreadMXBean
-dontwarn java.awt.Component
-dontwarn java.awt.GraphicsEnvironment
-dontwarn java.awt.HeadlessException
-dontwarn java.awt.Window
-dontwarn java.awt.geom.AffineTransform
-dontwarn java.lang.reflect.AnnotatedType
-dontwarn sun.nio.ch.**
-dontwarn sun.security.x509.**

# JSON Schema (not used on Android)
-dontwarn net.pwall.json.schema.JSONSchema
-dontwarn net.pwall.json.pointer.JSONPointer
-dontwarn net.pwall.json.schema.output.BasicOutput

# Compression codecs (optional)
-dontwarn com.aayushatharva.brotli4j.**
-dontwarn com.github.luben.zstd.**
-dontwarn com.jcraft.jzlib.**
-dontwarn com.ning.compress.**
-dontwarn lzma.sdk.**
-dontwarn net.jpountz.**

# Logging backends (server-side)
-dontwarn org.apache.log4j.**
-dontwarn org.apache.logging.log4j.**

# Jetty (server-side)
-dontwarn org.eclipse.jetty.**

# Server-side dependencies
-dontwarn org.jboss.marshalling.**
-dontwarn org.osgi.**
-dontwarn java.beans.**
-dontwarn java.rmi.**
-dontwarn javax.tools.**

# Netty native transport (server-side)
-dontwarn io.netty.channel.epoll.**
-dontwarn io.netty.channel.kqueue.**
-dontwarn io.netty.internal.tcnative.**
-dontwarn io.netty.handler.codec.haproxy.**
-dontwarn io.netty.util.internal.Hidden$NettyBlockHoundIntegration

# Vert.x (server-side)
-dontwarn io.vertx.codegen.**
-dontwarn io.vertx.core.impl.transports.**
-dontwarn io.vertx.core.logging.**
-dontwarn io.vertx.core.net.impl.HAProxyMessageCompletionHandler
-dontwarn io.vertx.core.http.impl.HttpServerWorker

# GraalVM (not used on Android)
-dontwarn com.oracle.svm.**

# Other optional dependencies
-dontwarn org.identityconnectors.**
-dontwarn groovy.**
-dontwarn reactor.blockhound.**
-dontwarn reactor.blockhound.integration.**
-dontwarn com.google.protobuf.nano.**
