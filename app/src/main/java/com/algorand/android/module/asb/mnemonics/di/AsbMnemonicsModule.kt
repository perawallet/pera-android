package com.algorand.android.module.asb.mnemonics.di

import android.content.SharedPreferences
import com.algorand.android.module.asb.mnemonics.data.repository.AsbMnemonicsRepositoryImpl
import com.algorand.android.module.asb.mnemonics.data.storage.AsbMnemonicsLocalSource
import com.algorand.android.module.asb.mnemonics.domain.repository.AsbMnemonicsRepository
import com.algorand.android.module.asb.mnemonics.domain.usecase.GetAsbBackUpMnemonics
import com.algorand.android.module.asb.mnemonics.domain.usecase.SetAsbBackUpMnemonics
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.encryption.di.TINK_ENCRYPTION_MANAGER
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

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
