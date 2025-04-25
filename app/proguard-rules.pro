# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ---------------- BEGIN PERA -------------------
-keep public enum com.algorand.android.**{ *; }
-keep class com.algorand.android.** { *; }
-keep class com.algorand.wallet.** { *; }
-keep interface com.algorand.wallet.** { *; }
-keep class androidx.** { *; }
-keep class com.algorand.android.**.model.** { *; }

-keep class com.algorand.android.ui.wctransactionrequest.WalletConnectTransactionListItem
# ---------------- END PERA ---------------------

# ---------------- BEGIN GLIDE -------------------
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
# ---------------- END GLIDE -------------------


# ---------------- BEGIN RETROFIT -------------------
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response
# ---------------- END RETR0FIT -------------------


# ---------------- BEGIN GSON -------------------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
# ---------------- END GSON -------------------


# ---------------- BEGIN FIREBASE -------------------
-keep class com.firebase.** { *; }
-keep class org.shaded.apache.** { *; }
-keepnames class com.shaded.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**


-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
 -dontwarn com.fasterxml.jackson.databind.**
 -keep class org.codehaus.** { *; }
# -keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
# public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
# ---------------- END FIREBASE -------------------


# ---------------- BEGIN CRASHLYTICS -------------------
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# ---------------- END CRASHLYTICS -------------------


# ---------------- BEGIN DAGGER -------------------
-dontwarn com.google.errorprone.annotations.*
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.EarlyEntryPoint class *
-keep class javax.inject.* { *; }
-keep class dagger.hilt.** { *; }
-keep class **.Hilt_* { *; }
-keep @dagger.hilt.EntryPoint class *
-keep @dagger.hilt.android.EarlyEntryPoint class *
-keep,allowobfuscation,allowshrinking @dagger.hilt.EntryPoint class *
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.EarlyEntryPoint class *
# ---------------- END DAGGER -------------------


# ---------------- BEGIN AlgoSDK -------------------
-keep class org.msgpack.core.buffer.** { *; }
# ---------------- END AlgoSDK -------------------


# ---------------- BEGIN BouncyCastle -------------------
-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.** { *; }

-dontwarn javax.naming.**
# ---------------- END BouncyCastle -------------------


# ---------------- BEGIN WALLET CONNECT -------------------
-keep class org.walletconnect.** { *; }
-keep interface org.walletconnect.** { *; }

-keep class com.walletconnect.** { *; }
-keep interface com.walletconnect.** { *; }
# ---------------- END WALLET CONNECT -------------------


# ---------------- BEGIN ROOM -------------------
# Keep Room database classes and their fields
-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }
-keep @androidx.room.Dao class *
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-keep @androidx.room.Entity interface *
-keep class * extends androidx.room.RoomDatabase

-keep class com.algorand.wallet.account.local.data.database.AddressDatabase { *; }
-keep class com.algorand.wallet.foundation.database.PeraDatabase { *; }
-keepclassmembers class com.algorand.wallet.**.model.** { *; }
-keepclassmembers class com.algorand.wallet.**.mapper.** { *; }
-keepclassmembers class com.algorand.wallet.**.dao.** { *; }
-keepclassmembers class com.algorand.wallet.**.entity.** { *; }

-keepclassmembers interface com.algorand.wallet.**.model.** { *; }
-keepclassmembers interface com.algorand.wallet.**.mapper.** { *; }
-keepclassmembers interface com.algorand.wallet.**.dao.** { *; }
-keepclassmembers interface com.algorand.wallet.**.entity.** { *; }
-keep @androidx.room.Dao interface *

# Keep any classes and methods annotated with Room annotations
-keep class * {
    @androidx.room.Query <methods>;
}

# Keep Room's AutoMigration-annotated classes
-keep class * {
    @androidx.room.AutoMigration <methods>;
}

