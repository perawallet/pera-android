package com.algorand.android.module.custominfo.data.di

import com.algorand.android.module.custominfo.data.mapper.entity.CustomInfoEntityMapper
import com.algorand.android.module.custominfo.data.mapper.entity.CustomInfoEntityMapperImpl
import com.algorand.android.module.custominfo.data.mapper.model.CustomInfoMapper
import com.algorand.android.module.custominfo.data.mapper.model.CustomInfoMapperImpl
import com.algorand.android.module.custominfo.data.repository.CustomInfoRepositoryImpl
import com.algorand.android.module.custominfo.domain.repository.CustomInfoRepository
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.encryption.di.DETERMINISTIC_ENCRYPTION_MANAGER
import com.algorand.android.module.shareddb.custominfo.dao.CustomInfoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

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
    fun provideCustomInfoMapper(impl: CustomInfoMapperImpl): CustomInfoMapper = impl

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
