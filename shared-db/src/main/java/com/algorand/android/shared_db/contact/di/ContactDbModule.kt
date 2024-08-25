package com.algorand.android.shared_db.contact.di

import com.algorand.android.shared_db.AccountSharedDatabase
import com.algorand.android.shared_db.contact.dao.ContactDao
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ContactDbModule {

    @Provides
    @Singleton
    fun provideContactDao(database: AccountSharedDatabase): ContactDao {
        return database.contactDao()
    }
}
