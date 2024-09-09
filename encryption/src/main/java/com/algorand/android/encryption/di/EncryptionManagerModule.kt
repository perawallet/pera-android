package com.algorand.android.encryption.di

import com.algorand.android.encryption.*
import com.google.crypto.tink.*
import com.google.crypto.tink.daead.*
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.*

const val TINK_ENCRYPTION_MANAGER = "TINK_ENCRYPTION_MANAGER"
const val DETERMINISTIC_ENCRYPTION_MANAGER = "DETERMINISTIC_ENCRYPTION_MANAGER"

@InstallIn(SingletonComponent::class)
@Module
internal object EncryptionManagerModule {

    @Provides
    @Singleton
    @Named(TINK_ENCRYPTION_MANAGER)
    fun provideTinkEncryptionManager(
        tinkEncryptionManager: TinkEncryptionManager
    ): EncryptionManager = tinkEncryptionManager

    @Provides
    @Singleton
    @Named(DETERMINISTIC_ENCRYPTION_MANAGER)
    fun provideDeterministicEncryptionManager(
        deterministicEncryptionManager: DeterministicEncryptionManager
    ): EncryptionManager = deterministicEncryptionManager

    @Provides
    @Singleton
    fun provideBase64Manager(
        base64ManagerImpl: Base64ManagerImpl
    ): Base64Manager = base64ManagerImpl
}
