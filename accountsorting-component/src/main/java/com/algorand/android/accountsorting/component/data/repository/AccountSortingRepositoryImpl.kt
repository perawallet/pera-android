package com.algorand.android.accountsorting.component.data.repository

import com.algorand.android.accountsorting.component.domain.model.*
import com.algorand.android.accountsorting.component.domain.repository.AccountSortingRepository
import com.algorand.android.caching.SharedPrefLocalSource
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.shared_db.accountsorting.dao.AccountIndexDao
import com.algorand.android.shared_db.accountsorting.model.AccountIndexEntity
import javax.inject.Inject

internal class AccountSortingRepositoryImpl @Inject constructor(
    private val accountIndexDao: AccountIndexDao,
    private val encryptionManager: EncryptionManager,
    private val accountSortingTypeIdentifierLocalSource: SharedPrefLocalSource<String>
) : AccountSortingRepository {

    override suspend fun getAllAccountOrderIndexes(): List<AccountOrderIndex> {
        return accountIndexDao.getAll().map {
            AccountOrderIndex(
                address = encryptionManager.decrypt(it.encryptedAddress),
                index = it.index
            )
        }
    }

    override suspend fun setAccountOrderIndex(address: String, orderIndex: Int) {
        val encryptedAddress = encryptionManager.encrypt(address)
        accountIndexDao.insert(AccountIndexEntity(encryptedAddress, orderIndex))
    }

    override suspend fun removeAccountOrderIndex(address: String) {
        val encryptedAddress = encryptionManager.encrypt(address)
        accountIndexDao.delete(encryptedAddress)
    }

    override suspend fun saveAccountSortPreference(identifier: AccountSortingTypeIdentifier) {
        accountSortingTypeIdentifierLocalSource.saveData(identifier.name)
    }

    override suspend fun getAccountSortPreference(): AccountSortingTypeIdentifier {
        val preference = accountSortingTypeIdentifierLocalSource.getDataOrNull()
        return AccountSortingTypeIdentifier.entries.firstOrNull {
            it.name == preference
        } ?: AccountSortingTypeIdentifier.DEFAULT_SORTING_TYPE
    }
}
