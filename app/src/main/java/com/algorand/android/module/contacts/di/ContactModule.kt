package com.algorand.android.module.contacts.di

import com.algorand.android.module.contacts.data.mapper.ContactEntityMapper
import com.algorand.android.module.contacts.data.mapper.ContactEntityMapperImpl
import com.algorand.android.module.contacts.data.mapper.ContactMapper
import com.algorand.android.module.contacts.data.mapper.ContactMapperImpl
import com.algorand.android.module.contacts.data.repository.ContactRepositoryImpl
import com.algorand.android.module.contacts.domain.repository.ContactRepository
import com.algorand.android.module.contacts.domain.usecase.DeleteAllContacts
import com.algorand.android.module.contacts.domain.usecase.DeleteContact
import com.algorand.android.module.contacts.domain.usecase.GetAllContacts
import com.algorand.android.module.contacts.domain.usecase.GetContactByAddress
import com.algorand.android.module.contacts.domain.usecase.GetUsersWithNameFiltered
import com.algorand.android.module.contacts.domain.usecase.SaveContact
import com.algorand.android.module.contacts.domain.usecase.UpdateContact
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.encryption.di.DETERMINISTIC_ENCRYPTION_MANAGER
import com.algorand.android.shared_db.contact.dao.ContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ContactModule {

    @Provides
    @Singleton
    fun provideContactMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): ContactMapper = ContactMapperImpl(encryptionManager)

    @Provides
    @Singleton
    fun provideContactRepository(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager,
        contactMapper: ContactMapper,
        contactEntityMapper: ContactEntityMapper,
        contactDao: ContactDao
    ): ContactRepository {
        return ContactRepositoryImpl(
            contactDao,
            contactMapper,
            contactEntityMapper,
            encryptionManager,
        )
    }

    @Provides
    @Singleton
    fun provideContactEntityMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): ContactEntityMapper = ContactEntityMapperImpl(encryptionManager)

    @Provides
    @Singleton
    fun provideDeleteAllContacts(
        repository: ContactRepository
    ): DeleteAllContacts = DeleteAllContacts(repository::deleteAllContacts)

    @Provides
    @Singleton
    fun provideDeleteContact(
        repository: ContactRepository
    ): DeleteContact = DeleteContact(repository::deleteContact)

    @Provides
    @Singleton
    fun provideGetAllContacts(
        repository: ContactRepository
    ): GetAllContacts = GetAllContacts(repository::getAllContacts)

    @Provides
    @Singleton
    fun provideGetContactByAddress(
        repository: ContactRepository
    ): GetContactByAddress = GetContactByAddress(repository::getContactByAddress)

    @Provides
    @Singleton
    fun provideGetUsersWithNameFiltered(
        repository: ContactRepository
    ): GetUsersWithNameFiltered = GetUsersWithNameFiltered(repository::getUsersWithNameFiltered)

    @Provides
    @Singleton
    fun provideSaveContact(
        repository: ContactRepository
    ): SaveContact = SaveContact(repository::saveContact)

    @Provides
    @Singleton
    fun provideUpdateContact(
        repository: ContactRepository
    ): UpdateContact = UpdateContact(repository::updateContact)
}
