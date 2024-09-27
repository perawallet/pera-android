package com.algorand.android.module.shareddb.contact.di

import com.algorand.android.module.shareddb.AccountSharedDatabase
import com.algorand.android.module.shareddb.contact.dao.ContactDao
import dagger.Module
import dagger.Provides
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
