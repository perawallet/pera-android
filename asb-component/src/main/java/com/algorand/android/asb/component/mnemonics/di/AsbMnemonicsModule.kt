package com.algorand.android.asb.component.mnemonics.di

import android.content.SharedPreferences
import com.algorand.android.asb.component.mnemonics.data.repository.AsbMnemonicsRepositoryImpl
import com.algorand.android.asb.component.mnemonics.data.storage.AsbMnemonicsLocalSource
import com.algorand.android.asb.component.mnemonics.domain.repository.AsbMnemonicsRepository
import com.algorand.android.asb.component.mnemonics.domain.usecase.*
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.encryption.di.TINK_ENCRYPTION_MANAGER
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.*

@Module
@InstallIn(SingletonComponent::class)
internal object AsbMnemonicsModule {

    @Provides
    @Singleton
    fun provideAsbMnemonicsRepository(
        @Named(TINK_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager,
        sharedPreferences: SharedPreferences
    ): AsbMnemonicsRepository {
        return AsbMnemonicsRepositoryImpl(
            encryptionManager = encryptionManager,
            asbMnemonicsLocalSource = AsbMnemonicsLocalSource(sharedPreferences)
        )
    }

    @Provides
    @Singleton
    fun provideGetAsbBackUpMnemonics(
        repository: AsbMnemonicsRepository
    ): GetAsbBackUpMnemonics = GetAsbBackUpMnemonics(repository::getBackupMnemonics)

    @Provides
    @Singleton
    fun provideSetAsbBackUpMnemonics(
        repository: AsbMnemonicsRepository
    ): SetAsbBackUpMnemonics = SetAsbBackUpMnemonics(repository::storeBackupMnemonics)

}