# Keep Embedded and Relation fields
-keepclassmembers class ** {
    @androidx.room.Embedded <fields>;
    @androidx.room.Relation <fields>;
    @androidx.room.ColumnInfo <fields>;
    @androidx.room.PrimaryKey <fields>;
    @androidx.room.ForeignKey <fields>;
    @androidx.room.Index <fields>;
}

# Keep constructors of @Entity classes with @Ignore annotations
-keepclassmembers class * extends androidx.room.RoomDatabase {
    androidx.room.InvalidationTracker invalidationTracker;
    androidx.room.dao.* *;
}

-keepattributes *Annotation*

# Keep TypeConverters
-keep class * extends androidx.room.TypeConverter
# ---------------- END ROOM -------------------


# ---------------- BEGIN GMS -------------------
# Keep classes used by Google Play Services
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }

# Keep methods with @Keep annotation
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Firebase Analytics
-keep class com.google.firebase.analytics.** { *; }

# Firebase Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep class com.crashlytics.** { *; }
-keep class com.google.firebase.crashlytics.** { *; }

# Google Play Services measurement
-keep class com.google.android.gms.measurement.** { *; }

# For Google Play Services auth
-keepattributes Signature
-keepattributes *Annotation*

# Google Location
-keep class com.google.android.gms.location.** { *; }
# ---------------- END GMS -------------------


# ---------------- BEGIN COROUTINE -------------------

-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep class kotlin.ranges.** { *; } # needed for ranges in coroutines
-keep class kotlin.sequences.** { *; } # needed for sequences in coroutines
-keep class kotlin.collections.** { *; } # needed for collections in coroutines
-keep class kotlinx.atomicfu.** { *; } # needed for atomic operations in coroutines
-keep class kotlinx.serialization.** { *; } # if you are using kotlinx-serialization with coroutines
-keep class kotlinx.io.** { *; } # if you are using kotlinx-io with coroutines
-keep class kotlinx.datetime.** { *; } # if you are using kotlinx-datetime with coroutines
-keep class kotlin.coroutines.Continuation { *; }
-keep class kotlin.coroutines.CoroutineContext { *; }
# ---------------- END COROUTINE -------------------


# ---------------- BEGIN COMPOSE -------------------
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.runtime.snapshots.** { *; }

-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

-keepclasseswithmembers class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}
# ---------------- END COMPOSE -------------------


# ---------------- BEGIN JNA -------------------
-keep class net.java.dev.jna.** { *; }
-keepclassmembers class net.java.dev.jna.** { *; }

-keep class **.jna.** { *; }

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class ** {
    @**.Structure$FieldOrder *;
}

-keep class ** {
    @**.** *;
}

-keepclassmembers class * {
    @net.java.dev.jna.annotation.* *;
}

-dontwarn com.sun.jna.Native

-keep class foundation.algorand.xhdwalletapi.** { *; }
-keep interface foundation.algorand.xhdwalletapi.** { *; }
# ---------------- END JNA -------------------


# ---------------- BEGIN AES -------------------
-keep interface com.algorand.wallet.encryption.domain.manager.AESPlatformManager { *; }
-keep class com.algorand.wallet.encryption.domain.manager.AESPlatformManagerImpl { *; }
-keepclassmembers class com.algorand.wallet.encryption.domain.manager.AESPlatformManagerImpl { *; }

-keep class javax.crypto.** { *; }
-keep class android.security.keystore.** { *; }
-keep class java.security.** { *; }
-keep class java.util.Base64 { *; }
# ---------------- END JNA -------------------


# ---------------- BEGIN OTHERS -------------------
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
# ---------------- END OTHERS -------------------

# Needed to supress warnings recommended by Android Studio
-dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.ThreadMXBean
-dontwarn net.pwall.json.schema.JSONSchema
-dontwarn java.awt.Component
-dontwarn java.awt.GraphicsEnvironment
-dontwarn java.awt.HeadlessException
-dontwarn java.awt.Window
-dontwarn net.pwall.json.pointer.JSONPointer
-dontwarn net.pwall.json.schema.output.BasicOutput
