package com.algorand.android.modules.collectibles.download

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DownloaderModule {

    @Provides
    fun provideDownloader(@ApplicationContext context: Context): Downloader {
        return DownloaderImpl(context)
    }
}
