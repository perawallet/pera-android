package com.algorand.android.module.asb.mnemonics.data.repository

import com.algorand.android.caching.SharedPrefLocalSource
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.module.asb.mnemonics.domain.repository.AsbMnemonicsRepository
import javax.inject.Inject

internal class AsbMnemonicsRepositoryImpl @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val asbMnemonicsLocalSource: SharedPrefLocalSource<String>
) : AsbMnemonicsRepository {

    override suspend fun storeBackupMnemonics(mnemonics: String) {
        val encryptedMnemonics = encryptionManager.encrypt(mnemonics)
        asbMnemonicsLocalSource.saveData(encryptedMnemonics)
    }

    override suspend fun getBackupMnemonics(): String? {
        return asbMnemonicsLocalSource.getDataOrNull()?.let { encryptionManager.decrypt(it) }
    }

    override suspend fun clearBackupMnemonics() {
        asbMnemonicsLocalSource.clear()
    }
}