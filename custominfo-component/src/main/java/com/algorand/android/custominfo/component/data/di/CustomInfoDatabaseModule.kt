package com.algorand.android.custominfo.component.data.di

import com.algorand.android.custominfo.component.data.mapper.entity.*
import com.algorand.android.custominfo.component.data.mapper.model.*
import com.algorand.android.custominfo.component.data.repository.CustomInfoRepositoryImpl
import com.algorand.android.custominfo.component.domain.repository.CustomInfoRepository
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.encryption.di.DETERMINISTIC_ENCRYPTION_MANAGER
import com.algorand.android.shared_db.custominfo.dao.CustomInfoDao
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.*

@Module
@InstallIn(SingletonComponent::class)
internal object CustomInfoDatabaseModule {

    @Provides
    @Singleton
    fun provideCustomInfoEntityMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): CustomInfoEntityMapper {
        return CustomInfoEntityMapperImpl(encryptionManager)
    }

    @Provides
    @Singleton
    fun provideCustomInfoMapper(
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): CustomInfoMapper {
        return CustomInfoMapperImpl(encryptionManager)
    }

    @Provides
    @Singleton
    fun provideCustomInfoRepository(
        customInfoDao: CustomInfoDao,
        customInfoMapper: CustomInfoMapper,
        customInfoEntityMapper: CustomInfoEntityMapper,
        @Named(DETERMINISTIC_ENCRYPTION_MANAGER) encryptionManager: EncryptionManager
    ): CustomInfoRepository {
        return CustomInfoRepositoryImpl(
            customInfoDao,
            customInfoMapper,
            customInfoEntityMapper,
            encryptionManager
        )
    }
}
